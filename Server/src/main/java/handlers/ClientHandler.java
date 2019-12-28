package handlers;

import algorithms.Algorithms;
import communications.Communication;
import communications.Communicator;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Communicator implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class);
    private Socket socket = null;
    private String clientName;
    //private AlgorithmsHandler algorithmsHandler;

    public ClientHandler(Socket socket, Algorithms algorithms) throws IOException {
        super(new DataInputStream(socket.getInputStream()),
                new DataOutputStream(socket.getOutputStream()));
        this.algorithmsHandler = new AlgorithmsHandler(algorithms);
        this.socket = socket;
        this.clientName = socket.getRemoteSocketAddress().toString();
        int connected = connectedClients.incrementAndGet();
        logger.info(String.format("created new client handler for: %s, currently %d clients connected", clientName, connected));
    }

    @Override
    public void run() {
        for(;;) {
            try{
                Communication.C2S client2server = readMessage(Communication.C2S.parser());
                Communication.S2C server2client;
                if (client2server.hasRequest())
                    server2client = algorithmsHandler.handleRequest(client2server.getRequest());
                else if (client2server.hasReport())
                    server2client = algorithmsHandler.handleReport(client2server.getReport());
                else{
                    if (!client2server.getFinish())
                        logger.error(String.format("got empty c2s message from client %s, closing connection", clientName));
                    break;
                }
                logger.info(String.format("sending s2c=%s to client=%s", server2client, clientName));
                sendMessage(server2client);
            } catch (IOException e) {
                logger.error(String.format("communication with client %s failed", clientName), e);
                break;
            }
        }
        close();
    }

    public void close() {
        connectedClients.decrementAndGet();
        if (socket == null) {
            try {
                closeStreams();
            } catch (IOException e) {
                logger.error("close streams failed", e);
            }
        }
        else {
            logger.info(String.format("closing connection with client %s", clientName));
            try {
                socket.close();
            } catch (IOException e) {
                logger.error(String.format("failed to close connection with client %s",
                        socket.getRemoteSocketAddress()), e);
            }
        }
    }

}
