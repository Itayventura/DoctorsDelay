package entities;

import java.util.ArrayList;
import java.util.List;

public class Patient  extends Entity {
    private String personalId;
    private String password;
    private int score;
    private Type type;

    public Patient (String personalId, String password, int score, Type type) {
        this.personalId = personalId;
        this.password = password;
        this.score = score;
        this.type = type;
    }


    public String getPersonalId() {
        return personalId;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public Type getReportType(){ return  type; }

    private void setScore(int score) {
        this.score = score;
    }

    public void addScore(int addedScore){
        setScore(score + addedScore);
    }

    public String toString(){

        List<String> title = new ArrayList<>();
        List<String> info = new ArrayList<>();
        title.add("personal ID:");
        title.add("password:");
        title.add("score:");
        title.add("type:");
        info.add(getPersonalId());
        info.add(getPassword());
        info.add(getScore()+"");
        info.add(getReportType()+"");
        return toString(title,info);
    }
}
