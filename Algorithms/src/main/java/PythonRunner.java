import org.python.util.PythonInterpreter;
import java.util.logging.Logger;

public class PythonRunner
{
    private static final Logger logger = Logger.getLogger(String.valueOf(PythonRunner.class));

    public void Run()
    {
        PythonInterpreter pythonInterpreter = new PythonInterpreter();
        logger.info("Start running python script");

        try
        {
            pythonInterpreter.exec("import sys");
            pythonInterpreter.exec("import sys.path");
            pythonInterpreter.execfile("C:\\Users\\shiranpilas\\Desktop\\shiran\\DoctorsDelay\\Algorithms\\scripts\\DoctorsDelay.py");
        }
        catch (Exception e)
        {
            logger.info("Error:" + e.getMessage());
        }

        logger.info("Finished running python script");
    }
}