package com.example.f22lovelace.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleMonthByStatus implements Serializable {

    //used to return multiple lists from dbHandler getDefaultAvailByMonth & getAvailByMonth

    //fields
    private ArrayList<Integer> done = new ArrayList<>();
    private ArrayList<Integer> inProgress = new ArrayList<>();

    //constructor
    public ScheduleMonthByStatus(ArrayList<Integer> done, ArrayList<Integer> inProgress) {
        this.done = done;
        this.inProgress = inProgress;
    }

    //setters
    public void setDone(ArrayList<Integer> done) {this.done = done;}
    public void setInProgress(ArrayList<Integer> inProgress) {this.inProgress = inProgress;}

    //getters
    public ArrayList<Integer> getDone() {return this.done;}
    public ArrayList<Integer> getInProgress() {return this.inProgress;}
}
