package entities;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Entity {
    private Map<AttributeName , String> attributeMap;// dynamic map.
    private static final String TEXT_IN_BOLD = "\033[0;1m";
    private static final String TEXT_NO_IN_BOLD = "\033[0m";
    private static final int COLUMN_WIDTH = 30;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);


    public enum Type {
        USER,
        FEEDBACK,
        EXPERT;
    }


    enum AttributeName{
        createdAt,
        patientId,
        doctorType,
        startTime,
        endTime,
        interval
    }

    void setAttribute(AttributeName name, String value){
        if (attributeMap == null)
            attributeMap  = new HashMap<>();
        attributeMap.put(name, value);
    }

    String getAttribute(AttributeName name){
        if (attributeMap != null)
            return attributeMap.get(name);
        return null;
    }

    boolean isAttributeExist(AttributeName attributeName) {
        if (attributeMap != null)
            return getAttribute(attributeName) != null;
        return false;
    }

    String toString(List<String> title, List<String> info){
        int num_of_columns = title.size();
        String toString ="";
        for (int i = 0; i < num_of_columns; i++) {
            toString += (TEXT_IN_BOLD + title.get(i) + TEXT_NO_IN_BOLD);
            String title_name = title.get(i);
            toString += (new String(new char[COLUMN_WIDTH - title_name.length()]).replace("\0", " "));
            String info_string = info.get(i);
           toString += (info_string) +"\n";
            }
        return toString;
    }



}




