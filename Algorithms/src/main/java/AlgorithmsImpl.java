import org.apache.log4j.Logger;
import java.time.LocalDateTime;

public class AlgorithmsImpl implements Algorithms {
    private static final Logger logger = Logger.getLogger(AlgorithmsImpl.class);
    private DataBase db;

    /**
     * @param db - mock when testing, real impl when server initializes
     */
    public AlgorithmsImpl(DataBase db) {
        logger.info("Algorithms is initialized with db=" + db.getClass().getName());
        this.db = db;
    }

    @Override
    public Response getCurrentDelay(String doctorsName) {
        return null; //TODO - implement
    }

    @Override
    public Response getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) {
        return null; //TODO - implement
    }

    @Override
    public void addReport(String doctorsName, int reportedDelay) {
        //TODO - implement
    }
}
