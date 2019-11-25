import java.time.LocalDateTime;

public class AlgorithmsMocker implements Algorithms {

    @Override
    public int getCurrentDelay(String doctorsName) throws Algorithms.AlgorithmException {
        if(!TestBase.doctorExists(doctorsName))
            throw new Algorithms.AlgorithmException(Algorithms.AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
        else if (!TestBase.doctors2curDelays.containsKey(doctorsName))
            throw new Algorithms.AlgorithmException(Algorithms.AlgorithmException.Reason.NO_CURRENT_DATA);
        return TestBase.doctors2curDelays.get(doctorsName);
    }

    @Override
    public int getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) throws Algorithms.AlgorithmException {
        if(!TestBase.doctorExists(doctorsName))
            throw new Algorithms.AlgorithmException(Algorithms.AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
        else if (!TestBase.doctors2expectedDelays.containsKey(doctorsName))
            throw new Algorithms.AlgorithmException(Algorithms.AlgorithmException.Reason.NO_DATA_FOUND);
        return TestBase.doctors2expectedDelays.get(doctorsName);
    }

    @Override
    public void addReport(String doctorsName, int reportedDelay) throws Algorithms.AlgorithmException {
        if(!TestBase.doctorExists(doctorsName)) {
            TestBase.doctors.add(doctorsName);
            TestBase.doctors2expectedDelays.put(doctorsName, reportedDelay);
        }
    }

}
