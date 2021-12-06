package com.example.racinggame;

import java.util.ArrayList;

public class MyDB {

    private static ArrayList<Record> records = new ArrayList<Record>();
    private final int MAX_RECORDS = 10;
    private static int minRecord=0;

    public MyDB() {
    }

    public static ArrayList<Record> getRecords() {
    return records;
    }

    public MyDB setRecords(ArrayList<Record> records) {
        this.records = records;
        return this;
    }
    public void checkRecord(Record record){
        if (minRecord < record.getScore() || records.size()<MAX_RECORDS){
            this.records.add(record);
            sortByScore(this.records);
            minRecord = record.getScore();
        }
        if (this.records.size() > MAX_RECORDS)
            this.records.remove(MAX_RECORDS);

    }

    private void sortByScore(ArrayList<Record> records) {
        int n = records.size();
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (records.get(j).getScore() < records.get(j+1).getScore())
                {
                    Record temp = records.get(j);
                    records.set(j , records.get(j+1));
                    records.set(j+1 , temp);
                }
    }


}
