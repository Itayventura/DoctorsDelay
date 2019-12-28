package algorithms;

import java.time.LocalDateTime;

public interface Algorithms {

    public class AlgorithmException extends Exception{
        private Reason reason;
        public enum Reason{ //TODO - if you want, you can add\remove reasons and just push
            DOCTOR_NOT_EXISTS,
            NO_CURRENT_DATA,
            NO_DATA_FOUND
        }

        public AlgorithmException(Reason reason, String error) {
            super(error);
            this.reason = reason;
        }

        public AlgorithmException(Reason reason) {
            super();
            this.reason = reason;
        }

        public Reason getReason() { return reason; }
    }

    /**
     * @param doctorsName - may not exist
     * @return the current estimated time according to reports
     */
    DelayEstimation getCurrentDelay(String doctorsName) throws AlgorithmException;

    /**
     * @param doctorsName - may not exist
     * @param meetingDateTime - to determine what the expected delay at that time will be.
     * @return the estimated time according to history - in minutes
     */
    DelayEstimation getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) throws AlgorithmException;

}
