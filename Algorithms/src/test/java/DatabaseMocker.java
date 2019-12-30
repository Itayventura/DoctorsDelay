import db.DataBase;
import entities.Appointment;
import entities.Delay;
import entities.Doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMocker implements DataBase {
    private ArrayList<String> doctorsNames = new ArrayList<String>()
    {{
        add("Dolittle");
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
    public Doctor getDoctor(String doctorsName) {
        return new Doctor(null, "Dolittle", LocalTime.now().minusHours(5), LocalTime.now().plusHours(5), 10);
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
    public String getUserPassword(int userId) {
        return null;
    }

    @Override
    public void feedbackOnEstimate(int userId, String doctorsName, int actualDelay) {

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