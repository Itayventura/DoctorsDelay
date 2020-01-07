package handlers;

import repository.AbstractRepository;
import repository.Repository;

import java.util.List;

public abstract class Handler {
    private static final String TEXT_IN_BOLD = "\033[0;1m";
    private static final String TEXT_NO_IN_BOLD = "\033[0m";
    private static final int DEFAULT_ATTRIBUTES = 3;
    static final int COLUMN_WIDTH = 30;

    Repository repository;

    public static void printHeadline(String headline){
        printHeadline(headline,DEFAULT_ATTRIBUTES);
    }

    static void printHeadline(String headline, int num_of_columns){
        int line_length = COLUMN_WIDTH * num_of_columns;
        print();
        printNTimesC(line_length, '_');
        print();
        printNTimesC((line_length - headline.length())/2, '-');
        System.out.print(TEXT_IN_BOLD + headline + TEXT_NO_IN_BOLD);
        printNTimesC((line_length - headline.length() + 1)/2, '-');
        print();
        printNTimesC(line_length, '-');
        print();
        print();
    }

    static void printNTimesC(int N, Character C) {
        System.out.print(new String(new char[N]).replace("\0", C+""));
    }

    static void print(){
        print("");
    }

    private static void print(String str){
        System.out.println(str);
    }

    void printTable(String sqlSelectAllFromDB) {
        List<List<String>> records = AbstractRepository.getTable(sqlSelectAllFromDB);
        if(!records.isEmpty()){
            List<String> fields = records.get(0);
            printColumnsNames(fields);
            records.remove(0);
            for(List<String> record: records){
                for (int i = 0; i < fields.size(); i++) {
                    printValue(record.get(i), fields.size(), i);
                }
            }
        }
        else{
            printHeadline("table is empty!");
        }
    }

    static void printValue(String value, int size, int i) {
        System.out.print(value);
        if (i < size-1)
            printNTimesC(COLUMN_WIDTH - value.length(),' ');
        else{
            print();
        }
    }

    static void printColumnsNames(List<String> columnsNames){
        int num_of_columns = columnsNames.size();
        for (int i = 0; i < num_of_columns; i++) {
            System.out.print(TEXT_IN_BOLD + columnsNames.get(i) + TEXT_NO_IN_BOLD);
            if (i < num_of_columns-1) {
                String column_name = columnsNames.get(i);
                printNTimesC(COLUMN_WIDTH - column_name.length(), ' ');
            }
        }
        System.out.println();
    }

}
