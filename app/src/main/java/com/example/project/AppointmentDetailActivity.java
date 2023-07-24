package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.model.SharedPrefManager;
import com.example.project.model.User;
import com.example.project.model.appointment;
import com.example.project.remote.ApiUtils;
import com.example.project.remote.AppointmentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


 public class AppointmentDetailActivity extends AppCompatActivity {

    AppointmentService appointmentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        // get appointment id sent by AppointmentListActivity, -1 if not found
        Intent intent = getIntent();
        int appointment_id = intent.getIntExtra("appointment_id", -1);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get appointment service instance
        appointmentService = ApiUtils.getAppointmentService();

        // execute the API query. send the token and appointment id
        appointmentService.getAppointment(user.getToken(), appointment_id).enqueue(new Callback<appointment>() {
            @Override
            public void onResponse(Call<appointment> call, Response<appointment> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // get appointment object from response
                appointment appointment = response.body();

                // get references to the view elements
                TextView tvDate = findViewById(R.id.tvDate);
                TextView tvTime = findViewById(R.id.tvTime);
                TextView tvDesc = findViewById(R.id.tvDesc);

                // set values
                tvDate.setText(appointment.getAppointment_date()); // Corrected here
                tvTime.setText(appointment.getAppointment_time()); // Corrected here
                tvDesc.setText(appointment.getAppointment_desc());

            }

            @Override
            public void onFailure(Call<appointment> call, Throwable t) {
                Toast.makeText(AppointmentDetailActivity.this, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });

    }
}
