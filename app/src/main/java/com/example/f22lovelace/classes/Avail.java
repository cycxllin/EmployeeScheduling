package com.example.f22lovelace.classes;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.DateFormatSymbols;

public class Avail implements Serializable {
    //fields
    private int id;
    private int month;
    private int year;
    private int day;
    private int status; //0 = not available, 1 = all day, 2 = morning, 3 = evening

    //constructor
    public Avail(int id, int month, int year, int day, int status){
        this.id = id;
        this.month = month;
        this.year = year;
        this.day = day;
        this.status = status;
    }

    //setters
    public void setId(int id) {this.id = id;}
    public void setMonth(int month) {this.month = month;}
    public void setYear(int year) {this.year = year;}
    public void setDay(int day) {this.day = day;}
    public void setStatus(int status) {this.status = status;}


    //getters
    public int getID() {return this.id;}
    public int getMonth() {return this.month;}
    public int getYear() {return this.year;}
    public int getDay() {return this.day;}
    public int getStatus() {return this.status;}


    public static ArrayList<Integer> makeDaysInts(ArrayList<String> daysStrings){
        //the days an employee is available is stored in the database as a CSV string.
        //this function changes the list of days from a string into a list of integers
        ArrayList<Integer> days = new ArrayList<>();

        //make list of strings into list of integers
        int i;
        for (i = 0; i<daysStrings.size(); i++){
            days.add(Integer.parseInt(daysStrings.get(i)));
        }

        return days;
    }

    public static String makeDaysString(ArrayList<Integer> days){
        //the days an employee is available is stored in the database as a CSV string.
        //this function changes the list of integers days into a CSV string
        StringBuilder daysString = new StringBuilder();

        int i;
        for (i=0; i<days.size(); i++){
            daysString.append(days.get(i));
            daysString.append(",");
        }

        //remove the last , from the string
        daysString = new StringBuilder(daysString.substring(0, daysString.length() - 1));

        return daysString.toString();
    }

    public String getMonth(int month){
        return new DateFormatSymbols().getMonths()[month-1];
    }
}
