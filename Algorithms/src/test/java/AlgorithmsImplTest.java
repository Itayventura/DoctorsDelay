import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class AlgorithmsImplTest {
    AlgorithmsImpl algorithms = new AlgorithmsImpl(new DataBaseMock());

    public class DataBaseMock implements DataBase{
        @Override
        public List<DelayReport> getReports(String doctorsName, LocalDateTime startTime, LocalDateTime endTime) {
            return null; //TODO - implement silly mocker
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
        System.out.println("yay it works!");
    }
}