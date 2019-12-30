import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlgorithmsImpl implements Algorithms {
    private static final Logger logger = Logger.getLogger(AlgorithmsImpl.class);
    private static LocalDateTime lastModelUpdatedTime;
    protected static double accuracy_model = 0;

    private final String MODEL_PATH = "scripts\\model.pkl";
    private final String CSV_PATH = "scripts\\doctorsReports.csv";
    private final String MODEL_DETAILS_PATH = "ModelDetailsFile.txt";
    private DataBase db;
    private ModelHandler httpCom;

    /**
     * @param db - mock when testing, real impl when server initializes
     */
    public AlgorithmsImpl(DataBase db, ModelHandler httpCom)
    {
        logger.info("Algorithms is initialized with db= " + db.getClass().getName());

        this.db = db;
        this.httpCom = httpCom;

        ModelDetails modelDetails = getModelDetailsFromFile(MODEL_DETAILS_PATH);
        lastModelUpdatedTime = modelDetails.getLastModelUpdatedTime();
        accuracy_model = modelDetails.getModelAccuracy();
    }

    @Override
    public DelayEstimation getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        if (!isModelAlreadyExist(MODEL_PATH) || shouldModelBeUpdated(this.lastModelUpdatedTime))
        {
            PrepareData prepareData = new PrepareData();
            prepareData.createCSVFileDoctorsReports(CSV_PATH, db);

            accuracy_model = httpCom.BuildModel();

            saveModelDetailsIntoFile(accuracy_model, LocalDateTime.now(),MODEL_DETAILS_PATH);
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
        Boolean shouldUpdate = true;

        try
        {
            if(lastModelUpdatedTime != null)
            {
                Duration lastUpdatedTimeDuration = Duration.between(LocalDateTime.now().minusMonths(1), lastModelUpdatedTime);
                shouldUpdate = lastUpdatedTimeDuration.isNegative();
            }
            else
            {
                shouldUpdate = true;
            }
        }
        finally
        {
            if (shouldUpdate)
            {
                logger.debug("Model should be updated");
            }
            else
            {
                logger.debug("Model is up to date");
            }

            return shouldUpdate;
        }
    }

    protected void checkRequestValidation(String doctorName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        Duration duration = Duration.between(LocalDateTime.now(), meetingDateTime);

        if(isMeetingTimePassed(duration) || isMeetingTimeInDoctorWorkRange(doctorName, meetingDateTime))
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

    protected Boolean isMeetingTimePassed(Duration duration)
    {
        return duration.isNegative();
    }

    protected Boolean isMeetingTimeInDoctorWorkRange(String doctorName, LocalDateTime meetingDateTime)
    {
        return !(Duration.between(db.getDoctor(doctorName).getStartTime(), meetingDateTime.toLocalTime()).isNegative()
               || Duration.between(meetingDateTime.toLocalTime(), db.getDoctor(doctorName).getEndTime()).isNegative());
    }

    protected void saveModelDetailsIntoFile(double accuracy, LocalDateTime lastModelUpdatedTime, String modelDetailsPathFile)
    {
        try
        {
            File file = new File(modelDetailsPathFile);
            if(file.exists())
            {
                file.delete();
            }

            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(accuracy + ";" + lastModelUpdatedTime.format(DateTimeFormatter.ISO_DATE_TIME));
            writer.flush();
            writer.close();
            logger.debug("Created model details file in: " + modelDetailsPathFile);
        }
        catch (IOException ex)
        {
            logger.error("Error: could not save model details to file");
        }
    }

    protected ModelDetails getModelDetailsFromFile(String modelDetailsPathFile)
    {
        LocalDateTime lastModelUpdatedTime = null;
        double accuracy = 0;

        File file = new File(modelDetailsPathFile);
        if(file.exists())
        {
            try
            {
                List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
                for(String line : lines)
                {
                    String[] res = line.split(";");
                    accuracy = Double.parseDouble(res[0]);
                    lastModelUpdatedTime = LocalDateTime.parse(res[1], DateTimeFormatter.ISO_DATE_TIME);
                }
            }
            catch (IOException ex)
            {
                logger.debug("Cannot read file. Exception: " + ex.getMessage());
            }
        }
        else
        {
            logger.debug("File not exist, creating default details");
        }

        return new ModelDetails(lastModelUpdatedTime, accuracy);
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

    @Override
    public int getCurrentDelay(String doctorsName) throws AlgorithmException
    {
        return 0;
        // TODO.
    }
}