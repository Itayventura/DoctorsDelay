import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class);
    private int listeningPort;
    private ExecutorService pool;
    private Algorithms algorithms;
    private AtomicInteger connectedClients = new AtomicInteger();

    public Server(int listeningPort, int numThreads, Algorithms algorithms) {
        this.listeningPort = listeningPort;
        this.pool = Executors.newFixedThreadPool(numThreads);
        this.algorithms = algorithms;
    }

    public void start() throws IOException {
        logger.info(String.format("Starting server on port=%d, Algorithms=%s",
                listeningPort, algorithms.getClass().getName()));
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            for(;;){
                pool.execute(new ClientHandler(serverSocket.accept(), algorithms));
            }
        }
    }

    public void stop() {
        pool.shutdown();
    }

    public int getConnectedClients() {
        return connectedClients.get();
    }

    public class ClientHandler extends Communicator implements Runnable {
        private Socket socket = null;
        private String clientName;
        private AlgorithmsHandler algorithmsHandler;

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

}
