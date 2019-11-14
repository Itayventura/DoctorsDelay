import java.time.LocalDateTime;

public interface Algorithms {

    public abstract class Response{
        public enum ResponseStatus{
            SUCCESSFUL,
            DOCTOR_NOT_EXISTS,
            NO_CURRENT_DATA,
            NO_DATA_FOUND
        }
        private int expected_delay;
        private int current_delay;
        public abstract int getExpected_delay();
        public abstract int getCurrent_delay();
    }

    /**
     * @param doctorsName - may not exist
     * @return the current estimated time according to reports
     */
    Response getCurrentDelay(String doctorsName);

    /**
     * @param doctorsName - may not exist
     * @param meetingDateTime - to determine what the expected delay at that time will be.
     * @return the estimated time according to history
     */
    Response getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime);

    /**
     * Straight forward - using DB addReport to set the records.
     * @param doctorsName
     * @param reportedDelay
     */
    void addReport(String doctorsName, int reportedDelay);
}
