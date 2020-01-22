import entities.Delay;
import entities.Doctor;
import entities.Entity;
import estimation.PrepareData;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PrepareDataTest
{
    @Test
    public void createCSVFileDoctorsReports_csvFileCreated_withExpectedRowsNumber_csvFileExist()
    {
        String csvPathTemp = "doctorsReportsTemp.csv";
        File file = new File(csvPathTemp);
        PrepareData prepareData = new PrepareData();
        int expectedLines = 19;

        try
        {
            prepareData.createCSVFileDoctorsReports(csvPathTemp, new DatabaseMocker());
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvPathTemp));
            String input;
            int count = 0;
            while((input = bufferedReader.readLine()) != null)
            {
                count++;
            }
            file.delete();
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
        LocalDateTime base = LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.of(11,00));
        PrepareData prepareData = new PrepareData();
        Delay delay1 = new Delay(20, base.plusMinutes(12).format(Delay.formatter), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", base.toLocalTime().minusHours(5),base.toLocalTime().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, base.format(Delay.formatter), Entity.Type.EXPERT);

        Assert.assertTrue(prepareData.ValidateReport(delay1,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_InvalidTime_2_false()
    {
        LocalDateTime base = LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.of(11,00));
        PrepareData prepareData = new PrepareData();
        Delay delay2 = new Delay(50, base.plusMinutes(12).format(Delay.formatter), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", base.toLocalTime().minusHours(5),base.toLocalTime().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, base.format(Delay.formatter), Entity.Type.EXPERT);

        Assert.assertFalse(prepareData.ValidateReport(delay2,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_ValidTime_3_true()
    {
        LocalDateTime base = LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.of(11,00));
        PrepareData prepareData = new PrepareData();
        Delay delay3 = new Delay(5, base.plusMinutes(15).format(Delay.formatter), Entity.Type.FEEDBACK);
        Doctor doctor = new Doctor(null,"Dolittle", base.toLocalTime().minusHours(5),base.toLocalTime().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, base.format(Delay.formatter), Entity.Type.EXPERT);

        Assert.assertTrue(prepareData.ValidateReport(delay3,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_ValidTime_4_true()
    {
        LocalDateTime base = LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.of(11,00));
        PrepareData prepareData = new PrepareData();
        Delay delay4 = new Delay(0, base.plusMinutes(10).format(Delay.formatter), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", base.toLocalTime().minusHours(5),base.toLocalTime().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, base.format(Delay.formatter), Entity.Type.EXPERT);

        Assert.assertTrue(prepareData.ValidateReport(delay4,doctor,lastExpertReport));
    }


    @Test
    public void checkValidationOfReport_InvalidTime_5_false()
    {
        LocalDateTime base = LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.of(11,00));
        PrepareData prepareData = new PrepareData();
        Delay delay5 = new Delay(-5, base.plusMinutes(15).format(Delay.formatter), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", base.toLocalTime().minusHours(5),base.toLocalTime().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, base.format(Delay.formatter), Entity.Type.EXPERT);

        Assert.assertFalse(prepareData.ValidateReport(delay5,doctor,lastExpertReport));
    }

    @Test
    public void checkValidationOfReport_validTime_6_true()
    {
        LocalDateTime base = LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.of(11,00));
        PrepareData prepareData = new PrepareData();
        Delay delay6 = new Delay(20, base.plusMinutes(8).format(Delay.formatter), Entity.Type.USER);
        Doctor doctor = new Doctor(null,"Dolittle", base.toLocalTime().minusHours(5),base.toLocalTime().plusHours(5),10);
        Delay lastExpertReport = new Delay(20, base.format(Delay.formatter), Entity.Type.EXPERT);
        boolean result = prepareData.ValidateReport(delay6,doctor,lastExpertReport);
        Assert.assertTrue(result);
    }
}