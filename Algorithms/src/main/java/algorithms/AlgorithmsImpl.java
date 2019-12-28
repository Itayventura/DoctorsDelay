package algorithms;

import entities.Delay;
import org.apache.log4j.Logger;
import utils.CurrentDelayUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

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
        this.db = db;
        logger.info("algorithms.Algorithms is initialized with db=" + db.getClass().getName());
    }

    public AlgorithmsImpl() {
        this.db = new DataBaseImpl();
        logger.info("algorithms.Algorithms is initialized with db=" + db.getClass().getName());
    }

    @Override
    public DelayEstimation getCurrentDelay(String doctorsName) throws AlgorithmException
    {
        if (!db.doctorExists(doctorsName)) {
            throw new AlgorithmException(AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
        }
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(CurrentDelayUtil.MINUTES_DURATION);
        List<Delay> delays = db.getReports(doctorsName, startTime, endTime);
        if (delays == null || delays.isEmpty()) {
            throw new AlgorithmException(AlgorithmException.Reason.NO_CURRENT_DATA);
        }
        double predictedDelay = CurrentDelayUtil.getPredictedDelay(delays, startTime);
        return new DelayEstimation(delayTimeToEstimationType(predictedDelay),
                CurrentDelayUtil.getCurrentAccuracy(db.getDoctor(doctorsName).getInterval(), delays.size()));
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

    private static DelayEstimation.EstimationType delayTimeToEstimationType(double delay) {
        if (delay <= 15) {
            return DelayEstimation.EstimationType.Small;
        } else if (delay <= 30) {
            return DelayEstimation.EstimationType.Medium;
        } else {
            return DelayEstimation.EstimationType.Large;
        }
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

}
