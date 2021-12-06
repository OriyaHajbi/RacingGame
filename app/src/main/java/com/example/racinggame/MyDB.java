package com.example.racinggame;

import java.util.ArrayList;

public class MyDB {

    private ArrayList<Record> records = new ArrayList<Record>();
    private static final int MAX_RECORDS = 10;
    private static int minRecord=0;

    public MyDB() {
    }

    public ArrayList<Record> getRecords() {
    return records;
    }

    public MyDB setRecords(ArrayList<Record> records) {
        this.records = records;
        return this;
    }
    public void addRecord(Record record){
        this.records.add(record);
        sortByScore();
        if (this.records.size() > MAX_RECORDS)
            this.records.remove(MAX_RECORDS);

    }

    private void sortByScore() {
        records.sort((o1, o2) -> o1.getScore()-o2.getScore());
    }


}
