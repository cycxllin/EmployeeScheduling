package com.example.f22lovelace.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class AvailByStatus implements Serializable {

    //used to return multiple lists from dbHandler getDefaultAvailByMonth & getAvailByMonth

    //fields
    private ArrayList<Integer> allDayAvail = new ArrayList<>();
    private ArrayList<Integer> mornAvail = new ArrayList<>();
    private ArrayList<Integer> eveAvail = new ArrayList<>();
    private ArrayList<Integer> notAvail = new ArrayList<>();

    //constructor
    public AvailByStatus(ArrayList<Integer> allDayAvail, ArrayList<Integer> mornAvail,
                         ArrayList<Integer> eveAvail, ArrayList<Integer> notAvail) {
        this.allDayAvail = allDayAvail;
        this.mornAvail = mornAvail;
        this.eveAvail = eveAvail;
        this.notAvail = notAvail;
    }

    //setters
    public void setAllDayAvail(ArrayList<Integer> allDayAvail) {this.allDayAvail = allDayAvail;}
    public void setMornAvail(ArrayList<Integer> mornAvail) {this.mornAvail = mornAvail;}
    public void setEveyAvail(ArrayList<Integer> eveAvail) {this.eveAvail = eveAvail;}
    public void setNotAvail(ArrayList<Integer> notAvail) {this.notAvail = notAvail;}

    //getters
    public ArrayList<Integer> getAllDayAvail() {return this.allDayAvail;}
    public ArrayList<Integer> getMornAvail() {return this.mornAvail;}
    public ArrayList<Integer> getEveAvail() {return this.eveAvail;}
    public ArrayList<Integer> getNotAvail() {return this.notAvail;}
}
