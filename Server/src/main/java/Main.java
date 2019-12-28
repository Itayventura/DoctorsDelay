import algorithms.AlgorithmsImpl;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * The heart of the server.
 * Listens to a socket and accepts client (on a new Thread)
 * Reads a C2S message, parses and handles. sends back the S2C response.
 * Uses Algorithm to set and get information.
 */

public class Main {
    private static final String listeningPortArg = "listeningPort";
    protected static int listeningPort = 80;
    private static final String numThreadsArg = "numThreads";
    protected static int numThreads = 4;
    private static Options options = createOptions();

    public static void main(String[] args){
        Server server = null;
        try{
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse( options, args);
            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "ant", options, true );
                return;
            }
            if (cmd.hasOption(listeningPortArg))
                listeningPort = Integer.parseInt(cmd.getOptionValue(listeningPortArg));
            if (cmd.hasOption(numThreadsArg))
                numThreads = Integer.parseInt(cmd.getOptionValue(numThreadsArg));
            server = new Server(listeningPort, numThreads, new AlgorithmsImpl(new DataBaseImpl()), new DataBaseImpl());
            server.start();
        } catch (ParseException e) {
            System.out.println("Wrong argument given, use --help, " + e.getMessage());
        } catch (IOException e) {
            server.stop();
            System.out.println("Server failed, " + e.getMessage());
        }
    }

    private static Options createOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "Arguments description");
        options.addOption("p", listeningPortArg,
                true, "The port that the server will be listening on");
        options.addOption("n", numThreadsArg,
                true, "Number of threads to handle clients");
        return options;
    }
}
