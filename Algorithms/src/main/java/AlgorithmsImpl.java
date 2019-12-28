import org.apache.log4j.Logger;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AlgorithmsImpl implements Algorithms {
    private static final Logger logger = Logger.getLogger(AlgorithmsImpl.class);
    private static LocalDateTime lastModelUpdatedTime = LocalDateTime.now();
    protected static double accuracy_model = 0;

    private final String MODEL_PATH = "scripts\\model.pkl";
    private DataBase db;
    private ModelHandler httpCom;

    /**
     * @param db - mock when testing, real impl when server initializes
     */
    public AlgorithmsImpl(DataBase db, ModelHandler httpCom)
    {
        logger.info("Algorithms is initialized with db=" + db.getClass().getName());

        this.db = db;
        this.httpCom = httpCom;
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
        if (!isModelAlreadyExist(MODEL_PATH) || shouldModelBeUpdated(this.lastModelUpdatedTime))
        {
            accuracy_model = httpCom.BuildModel();
            logger.debug("Python script run and build successfully a model in" + MODEL_PATH);
        }

        // Check if the request is valid and throw exception if needed.
        try
        {
            checkRequestValidation(doctorsName, meetingDateTime);
            logger.debug("Request details are valid. Start prediction");

            //ask estimation from python by http request.
            DelayEstimation delayEstimation = httpCom.Predict(doctorsName, meetingDateTime);
            logger.debug("Prediction result: " + delayEstimation.getTypeRange().estimationType.toString() +
                    ". Model accuracy: " + delayEstimation.getEstimationAccuracyPercentage());
            return delayEstimation;
        }
        catch(AlgorithmException ex)
        {
            logger.debug("the request details are not valid: " + ex.getMessage());
            throw ex;
        }
    }

    protected Boolean isModelAlreadyExist(String modelPath)
    {
        try
        {
            File modelFilePath = new File(modelPath);
            Boolean isModelPathFound = modelFilePath.exists() && !modelFilePath.isDirectory();
            logger.debug("Model exist: " + isModelPathFound.toString());
            return isModelPathFound;
        }
        catch (Exception e)
        {
            logger.error("Failed to find model file path " + modelPath + " " + e.getMessage());
            return false;
        }
    }

    protected Boolean shouldModelBeUpdated(LocalDateTime lastModelUpdatedTime)
    {
        // TODO ask Itay if it is possbile.
        // DB implementation support.
        Duration lastUpdatedTimeDuration = Duration.between(LocalDateTime.now().minusMonths(1), lastModelUpdatedTime);

        Boolean shouldUpdate = lastUpdatedTimeDuration.isNegative();
        if (shouldUpdate)
        {
            logger.debug("Model should be updated");
        }
        else
        {
            logger.debug("Model is up to date");
        }

        return shouldUpdate;

        // Simple implementation.
        //return LocalDateTime.now().getDayOfMonth() == 1;
    }

    protected void checkRequestValidation(String doctorName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        Duration duration = Duration.between(LocalDateTime.now(), meetingDateTime);

        if(duration.isNegative()
                || Duration.between(db.getDoctor(doctorName).getStartTime(), meetingDateTime).isNegative()
                || Duration.between(meetingDateTime, db.getDoctor(doctorName).getEndTime()).isNegative())
        {
            logger.debug("Invalid data time: Prediction request time has passed.");
            throw new AlgorithmException(AlgorithmException.Reason.INVALID_TIME_REQUEST);
        }

        if(!db.doctorExists(doctorName))
        {
            logger.debug("Doctor not exist");
            throw new AlgorithmException(AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
        }
    }

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