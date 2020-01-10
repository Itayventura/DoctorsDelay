package algorithms;

import current.CurrentDelayUtil;
import db.DataBase;
import db.DataBaseImpl;
import entities.Delay;
import estimation.*;
import org.apache.log4j.Logger;
import java.io.File;
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


    private final String MODEL_PATH = new File("").getAbsolutePath().concat("\\Algorithms\\scripts\\model.pkl");
    private final String CSV_PATH = new File("").getAbsolutePath().concat("\\Algorithms\\scripts\\doctorsReports.csv");
    private final String MODEL_DETAILS_PATH = "ModelDetailsFile.txt";
    private DataBase db;
    private ModelHandler httpCom;

    public AlgorithmsImpl(DataBase db, ModelHandler httpCom)
    {
        this.db = db;
        this.httpCom = httpCom;

        ModelDetails modelDetails = getModelDetailsFromFile(MODEL_DETAILS_PATH);
        lastModelUpdatedTime = modelDetails.getLastModelUpdatedTime();
        accuracy_model = modelDetails.getModelAccuracy();

        logger.info("Algorithms is initialized");
    }

    public AlgorithmsImpl()
    {
        this(new DataBaseImpl(), new HttpCommunications());
    }

    public static double getAccuracy_model()
    {
        return accuracy_model;
    }

    @Override
    public DelayEstimation getCurrentDelay(String doctorsName) throws AlgorithmException
    {
        if (!db.doctorExists(doctorsName))
        {
            throw new AlgorithmException(AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(CurrentDelayUtil.MINUTES_DURATION);

        List<Delay> delays = db.getReports(doctorsName, startTime, endTime);
        if (delays == null || delays.isEmpty())
        {
            throw new AlgorithmException(AlgorithmException.Reason.NO_CURRENT_DATA);
        }

        double predictedDelay = CurrentDelayUtil.getPredictedDelay(delays, startTime);
        return new DelayEstimation(delayTimeToEstimationType(predictedDelay),
                CurrentDelayUtil.getCurrentAccuracy(db.getDoctor(doctorsName).getInterval(), delays.size()));
    }

    @Override
    public DelayEstimation getEstimatedDelay(String doctorsName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        // Check if the request is valid and throw exception if needed.
        try
        {
            checkRequestValidation(doctorsName, meetingDateTime);
            logger.info("Request details are valid. Start prediction");
        }
        catch(AlgorithmException ex)
        {
            logger.error("the request details are not valid: " + ex.getMessage());
            throw ex;
        }


        if (!isModelAlreadyExist(MODEL_PATH) || shouldModelBeUpdated(this.lastModelUpdatedTime))
        {
            try
            {
                PrepareData prepareData = new PrepareData();
                prepareData.createCSVFileDoctorsReports(CSV_PATH, db);
            }
            catch (IOException ex)
            {
                logger.info("csv file did not updated. continue to base on older file");
            }

            accuracy_model = httpCom.BuildModel();

            saveModelDetailsIntoFile(accuracy_model, LocalDateTime.now(),MODEL_DETAILS_PATH);
            logger.info("Python script run and build successfully a model in: " + MODEL_PATH);
        }

        //ask estimation from python by http request.
        DelayEstimation delayEstimation = httpCom.Predict(doctorsName, meetingDateTime);
        logger.info("Prediction result: " + delayEstimation.getTypeRange().getEstimationType().toString() +
                ". Model accuracy: " + delayEstimation.getEstimationAccuracyPercentage());
        return delayEstimation;

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
            logger.info("Model exist: " + isModelPathFound.toString() + " in path:" + modelPath);
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
                logger.info("Model should be updated");
            }
            else
            {
                logger.info("Model is up to date");
            }

            return shouldUpdate;
        }
    }

    public void checkRequestValidation(String doctorName, LocalDateTime meetingDateTime) throws AlgorithmException
    {
        if(!db.doctorExists(doctorName))
        {
            logger.info("Doctor not exist");
            throw new AlgorithmException(AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
        }

        Duration duration = Duration.between(LocalDateTime.now().minusHours(1), meetingDateTime);

        if(isMeetingTimePassed(duration) || !isMeetingTimeInDoctorWorkRange(doctorName, meetingDateTime))
        {
            logger.info("Invalid data time: Prediction request time has passed.");
            throw new AlgorithmException(AlgorithmException.Reason.INVALID_TIME_REQUEST);
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

    public void saveModelDetailsIntoFile(double accuracy, LocalDateTime lastModelUpdatedTime, String modelDetailsPathFile)
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
            logger.info("Created model details file in: " + modelDetailsPathFile);
        }
        catch (IOException ex)
        {
            logger.error("Error: could not save model details to file");
        }
    }

    public ModelDetails getModelDetailsFromFile(String modelDetailsPathFile)
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
                logger.error("Cannot read file. Exception: " + ex.getMessage());
            }
        }
        else
        {
            logger.info("File not exist, creating default details");
        }

        return new ModelDetails(lastModelUpdatedTime, accuracy);
    }
}