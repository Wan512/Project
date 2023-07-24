package com.example.project.remote;


public class ApiUtils {

    // REST API server URL
    public static final String BASE_URL = "https://nurussyamila.000webhostapp.com/clinic/";

    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    // return AppointmentService instance

    public static AppointmentService getAppointmentService() {
        return RetrofitClient.getClient(BASE_URL).create(AppointmentService.class);
    }
}
