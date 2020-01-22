package handlers;

import entities.*;
import repository.DelaysRepository;

import java.time.*;
import java.util.*;

public class DataHandler extends Handler {
    private static final String doctorsName = "brodetzki";

    private static final int SMALL_BOUNDARY = 15;
    private static final int MEDIUM_BOUNDARY = 30;
    private static final int BIG_BOUNDARY = 45;
    private static final double SD_LOWER_BOUNDARY = 0.5;
    private static final double SD_UPPER_BOUNDARY = 1.0;

    private static Random random = new Random();

    private DelaysHandler delaysHandler;
    private DoctorsHandler doctorsHandler;
    private PatientsHandler patientsHandler;
    private AppointmentsHandler appointmentsHandler;

    DataHandler(){
        delaysHandler = new DelaysHandler(doctorsName);
        doctorsHandler = new DoctorsHandler();
        patientsHandler = new PatientsHandler();
        appointmentsHandler = new AppointmentsHandler();
    }

    private List<String> getDailyPatients(int length, List <Patient> patients) {
        List<String> patientsGenerated = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int patientIndex = (int) generateRandomBetweenRange(0, patients.size());
            String patientId = patients.get(patientIndex).getPersonalId();
            if (patientsGenerated.contains(patientId)) {
                i--;
            }
            else
            {
                patientsGenerated.add(patientId);
            }
        }
        return patientsGenerated;
    }

    private void addMonthlyAppointments(Doctor doctor, List<Patient> patients, List<String> dates) {
        List<Appointment> appointments = new ArrayList<>();
        for (String date : dates) {
            System.out.println(date);
            String[] timestamps = getTimestamps(doctor.getStartTime().getHour(),doctor.getEndTime().getHour(),doctor.getInterval(),date);
            List<String> patientIds = getDailyPatients(timestamps.length, patients);
            for(int i = 0; i < timestamps.length; i++){
                Appointment appointment = new Appointment(patientIds.get(i), timestamps[i], doctorsName);
                appointments.add(appointment);
            }
        }
        appointmentsHandler.addAppointments(appointments);
    }

    /* duplicate key might be thrown */
    private void addAppointments(Doctor doctor){
        List<Patient> patients = patientsHandler.getPatients();
        for (final Month month : Month.values()) {
            List<String> dates = getDates(month);
            addMonthlyAppointments(doctor, patients, dates);
        }
    }


    private Patient createPatient(){
        int patientId = (int) generateRandomBetweenRange(1000000, 999999999);
        String password = patientId + "";
        return new Patient(patientId+"",password);
    }

    private void createPatients(int numOfPatients) {
        for (int j = 0; j < numOfPatients; j+= 5000) {
            System.out.println(j);
            List<Patient> newPatients = new ArrayList<>();
            for (int i = 0; i < 5000; i++) {
                newPatients.add(createPatient());
            }
            patientsHandler.addPatients(newPatients);
        }
    }

    private String[] getTimestamps(int startHour, int endHour, int interval, String date) {
        String seconds = "00";
        int numOfAppointments = ((endHour - startHour) * 60) / interval;
        String[] timestamps = new String[numOfAppointments];
        int i = 0;
        for (int hour = startHour; hour < endHour; hour++) {
            for (int minute = 0; minute < 60; minute += interval, i++) {
                String hours = hour < 10 ? "0" + hour : "" + hour;
                String minutes = minute < 10 ? "0" + minute : "" + minute;
                String time = hours + ":" + minutes + ":" + seconds;
                timestamps[i] = date + " " + time;
            }
        }
        return timestamps;
    }

    private double[] getObservationsFromNormalDistribution(int numOfAppointments, double[] expectancy, Double standardDerivation, int day) {
        double[] observations = new double[numOfAppointments];

        for(int i =0;i < numOfAppointments; i++){
            if (i%6 != 0)
                observations[i] = random.nextGaussian()*standardDerivation + expectancy[i + day*numOfAppointments];
            else
                observations[i] = expectancy[i + day*numOfAppointments];
        }
        return observations;
    }

    private int getDay(DayOfWeek day){
        if (day == DayOfWeek.SUNDAY)
            return 0;
        return day.getValue();
    }

    private double getMorningLate(DayOfWeek day) {
        switch (day){
            case SUNDAY:
                return generateRandomBetweenRange(SD_LOWER_BOUNDARY/3, 2*SMALL_BOUNDARY/3);
            case WEDNESDAY:
                return generateRandomBetweenRange(0, SMALL_BOUNDARY/3);
            case MONDAY:
                return generateRandomBetweenRange(SMALL_BOUNDARY + 2*SMALL_BOUNDARY/3, MEDIUM_BOUNDARY);
            case THURSDAY:
                return generateRandomBetweenRange(SMALL_BOUNDARY, SMALL_BOUNDARY + 2*SMALL_BOUNDARY/3);
            case FRIDAY:
                return generateRandomBetweenRange(SMALL_BOUNDARY, SMALL_BOUNDARY + SMALL_BOUNDARY/3);
            case TUESDAY:
                return generateRandomBetweenRange(MEDIUM_BOUNDARY + SMALL_BOUNDARY/3, BIG_BOUNDARY - SMALL_BOUNDARY/3);
            case SATURDAY:
                return generateRandomBetweenRange(MEDIUM_BOUNDARY, MEDIUM_BOUNDARY + SMALL_BOUNDARY/3);
        }
        return -10000;

    }

    private List<Delay> getDelayList(Doctor doctor,
                                     String sdate,
                                     double[] expectancy){
        Delay delay;
        List<Delay> delayList = new ArrayList<>();
        Entity.Type reportType = Entity.Type.USER;
        DayOfWeek dayOfWeek = (LocalDate.parse(sdate)).getDayOfWeek();
        double reportedDelay = getMorningLate(dayOfWeek);
        double standardDerivation = generateRandomBetweenRange(SD_LOWER_BOUNDARY, SD_UPPER_BOUNDARY);
        int interval = doctor.getInterval();
        int startHour = doctor.getStartTime().getHour();
        int endHour = doctor.getEndTime().getHour();
        int numOfAppointments = ((endHour - startHour) * 60) / interval; //60 minutes in hour
        double[] observations = getObservationsFromNormalDistribution(numOfAppointments, expectancy, standardDerivation, getDay(dayOfWeek));
        String[] timestamps = getTimestamps(startHour,endHour,interval,sdate);
        int userId = 111111111;

        System.out.println("Date: " + sdate);
        System.out.println("Day:" + dayOfWeek);
        System.out.println("morning late: " + reportedDelay);
        System.out.println("standardDerivation:" + standardDerivation);
        System.out.println();

        for(int i = 0; i < numOfAppointments; i++){
            reportedDelay += (observations[i] - interval);
            reportedDelay = reportedDelay<0? 0: reportedDelay;
            if (i%6 != 0)
                delay = new Delay((int)reportedDelay, timestamps[i], reportType,userId);
            else
                delay = new Delay((int)reportedDelay, timestamps[i], Entity.Type.EXPERT,1);
            delayList.add(delay);
        }
        return delayList;
    }

    private List<String> getDates(Month month){
        int daysInMonth = 0;
        switch(month){
            case JANUARY:
            case MARCH:
            case MAY:
            case JULY:
            case AUGUST:
            case OCTOBER:
            case DECEMBER:
                daysInMonth = 31;
                break;
            case FEBRUARY:
                daysInMonth = 28;
                break;
            case APRIL:
            case JUNE:
            case SEPTEMBER:
            case NOVEMBER:
                daysInMonth =30;

        }
        List<String> dates = new ArrayList<>();
        String mm = month.getValue()<10? "0" + month.getValue(): month.getValue()+"";
        for (int day = 1; day <= daysInMonth; day++) {
            String dd = day < 10 ? "0" + day : "" + day;
            String date = ("2019-" + mm + "-" + dd);
            dates.add(date);
        }
        return dates;
    }

    private double generateRandomBetweenRange(double lowerBoundary, double upperBoundary) {
        return (Math.random() * (upperBoundary - lowerBoundary)) + lowerBoundary;
    }

    private double[] getExpectancy(Doctor doctor) {
        int interval = doctor.getInterval();
        LocalTime start = doctor.getStartTime();
        LocalTime end = doctor.getEndTime();
        int numOfWorkingHours = end.getHour() - start.getHour();
        double[] Expectancy = new double[7 * numOfWorkingHours * 60 / interval];
        for (int i = 0; i < Expectancy.length; i++){
            Expectancy[i] = interval - 0.125 + generateRandomBetweenRange(-4.0,4.0);
            System.out.println("expectancy: " + Expectancy[i]);
        }
        return Expectancy;
    }

    private void addDelays(Doctor doctor){
        double[] expectancy = getExpectancy(doctor);
        for (final Month month : Month.values()) {
            List<Delay> delays = new ArrayList<>();
            List<String> dates = getDates(month);
            for (String date : dates) {
                delays.addAll(getDelayList(doctor, date, expectancy));
            }
            delaysHandler.addReportList(delays);
        }
    }

    private void addNormal() {
        Doctor doctor = doctorsHandler.getDoctor(doctorsName);
        if (doctor != null)
            System.out.println(doctor.toString());
        if(doctor == null){
            doctorsHandler.addDoctor(doctorsName);
            doctor = doctorsHandler.getDoctor(doctorsName);
        }

        addDelays(doctor);
        addAppointments(doctor);
    }

    public static void main(String [] args) {
        printHeadline("Data Handler");

        DataHandler dataHandler = new DataHandler();
        //dataHandler.addNormal(); /* this method adds doctor to doctors table. create table of delays specified to this doctor. creates appointments for this doctor. parameters: expectancy, standard derivation, doctors name. some are global. ****attention*** foreign key might be thrown because there is a constraint: a patient cannot have two appointments in the same time and patients are sampled randomly*/
        //dataHandler.createPatients(50000);

        dataHandler.printHeadline("Data Handler finished successfully");
    }



}
