package handlers;

import entities.Delay;
import repository.DelaysRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DelaysHandler extends Handler {
    private static final int DELAYS_ATTRIBUTES = 6;

    private static final String sqlSelectFrom = "Select reported_delay_minutes, report_type, timestamp from ";
    private static final String sqlSelectAllFrom = "Select * from  ";
    private static final String sqlDeleteFrom = "delete from ";
    private static final String sqlInsertInto = "Insert INTO ";

    private final String sqlSelect;
    private final String sqlSelectAll;
    private final String sqlDelete;
    private final String sqlInsertWithTimestamp;
    private final String sqlInsertWithoutTimestamp;

    private String doctorsName;

    public DelaysHandler(String doctor_name) {
        repository = new DelaysRepository();

        String doctorsNameFormatted = String.format("`%s`", doctor_name.replace("`", "``"));
        this.doctorsName = doctorsNameFormatted;

        sqlSelect = sqlSelectFrom + doctorsName;
        sqlSelectAll = sqlSelectAllFrom + doctorsName + " ORDER BY timestamp ";
        sqlDelete = sqlDeleteFrom + doctorsName;
        sqlInsertWithTimestamp = sqlInsertInto + doctorsName + " (reported_delay_minutes, report_type, timestamp) VALUES";
        sqlInsertWithoutTimestamp = sqlInsertInto + doctorsName + " (reported_delay_minutes,report_type) VALUES";
    }

    public List<Delay> getReports(LocalDateTime startTime, LocalDateTime endTime){
        String sqlReportsList = sqlSelect +
                "               WHERE TIMESTAMP >= '" + Timestamp.valueOf(startTime) + "' AND " +
                "                     TIMESTAMP <= '" + Timestamp.valueOf(endTime) + "' " +
                "               ORDER BY timestamp";
        return repository.select(sqlReportsList);

    }

    public void addReport(Delay delay) {
        String sqlInsertReport;
        String sqlValue = " ('" + delay.getReportedDelay() + "','" + (delay.getReportType() + "").toLowerCase();
        if (!delay.hasTimestamp()) {
            sqlInsertReport = sqlInsertWithoutTimestamp + sqlValue + "')";
        } else {
            sqlInsertReport = sqlInsertWithTimestamp + sqlValue + "','" +  Timestamp.valueOf(delay.getReportTimestamp()) + "')";
        }
        repository.insert(sqlInsertReport);
    }

    public void addReportList(List<Delay> delayList) {
        if (!delayList.isEmpty()) {
            Delay delay = delayList.get(0);
            String sqlInsertReports;

            if (!delay.hasTimestamp()) {
                sqlInsertReports = sqlInsertWithoutTimestamp;
                for (int i = 0; i < delayList.size(); i++) {
                    delay = delayList.get(i);
                    String sqlValues = "('" + delay.getReportedDelay() + "','" +
                            (delay.getReportType() + "").toLowerCase() + "')";
                    sqlInsertReports += sqlValues;
                    if (i != delayList.size() - 1)
                        sqlInsertReports += ",";
                }
            } else {
                sqlInsertReports = sqlInsertWithTimestamp;
                for (int i = 0; i < delayList.size(); i++) {
                    delay = delayList.get(i);
                    String sqlValues = "('" + delay.getReportedDelay() + "','" +
                            (delay.getReportType() + "").toLowerCase() + "','" +
                            Timestamp.valueOf(delay.getReportTimestamp()) + "')";
                    sqlInsertReports += sqlValues;
                    if (i != delayList.size() - 1)
                        sqlInsertReports += ",";
                }
            }
            repository.insert(sqlInsertReports);
        }
    }


    public void deleteReports(LocalDateTime start, LocalDateTime end) {
        String sqlDeleteReports = sqlDelete +
                " WHERE TIMESTAMP >= '" + Timestamp.valueOf(start) + "' AND " +
                "       TIMESTAMP <= '" + Timestamp.valueOf(end) + "' ";
        repository.delete(sqlDeleteReports);
    }

    public static void printReportsFromList(List<Delay> reportList){
        List<String> columnsNames = new ArrayList<>();
        columnsNames.add("reported_delay_minutes");
        columnsNames.add("report_type");
        columnsNames.add("timestamp");
        printColumnsNames(columnsNames);

        int num_of_columns = columnsNames.size();
        for (Delay delay: reportList){
            printDelay(delay, num_of_columns);
        }
        printNTimesC(COLUMN_WIDTH * num_of_columns, '-');
        print();
        print();
    }

    private static void printDelay(Delay delay, int num_of_columns) {
        printValue(String.valueOf(delay.getReportedDelay()), num_of_columns, 0);
        printValue(delay.getReportType()+"", num_of_columns, 1);
        printValue(delay.getReportTimestamp().format(Delay.formatter), num_of_columns, 2);
    }

    public void printTable(){
        printHeadline(doctorsName.toUpperCase(), DELAYS_ATTRIBUTES);
        super.printTable(sqlSelectAll);
    }

    public List<Delay> getDelays() {
        return repository.select(sqlSelectAll);
    }


}

