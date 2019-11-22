import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Logger logger = Logger.getLogger(Main.class);
    private int listeningPort;
    private int numThreads;
    private Algorithms algorithms;

    public Server(int listeningPort, int numThreads, Algorithms algorithms) {
        this.listeningPort = listeningPort;
        this.numThreads = numThreads;
        this.algorithms = algorithms;
    }

    public void start() throws IOException {
        ExecutorService pool =  Executors.newFixedThreadPool(numThreads);
        try (ServerSocket serverSocket = new ServerSocket(listeningPort)) {
            for(;;){
                pool.execute(new ClientHandler(serverSocket.accept(), algorithms));
            }
        }
    }
}
