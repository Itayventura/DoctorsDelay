import algorithms.AlgorithmsImpl;
import db.DataBaseImpl;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * A simple API to activate our server.
 * Use --help to see program arguments.
 */

public class Main {
    private static final String listeningPortArg = "listeningPort";
    static int listeningPort = 80;
    static int numThreads = 4;
    private static final String numThreadsArg = "numThreads";
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
            server = new Server(listeningPort, numThreads);
            server.start();
        } catch (ParseException e) {
            System.out.println("Wrong argument given, use --help, " + e.getMessage());
        } catch (Exception e) {
            if (server != null)
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
