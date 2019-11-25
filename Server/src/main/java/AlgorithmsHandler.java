import org.apache.log4j.Logger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class AlgorithmsHandler {
    private static final Logger logger = Logger.getLogger(AlgorithmsHandler.class);
    private Algorithms algorithms;

    protected AlgorithmsHandler(Algorithms algorithms) {
        this.algorithms = algorithms;
    }

    protected Communication.S2C handleRequest(Communication.C2S.Request request){
        Communication.S2C.Response.Builder response = Communication.S2C.Response.newBuilder();
        response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
        Communication.S2C.Response.ExpectedDelay.Builder delay = Communication.S2C.Response.ExpectedDelay.newBuilder();
        try {
            switch (request.getType()) {
                case NOW:
                    try{
                        getCurrentDelay(delay, request);
                    } catch (Algorithms.AlgorithmException e) {
                        if (e.getReason() == Algorithms.AlgorithmException.Reason.NO_CURRENT_DATA) {
                            logger.warn(String.format("No current data on doctor %s", request.getDoctorsName()));
                            getEstimatedDelay(delay, request);
                        } else {
                            throw e;
                        }
                    }
                    break;
                case ESTIMATE:
                    getEstimatedDelay(delay, request);
                    break;
            }
        } catch (Algorithms.AlgorithmException e) {
            switch (e.getReason()) {
                case DOCTOR_NOT_EXISTS:
                    logger.warn(String.format("doctor %s does not exist", request.getDoctorsName()));
                    setFailure(response, "Doctor not found");
                    break;
                case NO_DATA_FOUND:
                    logger.warn(String.format("There's no data on doctor %s", request.getDoctorsName()));
                    setFailure(response, "Data does not exist");
                    break;
            }
        }
        response.setExpectedDelay(delay);
        return Communication.S2C.newBuilder().setResponse(response).build();
    }

    protected Communication.S2C handleReport(Communication.C2S.Report report){
        try {
            algorithms.addReport(report.getDoctorsName(), report.getCurrentDelayMinutes());
        } catch (Algorithms.AlgorithmException e) {
            logger.error("handle report failed, report=" + report, e);
        }
        return Communication.S2C.newBuilder()
                .setResponse(Communication.S2C.Response.newBuilder()
                        .setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL)).build();
    }

    private void setFailure(Communication.S2C.Response.Builder response, String error) {
        response.setStatusCode(Communication.S2C.Response.Status.FAILURE);
        response.setErrorMessage(error);
    }

    private void getEstimatedDelay(Communication.S2C.Response.ExpectedDelay.Builder delay, Communication.C2S.Request request) throws Algorithms.AlgorithmException {
        delay.setTime(algorithms.getEstimatedDelay(request.getDoctorsName(),
                epochToDateTime(request.getTimestamp())));
        delay.setIsEstimated(true);
    }

    private void getCurrentDelay(Communication.S2C.Response.ExpectedDelay.Builder delay, Communication.C2S.Request request) throws Algorithms.AlgorithmException {
        delay.setTime(algorithms.getCurrentDelay(request.getDoctorsName()));
    }

    private LocalDateTime epochToDateTime(long milliSinceEpoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliSinceEpoch), ZoneId.systemDefault());
    }
}
