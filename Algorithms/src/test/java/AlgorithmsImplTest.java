import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AlgorithmsImplTest
{
    AlgorithmsImpl algorithms = new AlgorithmsImpl(new DataBaseMock());

    public class DataBaseMock implements DataBase{
        @Override
        public List<DelayReport> getReports(String doctorsName, Timestamp startTime, Timestamp endTime) {
            List<DelayReport> list = new ArrayList<DelayReport>();

            Path pathToFile = Paths.get("C:\\Users\\shiranpilas\\university\\4 year\\crowdsourcing\\dataSet.csv");

            // create an instance of BufferedReader
            // using try with resource, Java 7 feature to close resources
            try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

                // read the first line from the text file
                String line = br.readLine();
                line = br.readLine();

                // loop until all lines are read
                while (line != null) {

                    // use string.split to load a string array with the values from
                    // each line of
                    // the file, using a comma as the delimiter
                    String[] attributes = line.split(",");

                    DelayReport report = createReport(attributes);

                    // adding book into ArrayList
                    list.add(report);

                    // read next line before looping
                    // if end of file reached, line would be null
                    line = br.readLine();
                }

            } catch (Exception ioe) {
                ioe.printStackTrace();
            }

            return list; //TODO - implement silly mocker
        }

        private DelayReport createReport(String[]attributes)
        {
            // create and return book of this metadata
            return new DelayReport() {
                private int reportedDelay = getReportedDelay(); //the expected delay that the client reported
                private Timestamp reportTimestamp = getReportTimestamp() ;
                private String doctorsName = getDoctorsName();
                @Override
                public int getReportedDelay() {
                    return Integer.parseInt(attributes[5]);
                }

                @Override
                public Timestamp getReportTimestamp() {
                    int day;
                    if(attributes[2].equals("SUNDAY")){
                        day = 21;
                    }
                    else{
                        day = 24;
                    }
                    LocalDateTime timeTemp = LocalDateTime.of(Integer.parseInt(attributes[0]), Integer.parseInt(attributes[1]),day,Integer.parseInt(attributes[3]),Integer.parseInt(attributes[4]),0);
                    Timestamp time = Timestamp.valueOf(timeTemp);
                    return time;
                }

                @Override
                public String getDoctorsName() {
                    return "Shiran";
                }
            };
        }

        @Override
        public boolean doctorExists(String doctorsName) {
            return false; //TODO - implement silly mocker
        }

        @Override
        public void addReport(String doctorsName, int expectedDelay) {
            //TODO - implement silly mocker
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        DataBaseMock db = new DataBaseMock();
        //algorithms.writeToCSV(db.getReports("Ron",LocalDateTime.now().minusMonths(6),LocalDateTime.now()));
        //algorithms.getDataSet();
        //algorithms.convertCSVToArff("C:\\Users\\shiranpilas\\university\\4 year\\crowdsourcing\\dataSet.csv","C:\\Users\\shiranpilas\\university\\4 year\\crowdsourcing\\dataSet.arff");
        //algorithms.getEstimatedDelay("Shiran",LocalDateTime.now().plusMinutes(45));

        List<DataBase.DelayReport> filtered =  algorithms.filteredData(db.getReports("Shiran",Timestamp.valueOf(LocalDateTime.now().minusMonths(6)),Timestamp.valueOf(LocalDateTime.now())));
        System.out.println("yay it works!");
    }
}