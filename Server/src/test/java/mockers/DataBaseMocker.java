package mockers;

import db.DataBase;
import entities.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseMocker implements DataBase {
    public static final String DOCTOR = "DOCTOR";
    public static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(Entity.TIMESTAMP_FORMAT);
    public static final LocalDateTime startTime = LocalDateTime.of(2019, Month.DECEMBER, 27, 8, 0);
    public static final int USER = 1;
    public static final Map<Integer, Patient> clients = new HashMap<Integer, Patient>() {{
        put(2, new Patient("2", "2", 2, Entity.Type.USER));
        put(3, new Patient("3", "3", 3, Entity.Type.USER));
        put(4, new Patient("4", "4", 4, Entity.Type.USER));
    }};
    private Map<String, Map<Integer, List<Integer>>> reports = new HashMap<>();

    @Override
    public void addReport(int personalId, String doctorsName, int delay) {
        Map<Integer, List<Integer>> clientId2reports = reports.computeIfAbsent(doctorsName, key -> new HashMap<>());
        List<Integer> reports = clientId2reports.computeIfAbsent(personalId, key -> new ArrayList<>());
        reports.add(delay);
    }

    public List<Integer> getReports(String doctorsName, int userId) {
        return reports.get(doctorsName).get(userId);
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
        return new Doctor("TYPE", DOCTOR, startTime.toLocalTime(), startTime.plusHours(9).toLocalTime(), 15);
    }

    @Override
    public List<Appointment> getUserFutureAppointments(int userId) {
        return null;
    }

    @Override
    public boolean doctorExists(String doctorsName) {
        return doctorsName.equals(DOCTOR);
    }

    @Override
    public String getUserPassword(int userId) {
        if (clients.containsKey(userId)) {
            return clients.get(userId).getPassword();
        }
        return "";
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
    public List<Doctor> getDoctors() {
        return null;
    }

    @Override
    public List<Delay> getDelays(String doctorsName) {
        return null;
    }

    @Override
    public void printTable(String tableName) {

    }
}
