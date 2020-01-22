package repository;

import java.util.List;

public interface Repository<Entity> {


    void insert(String sqlQuery);
    void delete(String sqlQuery);
    void update(String sqlQuery);
    List<Entity> select(String sqlQuery);

}

