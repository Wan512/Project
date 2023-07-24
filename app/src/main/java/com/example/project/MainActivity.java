package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.model.SharedPrefManager;
import com.example.project.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference to the textview
        TextView txtHello = findViewById(R.id.txtHello);
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        // set the textview to display username
        txtHello.setText("Welcome " + user.getUsername() + " !");

        // assign action to logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // clear the shared preferences
                SharedPrefManager.getInstance(getApplicationContext()).logout();

                // display message
                Toast.makeText(getApplicationContext(),
                        "You have successfully logged out.",
                        Toast.LENGTH_LONG).show();

                // forward to LoginActivity
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        });

        // assign action to Book List button
        Button btnAppointmentList = findViewById(R.id.btnAppointmentList);
        btnAppointmentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to BookListActivity
                Intent intent = new Intent(getApplicationContext(), AppointmentListActivity.class);
                startActivity(intent);
            }
        });
    }
}