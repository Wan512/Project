package com.example.project.remote;

import com.example.project.model.DeleteResponse;
import com.example.project.model.appointment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AppointmentService {

    @GET("api/appointment")
    Call<List<appointment>> getAllAppointment(@Header("api-key") String api_key);

    @GET("api/appointment/{appointment_id}")
    Call<appointment> getAppointment(@Header("api-key") String api_key, @Path("appointment_id")int appointment_id);

    /**
     * Add book by sending a single Book JSON
     * @return book object
     */

    @POST("api/appointment")
    Call<appointment> Addappointment(@Header ("api-key") String apikey, @Body appointment appointment);

    /**
     * Delete book based on the id
     * @return DeleteResponse object
     */
    @POST("api/appointment/delete/{id}")
    Call<DeleteResponse> deleteAppointment(@Header ("api-key") String apiKey, @Path("id") int id);

    Call<appointment> updateAppointment(@Header ("api-key") String apiKey, @Body Object appointment);
}
