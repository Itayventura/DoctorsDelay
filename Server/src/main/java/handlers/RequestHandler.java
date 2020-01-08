package handlers;

import algorithms.Algorithms;
import db.DataBase;
import entities.Appointment;
import entities.Delay;
import estimation.DelayEstimation;
import estimation.DelayRange;
import generated.Communication;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;


/**
 * Handles all client requests, according to their type.
 * Either getting a delay (estimated or current), getting the next
 * appointments (needed by the client's app) or getting their score.
 */
public class RequestHandler {
    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private Algorithms algorithms;
    private DataBase db;
    private int clientId;

    public RequestHandler(int clientId, Algorithms algorithms, DataBase db) {
        this.clientId = clientId;
        this.algorithms = algorithms;
        this.db = db;
    }

    Communication.S2C handle(Communication.C2S.Request request) {
        Communication.S2C.Response.Builder response = ClientHandler.getFailureResponse();
        logger.info("Client " + clientId + " requesting " + request.getType().name());
        switch (request.getType()) {
            case NOW:
                getCurrentDelay(response, request);
                break;
            case ESTIMATE:
                getEstimatedDelay(response, request);
                break;
            case NEXT_APPOINTMENTS:
                getNextAppointments(response, request);
                break;
            case GET_SCORE:
                getScore(response, request);
                break;
        }
        return Communication.S2C.newBuilder().setResponse(response).build();
    }

    public void getEstimatedDelay(Communication.S2C.Response.Builder response, Communication.C2S.Request request) {
        Communication.S2C.Response.ExpectedDelay.Builder delay = Communication.S2C.Response.ExpectedDelay.newBuilder();
        try {
            LocalDateTime dateTime = epochToDateTime(request.getTimestamp());
            DelayEstimation delayEstimation = algorithms.getEstimatedDelay(request.getDoctorsName(),
                    dateTime);
            delayEstimationToDelay(delayEstimation, delay);
            delay.setIsEstimated(true);
            response.setExpectedDelay(delay);
            response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
        } catch (Algorithms.AlgorithmException e) {
            logger.error("Could not get estimated delay, clientId=" + clientId, e);
            algorithmExeptionToResponse(e, response);
        }
    }

    public void getCurrentDelay(Communication.S2C.Response.Builder response, Communication.C2S.Request request) {
        Communication.S2C.Response.ExpectedDelay.Builder delay = Communication.S2C.Response.ExpectedDelay.newBuilder();
        try{
            delayEstimationToDelay(algorithms.getCurrentDelay(request.getDoctorsName()), delay);
            setRecentDelays(request.getDoctorsName(), delay);
            response.setExpectedDelay(delay);
            response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
        } catch (Algorithms.AlgorithmException e) {
            if (e.getReason() == Algorithms.AlgorithmException.Reason.NO_CURRENT_DATA) {
                logger.warn(String.format("No current data on doctor %s", request.getDoctorsName()));
                getEstimatedDelay(response, request);
            } else {
                logger.error("Could not get current delay, clientId=" + clientId, e);
                algorithmExeptionToResponse(e, response);
            }
        }
    }

    private void setRecentDelays(String doctorsName, Communication.S2C.Response.ExpectedDelay.Builder reportedDelay) {
        if (!db.doctorExists(doctorsName))
            return;

        final LocalDateTime now = LocalDateTime.now();
        List<Delay> delays = db.getReports(doctorsName, now.minusMinutes(120), now);
        for (Delay delay : delays) {
            reportedDelay.addRecentReportDelays(delay.getReportedDelay());
            reportedDelay.addRecentReportTimes(120 - (int)ChronoUnit.MINUTES.between(delay.getReportTimestamp(), now));
        }
    }

    public void getNextAppointments(Communication.S2C.Response.Builder response, Communication.C2S.Request request) {
        if (!db.doctorExists(request.getDoctorsName())) {
            response.setErrorCode(Communication.S2C.Response.ErrorCode.DOCTOR_NOT_FOUND);
        } else {
            List<Appointment> appointments = db.getUserFutureAppointments(clientId);
            if (appointments == null || appointments.isEmpty()){
                response.setErrorCode(Communication.S2C.Response.ErrorCode.NO_DATA);
            } else {
                appointments.forEach(appointment ->
                        response.addNextAppointments(dateTimeToEpoch(appointment.getAppointmentDateTime())));
                response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
            }
        }
    }

    public void getScore(Communication.S2C.Response.Builder response, Communication.C2S.Request request) {
        response.setScore(db.getScore(clientId));
        response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
    }

    private void algorithmExeptionToResponse(Algorithms.AlgorithmException e, Communication.S2C.Response.Builder response) {
        response.setStatusCode(Communication.S2C.Response.Status.FAILURE);
        switch (e.getReason()){
            case DOCTOR_NOT_EXISTS:
                response.setErrorCode(Communication.S2C.Response.ErrorCode.DOCTOR_NOT_FOUND);
                break;
            case NO_DATA_FOUND:
                response.setErrorCode(Communication.S2C.Response.ErrorCode.NO_DATA);
                break;
            case INVALID_TIME_REQUEST:
                response.setErrorCode(Communication.S2C.Response.ErrorCode.INVALID_TIME);
                break;

        }
    }

    private void delayEstimationToDelay(DelayEstimation delayEstimation , Communication.S2C.Response.ExpectedDelay.Builder delay) {
        delay.setAccuracy(delayEstimation.getEstimationAccuracyPercentage());
        DelayRange range = delayEstimation.getTypeRange();
        delay.setMaxTime(range.getMaximalDelay());
        delay.setMinTime(range.getMinimalDelay());
    }

    private LocalDateTime epochToDateTime(long milliSinceEpoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliSinceEpoch), ZoneId.of("Israel"));
    }

    private long dateTimeToEpoch(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("Israel")).toInstant().toEpochMilli();
    }
}
