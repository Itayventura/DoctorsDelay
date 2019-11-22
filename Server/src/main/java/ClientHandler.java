import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ClientHandler extends Communicator implements Runnable{
    private static final Logger logger = Logger.getLogger(ClientHandler.class);

    private Socket socket;
    private Algorithms algorithms;

    public ClientHandler(Socket socket, Algorithms algorithms) throws IOException {
        super(new DataInputStream(socket.getInputStream()),
                new DataOutputStream(socket.getOutputStream()));
        this.socket = socket;
        this.algorithms = algorithms;
        logger.info(String.format("created new client handler for: %s", socket.getRemoteSocketAddress()));
    }

    @Override
    public void run() {
        for(;;) {
            try{
                Communication.C2S client2server = readMessage(Communication.C2S.parser());
                Communication.S2C server2client;
                if (client2server.hasRequest())
                    server2client = handleRequest(client2server.getRequest());
                else if (client2server.hasReport())
                    server2client = handleReport(client2server.getReport());
                else{
                    if (!client2server.getFinish())
                        logger.error(String.format("got empty c2s message from client %s, closing connection",
                                socket.getRemoteSocketAddress()));
                    close();
                    return;
                }
                sendMessage(server2client);
            } catch (IOException e) {
                logger.error(String.format("communication with client %s failed",
                        socket.getRemoteSocketAddress()), e);
                return;
            }
        }
    }

    private Communication.S2C handleRequest(Communication.C2S.Request request){
        Algorithms.Response algorithmResponse;
        switch (request.getType()) {
            case NOW:
                algorithmResponse = algorithms.getCurrentDelay(request.getDoctorsName());
                break;
            case ESTIMATE:
                algorithmResponse = algorithms.getEstimatedDelay(request.getDoctorsName(),
                        epochToDateTime(request.getTimestamp()));
                break;
        }
        

    }

    private Communication.S2C toClientResponse(Algorithms.Response algorithmResponse) {

    }

    private Communication.S2C handleReport(Communication.C2S.Report report){

    }

    private LocalDateTime epochToDateTime(long milliSinceEpoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliSinceEpoch), ZoneId.systemDefault());
    }

    private void close() {
        logger.info(String.format("closing connection with client %s", socket.getRemoteSocketAddress()));
        try {
            socket.close();
        } catch (IOException e) {
            logger.error(String.format("failed to close connection with client %s",
                    socket.getRemoteSocketAddress()), e);
        }
    }

}
