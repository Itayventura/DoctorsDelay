package repository;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface Repository<Entity> {

    SimpleDateFormat sdf = new SimpleDateFormat(entities.Entity.TIMESTAMP_FORMAT);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(entities.Entity.TIMESTAMP_FORMAT);

    void insert(String sqlQuery);
    void delete(String sqlQuery);
    void update(String sqlQuery);
    List<Entity> select(String sqlQuery);

}

