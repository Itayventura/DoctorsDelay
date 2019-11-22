import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The heart of the server.
 * Listens to a socket and accepts client (on a new Thread)
 * Reads a C2S message, parses and handles. sends back the S2C response.
 * Uses Algorithm to set and get information.
 */

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    private static final int LISTENING_PORT = 80;
    private static final int NUM_THREADS = 4;

    public static void main(String[] args){


    }
}
