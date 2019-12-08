import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.sql.Timestamp;

public class DataBaseImpl implements DataBase {
    private static SQLQuery sqlQuery;

    protected static final Logger logger = Logger.getLogger(DataBaseImpl.class);
    private static final int column_width = 30;
    private static final int report_attributes = 2;
    private static final int delays_attributes = 3;
    private static final int doctors_attributes = 4;
    private static final String textInBold = "\033[0;1m";
    private static final String textNotInBold = "\033[0m";
    /* Timestamp format: "05/30/2015 12:45:05";
     * Date format: "yyyy-MM-dd"
     * Time Format: "12:45:05"
     */

    public DataBaseImpl() throws SQLException {
        sqlQuery = new SQLQueryImpl();
    }


    @Override
    public DayReport getDayReport(String doctorsName, String date) {
        return new DayReport() {

            private Time getStartTime() {
                return sqlQuery.getTime("startTime", doctorsName);
            }

            private Time getEndTime() {
                return sqlQuery.getTime("endTime", doctorsName);
            }

            private DayOfWeek convertDateToDay() {
                SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    java.util.Date dateJava = textFormat.parse(date);
                    Calendar c = Calendar.getInstance();
                    c.setTime(dateJava);
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1; //dayOfWeek starts from monday and not from sunday
                    return dayOfWeek != 0? DayOfWeek.of(dayOfWeek):DayOfWeek.of(dayOfWeek+7);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void printDayReport() {
                printHeadline(doctorsName + " Day Report", report_attributes);
                List<String> dayReportMetaData = new ArrayList<>();
                dayReportMetaData.add("Date: " + date);
                dayReportMetaData.add("Day: " + (getDay() + "").toLowerCase());
                printColumnsNames(dayReportMetaData);
                print();
                printReportsFromList(getReportList());
            }

            @Override
            public void printReportsBetween(Time startTime, Time endTime) {
                printHeadline(doctorsName + "Report from " + startTime + " to " + endTime + " at " + date, report_attributes);
                printReportsFromList(getReportsBetween(startTime, endTime));
            }


            @Override
            public DayOfWeek getDay() {
                return convertDateToDay();

            }

            @Override
            public List<DelayReport> getReportList() {
                return getReportsBetween(getStartTime(), getEndTime());
            }

            //todo test
            @Override
            public List<DelayReport> getReportsBetween(Time startTime, Time endTime) {
                Timestamp start = Timestamp.valueOf(date + " " + startTime);
                Timestamp end = Timestamp.valueOf(date + " " + endTime);
                return getReports(doctorsName, start, end);
            }

        };
    }

    @Override
    public DoctorReport getDoctorReport(String doctorsName){
        return new DoctorReport() {
            //todo sql exception
            @Override
            public List<DayReport> getDayReports() {
                try{
                    List<DayReport> dayReport = new ArrayList<DayReport>();
                    ResultSet dayReportSet = sqlQuery.getDayReports(doctorsName);
                        while(dayReportSet.next()) {
                            Date date = dayReportSet.getDate("DATE");
                            DayReport dr = getDayReport(doctorsName, date + "");
                            dayReport.add(dr);
                        }

                    return dayReport;
                }catch (SQLException ex) {
                String errorMessage = "DoctorReport getDayReports Exception\n";
                logger.error(errorMessage, ex);
                throw new RuntimeException("errorMessage", ex);
            }
            }

            @Override
            public String getDoctorName() {
                return doctorsName;
            }

            @Override
            public int getInterval() {
                try{
                    ResultSet ResultSetInterval = sqlQuery.getInterval(doctorsName);
                    ResultSetInterval.next();
                    return ResultSetInterval.getInt("appointment_interval");
                }catch (SQLException ex) {
                    String errorMessage = "DataBaseImpl getInterval Exception \n";
                    logger.error(errorMessage, ex);
                    throw new RuntimeException("errorMessage", ex);
                }
            }

            @Override
            public void printDoctorReport(){
                printHeadline(doctorsName + " Doctor Report", doctors_attributes);
                printDoctorMetaData();
                for(DayReport dr:getDayReports()){
                    dr.printDayReport();
                }
            }

            //todo sql exception
            private void printDoctorMetaData() {
                try {
                    ResultSet ResultSetDoctorMetaData = sqlQuery.getDoctorMetaData(doctorsName);
                    ResultSetDoctorMetaData.next();
                    Time startTime = ResultSetDoctorMetaData.getTime("startTime");
                    Time endTime = ResultSetDoctorMetaData.getTime("endTime");
                    int interval = ResultSetDoctorMetaData.getInt("appointment_interval");

                    List<String> metaDataDoctor = new ArrayList<>();
                    metaDataDoctor.add("dr's name: " + doctorsName);
                    metaDataDoctor.add("start time: " + startTime);
                    metaDataDoctor.add("end time: " + endTime);
                    metaDataDoctor.add("interval length: " + interval);
                    printColumnsNames(metaDataDoctor);
                } catch (SQLException ex) {
                    String errorMessage = "printDoctorMetaData Exception \n";
                    logger.error(errorMessage, ex);
                    throw new RuntimeException("errorMessage", ex);
                }
            }
        };
    }

    //todo sql exception
    @Override
    public List<DelayReport> getReports(String doctorsName, Timestamp startTime, Timestamp endTime) {

        try{
            List<DelayReport> delayReports = new ArrayList<DelayReport>();
            ResultSet reportListSet = sqlQuery.getReports(doctorsName,startTime,endTime);
            while (reportListSet.next()) {
                int delay = reportListSet.getInt("reported_delay_minutes");
                Timestamp ts = reportListSet.getTimestamp("timestamp");
                DelayReport dr = new DelayReport(){
                    @Override
                    public int getReportedDelay() {
                        return delay;
                    }

                    @Override
                    public Timestamp getReportTimestamp() {
                        return ts;
                    }
                };
                delayReports.add(dr);
                }
            return delayReports;
            } catch (SQLException ex) {

            String errorMessage = "getReports Exception \n";
            logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }
    }

    @Override
    public boolean doctorExists(String doctorsName) {
        return sqlQuery.isDoctorExist(doctorsName);

    }

    //todo dont allow !doctorExists, it is temporary for internal use
    @Override
    public void addReport(String doctorsName, int expectedDelay) {
        if (!doctorExists(doctorsName)) {
            sqlQuery.insertDoctor(doctorsName);
        }
        SQLQueryImpl.insertReport(doctorsName, expectedDelay);
    }

    private void addReport(String doctorsName, int expectedDelay, Timestamp timestamp) {
            if (!doctorExists(doctorsName)) {
                sqlQuery.insertDoctor(doctorsName);
            }
        SQLQueryImpl.insertReport(doctorsName, expectedDelay, timestamp);
    }

    private static int getPolynomialDelay(int x, List<Double> coefficients) {
        Double delay = 0.0;
        int y = 1;
        for(double d: coefficients){
            delay +=  (y * d);
            y = y * x;
        }
        return delay.intValue();
    }

    //todo the 2 fors is redundant, count minute from start
    private void addPolynomialReportsToDoctor(List<Double> coefficients, String doctorsName, String date, int interval){
        //String startTime = "08:00:00";

        int delay = 0;
        for(int hour = 8; hour < 20; hour++){ //todo global
            for (int minute = 0; minute < 60; minute+=interval){ //todo global
                String hours = hour<10? "0"+hour:""+hour;
                String minutes = minute<10? "0"+minute:""+minute;
                String seconds = "00";
                String time = hours + ":" + minutes + ":" + seconds;
                Timestamp timestamp = Timestamp.valueOf(date + " " + time);
                delay = getPolynomialDelay(minute + (hour-8)*60, coefficients);
                addReport(doctorsName,delay,timestamp);
            }
        }

        /*

         */

    }
    private static void printNTimesC(int N, Character C) {
        for(int i = 0; i < N; i++){
            System.out.print(C);
        }
    }

    static void printHeadline(String headline){
        printHeadline(headline,delays_attributes);
    }

    private static void printHeadline(String headline, int num_of_columns){
        int line_length = column_width * num_of_columns;
        print();
        printNTimesC(line_length, '_');
        print();
        printNTimesC((line_length - headline.length())/2, '-');
        System.out.print(textInBold + headline + textNotInBold);
        printNTimesC((line_length - headline.length() + 1)/2, '-');
        print();
        printNTimesC(line_length, '-');
        print();
        print();
    }

    private static void printColumnsNames(List<String> columnsNames){
            int num_of_columns = columnsNames.size();
            //printNTimesC(1, ' ');
            for (int i = 0; i < num_of_columns; i++) {
                System.out.print(textInBold + columnsNames.get(i) + textNotInBold);
                if (i < num_of_columns-1) {
                    String column_name = columnsNames.get(i);
                    printNTimesC(column_width - column_name.length(), ' ');
                }
            }
            print();
            //printNTimesC(column_width * num_of_columns, '-');
            //System.out.println("");
    }

    //todo SQL Exception
    private static void printTable(List<String> columnsNames, String DB){
        try{
            ResultSet resultSetDB = sqlQuery.getDB(DB);
            while (resultSetDB.next()) {   // Move the cursor to the next row
                printRowInTable(columnsNames, resultSetDB);
            //int num_of_columns = columnsNames.size();
            //printNTimesC(column_width * num_of_columns, '-');
            //System.out.println();
            }
    } catch (SQLException e) {

            String errorMessage = "printTable Exception \n";
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);

      }
    }

