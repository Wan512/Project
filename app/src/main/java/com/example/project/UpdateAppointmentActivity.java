package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.model.SharedPrefManager;
import com.example.project.model.User;
import com.example.project.model.appointment;
import com.example.project.remote.ApiUtils;
import com.example.project.remote.AppointmentService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAppointmentActivity extends AppCompatActivity {
    private AppointmentService appointmentService;
    private appointment appointment;
    private static TextView tvCreated; // Declare the TextView for displaying the selected date
    private static Date createdAt;
    private static TextView getTvCreated;

   // private Context context;

    private EditText txtDesc; // Declare the Date variable for storing the selected date
    private com.example.project.remote.AppointmentService AppointmentService;

    /// @Override
    //protected void onCreate(Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
     //   setContentView(R.layout.activity_update_appointment);

        // Get the reference to the TextView for displaying the selected date
     //   tvCreated = findViewById(R.id.tvCreated);
   // }

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            // create a date object from selected year, month and day
            createdAt = new GregorianCalendar(year, month, day).getTime();

            // display in the label beside the button with specific date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            tvCreated.setText(sdf.format(createdAt));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_appointment);

        // retrieve book id from intent
        // get book id sent by BookListActivity, -1 if not found
        Intent intent = getIntent();
        int appointment_id = intent.getIntExtra("appointment_id", -1);

        // initialize createdAt to today's date
        createdAt = new Date();

       // appointment = new appointment();

        // get references to the form fields in layout
        txtDesc = findViewById(R.id.txtDesc);
        tvCreated = findViewById(R.id.tvCreated);

        // retrieve book info from database using the book id
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get book service instance
        appointmentService = ApiUtils.getAppointmentService();

        appointmentService.getAppointment(user.getToken(),appointment_id).enqueue(new Callback<com.example.project.model.appointment>() {
            @Override
            public void onResponse(Call<com.example.project.model.appointment> call, Response<com.example.project.model.appointment> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // get book object from response
                appointment = response.body();


            }

            @Override
            public void onFailure(Call<com.example.project.model.appointment> call, Throwable t) {

            }
        });
        // execute
//        call.enqueue(new Callback<appointment>() {
//            @Override
//            public void onResponse(Call<appointment> call, Response<appointment> response) {
//
//                // for debug purpose
//                Log.d("MyApp:", "Response: " + response.raw().toString());
//
//                // invalid session?
//                if (response.code() == 401)
//                    displayAlert("Invalid session. Please re-login");
//
//                // book updated successfully?
//                appointment updatedAppointment = response.body();
//                if (updatedAppointment != null) {
//                    // display message
//                    Toast.makeText(context,
//                            updatedAppointment.getAppointment_id() + " updated successfully.",
//                            Toast.LENGTH_LONG).show();
//
//                    // end this activity and forward user to BookListActivity
//                    Intent intent = new Intent(context, AppointmentListActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    displayAlert("Update Appointment failed.");
//                }
//            }
//            @Override
//            public void onFailure(Call<appointment> call, Throwable t) {
//                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
//            }
//
//
//
//
//        });
    }
    public void updateAppointment(View view) {
        // get values in form
        String date = tvCreated.getText().toString();
        String description = txtDesc.getText().toString();

        // convert createdAt date to format in DB
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String created_at = sdf.format(createdAt);

        // set updated_at to current date and time
       // String updated_at = sdf.format(new Date());

        // update the book object retrieved in onCreate with the new data. the book object
        // already contains the id
        appointment.setDesc(description);
        appointment.setCreatedAt(created_at);
       // appointment.setUpdatedAt(updated_at);

        Log.d("MyApp:", "appointment info: " + appointment.toString());

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to update the book record to the REST API
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<appointment> call = appointmentService.updateAppointment(user.getToken(), appointment);

        Context context = this;
        // execute
        call.enqueue(new Callback<appointment>() {
            @Override
            public void onResponse(Call<appointment> call, Response<appointment> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // book updated successfully?
                appointment updatedAppointment = response.body();
                if (updatedAppointment != null) {
                    // display message
                    Toast.makeText(context,
                            updatedAppointment.getAppointment_id() + " updated successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, AppointmentListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Update Book failed.");
                }
            }

            @Override
            public void onFailure(Call<appointment> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
    /**
     * Called when pick date button is clicked. Display a date picker dialog
     *
     * @param message
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}