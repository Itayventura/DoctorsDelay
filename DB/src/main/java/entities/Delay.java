package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Delay extends Entity{

    private int reportedDelay;
    private Type reportType;

    /** this constructor is for internal use */
    private Delay(int delayInMinutes , Type reportType){
        this.reportedDelay = delayInMinutes;
        this.reportType = reportType;
    }

    /** this constructor serves getReports
     * and in general select from table 'doctorsName' (delays of doctor)
      */
    public Delay(int delayInMinutes, String createdAt, Type reportType) {
        this(delayInMinutes, reportType);
        setAttribute(AttributeName.createdAt, createdAt + "");
    }

    /** this constructor serves add report*/
    public Delay(int delayInMinutes, Type reportType, int userId) {
        this(delayInMinutes, reportType);
        setAttribute(AttributeName.patientId, userId+"");
    }

    /** this constructor serves dataHandler,
     * and in general insert into table 'doctorsName' (delays of doctor)
     */
    public Delay(int delayInMinutes, String createdAt, Type reportType, int userId){
        this(delayInMinutes,reportType,userId);
        setAttribute(AttributeName.createdAt, createdAt + "");
    }

    public int getReportedDelay() {
        return reportedDelay;
    }

    public LocalDateTime getReportTimestamp() {

        if (hasTimestamp()) {
            return LocalDateTime.parse(getAttribute(AttributeName.createdAt), formatter);
        }
        return null;
    }

    public boolean hasTimestamp(){
        return isAttributeExist(AttributeName.createdAt);
    }

    public int getPatientId(){
        if(isAttributeExist(AttributeName.patientId))
            return Integer.parseInt(getAttribute(AttributeName.patientId));
        return -1;
    }

    public Type getReportType() {
        return reportType;
    }

    public String toString(){
        List<String> title = new ArrayList<>();
        List<String> info = new ArrayList<>();
        title.add("reported delay:");
        info.add(getReportedDelay()+"");

        title.add("report type:");
        info.add(getReportType()+"");

        if (isAttributeExist(AttributeName.createdAt)) {
            title.add("created at: ");
            info.add(getReportTimestamp().format(formatter));
        }
        if (isAttributeExist(AttributeName.patientId)) {
                title.add("user's id: ");
                info.add(getPatientId()+"");
        }
        return toString(title,info);
    }

}

