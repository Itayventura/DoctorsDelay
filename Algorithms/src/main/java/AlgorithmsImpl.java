import org.apache.log4j.Logger;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class AlgorithmsImpl implements Algorithms {
    // TODO generic path.
    private final String MODEL_PATH = "C:\\Users\\shiranpilas\\Desktop\\shiran\\model.pkl";
    private static final Logger logger = Logger.getLogger(AlgorithmsImpl.class);
    private LocalDateTime lastModelUpdatedTime;
    private DataBase db;

    /**
     * @param db - mock when testing, real impl when server initializes
     */
    public AlgorithmsImpl(DataBase db) {
        logger.info("Algorithms is initialized with db=" + db.getClass().getName());
        this.db = db;
    }

    @Override
    public int getCurrentDelay(String doctorsName) throws AlgorithmException
    {
        return 0;
        // TODO.
    }

    @Override
    public DelayEstimation getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        if (!isModelAlreadyExist() || !isModelUpdated())
        {
            buildModel();
        }

        //model = importModel(); TODO
        //requestVector = buildRequestVectorToPredict(doctorsName, meetingDateTime); TODO
        //DelayEstimation predictionResult = predict(model, requestVector); TODO
        DelayEstimation predictionResult = new DelayEstimation(DelayEstimation.EstimationType.Small, -1);
        return predictionResult;
    }

    private Boolean isModelAlreadyExist()
    {
        try
        {
            File modelFilePath = new File(MODEL_PATH);
            Boolean isModelPathFound = modelFilePath.exists() && !modelFilePath.isDirectory();
            logger.debug("Model exist: " + isModelPathFound.toString());
            return isModelPathFound;
        }
        catch (Exception e)
        {
            logger.error("Failed to find model file path " + MODEL_PATH + " " + e.getMessage());
            return false;
        }
    }

    private Boolean isModelUpdated()
    {
        // TODO ask Itay if it is possbile.
        // DB implementation support.
//        LocalDateTime monthDeltaTime = LocalDateTime.now().minusMonths(1);
//
//        Duration duration = Duration.between(LocalDateTime.now(), lastModelUpdatedTime);
//        return !(duration.getSeconds() > monthDeltaTime.getSecond());

        // Simple implementation.
        return LocalDateTime.now().getDayOfMonth() == 1;
    }

    private void buildModel()
    {
        PythonRunner pythonRunner = new PythonRunner();
        pythonRunner.Run();
        logger.debug("Python script run and build successfully a model in" + MODEL_PATH);
    }

//    private Model importModel()
//    {
//        // TODO implement
//    }

    @Override
    public void addReport(String doctorsName, int reportedDelay, ReporterType type) throws AlgorithmException
    {
        //TODO insert type of reporter(user,feedback,expert)
    }

    @Override
    public void addReportByNumber(String doctorsName, int appointmentNumber) throws AlgorithmException
    {
        //TODO (maybe) insert type of reporter(user,feedback,expert)
    }
}
