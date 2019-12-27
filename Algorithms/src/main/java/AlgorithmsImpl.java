import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class AlgorithmsImpl implements Algorithms {
    // TODO generic path.
    private final String MODEL_PATH = "src\\main\\resources\\model.pkl";
    private static final Logger logger = Logger.getLogger(AlgorithmsImpl.class);
    private static double accuracy_model = 0;
    private static LocalDateTime lastModelUpdatedTime;
//    private DataBase db;
//
//    /**
//     * @param db - mock when testing, real impl when server initializes
//     */
//    public AlgorithmsImpl(DataBase db) {
//        logger.info("Algorithms is initialized with db=" + db.getClass().getName());
//        this.db = db;
//    }

    @Override
    public int getCurrentDelay(String doctorsName) throws AlgorithmException
    {
        return 0;
        // TODO.
    }

    @Override
    public DelayEstimation getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        DelayEstimation predictionResult = null;
        if (!isModelAlreadyExist() || !isModelUpdated())
        {
            buildModelAndUpdateAccuracy("buildModel");
        }

        //check if the request is valid and throw exception if needed.
        try
        {
            checkRequestValidation(doctorsName, meetingDateTime);

            //ask estimation from python by http request.
            return predictDelayEstimation(doctorsName, meetingDateTime,"predict");
        }
        catch(AlgorithmException ex)
        {
            logger.debug("the request details are not valid: " + ex.getMessage());
            throw ex;
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
        LocalDateTime monthDeltaTime = LocalDateTime.now().minusMonths(1);

        Duration duration = Duration.between(LocalDateTime.now(), lastModelUpdatedTime);
        return !(duration.getSeconds() > monthDeltaTime.getSecond());

        // Simple implementation.
        //return LocalDateTime.now().getDayOfMonth() == 1;
    }

    private void checkRequestValidation(String doctorName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        Duration duration = Duration.between(LocalDateTime.now(), meetingDateTime);
        if(duration.isNegative())
        {
            logger.debug("Invalid data time: Prediction request time has passed.");
            throw new AlgorithmException(AlgorithmException.Reason.INVALID_TIME_REQUEST);
        }

//        if(!db.doctorExists(doctorName))
//        {
//            logger.debug("Doctor not exist");
//            throw new AlgorithmException(AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
//        }
    }

    private void buildModelAndUpdateAccuracy(String httpRequest)
    {
        HttpCommunications httpConnection = new HttpCommunications(httpRequest);
        accuracy_model = parseResponseBuildModel(httpConnection.readResponseRequest());
        logger.debug("Python script run and build successfully a model in" + MODEL_PATH);
    }

    private DelayEstimation predictDelayEstimation(String doctorsName, LocalDateTime meetingDateTime, String httpRequest)
    {
        HttpCommunications httpConnection = new HttpCommunications(httpRequest);
        DelayEstimation predictResult = parseResponseStringPrediction(httpConnection.readResponseRequest());
        logger.debug("Python script run and return appropriate prediction");
        return predictResult;
    }

    public DelayEstimation parseResponseStringPrediction(String response)
    {
        JSONObject jsonParser = new JSONObject(response);
        String predictionType = jsonParser.getString("prediction");

        DelayEstimation delayEstimation = new DelayEstimation(
                DelayEstimation.StringToEstimationType.get(predictionType),
                (int)Math.floor(this.accuracy_model));
        return delayEstimation;
    }

    public double parseResponseBuildModel(String response)
    {
        JSONObject jsonParser = new JSONObject(response);
        String accuracyStr = jsonParser.getString("accuracy");
        return Double.parseDouble(accuracyStr);
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
