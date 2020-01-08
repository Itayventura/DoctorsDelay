package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Appointment extends Entity {

    private String appointmentDateTime;
    private String doctorsName;

    /** this constructor is used whenever selection from "appointments" is made */
    public Appointment(String appointmentDateTime, String doctorsName){
        this.appointmentDateTime = appointmentDateTime;
        this.doctorsName = doctorsName;
    }

    /** this constructor serves DataHandler */
    public Appointment(String patientId, String appointmentDateTime, String doctorsName) {
        this(appointmentDateTime, doctorsName);
        setAttribute(AttributeName.patientId, patientId);
    }

    public String getPatientId() {
        return getAttribute(AttributeName.patientId);
    }

        public String getDoctorsName() {
        return doctorsName;
    }

    public LocalDateTime getAppointmentDateTime() {
        return LocalDateTime.parse(appointmentDateTime, formatter);
    }

    public String toString(){

        List<String> title = new ArrayList<>();
        List<String> info = new ArrayList<>();

        if (isAttributeExist(AttributeName.patientId)) {
            title.add("User's id: ");
            info.add(getPatientId());
        }

        title.add("appointment time:");
        info.add(getAppointmentDateTime().format(formatter));


        title.add("doctor name:");
        info.add(getDoctorsName());

        return toString(title,info);
    }

}
