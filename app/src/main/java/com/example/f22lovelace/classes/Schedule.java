package com.example.f22lovelace.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Schedule implements Serializable {
    //fields
    private int day;
    private int month;
    private int year;

    private int shift1; //if weekday this is morning shift1; weekend = AllDay1
    private int shift2; //if weekday this is morning shift2; weekend = AllDay2
    private int mornBusy;
    private int eve1;
    private int eve2;
    private int eveBusy;

    private int busy; //0 if normal day, 1 if busy day
    private int status; //1 if done, 2 if in progress

    //constructors

    /**
     * Use null for any fields not needed
     */
    public Schedule(int day, int month, int year, int shift1, int shift2, int mornBusy, int eve1,
                    int eve2, int eveBusy, int busy, int status) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.shift1 = shift1;
        this.shift2 = shift2;
        this.mornBusy = mornBusy;
        this.eve1 = eve1;
        this.eve2 = eve2;
        this.eveBusy = eveBusy;
        this.busy = busy;
        this.status = status;
    }

    public Schedule(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.shift1 = -1;
        this.shift2 = -1;
        this.mornBusy = -1;
        this.eve1 = -1;
        this.eve2 = -1;
        this.eveBusy = -1;
        this.busy = 0;
        this.status = 2;
    }


    //setters
    public void setDay(int day) {this.day = day;}
    public void setMonth(int month) {this.month = month;}
    public void setYear(int year) {this.year = year;}
    public void setShift1(int shift1) {this.shift1 = shift1;}
    public void setShift2(int shift2) {this.shift2 = shift2;}
    public void setMornBusy(int mornBusy) {this.mornBusy = mornBusy;}
    public void setEve1(int eve1) {this.eve1 = eve1;}
    public void setEve2(int eve2) {this.eve2 = eve2;}
    public void setEveBusy(int eveBusy) {this.eveBusy = eveBusy;}
    public void setBusy(int busy) {this.busy = busy;}
    public void setStatus(int status){this.status = status;}

    //getters
    public int getDay(){return this.day;}
    public int getMonth(){return this.month;}
    public int getYear(){return this.year;}
    public int getShift1(){return this.shift1;}
    public int getShift2(){return this.shift2;}
    public int getMornBusy(){return this.mornBusy;}
    public int getEve1(){return this.eve1;}
    public int getEve2(){return this.eve2;}
    public int getEveBusy(){return this.eveBusy;}
    public int getBusy(){return this.busy;}
    public int getStatus(){return this.status;}

    public boolean equals(Schedule sched){
        return (sched.getShift1() == this.getShift1())
                && (sched.getShift2() == this.getShift2()) && (sched.getMornBusy() == this.getMornBusy())
                && (sched.getEve1() == this.getEve1()) && (sched.getEve2() == this.getEve2())
                && (sched.getEveBusy() == this.getEveBusy()) && (sched.getBusy() == this.getBusy())
                && (sched.getStatus() == this.getStatus());
    }

    /** Remove given employee id from schedule object and set status to In Progress
     */
    public void removeEmp(int id){
        if (this.shift1 == id) {this.shift1 = -1; this.setStatus(2);}
        if (this.shift2 == id) {this.shift2 = -1; this.setStatus(2);}
        if (this.mornBusy == id) {this.mornBusy = -1; this.setStatus(2);}
        if (this.eve1 == id) {this.eve1 = -1; this.setStatus(2);}
        if (this.eve2 == id) {this.eve2 = -1; this.setStatus(2);}
        if (this.eveBusy == id) {this.eveBusy = -1; this.setStatus(2);}
    }

    @NonNull
    @Override
    public String toString(){

        return this.getYear() + "/" + this.getMonth() + "/" + this.getDay() + " \n  " + this.getShift1()
                + " | " + this.getShift2() + " | " + this.getMornBusy() + " | " + this.getEve1()
                + " | " + this.getEve2() + " | " + this.getEveBusy();
    }

}
