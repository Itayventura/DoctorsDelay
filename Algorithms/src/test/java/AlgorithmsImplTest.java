import org.junit.Test;

import static org.junit.Assert.*;


public class AlgorithmsImplTest
{
    @Test
    public void httpRequestsCreateTest()
    {
        HttpCommunications req = new HttpCommunications("predict");
        try
        {
            req.createRequestForPrediction("Itay Ventura","DECEMBER","Tuesday",17,3);
            req.readResponseRequest();
        }
        catch (Exception e)
        {
            fail();
        }
        HttpCommunications req2 = new HttpCommunications("predict");

        try
        {
            req2.createRequestForPrediction("Itay Ventura","DECEMBER","Tuesday",17,30);
            req2.readResponseRequest();
        }
        catch (Exception e)
        {
            fail();
        }
        HttpCommunications req3 = new HttpCommunications("buildModel");

        try
        {
            //req.createRequestForPredict("Itay Ventura","DECEMBER","Tuesday",8,3);
            req3.readResponseRequest();
        }
        catch (Exception e)
        {
            fail();
        }
    }




/*    @Test
    public void buildModelWithPyhtonInterpreter()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl();

        try
        {
            algorithms.getEstimatedDelay("Dulitel", LocalDateTime.now());
        }
        catch (Algorithms.AlgorithmException e)
        {
            fail();
        }
    }*/

    /*public class DataBaseMock implements DataBase
    {

        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DataBaseMock());

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
    }*/
}