    //todo sql exception
    private static void printRowInTable(List<String> columnsNames, ResultSet resultSetDB) {
        for (int i = 0; i < columnsNames.size(); i++) {
            try {
                String value = resultSetDB.getString(columnsNames.get(i));
                printValue(value, columnsNames.size(), i);
            } catch (SQLException e) {
                String errorMessage = "printRowInTable Exception \n";
                logger.error(errorMessage, e);
                throw new RuntimeException("errorMessage", e);
            }
        }
    }

    private static void printValue(String value, int size, int i)
    {
        //printNTimesC(1, '|');
        System.out.print(value);
        if (i < size-1)
            printNTimesC(column_width - value.length(),' ');
        else{
            print();
        }
    }

    private static void print(String str){
        System.out.println(str);
    }

    private static void print(){
        print("");
    }

    private static void printReports(String DB) {
        List<String> columnsNames = sqlQuery.getColumnsNames(DB);

        printHeadline("Database " + DB.toUpperCase(), columnsNames.size());
        printColumnsNames(columnsNames);
        printTable(columnsNames, DB);
    }

    static void printReportsFromList(List<DelayReport> reportList){
        List<String> columnsNames = new ArrayList<>();
        columnsNames.add("reported_delay_minutes");
        columnsNames.add("timestamp");
        printColumnsNames(columnsNames);

        int num_of_reports = reportList.size();
        int num_of_columns = columnsNames.size();

        for(int i = 0; i < num_of_reports; i++){
            DelayReport report = reportList.get(i);
            printDelayReport(report, num_of_columns);
        }
        printNTimesC(column_width * num_of_columns, '-');
        print();
        print();
    }

