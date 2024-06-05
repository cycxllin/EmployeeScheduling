package com.example.f22lovelace.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class DefAvail implements Serializable {
    //fields
    private int id;
    private ArrayList<Integer> statusList;
    //list ordered Sun-Sat (indices 0-6)
    //status: 0 = not available, 1 = all day, 2 = morning, 3 = evening

    //constructor
    public DefAvail (int id, ArrayList<Integer> statusList){
        this.id = id;
        this.statusList = statusList;
    }

//setters
    public void setId(int id){this.id = id;}
    public void setStatus(ArrayList<Integer> statusList){this.statusList = statusList;}

//getters
    public int getId(){return this.id;}
    public ArrayList<Integer> getStatusList(){return this.statusList;}
}