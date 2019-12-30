package algorithms;

import current.CurrentDelayUtil;
import db.DataBase;
import db.DataBaseImpl;
import entities.Delay;
import estimation.DelayEstimation;
import estimation.HttpCommunications;
import estimation.ModelHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class AlgorithmsImpl implements Algorithms {
    private static final Logger logger = Logger.getLogger(AlgorithmsImpl.class);
    private static LocalDateTime lastModelUpdatedTime = LocalDateTime.now();
    protected static double accuracy_model = 0;

    private final String MODEL_PATH = "scripts\\model.pkl";
    private DataBase db;
    private ModelHandler httpCom;

    public AlgorithmsImpl(DataBase db, ModelHandler httpCom)
    {
        this.db = db;
        this.httpCom = httpCom;
        logger.info("Algorithms is initialized");
    }

    public AlgorithmsImpl() {
        this(new DataBaseImpl(), new HttpCommunications());
    }

    public static double getAccuracy_model() {
        return accuracy_model;
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
            logger.debug("Prediction result: " + delayEstimation.getTypeRange().getEstimationType().toString() +
                    ". Model accuracy: " + delayEstimation.getEstimationAccuracyPercentage());
            return delayEstimation;
        }
        catch(AlgorithmException ex)
        {
            logger.debug("the request details are not valid: " + ex.getMessage());
            throw ex;
        }
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

    public Boolean isModelAlreadyExist(String modelPath)
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

    public Boolean shouldModelBeUpdated(LocalDateTime lastModelUpdatedTime)
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

    public void checkRequestValidation(String doctorName, LocalDateTime meetingDateTime) throws AlgorithmException
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

}
