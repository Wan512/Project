package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.project.adapter.AppointmentAdapter;
import com.example.project.model.DeleteResponse;
import com.example.project.model.appointment;
import com.example.project.model.SharedPrefManager;
import com.example.project.model.User;
import com.example.project.remote.ApiUtils;
import com.example.project.remote.AppointmentService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentListActivity extends AppCompatActivity {

    AppointmentService appointmentService;
    Context context;
    AppointmentAdapter adapter;
    RecyclerView appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        context = this; // get current activity context

        // get reference to the RecyclerView appointmentList
        appointmentList = findViewById(R.id.AppointmentList);

        // register for context menu
        registerForContextMenu(appointmentList);

        // action handler for Add Appointment floating button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to NewAppointmentActivity
                Intent intent = new Intent(context, NewAppointmentActivity.class);
                startActivity(intent);
            }
        });

        // Fetch data and update the ListView
        updateListView();
    }

    /**
     * Fetch data for ListView
     */
    private void updateListView() {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get appointment service instance
        appointmentService = ApiUtils.getAppointmentService();

        // execute the call. send the user token when sending the query
        appointmentService.getAllAppointment(user.getToken()).enqueue(new Callback<List<appointment>>() {
            @Override
            public void onResponse(Call<List<appointment>> call, Response<List<appointment>> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // token is not valid/expired
                if (response.code() == 401) {
                    displayAlert("Session Invalid");
                }

                // Get list of appointment object from response
                List<appointment> appointments = response.body();

                // initialize adapter
                adapter = new AppointmentAdapter(context, appointments);

                // set adapter to the RecyclerView
                appointmentList.setAdapter(adapter);

                // set layout to recycler view
                appointmentList.setLayoutManager(new LinearLayoutManager(context));

                // add separator between items in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(appointmentList.getContext(),
                        DividerItemDecoration.VERTICAL);
                appointmentList.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<appointment>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appointment_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        appointment selectedAppointment = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedAppointment.toString());
        switch (item.getItemId()) {
            case R.id.menu_details: // should match the id in the context menu file
                doViewDetails(selectedAppointment);
                break;
            case R.id.menu_delete://should match the id in the context menu file
                doDeleteappointment(selectedAppointment);
                break;
            case R.id.menu_update://should match the id in the context menu file
                doUpdate(selectedAppointment);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void doUpdate(appointment selectedAppointment) {
        // for debugging purpose
        Log.d("MyApp:", "launching update activity for "+selectedAppointment.toString());

        // launch UpdateBookActivity and pass the book id
        Intent intent = new Intent(context, UpdateAppointmentActivity.class);
        intent.putExtra("appointment_id", selectedAppointment.getAppointment_id());
        startActivity(intent);
    }
    private void doViewDetails(appointment selectedAppointment) {
        Log.d("MyApp:", "viewing details " + selectedAppointment.toString());
        Intent intent = new Intent(context, AppointmentDetailActivity.class);
        intent.putExtra("appointment_id", selectedAppointment.getAppointment_id());
        startActivity(intent);
    }

    /**
     * Delete book record. Called by contextual menu "Delete"
     * @param selectedAppointment - book selected by user
     */
    private void doDeleteappointment(appointment selectedAppointment) {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // prepare REST API call
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<DeleteResponse> call = appointmentService.deleteAppointment(user.getToken(), selectedAppointment.getAppointment_id());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                Toast.makeText(context, "My code error: "+response.code(), Toast.LENGTH_LONG).show();
                Log.d("MyApp", "Response code: " + response.code());
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Appointment successfully deleted");
                    // update data in list view
                    updateListView();
                } else {
                    displayAlert("Appointment failed to delete");
                    Log.e("MyApp:", response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    /**
     * Displaying an alert dialog with a single button
     *
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
