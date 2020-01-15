package db;

import entities.*;
import handlers.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class DataBaseImpl implements DataBase {

    protected static final Logger logger = Logger.getLogger(DataBaseImpl.class);

    private HashMap<String, DelaysHandler> doctorsDelaysHandler;
    private AppointmentsHandler appointmentsHandler;
    private DoctorsHandler doctorsHandler;
    private PatientsHandler patientsHandler;

    private static Process mySqlTask;
    private static final String DB = "\\DB";
    private static final String mySql = "\\MySQLServer\\bin\\mysqld";

    static {
        try {
            String path = System.getProperty("user.dir");
            String substring = path.length() > 2 ? path.substring(path.length() - 2) : null;
            if (substring != null) {
                if (!substring.equals("DB"))
                    path += DB;
                path += mySql;
                mySqlTask = Runtime.getRuntime().exec(path);
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info("Closing mysql process");
                    mySqlTask.destroy();
                }
            });
        } catch(IOException e){
            String errorMessage = "Couldn't start mysql from path " + System.getProperty("user.dir") + mySql;
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    public DataBaseImpl(){
        appointmentsHandler = new AppointmentsHandler();
        doctorsHandler = new DoctorsHandler();
        patientsHandler = new PatientsHandler();
        doctorsDelaysHandler = new HashMap<>();
    }

    @Override
    public void addReport(int personalId, String doctorsName, int expectedDelay){
        Patient patient = patientsHandler.getPatient(personalId);
        if (patient == null) {
            logger.info("patient " + personalId + " doesn't exist");
            return;
        }
        DelaysHandler delaysHandler = getDelayHandler(doctorsName);
       if (delaysHandler == null) {
           logger.info("doctor " + doctorsName + " doesn't exist");
           return;
       }
        delaysHandler.addReport(new Delay(expectedDelay, patientsHandler.getPatient(personalId).getReportType(),personalId));
    }

    @Override
    public void feedbackOnEstimate(int userId, int actualDelay) {
        Appointment lastAppointment = getLastAppointment(userId);
        if (lastAppointment == null) {
            logger.info("patient " + userId + "'s last appointment doesn't exist");
            return;
        }
        String appointmentTime = lastAppointment.getAppointmentDateTime().format(Entity.formatter);
        Delay delay = new Delay(actualDelay, appointmentTime, Entity.Type.FEEDBACK, userId);
        DelaysHandler delaysHandler = getDelayHandler(lastAppointment.getDoctorsName());
        delaysHandler.addReport(delay);
    }

    @Override
    public List<Delay> getReports(String doctorsName, LocalDateTime from, LocalDateTime to) {
        DelaysHandler delaysHandler = getDelayHandler(doctorsName);
        if (delaysHandler == null){
            logger.info("doctor " + doctorsName + " doesn't exist");
            return null;
        }
        return delaysHandler.getReports(from,to);
    }

    @Override
    public List<Delay> getDelays(String doctorsName) {
        DelaysHandler delaysHandler = getDelayHandler(doctorsName);
        if (delaysHandler == null) {
            logger.info("doctor " + doctorsName + " doesn't exist");
            return null;
        }
        return delaysHandler.getDelays();
    }

    @Override
    public List<Delay> getDayReport(String doctorsName, LocalDate date) {
        Doctor doctor = doctorsHandler.getDoctor(doctorsName);
        if (doctor == null) {
            logger.info("doctor " + doctorsName + " doesn't exist");
            return null;
        }
        DelaysHandler delaysHandler = getDelayHandler(doctorsName);
        return delaysHandler.getReports(LocalDateTime.of(date, doctor.getStartTime()),
                                        LocalDateTime.of(date, doctor.getEndTime()));
    }

    @Override
    public Doctor getDoctor(String doctorsName) {
        return doctorsHandler.getDoctor(doctorsName);
    }

    @Override
    public List<Doctor> getDoctors() {
        return doctorsHandler.getDoctors();
    }

    @Override
    public boolean doctorExists(String doctorsName) {
        if(doctorsDelaysHandler.isEmpty()){
            initializeDoctorsDelaysHandler();
        }
        return doctorsDelaysHandler.containsKey(doctorsName);
    }

    @Override
    public List<Appointment> getUserFutureAppointments(int userId) {
        return appointmentsHandler.getUserFutureAppointments(userId);
    }

    @Override
    public Appointment getLastAppointment(int userId) {
        return appointmentsHandler.getLastAppointment(userId);
    }

    @Override
    public String getUserPassword(int userId) {
        return patientsHandler.getUserPassword(userId);
    }

    @Override
    public void addScore(int userId, int scoreAdded) {
        patientsHandler.addScore(userId,scoreAdded);
    }

    @Override
    public int getScore(int userId) {
        return patientsHandler.getScore(userId);
    }

    @Override
    public void printTable(String tableName) {
        switch(tableName){
            case "doctors":
                doctorsHandler.printTable();
                return;
            case "appointments":
                appointmentsHandler.printTable();
                return;
            case "patients":
                patientsHandler.printTable();
                return;
            default:
                DelaysHandler delaysHandler = getDelayHandler(tableName);
                if (delaysHandler != null)
                    delaysHandler.printTable();
                else
                    logger.info("table " + tableName + " doesn't exist");
        }
    }

    public DelaysHandler getDelayHandler(String doctorsName) {
        if(doctorsDelaysHandler.isEmpty()){
            initializeDoctorsDelaysHandler();
        }
        return doctorsDelaysHandler.get(doctorsName);
    }

    private void initializeDoctorsDelaysHandler() {
        List<Doctor> doctors = getDoctors();
        for (Doctor doctor: doctors) {
            doctorsDelaysHandler.put(doctor.getName(), new DelaysHandler(doctor.getName()));
        }
    }


}
