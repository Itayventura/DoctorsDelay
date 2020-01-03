import db.DataBase;
import entities.Appointment;
import entities.Delay;
import entities.Doctor;
import entities.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMocker implements DataBase {
    private ArrayList<String> doctorsNames = new ArrayList<String>()
    {{
        add("Dolittle");
        add("Shiran");
        add("Tal");
    }};

    @Override
    public void addReport(int personalId, String doctorsName, int delay) {

    }

    @Override
    public List<Delay> getDayReport(String doctorsName, LocalDate date) {
        return null;
    }

    @Override
    public List<Delay> getReports(String doctorsName, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public Doctor getDoctor(String doctorsName)
    {
        return new Doctor(null, doctorsName, LocalTime.now().minusHours(5), LocalTime.now().plusHours(5), 10);
    }

    @Override
    public List<Appointment> getUserFutureAppointments(int userId) {
        return null;
    }

    @Override
    public boolean doctorExists(String doctorsName)
    {
        return doctorsNames.contains(doctorsName);
    }

    @Override
    public List<Doctor> getDoctors() {
        ArrayList<Doctor> doctorsNames = new ArrayList<Doctor>()
        {{
            add(new Doctor(null,"Dolittle",LocalTime.now().minusHours(3),LocalTime.now().plusHours(3),10));
            add(new Doctor(null,"Shiran",LocalTime.now().minusHours(5),LocalTime.now().plusHours(3),5));
            add(new Doctor(null,"Tal",LocalTime.now().minusHours(5),LocalTime.now().plusHours(5),15));
        }};
        return doctorsNames;
    }

    @Override
    public List<Delay> getDelays(String doctorsName) {
        ArrayList<Delay> doctorDelayReports = new ArrayList<Delay>()
        {{
            add(new Delay(5, LocalDateTime.now().format(Delay.formatter), Entity.Type.EXPERT));
            add(new Delay(15, LocalDateTime.now().plusMinutes(10).format(Delay.formatter), Entity.Type.USER));
            add(new Delay(20, LocalDateTime.now().plusMinutes(12).format(Delay.formatter), Entity.Type.USER)); // Not valid
            add(new Delay(20, LocalDateTime.now().plusMinutes(30).format(Delay.formatter), Entity.Type.USER));
            add(new Delay(30, LocalDateTime.now().plusMinutes(60).format(Delay.formatter), Entity.Type.EXPERT));
            add(new Delay(5, LocalDateTime.now().plusMinutes(65).format(Delay.formatter), Entity.Type.USER));
            add(new Delay(15, LocalDateTime.now().plusMinutes(120).format(Delay.formatter), Entity.Type.EXPERT));
        }};
        return doctorDelayReports;
    }

    @Override
    public String getUserPassword(int userId) {
        return null;
    }

    @Override
    public void feedbackOnEstimate(int userId, int actualDelay) {

    }

    @Override
    public void addScore(int userId, int scoreAdded) {

    }

    @Override
    public int getScore(int userId) {
        return 0;
    }

    @Override
    public Appointment getLastAppointment(int userId) {
        return null;
    }

    @Override
    public void printTable(String tableName) {

    }
}