import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entities.Delay;
import entities.Doctor;
import entities.Entity;
import org.apache.log4j.Logger;

public class PrepareData
{
    private static final Logger logger = Logger.getLogger(PrepareData.class);

    public void createCSVFileDoctorsReports(String csvPathFile, DataBase db)
    {
        try
        {
            FileWriter writer = new FileWriter(csvPathFile);
            CSVUtils.writeLine(writer, Arrays.asList("Doctor\'s name", "Delay", "TypeReport","Month","Day","Hour","Minutes"));
            List<Doctor> doctorsList = db.getDoctors();
            for (Doctor doctor : doctorsList)
            {
                List<Delay> reportsDelayList = db.getDelays(doctor.getName());
                Delay lastExpertReport = reportsDelayList.get(0);

                for (Delay reportDelay: reportsDelayList)
                {
                    if(reportDelay.getReportType().equals(Entity.Type.EXPERT))
                    {
                        lastExpertReport = reportDelay;
                    }

                    if(checkValidationOfReport(reportDelay, doctor, lastExpertReport))
                    {
                        List<String> rowInCSV = new ArrayList<>();

                        rowInCSV.add(doctor.getName());
                        rowInCSV.add(String.valueOf(reportDelay.getReportedDelay()));
                        rowInCSV.add(reportDelay.getReportType().toString().toLowerCase());
                        rowInCSV.add(String.valueOf(reportDelay.getReportTimestamp().getMonth()));
                        rowInCSV.add(reportDelay.getReportTimestamp().getDayOfWeek().toString());
                        rowInCSV.add(String.valueOf(reportDelay.getReportTimestamp().getHour()));
                        rowInCSV.add(String.valueOf(reportDelay.getReportTimestamp().getMinute()));

                        CSVUtils.writeLine(writer, rowInCSV);
                    }
                }
            }

            writer.flush();
            writer.close();
            logger.debug("CSV file was created successfully");

        }
        catch(IOException ex)
        {
            logger.error("Failed to create FileWriter for csv path: "+csvPathFile+" ," + ex.getMessage());
        }
    }

    protected boolean checkValidationOfReport(Delay reportDelay, Doctor doctor, Delay lastExpertReport)
    {
       // reported timestamp is after the doctor start to work.
        Duration duration = Duration.between(doctor.getStartTime(), reportDelay.getReportTimestamp().toLocalTime());

        if(duration.isNegative())
        {
            logger.debug("report is not reliable: timestamp is before the doctor started his day work");
            return false;
        }

        //check max possible delay report.
        long max = lastExpertReport.getReportedDelay() + Duration.between(lastExpertReport.getReportTimestamp(),reportDelay.getReportTimestamp()).toMinutes();
        if(reportDelay.getReportedDelay() > max || reportDelay.getReportedDelay() < 0)
        {
            logger.debug("report is not reliable: delay reported is too large or invalid(negative)");
            return false;
        }

        logger.debug("report is reliable");
        return true;
    }
}