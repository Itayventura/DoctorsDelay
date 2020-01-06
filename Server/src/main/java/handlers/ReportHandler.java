package handlers;

import db.DataBase;
import entities.Doctor;
import communications.Communication;
import org.apache.log4j.Logger;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Handles a client's report on delay or
 * a client's feedback on our estimation of delay.
 */
public class ReportHandler {
    private static final Logger logger = Logger.getLogger(ReportHandler.class);
    private DataBase db;
    private int clientId;

    ReportHandler(int clientId, DataBase db) {
        this.clientId = clientId;
        this.db = db;
    }

    public Communication.S2C handle(Communication.C2S.Report report) {
        logger.info("Client " + clientId + " is reporting");
        Communication.S2C.Response.Builder response = ClientHandler.getFailureResponse();
        if (!db.doctorExists(report.getDoctorsName())) {
            logger.error("report for doctor " + report.getDoctorsName() + " does not exist");
            response.setErrorCode(Communication.S2C.Response.ErrorCode.DOCTOR_NOT_FOUND);
        } else {
            int delay = report.getCurrentDelayMinutes();
            if (report.getCurrentAppointmentIn() != 0) {
                delay = convertCurrentAppointmentToDelay(report.getDoctorsName(), report.getCurrentAppointmentIn(),
                        LocalTime.now());
            }
            if (delay < 0) {
                logger.error("Failed to calculate delay for doctor " + report.getDoctorsName());
                response.setErrorCode(Communication.S2C.Response.ErrorCode.NO_DATA);
            } else {
                db.addReport(clientId, report.getDoctorsName(), delay);
                db.addScore(clientId, 1);
                System.out.println("added 1 to score for client=" + clientId);
                response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
            }
        }
        return Communication.S2C.newBuilder().setResponse(response).build();
    }
    public void handleFeedback(Communication.C2S.Report feedback) {
        logger.info("Client " + clientId + " reporting feedback");
        db.feedbackOnEstimate(clientId, feedback.getCurrentDelayMinutes());
        db.addScore(clientId, 5);
    }

    public int convertCurrentAppointmentToDelay(String doctorsName, int currentAppointmentIn, LocalTime actualTime) {
        Doctor doctor = db.getDoctor(doctorsName);
        LocalTime startTime = doctor.getStartTime();
        int interval = doctor.getInterval();
        if (startTime != null && interval > 0) {
            LocalTime expectedTime = startTime.plusMinutes(currentAppointmentIn * interval);
            return (int)ChronoUnit.MINUTES.between(expectedTime, actualTime);
        }
        return -1;
    }

}
