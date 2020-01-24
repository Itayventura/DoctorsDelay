import algorithms.Algorithms;
import algorithms.AlgorithmsImpl;
import db.DataBase;
import db.DataBaseImpl;
import handlers.ClientHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Activates the ServerSocket, waits for connections, accepts,
 * and launches ClientHandler's to manage them within a multi threaded ExecutorService.
 */
public class Server {
    private static final Logger logger = Logger.getLogger(Server.class);
    private int listeningPort;
    private ExecutorService pool;
    private Constructor<? extends Algorithms> algorithmsConstructor;
    private Constructor<? extends DataBase> dbConstructor;
    private ServerSocket serverSocket;

    public Server(int listeningPort, int numThreads) throws NoSuchMethodException{
        this.listeningPort = listeningPort;
        this.pool = Executors.newFixedThreadPool(numThreads);
        this.algorithmsConstructor = AlgorithmsImpl.class.getConstructor();
        this.dbConstructor = DataBaseImpl.class.getConstructor();
    }

    public Server(int listeningPort, int numThreads, Constructor<? extends Algorithms> algorithmsConstructor,
                  Constructor<? extends DataBase> dbConstructor) {
        this.listeningPort = listeningPort;
        this.pool = Executors.newFixedThreadPool(numThreads);
        this.algorithmsConstructor = algorithmsConstructor;
        this.dbConstructor = dbConstructor;
    }

    void start() throws Exception {
        logger.info(String.format("Starting server on port=%d", listeningPort));
        serverSocket = new ServerSocket(listeningPort);
        for (;;) {
            try {
                pool.execute(new ClientHandler(serverSocket.accept(), algorithmsConstructor.newInstance(),
                        dbConstructor.newInstance()));
            } catch (IOException e) {
                logger.warn("server was interrupted", e);
                break;
            }
        }
    }

    void stop() {
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            logger.error("server socket close() failed", e);
        }
        pool.shutdownNow();
        try {
            if (!pool.awaitTermination(100, TimeUnit.MICROSECONDS)) {
                logger.warn("Still waiting...");
            }
        } catch (InterruptedException e) {
            logger.warn("Server was interrupted when trying to exit");
        }
        logger.info("Exiting normally...");
    }

}
