package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.model.SharedPrefManager;
import com.example.project.model.User;
import com.example.project.model.appointment;
import com.example.project.remote.ApiUtils;
import com.example.project.remote.AppointmentService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAppointmentActivity extends AppCompatActivity {

    private EditText txtTitle;
    private EditText txtDesc;
    private EditText txtDate;
    private EditText txtTime;
    private EditText txtYear;
    private Button buttonAddAppointment;
    private static TextView tvCreated; // static because need to be accessed by DatePickerFragment

    private static Date createdAt;// static because need to be accessed by DatePickerFragment
    private Context context;

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
            tvCreated.setText( sdf.format(createdAt) );
        }
    }

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        // store context
        context = this;

        // get view objects references
        txtDesc = findViewById(R.id.txtDesc);
        tvCreated = findViewById(R.id.tvDate);
     //   txtTime = findViewById(R.id.tvTime);
        buttonAddAppointment = findViewById(R.id.buttonAddAppointment);

        // set default createdAt value, get current date
        createdAt = new Date();
        // display in the label beside the button with specific date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tvCreated.setText( sdf.format(createdAt) );

        buttonAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {addNewAppointment(v);
            }
        });

    }

    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Called when Add Appointment button is clicked
     * @param v
     */
    public void addNewAppointment(View v) {
        // get values in form
        String desc = txtDesc.getText().toString();
       // String date = txtDate.getText().toString();
       // String time = txtTime.getText().toString();

        // convert createdAt date to format in DB
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String created_at = sdf.format(createdAt);//perlu betulkan

        // set updated_at with the same value as created_at
 //       String updated_at = created_at;//perlu betulkan

        // create a Book object
        // set id to 0, it will be automatically generated by the db later
        // set image to dummy value
        appointment b  = new appointment(0, created_at, desc);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to add new appointment to the REST API
        AppointmentService appointmentService = ApiUtils.getAppointmentService();
        Call<appointment> call = appointmentService.Addappointment(user.getToken(), b);

        // execute
        call.enqueue(new Callback<appointment>() {
            @Override
            public void onResponse(Call<appointment> call, Response<appointment> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // appointment added successfully?
                appointment addedAppointment = response.body();
                if (addedAppointment != null) {
                    // display message
                    Toast.makeText(context,
                            addedAppointment.getAppointment_desc() + addedAppointment.getAppointment_date() + " added successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, AppointmentListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Add New Appointment failed.");
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
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
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