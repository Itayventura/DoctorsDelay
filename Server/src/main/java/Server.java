import algorithms.Algorithms;
import handlers.ClientHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class);
    private int listeningPort;
    private ExecutorService pool;
    private Algorithms algorithms;
    private DataBase db;
    private AtomicInteger connectedClients = new AtomicInteger();

    public Server(int listeningPort, int numThreads, Algorithms algorithms, DataBase db) {
        this.listeningPort = listeningPort;
        this.pool = Executors.newFixedThreadPool(numThreads);
        this.algorithms = algorithms;
        this.db = db;
    }

    public void start() throws IOException {
        logger.info(String.format("Starting server on port=%d, algorithms.Algorithms=%s",
                listeningPort, algorithms.getClass().getName()));
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            for(;;){
                logger.info(connectedClients.get() + " Connected clients to server");
                pool.execute(new ClientHandler(serverSocket.accept(), algorithms));
            }
        }
    }

    public void stop() {
        pool.shutdown();
    }



}
