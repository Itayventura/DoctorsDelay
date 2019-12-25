package entities;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends Entity{
    private String name;

    //todo add more doctorTypes
    public enum DoctorType {
        FAMILY
    }

    /** this constructor is used whenever selection from "doctors" is made */
    public Doctor (String type, String name, LocalTime startTime, LocalTime endTime, int interval) {
        this.name = name;
        setAttribute(AttributeName.doctorType, type);
        setAttribute(AttributeName.startTime, startTime + "");
        setAttribute(AttributeName.endTime, endTime + "");
        setAttribute(AttributeName.interval, interval + "");

    }

    /** this constructor serves DataHandler */
    public Doctor(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public DoctorType getType() {
        if (isAttributeExist(AttributeName.doctorType))
            return toDoctorType(getAttribute(AttributeName.doctorType));
        return null;
    }

    //todo add more doctorTypes
    private DoctorType toDoctorType(String type){
        if (type.equals("family")) return DoctorType.FAMILY;
        return null;
    }

    public LocalTime getStartTime() {
        if (isAttributeExist(AttributeName.startTime))
            return LocalTime.parse(getAttribute(AttributeName.startTime));
        return null;
    }

    public LocalTime getEndTime() {
        if (isAttributeExist(AttributeName.endTime))
            return LocalTime.parse(getAttribute(AttributeName.endTime));
        return null;
    }

    public int getInterval() {
        if (isAttributeExist(AttributeName.interval))
            return Integer.parseInt(getAttribute(AttributeName.interval));
        return -1;
    }

    @Override
    public String toString(){
        List<String> title = new ArrayList<>();
        List<String> info = new ArrayList<>();

        title.add("doctor name:");
        info.add(getName());

        if(!isCompactDoctor())
        {
            title.add("start time:");
            info.add(getStartTime() + "");
            title.add("end time:");
            info.add(getEndTime() + "");
            title.add("doctor type: ");
            info.add((getType()+"").toLowerCase());
            title.add("interval length: ");
            info.add(getInterval()+"");

        }
        return toString(title,info);
    }

    private boolean isCompactDoctor() {
        return !isAttributeExist(AttributeName.doctorType);
    }
}
