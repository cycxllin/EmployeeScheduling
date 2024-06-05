package com.example.f22lovelace.classes;//class used to manage employee objects

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Serializable, Comparable<Employee> {
    //fields
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private int open; //1 is trained, 0 is not
    private int close; //1 is trained, 0 is not
    private int active; //1 is active, 0 is not; for deletion on employee in database

    //constructors
    public Employee(int id, String firstName,String middleName, String lastName, String email,
                    String phone,int open, int close, int active){
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.open = open;
        this.close = close;
        this.active = active;
    }

    //setters
    public void setID (int id) {this.id = id;}
    public void setFirstName (String firstName){
        this.firstName = firstName;
    }
    public void setMiddleName (String middleName){
        this.middleName = middleName;
    }
    public void setLastName (String lastName){
        this.lastName = lastName;
    }
    public void setEmail (String email){
        this.email = email;
    }
    public void setPhone (String phone){
        this.phone = phone;
    }
    public void setOpen (int open){this.open = open;}
    public void setClose (int close){this.close = close;}
    public void setActive (int active) {this.active = active;}

    //getters
    public int getID () {return this.id;}
    public String getFullName() {
        if (this.middleName.isEmpty()){
            return this.firstName + ' ' + this.lastName;
        }
        return this.firstName + ' ' + this.middleName +  ' ' + this.lastName;
    }
    public String getFirstName() {return this.firstName;}
    public String getMiddleName() {return this.middleName;}
    public String getLastName() {return this.lastName;}
    public String getEmail() {return this.email;}
    public String getPhone(){return this.phone;}
    public int getOpen(){return this.open;}
    public int getClose(){return this.close;}
    public int getActive(){return this.active;}

    @NonNull
    @Override //used to print employee full name in spinner for scheduling
    public String toString() {
        if (this.getID()<0) {
            return "ID #: " + this.getFullName();
        }

        if (this.getOpen() == 1 && this.getClose() == 1)
            return this.getID() + ": " + this.getFullName() + " (O/C)";
        else if (this.getOpen() == 1 && this.getClose() == 0)
            return this.getID() + ": " + this.getFullName() + " (O)";
        else if (this.getOpen() == 0 && this.getClose() == 1)
            return this.getID() + ": " + this.getFullName() + " (C)";

        //here is not trained for either open or close
        return this.getID() + ": " + this.getFullName() ;
    }


    public boolean equals(Employee employee){
        return (Objects.equals(employee.getFullName(), this.getFullName()))
                && (Objects.equals(employee.getEmail(), this.getEmail()))
                && (Objects.equals(employee.getPhone(), this.getPhone()))
                && (employee.getOpen() == this.getOpen())
                && (employee.getClose() == this.getClose());
    }

    @Override
    public int compareTo(Employee emp) {
        return this.getFullName().compareTo(emp.getFullName());
    }

    //formatting
    /*
    public static String stripPhone(String ph){
        String strippedPhone = "";

        int i;
        for (i = 0; i< ph.length(); i++){
            char ch = ph.charAt(i);
            if (Character.isDigit(ch)){
                strippedPhone += Character.toString(ch);
            }
        }
        return strippedPhone;
    }

    public static String formatPhone(String ph){
        String formPhone = "(";
        int i;
        char ch;

        for (i = 0; i<3; i++){
            ch = ph.charAt(i);
            formPhone += Character.toString(ch);
        }

        formPhone += ") ";

        for (; i<6; i++){
            ch = ph.charAt(i);
            formPhone += Character.toString(ch);
        }

        formPhone += "-";

        for (; i<ph.length(); i++){
            ch = ph.charAt(i);
            formPhone += Character.toString(ch);
        }

        return formPhone;
    }*/

}
