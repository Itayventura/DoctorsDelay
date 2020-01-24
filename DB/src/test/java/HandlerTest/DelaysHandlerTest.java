package HandlerTest;

import db.DataBaseImpl;
import entities.Delay;
import handlers.DelaysHandler;
import handlers.Handler;
import org.junit.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DelaysHandlerTest {

    static {
        DataBaseImpl.init();
    }

    static class AddReport extends Thread {
        private Delay delay;
        AddReport( Delay delay) {
            this.delay = delay;
        }
        public void run() {
            delaysHandler.addReport(delay);
        }
    }

    private static DelaysHandler delaysHandler;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;

    @BeforeClass
    public static void setUpClass(){
        Handler.printHeadline("DelaysHandlerTest");
    }

    @Before
    public void setUp(){
        Handler.printHeadline("Set Up");

        System.out.println("delaysHandler = new DelaysHandler();\n" +
                "\n" +
                "start = Timestamp.valueOf(\"2019-01-01 07:00:00\").toLocalDateTime();\n" +
                "end = Timestamp.valueOf(\"2019-02-01 07:00:00\").toLocalDateTime();\n" +
                "\n" +
                "futureStart = Timestamp.valueOf(\"2021-01-01 07:00:00\").toLocalDateTime();\n" +
                "futureEnd = Timestamp.valueOf(\"2021-02-01 07:00:00\").toLocalDateTime();\n");
        delaysHandler = new DelaysHandler("Luba");

        start = Timestamp.valueOf("2019-01-01 07:00:00").toLocalDateTime();
        end = Timestamp.valueOf("2019-02-01 07:00:00").toLocalDateTime();

        futureStart = Timestamp.valueOf("2021-01-01 07:00:00").toLocalDateTime();
        futureEnd = Timestamp.valueOf("2021-02-01 07:00:00").toLocalDateTime();

    }

    @Ignore("long")
    @Test
    public void LubaGetReportsTest() {
        Handler.printHeadline("Luba get Reports Test");

        System.out.println("List<Delay> list = delaysHandler.getReports(\"Luba\",start, end);\n" +
                "delaysHandler.printReportsFromList(list);\n");
        List<Delay> list = delaysHandler.getReports(start, end);
        delaysHandler.printReportsFromList(list);

        System.out.println("int num_of_reports = list.size();\n" +
                "Assert.assertEquals(num_of_reports, 2232);");
        int num_of_reports = list.size();
        Assert.assertEquals(num_of_reports, 2232);
        Handler.printHeadline("Luba Test finished successfully");
    }

    @Ignore("long")
    @Test
    public void LubaAddReportTest(){
        Handler.printHeadline("Luba add Report Test");

        System.out.println("List<Delay> list = delaysHandler.getReports(\"Luba\",futureStart, futureEnd);\n" +
                "delaysHandler.printReportsFromList(list);\n");
        List<Delay> list = delaysHandler.getReports(futureStart, futureEnd);
        delaysHandler.printReportsFromList(list);
        System.out.println("Assert.assertEquals(0, list.size());\n");
        Assert.assertEquals(0, list.size());

        System.out.println("Delay delay = new Delay(5, \"2021-01-01 07:00:00\", Delay.Type.USER,111111111);\n" +
                "int num_of_threads = 100;\n" +
                "Thread[] threads = new Thread[num_of_threads];\n" +
                "for(int i = 0; i < num_of_threads; i++) {\n" +
                "            threads[i] = new AddReport(delaysHandler, delay);\n" +
                "}\n" +
                "for (Thread T: threads)\n" +
                "            T.start();\n" +
                "for (Thread T: threads) {\n" +
                "     try {\n" +
                "                T.join();\n" +
                "     } catch (InterruptedException e) {\n" +
                "           e.printStackTrace();\n" +
                "    }\n" +
                "}\n");
        Delay delay = new Delay(5, "2021-01-01 07:00:00", Delay.Type.USER,111111111);
        int num_of_threads = 100;
        Thread[] threads = new Thread[num_of_threads];
        for(int i = 0; i < num_of_threads; i++) {
            threads[i] = new AddReport(delay);
        }
        for (Thread T: threads)
            T.start();
        for (Thread T: threads) {
            try {
                T.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("list = delaysHandler.getReports(\"Luba\",futureStart, futureEnd);\n" +
                "delaysHandler.printReportsFromList(list);\n");
        list = delaysHandler.getReports(futureStart, futureEnd);
        delaysHandler.printReportsFromList(list);
        System.out.println("Assert.assertEquals(num_of_threads, list.size());\n");
        Assert.assertEquals(num_of_threads, list.size());

        System.out.println("delaysHandler.deleteReports(\"Luba\", futureStart, futureEnd);\n" +
                "list = delaysHandler.getReports(\"Luba\",futureStart, futureEnd);\n" +
                "delaysHandler.printReportsFromList(list);\n");
        delaysHandler.deleteReports(futureStart, futureEnd);
        list = delaysHandler.getReports(futureStart, futureEnd);
        delaysHandler.printReportsFromList(list);
        System.out.println("Assert.assertEquals(0, list.size());");
        Assert.assertEquals(0, list.size());

        Handler.printHeadline("Luba add Report Test finished successfully");
    }

    @Test
    public void LubaAddReportCurrentTimestampTest(){
        Handler.printHeadline("Luba add Report Test");
        System.out.println("LocalDateTime now = LocalDateTime.now().minusMinutes(1);");
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        System.out.println("now minus minutes: " + now + "\n");

        System.out.println("LocalDateTime nowEnd = LocalDateTime.now().plusMinutes(1);");
        LocalDateTime nowEnd = LocalDateTime.now().plusMinutes(1);
        System.out.println("nowEnd plus minute: " + nowEnd + "\n");


        System.out.println("Delay delay = new Delay(5, Delay.Type.USER,111111111);\n" +
                             "delaysHandler.addReport(\"Luba\", delay);\n");
        Delay delay = new Delay(5, Delay.Type.USER,111111111);
        delaysHandler.addReport( delay);

        System.out.println("List<Delay> delays = delaysHandler.getReports(\"Luba\",now, nowEnd);\n" +
                "delaysHandler.printReportsFromList(delays);\n");
        List<Delay> delays = delaysHandler.getReports(now, nowEnd);
        delaysHandler.printReportsFromList(delays);

        System.out.println("Assert.assertEquals(1, delays.size());\n");
        Assert.assertEquals(1, delays.size());

        System.out.println("delaysHandler.deleteReports(\"Luba\", now, nowEnd);\n" +
                "delays = delaysHandler.getReports(\"Luba\",now, nowEnd);\n" +
                "delaysHandler.printReportsFromList(delays);\n");
        delaysHandler.deleteReports(now, nowEnd);
        delays = delaysHandler.getReports(now, nowEnd);
        delaysHandler.printReportsFromList(delays);

        System.out.println("Assert.assertEquals(0, delays.size());");
        Assert.assertEquals(0, delays.size());

        Handler.printHeadline("Luba add Report Test current timestamp finished successfully");
    }

    @Ignore("long")
    @Test
    public void LubaAddReportListTest(){
        Handler.printHeadline("Luba add Report List Test");
        System.out.println("List<Delay> delays = new ArrayList<>();");
        List<Delay> delays = new ArrayList<>();

        System.out.println("List<Delay> delaysList = delaysHandler.getReports(\"Luba\",futureStart,futureEnd);\n" +
                "delaysHandler.printReportsFromList(delaysList);\n");
        List<Delay> delaysList = delaysHandler.getReports(futureStart,futureEnd);
        delaysHandler.printReportsFromList(delaysList);

        System.out.println("Assert.assertEquals(0,delaysList.size());\n");
        Assert.assertEquals(0,delaysList.size());

        System.out.println("delays.add(new Delay(5,\"2021-01-01 07:00:00\",Delay.Type.FEEDBACK,\"Luba\",111111111));\n" +
                "delays.add(new Delay(3,\"2021-01-01 07:00:00\",Delay.Type.EXPERT,\"Luba\",111111111));\n" +
                "delaysHandler.addReportList(delays);\n" +
                "delaysList = delaysHandler.getReports(\"Luba\",futureStart,futureEnd);\n" +
                "delaysHandler.printReportsFromList(delaysList);\n");
        delays.add(new Delay(5,"2021-01-01 07:00:00",Delay.Type.FEEDBACK,111111111));
        delays.add(new Delay(3,"2021-01-01 07:00:00",Delay.Type.EXPERT,111111111));
        delaysHandler.addReportList(delays);
        delaysList = delaysHandler.getReports(futureStart,futureEnd);
        delaysHandler.printReportsFromList(delaysList);

        System.out.println("Assert.assertEquals(2,delaysList.size());\n");
        Assert.assertEquals(2,delaysList.size());

        System.out.println("delaysHandler.deleteReports(\"Luba\",futureStart,futureEnd);\n" +
                "delaysList = delaysHandler.getReports(\"Luba\",futureStart,futureEnd);\n" +
                "delaysHandler.printReportsFromList(delaysList);\n");
        delaysHandler.deleteReports(futureStart,futureEnd);
        delaysList = delaysHandler.getReports(futureStart,futureEnd);
        delaysHandler.printReportsFromList(delaysList);

        System.out.println("Assert.assertEquals(0,delaysList.size());\n");
        Assert.assertEquals(0,delaysList.size());

        Handler.printHeadline("Luba add Report List Test finished successfully");
    }

    @Test
    public void LubaAddReportListCurrentTimestampTest(){
        Handler.printHeadline("Luba add Report List with current timestamp Test");

        System.out.println("LocalDateTime now = LocalDateTime.now().minusMinutes(1);");
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        System.out.println("now minus minutes: " + now + "\n");

        System.out.println("LocalDateTime nowEnd = LocalDateTime.now().plusMinutes(1);");
        LocalDateTime nowEnd = LocalDateTime.now().plusMinutes(1);
        System.out.println("nowEnd plus minute: " + nowEnd + "\n");

        System.out.println("List<Delay> delaysList = delaysHandler.getReports(\"Luba\",now,nowEnd);\n" +
                "delaysHandler.printReportsFromList(delaysList);\n");
        List<Delay> delaysList = delaysHandler.getReports(now,nowEnd);
        delaysHandler.printReportsFromList(delaysList);

        System.out.println("Assert.assertEquals(0,delaysList.size());\n");
        Assert.assertEquals(0,delaysList.size());

        System.out.println("List<Delay> delays = new ArrayList<>();\n" +
                "delays.add(new Delay(5,Delay.Type.FEEDBACK,\"Luba\",111111111));\n" +
                "delays.add(new Delay(3,Delay.Type.FEEDBACK,\"Luba\",111111111));\n" +
                "delaysHandler.addReportList(delays);\n");
        List<Delay> delays = new ArrayList<>();
        delays.add(new Delay(5,Delay.Type.FEEDBACK,111111111));
        delays.add(new Delay(3,Delay.Type.FEEDBACK,111111111));
        delaysHandler.addReportList(delays);

        System.out.println("delaysList = delaysHandler.getReports(\"Luba\",now,nowEnd);\n" +
                "delaysHandler.printReportsFromList(delaysList);\n");
        delaysList = delaysHandler.getReports(now,nowEnd);
        delaysHandler.printReportsFromList(delaysList);

        System.out.println("Assert.assertEquals(2,delaysList.size());\n");
        Assert.assertEquals(2,delaysList.size());

        System.out.println("delaysHandler.deleteReports(\"Luba\",now,nowEnd);\n" +
                "delaysList = delaysHandler.getReports(\"Luba\",now,nowEnd);\n" +
                "delaysHandler.printReportsFromList(delaysList);\n");
        delaysHandler.deleteReports(now,nowEnd);
        delaysList = delaysHandler.getReports(now,nowEnd);
        delaysHandler.printReportsFromList(delaysList);

        System.out.println("Assert.assertEquals(0,delaysList.size());\n");
        Assert.assertEquals(0,delaysList.size());

        Handler.printHeadline("Luba add Report List with current timestamp Test finished successfully");
    }

    /**this method works but there are many records in the table so only the latest are viewed*/
    @Ignore("Used to get information")
    @Test
    public void LubaPrintTable(){
        delaysHandler.printTable();
    }

    @Test
    public void getDelaysTest(){
        Handler.printHeadline("get Delays Test");
        System.out.println("List<Delay> delays = delaysHandler.getDelays(\"Luba\");\n" +
                "Assert.assertEquals(26208, delays.size());\n");
        List<Delay> delays = delaysHandler.getDelays();
        Assert.assertEquals(26208, delays.size());
        Handler.printHeadline("get Delays Test finished successfully");

    }
}
