package handlers;

import entities.Appointment;
import repository.AppointmentsRepository;

import java.sql.Timestamp;
import java.util.List;


public class AppointmentsHandler extends Handler {
    private static final int APPOINTMENTS_ATTRIBUTES = 4;

    private static final String sqlSelect = "Select appointment_time, doctor_name From appointments WHERE patient_id = '";
    private static final String sqlSelectAll = "select * from APPOINTMENTS ORDER BY patient_id, appointment_time";
    private static final String sqlInsert = "INSERT INTO appointments(patient_id, appointment_time, doctor_name) VALUES";
    private static final String sqlDelete = "DELETE FROM appointments WHERE ";

    public AppointmentsHandler(){
        repository = new AppointmentsRepository();
    }

    public List<Appointment> getUserFutureAppointments(int userId) {
        String sqlGetFutureAppointments = sqlSelect + userId + "' AND appointment_time > CURRENT_TIMESTAMP";
        return repository.select(sqlGetFutureAppointments);
    }


    public Appointment getLastAppointment(int userId){
        String sqlGetLastAppointment = sqlSelect + userId +"' AND \n" +
                "\t\tappointment_time <= CURRENT_TIMESTAMP AND\n" +
                "\t\tappointment_time >= ALL(SELECT appointment_time\n" +
                "\t\t\t\t\t\t\t\t\t\tFROM appointments\n" +
                "\t\t\t\t\t\t\t\t\t\tWHERE patient_id = '" + userId + "' AND\n" +
                "\t\t\t\t\t\t\t\t\t\t\t\tappointment_time <= CURRENT_TIMESTAMP);\n";
        List<Appointment> appointments = repository.select(sqlGetLastAppointment);
        if (!appointments.isEmpty())
            return appointments.get(0);
        return null;
    }


    public void addAppointments(List<Appointment> appointments){
        String sqlInsertAppointments = sqlInsert;
        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment = appointments.get(i);
            String sqlValues = "('" +appointment.getPatientId() + "','"  +
                    Timestamp.valueOf(appointment.getAppointmentDateTime()) + "','"
                    + appointment.getDoctorsName() + "')";
            sqlInsertAppointments += sqlValues;
            if (i != appointments.size() - 1)
                sqlInsertAppointments += ",";
        }
        repository.insert(sqlInsertAppointments);
    }

    public void removeAppointment(String appointmentTime, String doctorsName){
        String sqlRemoveAppointment = sqlDelete + "appointment_time = '" + appointmentTime
                                                + "' AND doctor_name = '" + doctorsName + "'";
        repository.delete(sqlRemoveAppointment);
    }

    public void printTable(){
        printHeadline("APPOINTMENTS", APPOINTMENTS_ATTRIBUTES);
        printTable(sqlSelectAll);
    }
}
