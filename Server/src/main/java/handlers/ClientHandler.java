package handlers;

import algorithms.Algorithms;
import communications.Communicator;
import db.DataBase;
import generated.Communication;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * First manages login of the client to the server.
 * If successful, reads a message from the client, handles it
 * and sends a response if required.
 * This goes on until client decides to disconnect.
 */
public class ClientHandler extends Communicator implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class);
    private Socket socket;
    private Algorithms algorithms;
    private DataBase db;
    private int clientId = -1;

    public ClientHandler(Socket socket, Algorithms algorithms, DataBase db) throws IOException {
        super(new DataInputStream(socket.getInputStream()),
                new DataOutputStream(socket.getOutputStream()));
        this.socket = socket;
        this.algorithms = algorithms;
        this.db = db;
    }

    @Override
    public void run() {
        if (login()) {
            logger.info("Client " + clientId + " has successfully logged in!");
            final ReportHandler reportHandler = new ReportHandler(clientId, db);
            final RequestHandler requestHandler = new RequestHandler(clientId, algorithms, db);
            for(;;) {
                try{
                    Communication.C2S client2server = readMessage(Communication.C2S.parser());
                    if (client2server.hasRequest())
                        sendMessage(requestHandler.handle(client2server.getRequest()));
                    else if (client2server.hasReport())
                        sendMessage(reportHandler.handle(client2server.getReport()));
                    else if (client2server.hasFeedback())
                        reportHandler.handleFeedback(client2server.getFeedback());
                    else{
                        if (!client2server.getFinish())
                            logger.error(String.format("got empty c2s message from client %d, closing connection", clientId));
                        break;
                    }
                } catch (IOException e) {
                    logger.error(String.format("communication with client %d failed", clientId), e);
                    break;
                } catch (Throwable e) {
                    logger.error("Client " + clientId + "was interrupted", e);
                }
            }
        }
        close();
    }

    private boolean login() {
        boolean loggedIn = false;
        while (!loggedIn) {
            try {
                Communication.C2S client2server = readMessage(Communication.C2S.parser());
                Communication.S2C.Response.Builder response = getFailureResponse();
                if (client2server.getFinish()) {
                    logger.warn("client decided to close during login");
                    return false;
                }
                if (client2server.hasLogin()) {
                    Communication.C2S.Login login = client2server.getLogin();
                    logger.info("Client " + login.getClientId() + " attempts to login");
                    String password = db.getUserPassword(login.getClientId());
                    if (password != null && password.equals(login.getPassword())) {
                        loggedIn = true;
                        clientId = login.getClientId();
                        response.setStatusCode(Communication.S2C.Response.Status.SUCCESSFUL);
                    }
                }
                sendMessage(Communication.S2C.newBuilder().setResponse(response).build());
            } catch (IOException e) {
                logger.error("Login failed, exiting...");
                break;
            }
        }
        return loggedIn;
    }

    public void close() {
        if (socket == null) {
            try {
                closeStreams();
            } catch (IOException e) {
                logger.error("close streams failed", e);
            }
        }
        else {
            logger.info(String.format("closing connection with client %d", clientId));
            try {
                socket.close();
            } catch (IOException e) {
                logger.error(String.format("failed to close connection with client %s",
                        socket.getRemoteSocketAddress()), e);
            }
        }
    }

    protected static Communication.S2C.Response.Builder getFailureResponse() {
        return Communication.S2C.Response.newBuilder()
                .setStatusCode(Communication.S2C.Response.Status.FAILURE);
    }

}
