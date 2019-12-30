package estimation;

import algorithms.AlgorithmsImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import org.apache.log4j.Logger;

public class HttpCommunications implements ModelHandler
{
    private static final Logger logger = Logger.getLogger(String.valueOf(HttpCommunications.class));

    @Override
    public double BuildModel()
    {
        HttpURLConnection con = CreateConnection("buildModel");
        String responseJson = readResponseRequest(con);
        return parseResponseBuildModel(responseJson);
    }

    public double parseResponseBuildModel(String response)
    {
        JSONObject jsonParser = new JSONObject(response);
        return jsonParser.getDouble("accuracy");
    }

    @Override
    public DelayEstimation Predict(String doctorName, LocalDateTime localDateTime)
    {
        HttpURLConnection con = CreateConnection("predict");
        createRequestForPrediction(con, doctorName, localDateTime.getMonth().name(), localDateTime.getDayOfWeek().name(),
                localDateTime.getHour(), localDateTime.getMinute());
        DelayEstimation predictResult = parseResponseStringPrediction(
                readResponseRequest(con), AlgorithmsImpl.getAccuracy_model());
        return predictResult;
    }

    private void createRequestForPrediction(HttpURLConnection con, String doctorName, String monthName, String dayName, int hour, int minutes)
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

    public DelayEstimation parseResponseStringPrediction(String response, double accuracy)
    {
        JSONObject jsonParser = new JSONObject(response);
        JSONArray predictionType = jsonParser.getJSONArray("prediction");

        DelayEstimation delayEstimation = new DelayEstimation(
                DelayEstimation.StringToEstimationType.get(predictionType.get(0).toString()),
                (int)Math.floor(accuracy));
        return delayEstimation;
    }

    private String readResponseRequest(HttpURLConnection con)
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

    private HttpURLConnection CreateConnection(String httpRequest)
    {
        HttpURLConnection con = null;
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

        return con;
    }
}