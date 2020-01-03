package handlers;

import algorithms.Algorithms;
import communications.Communication;
import db.DataBase;
import entities.Appointment;
import estimation.DelayEstimation;
import estimation.DelayRange;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            DelayEstimation delayEstimation = algorithms.getEstimatedDelay(request.getDoctorsName(),
                    epochToDateTime(request.getTimestamp()));
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
        if (!db.doctorExists(request.getDoctorsName())) {
            response.setErrorCode(Communication.S2C.Response.ErrorCode.DOCTOR_NOT_FOUND);
        } else {
            response.setScore(db.getScore(clientId));
            response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
        }
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

        }
    }

    private void delayEstimationToDelay(DelayEstimation delayEstimation , Communication.S2C.Response.ExpectedDelay.Builder delay) {
        delay.setAccuracy(delayEstimation.getEstimationAccuracyPercentage());
        DelayRange range = delayEstimation.getTypeRange();
        delay.setMaxTime(range.getMaximalDelay());
        delay.setMinTime(range.getMinimalDelay());
    }

    private LocalDateTime epochToDateTime(long milliSinceEpoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliSinceEpoch), ZoneId.systemDefault());
    }

    private long dateTimeToEpoch(LocalDateTime dateTime) {
        return Timestamp.valueOf(dateTime).getTime();
    }
}