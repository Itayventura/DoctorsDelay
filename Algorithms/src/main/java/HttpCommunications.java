//import org.python.util.PythonInterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class HttpCommunications
{
    private static final Logger logger = Logger.getLogger(String.valueOf(HttpCommunications.class));
    private HttpURLConnection con;

    public HttpCommunications(String httpRequest)
    {
        try {
            //Create a URL object.
            URL url = new URL("http://127.0.0.1:11111/"+httpRequest);//("https://reqres.in/api/users");

            //open a connection.
            con = (HttpURLConnection)url.openConnection();

            //set the request method
            con.setRequestMethod("POST");

            //set the request parameters.
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            //set response format type.
            con.setRequestProperty("Accept", "application/json");

            //ensure the connection will be used to send content.
            con.setDoOutput(true);
        }
        catch (MalformedURLException ex)
        {
            logger.info("Error: could not create a URL object: "+ex.getMessage());
        }
        catch (IOException ex)
        {
            logger.info("Error: could not connect: "+ex.getMessage());
        }
    }

    public void createRequestForPrediction(String doctorName, String monthName, String dayName, int hour, int minutes)
    {
        String jsonInputString = String.format("[{\"Doctor's name\": \"%s\", \"month\": \"%s\", \"day\": \"%s\", \"hour\": %d, \"minutes\": %d}]",doctorName,monthName,dayName,hour,minutes);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
            os.flush();
        }
        catch(IOException ex)
        {
            logger.info("Error: could not write json string via the HTTP connection: "+ex.getMessage());
        }
    }

    public String readResponseRequest()
    {
        String result = null;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            result = response.toString();
            br.close();
            con.disconnect();
        }
        catch(IOException ex)
        {
            logger.info("Error: could not read the answer of the request: "+ex.getMessage());
        }
        return result;//parseResponseString(response);
    }
}