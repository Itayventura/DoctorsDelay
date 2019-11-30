import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AlgorithmsImplTest {
    AlgorithmsImpl algorithms = new AlgorithmsImpl(new DataBaseMock());

    public class DataBaseMock implements DataBase{
        @Override
        public List<DelayReport> getReports(String doctorsName, LocalDateTime startTime, LocalDateTime endTime) {
            int count = 0;
            List<DelayReport> list = new ArrayList<DelayReport>();
            DelayReport report = new DelayReport() {
                @Override
                public int getReportedDelay() {
                    return 10;
                }

                @Override
                public LocalDateTime getReportTimestamp() {
                    return LocalDateTime.now();
                }
            };
            list.add(report);
            report = new DelayReport() {
                @Override
                public int getReportedDelay() {
                    return 15;
                }

                @Override
                public LocalDateTime getReportTimestamp() {
                    return LocalDateTime.now();
                }
            };
            list.add(report);
            report = new DelayReport() {
                @Override
                public int getReportedDelay() {
                    return 25;
                }

                @Override
                public LocalDateTime getReportTimestamp() {
                    return LocalDateTime.now();
                }
            };
            list.add(report);

            return list; //TODO - implement silly mocker
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
        algorithms.getEstimatedDelay("Shiran",LocalDateTime.now().plusMinutes(45));
        System.out.println("yay it works!");
    }
}