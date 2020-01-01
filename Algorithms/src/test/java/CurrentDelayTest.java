import algorithms.Algorithms;
import algorithms.AlgorithmsImpl;
import db.DataBase;
import entities.Appointment;
import entities.Delay;
import entities.Doctor;
import entities.Entity;
import estimation.DelayEstimation;
import org.junit.Assert;
import org.junit.Test;
import current.CurrentDelayUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CurrentDelayTest {
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern(Entity.TIMESTAMP_FORMAT);
    private static final LocalDateTime startTime = LocalDateTime.of(2019, Month.DECEMBER, 27, 8, 0);
    private static final String DOCTOR = "DOCTOR";

    @Test
    public void testCurrentDelay() throws Exception{
        final int delay = 10;
        final List<Delay> delays = Arrays.asList(newDelay(delay, startTime.plusMinutes(10)),
                newDelay(delay, startTime.plusMinutes(70)),
                newDelay(delay, startTime.plusMinutes(130)));
        final Algorithms algorithms = new AlgorithmsImpl(new DataBaseMocker(delays), null);
        DelayEstimation delayEstimation = algorithms.getCurrentDelay(DOCTOR);
        Assert.assertEquals(15, delayEstimation.getTypeRange().getMaximalDelay());
        Assert.assertEquals(33, delayEstimation.getEstimationAccuracyPercentage());
    }

    @Test
    public void testCurrentDelayNoDoctor() {
        final Algorithms algorithms = new AlgorithmsImpl(new DataBaseMocker(Collections.emptyList()), null);
        Assert.assertThrows(Algorithms.AlgorithmException.class, () -> algorithms.getCurrentDelay("NO_DOCTOR"));
    }

    @Test
    public void testCurrentDelayNoDelays() {
        final Algorithms algorithms = new AlgorithmsImpl(new DataBaseMocker(Collections.emptyList()), null);
        Assert.assertThrows(Algorithms.AlgorithmException.class, () -> algorithms.getCurrentDelay(DOCTOR));
    }

    @Test
    public void testPredictedDelaySteady() {
        final int delay = 10;
        final List<Delay> delays = Arrays.asList(newDelay(delay, startTime.plusMinutes(10)),
                newDelay(delay, startTime.plusMinutes(70)),
                newDelay(delay, startTime.plusMinutes(130)));
        Assert.assertEquals(delay, (int) CurrentDelayUtil.getPredictedDelay(delays, startTime));
    }

    @Test
    public void testPredictedDelaySloped() throws Exception{
        final List<Delay> delays = Arrays.asList(newDelay(10, startTime.plusMinutes(10)),
                newDelay(13, startTime.plusMinutes(70)),
                newDelay(20, startTime.plusMinutes(130)));
        Assert.assertEquals(24, (int)CurrentDelayUtil.getPredictedDelay(delays, startTime));
    }

    @Test
    public void testPredictedDelayOutliers() throws Exception{
        final List<Delay> delays = Arrays.asList(newDelay(10, startTime.plusMinutes(10)),
                newDelay(0, startTime.plusMinutes(50)),
                newDelay(13, startTime.plusMinutes(70)),
                newDelay(20, startTime.plusMinutes(130)));
        Assert.assertEquals(24, (int)CurrentDelayUtil.getPredictedDelay(delays, startTime));
    }

    @Test
    public void testRemoveOutliers() {
        final Random random = new Random();
        final List<Integer> predictions = new ArrayList<>();
        final int mean = 20;
        final int s = 5;

        for (int i=0; i<1000; i++) {
            predictions.add((int)(mean + random.nextGaussian()*s));
        }
        CurrentDelayUtil.removeOutliers(predictions);
        predictions.forEach(p -> Assert.assertTrue(p <= mean + s + 1 && p >= mean - s - 1));
    }

    @Test
    public void getCurrentAccuracy() {
        Assert.assertEquals(100, CurrentDelayUtil.getCurrentAccuracy(10, 18));
        Assert.assertEquals(100, CurrentDelayUtil.getCurrentAccuracy(10, 20));
        Assert.assertEquals(66, CurrentDelayUtil.getCurrentAccuracy(60, 1));
    }

    @Test
    public void testPredictSteady() {
        final int delay = 20;
        final Delay delay1 = new Delay(delay, timestampFormat.format(startTime), Entity.Type.USER);
        final Delay delay2 = new Delay(delay, timestampFormat.format(startTime.plusMinutes(10)), Entity.Type.USER);
        Assert.assertEquals(delay, CurrentDelayUtil.predict(delay1, delay2, startTime));
    }

    @Test
    public void testPredictSloped() {
        final Delay delay1 = new Delay(10, timestampFormat.format(startTime.plusMinutes(160)), Entity.Type.USER);
        final Delay delay2 = new Delay(20, timestampFormat.format(startTime.plusMinutes(170)), Entity.Type.USER);
        Assert.assertEquals(30, CurrentDelayUtil.predict(delay1, delay2, startTime));
    }

    private Delay newDelay(int delay, LocalDateTime timestamp) {
        return new Delay(delay, timestampFormat.format(timestamp), Entity.Type.USER);
    }

    private class DataBaseMocker implements DataBase {
        private List<Delay> delays;

        private DataBaseMocker(List<Delay> delays) {
            this.delays = delays;
        }

        @Override
        public List<Delay> getReports(String doctorsName, LocalDateTime from, LocalDateTime to) {
            return delays;
        }

        @Override
        public Doctor getDoctor(String doctorsName) {
            return new Doctor("type", DOCTOR, LocalTime.now(), LocalTime.now(), 10);
        }

        @Override
        public boolean doctorExists(String doctorsName) {
            return doctorsName.equals(DOCTOR);
        }

        public void addReport(int personalId, String doctorsName, int delay) { }
        public List<Delay> getDayReport(String doctorsName, LocalDate date) { return null; }
        public List<Appointment> getUserFutureAppointments(int userId) { return null; }
        public String getUserPassword(int userId) { return null; }
        public void feedbackOnEstimate(int userId, String doctorsName, int actualDelay) { }
        public void addScore(int userId, int scoreAdded) { }
        public int getScore(int userId) { return 0; }
        public Appointment getLastAppointment(int userId) { return null; }
        public void printTable(String tableName) { }

        public List<Doctor> getDoctors(){return null;}
        public List<Delay> getDelays(String doctorsName){return null;}
    }



}