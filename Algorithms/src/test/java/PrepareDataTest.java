import entities.Delay;
import entities.Doctor;
import entities.Entity;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PrepareDataTest
{
    @Test
    public void createCSVFileDoctorsReports_csvFileCreated_with19rows_csvFileExist()
    {
        String csvPath = "scripts/doctorsReports.csv";
        PrepareData prepareData = new PrepareData();
        prepareData.createCSVFileDoctorsReports(csvPath, new DatabaseMocker());
        int expectedLines = 22;

        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvPath));
            String input;
            int count = 0;
            while((input = bufferedReader.readLine()) != null)
            {
                count++;
            }

            Assert.assertEquals(expectedLines, count);
        }
        catch(IOException ex)
        {
            Assert.fail();
        }
    }

    @Test
    public void checkValidationOfReport_ValidTime_1_true()
    {
        PrepareData prepareData = new PrepareData();
        Delay delay1 = new Delay(20, LocalDateTime.now().plusMinutes(12).toString(), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", LocalTime.now().minusHours(5),LocalTime.now().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, LocalDateTime.now().toString(), Entity.Type.EXPERT);

        Assert.assertTrue(prepareData.checkValidationOfReport(delay1,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_InvalidTime_2_false()
    {
        PrepareData prepareData = new PrepareData();
        Delay delay2 = new Delay(50, LocalDateTime.now().plusMinutes(12).toString(), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", LocalTime.now().minusHours(5),LocalTime.now().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, LocalDateTime.now().toString(), Entity.Type.EXPERT);

        Assert.assertFalse(prepareData.checkValidationOfReport(delay2,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_ValidTime_3_true()
    {
        PrepareData prepareData = new PrepareData();
        Delay delay3 = new Delay(5, LocalDateTime.now().plusMinutes(15).toString(), Entity.Type.FEEDBACK);
        Doctor doctor = new Doctor(null,"Dolittle", LocalTime.now().minusHours(5),LocalTime.now().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, LocalDateTime.now().toString(), Entity.Type.EXPERT);

        Assert.assertTrue(prepareData.checkValidationOfReport(delay3,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_ValidTime_4_true()
    {
        PrepareData prepareData = new PrepareData();
        Delay delay4 = new Delay(0, LocalDateTime.now().plusMinutes(10).toString(), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", LocalTime.now().minusHours(5),LocalTime.now().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, LocalDateTime.now().toString(), Entity.Type.EXPERT);

        Assert.assertTrue(prepareData.checkValidationOfReport(delay4,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_InvalidTime_5_false()
    {
        PrepareData prepareData = new PrepareData();
        Delay delay5 = new Delay(-5, LocalDateTime.now().plusMinutes(15).toString(), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", LocalTime.now().minusHours(5),LocalTime.now().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, LocalDateTime.now().toString(), Entity.Type.EXPERT);

        Assert.assertFalse(prepareData.checkValidationOfReport(delay5,doctor,lastExpertReport));
    }

    @Test
    public void checkValidationOfReport_InvalidTime_6_false()
    {
        PrepareData prepareData = new PrepareData();
        Delay delay6 = new Delay(20, LocalTime.now().minusHours(8).toString(), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", LocalTime.now().minusHours(5),LocalTime.now().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, LocalDateTime.now().toString(), Entity.Type.EXPERT);

        Assert.assertFalse(prepareData.checkValidationOfReport(delay6,doctor,lastExpertReport));
    }
}
