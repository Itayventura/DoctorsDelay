package handlers;

import generated.Communication;
import mockers.DataBaseMocker;
import org.junit.Assert;
import org.junit.Test;


public class ReportHandlerTest {
    private final DataBaseMocker db = new DataBaseMocker();
    private final ReportHandler reportHandler = new ReportHandler(DataBaseMocker.USER, db);

    @Test
    public void testConvertCurrentAppointmentToDelay() {
        Assert.assertEquals(30, reportHandler.convertCurrentAppointmentToDelay(DataBaseMocker.DOCTOR,
                10, DataBaseMocker.startTime.plusHours(3).toLocalTime()));
    }

    @Test
    public void testHandleNoDoctor() {
        Communication.S2C s2c = reportHandler.handle(Communication.C2S.Report.newBuilder()
                .setDoctorsName("NOT_DOCTOR").build());
        Assert.assertEquals(Communication.S2C.Response.Status.FAILURE, s2c.getResponse().getStatusCode());
        Assert.assertEquals(Communication.S2C.Response.ErrorCode.DOCTOR_NOT_FOUND, s2c.getResponse().getErrorCode());
    }

    @Test
    public void testHandleDelayInMinutes() {
        final int delay = 20;
        Communication.S2C s2c = reportHandler.handle(Communication.C2S.Report.newBuilder()
                .setDoctorsName(DataBaseMocker.DOCTOR)
                .setCurrentDelayMinutes(delay)
                .build());
        Assert.assertEquals(Communication.S2C.Response.Status.SUCCESSFUL, s2c.getResponse().getStatusCode());
        Assert.assertTrue(db.getReports(DataBaseMocker.DOCTOR, DataBaseMocker.USER).contains(delay));
    }

}