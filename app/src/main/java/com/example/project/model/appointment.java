package com.example.project.model;

public class appointment {

    private String description;

    // Constructor and other methods

    // Setter method for the description
    public void setDesc(String description) {
        this.description = description;
    }
    private String createdAt;

    // Constructor and other methods

    // Setter method for the creation date
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    private String updatedAt;

    // Constructor and other methods

    // Setter method for the update date
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    private int appointment_id;

    //private String appointment_time;
    private String appointment_date;

    private String appointment_desc;
    //private int id;

    public appointment() {
    }

    public appointment(int appointment_id, String appointment_date, String appointment_desc) {
        this.appointment_id = appointment_id;
        this.appointment_date = appointment_date;
       // this.appointment_time = appointment_time;
        this.appointment_desc = appointment_desc;
        //this.id = id;
    }


    public int getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

   // public String getAppointment_time() {
        ///return appointment_time;
   // }

    //public void setAppointment_time(String appointment_time) {
    //    this.appointment_time = appointment_time;
    //}

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getAppointment_desc() {
        return appointment_desc;
    }

    public void setAppointment_desc(String appointment_desc) {
        this.appointment_desc = appointment_desc;
    }

   // public int getId() {
       // return id;
    //}

    //public void setId(int id) {
        //this.id = id;
  //  }

    @Override
    public  String toString() {
        return "appointment{" +
                "Appointment_id=" + appointment_id +
                //", appointment_time='" + appointment_time + '\'' +
                ", appointment_date='" + appointment_date + '\'' +
                ", appointment_desc='" + appointment_desc + '\'' +
             //   ", id='" + id + '\'' +
                '}';
    }
}