    private static void printDelayReport(DelayReport report, int num_of_columns) {
            printValue(String.valueOf(report.getReportedDelay()), num_of_columns, 0);
            printValue(String.valueOf(report.getReportTimestamp()), num_of_columns, 1);
    }

    public static void main(String[] args) {
        try {
            DataBaseImpl db = new DataBaseImpl(); //todo DataBase db

            printReports("delays");
            printReports("doctors");
/*List<Double> coefficients = new ArrayList<>(); // = {a,b,c,d...} f(x) = a + b*X + c*X^2...
            coefficients.add(1.3);
            coefficients.add(0.001);
            coefficients.add(0.00002);
            db.addPolynomialReportsToDoctor(coefficients, doctorsName, date, 20);

 */

            String date = "2019-12-5";
            String doctorsName = "QuadraticDelayDoctor";

            //printReportsFromList(db.getDayReport(doctorsName, date).getReportList());
            db.getDayReport(doctorsName, date).printDayReport();
            //printReports("doctors");



            DayReport dayReport = db.getDayReport("Bar Gal","2019-11-29");
            DayReport dayReport2 = db.getDayReport("Bar Gal","2019-12-03");

            dayReport.printDayReport(); //todo test new abstract classes
            DayOfWeek day = dayReport.getDay();
            print("DayOfWeek: " + day);

            dayReport2.printDayReport(); //todo test new abstract classes
            DayOfWeek day2 = dayReport2.getDay();
            print("DayOfWeek: " + day2);


            DoctorReport dr = db.getDoctorReport("Itay Ventura");
            dr.printDoctorReport();
           // print(dr.getDoctorName() + " has " + dr.getInterval() + " minutes interval");

            //DataBaseUnitTest dbus = new DataBaseUnitTest(db);


        } catch(SQLException e) {
            System.out.println("DataBaseImpl main Exception: " + e);
        }
    }

}
