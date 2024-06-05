package com.example.f22lovelace.classes;//class used to manage database

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    //database variables
    //region
    private static final int dbVersion = 1;
    private static final String dbName = "restaurantDB";

    private static final String employeeTable = "employee";
    public static final String _id = "employeeID";
    public static final String fName = "firstName";
    public static final String mName = "middleName";
    public static final String lName = "lastName";
    public static final String email = "email";
    public static final String phone = "phone";
    public static final String open = "open"; //0 not trained, 1 trained
    public static final String close = "close"; //0 not trained, 1 trained
    public static final String active = "active"; //0 inactive, 1 active

    public static final String availTable = "availChanges";
    public static final String month = "month";
    public static final String year = "year";
    public static final String day = "day";
    public static final String availStatus = "availStatus";

    public static final String defAvailTable = "defAvail";
    //for below: 0 = not available, 1 = all day, 2 = morning, 3 = evening
    public static final String sun = "sun";
    public static final String mon = "mon";
    public static final String tue = "tue";
    public static final String wed = "wed";
    public static final String thu = "thu";
    public static final String fri = "fri";
    public static final String sat = "sat";

    public static final String sched = "schedule";
    public static final String shift1 = "shift1";
    public static final String shift2 = "shift2";
    public static final String mornBusy = "mornBusy";
    public static final String eve1 = "eve1";
    public static final String eve2 = "eve2";
    public static final String eveBusy = "eveBusy";
    public static final String busy = "busy";
    public static final String status = "status"; // 1 = done, 2 = inProgress

    private final List<String> morning = Arrays.asList("shift1", "shift2", "morn3");
    private final List<String> evening = Arrays.asList("eve1", "eve2", "eve3");


    //endregion

    //constructor
    public DBHandler(Context cxt) {super(cxt, dbName, null, dbVersion);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EMPLOYEE_TABLE = "CREATE TABLE IF NOT EXISTS " + employeeTable + "(" + _id + " " +
                "INTEGER PRIMARY KEY AUTOINCREMENT," + fName + " TEXT," + mName + " TEXT," + lName +
                " TEXT," + email + " TEXT," + phone + " TEXT," + open + " INTEGER," + close + " INTEGER, " +
                active + " INTEGER DEFAULT 1)";
        db.execSQL(CREATE_EMPLOYEE_TABLE);

        String CREATE_AVAIL_TABLE = "CREATE TABLE IF NOT EXISTS " + availTable + "(" + _id + " INTEGER REFERENCES " + employeeTable + ","
                + month + " INTEGER," + year + " INTEGER," + day + " INTEGER," + availStatus + " INTEGER, "
                + " PRIMARY KEY (" + _id + "," + month + "," + year + "," + day + "))";
        db.execSQL(CREATE_AVAIL_TABLE);

        String CREATE_DEF_AVAIL_TABLE = "CREATE TABLE IF NOT EXISTS " + defAvailTable + "(" + _id + " INTEGER REFERENCES " + employeeTable + ","
                + sun + " INTEGER DEFAULT 1," + mon + " INTEGER DEFAULT 1," + tue + " INTEGER DEFAULT 1,"
                + wed + " INTEGER DEFAULT 1," + thu + " INTEGER DEFAULT 1," + fri + " INTEGER DEFAULT 1,"
                + sat + " INTEGER DEFAULT 1, PRIMARY KEY (" + _id + "))";
        db.execSQL(CREATE_DEF_AVAIL_TABLE);

        String CREATE_SCHED_TABLE = "CREATE TABLE IF NOT EXISTS " + sched + "(" + day + " INTEGER,"
                + month + " INTEGER DEFAULT -1," + year + " INTEGER DEFAULT -1," + shift1 +
                " INTEGER DEFAULT -1," + shift2 + " INTEGER DEFAULT -1," + mornBusy + " INTEGER DEFAULT -1,"
                + eve1 + " INTEGER  DEFAULT -1," + eve2 + " INTEGER DEFAULT -1," + eveBusy + " INTEGER DEFAULT -1,"
                + busy + " INTEGER DEFAULT 0, " + status + " INTEGER DEFAULT 2, PRIMARY KEY (" + day
                + "," + month + "," + year + "))";
        db.execSQL(CREATE_SCHED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + employeeTable);
            db.execSQL("DROP TABLE IF EXISTS " + availTable);
            db.execSQL("DROP TABLE IF EXISTS " + defAvailTable);
            db.execSQL("DROP TABLE IF EXISTS " + sched);
            onCreate(db);
        }
    }

    //Employee methods
    //region
    public void addEmp(Employee employee){

        //create values; employee ID is autogenerated
        ContentValues values = new ContentValues();
        values.put(fName, employee.getFirstName());
        values.put(mName, employee.getMiddleName());
        values.put(lName, employee.getLastName());
        values.put(email, employee.getEmail());
        values.put(phone, employee.getPhone());
        values.put(open, employee.getOpen());
        values.put(close, employee.getClose());
        values.put(active, employee.getActive());

        //open database, insert values, close database
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(employeeTable, null, values);
        db.close();

        //get employee ID after it is generated in DB, then make default avail with default values (all 1)
        Employee forEmpId = getEmpIdforAddEmpOnly(employee.getFirstName(), employee.getEmail(), employee.getPhone());
        addDefAvail(forEmpId.getID());
        setDefNotAvail(forEmpId.getID());
    }

    //check for dups based on name. Include inactive, make popup "Do you mean?
    public ArrayList<Employee> checkDupEmp(Employee employee) {
        ArrayList<Employee> dups = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from employee where " + fName + " = ? COLLATE NOCASE AND "
                + lName + " = ? COLLATE NOCASE", new String[]{employee.getFirstName(), employee.getLastName()});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
            String first = cursor.getString(cursor.getColumnIndexOrThrow(fName));
            String mid = cursor.getString(cursor.getColumnIndexOrThrow(mName));
            String last = cursor.getString(cursor.getColumnIndexOrThrow(lName));
            String em = cursor.getString(cursor.getColumnIndexOrThrow(email));
            String ph = cursor.getString(cursor.getColumnIndexOrThrow(phone));
            int op = cursor.getInt(cursor.getColumnIndexOrThrow(open));
            int cl = cursor.getInt(cursor.getColumnIndexOrThrow(close));
            int ac = cursor.getInt(cursor.getColumnIndexOrThrow(active));

            Employee emp = new Employee(id, first, mid, last, em, ph, op, cl, ac);
            dups.add(emp);
        }
        return dups;
    }
    /**
     Gets all employees and stores them in a list of employees
     Returns empty list when database has no employees
     */
    public ArrayList<Employee> listAllEmps(){

        ArrayList<Employee> allEmps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from employee", null );

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
            String first = cursor.getString(cursor.getColumnIndexOrThrow(fName));
            String mid = cursor.getString(cursor.getColumnIndexOrThrow(mName));
            String last = cursor.getString(cursor.getColumnIndexOrThrow(lName));
            String em = cursor.getString(cursor.getColumnIndexOrThrow(email));
            String ph = cursor.getString(cursor.getColumnIndexOrThrow(phone));
            int op = cursor.getInt(cursor.getColumnIndexOrThrow(open));
            int cl = cursor.getInt(cursor.getColumnIndexOrThrow(close));
            int ac = cursor.getInt(cursor.getColumnIndexOrThrow(active));

            Employee emp = new Employee(id, first, mid, last, em, ph, op, cl,ac);
            allEmps.add(emp);
        }

        db.close();
        cursor.close();
        return allEmps;
    }

    /**
     Gets all employees and stores them in a list of employees
     Returns empty list when database has no employees or all employees are inactive
     */
    public ArrayList<Employee> listActiveEmps(){
        ArrayList<Employee> allEmps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from employee WHERE active = 1", null );

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
            String first = cursor.getString(cursor.getColumnIndexOrThrow(fName));
            String mid = cursor.getString(cursor.getColumnIndexOrThrow(mName));
            String last = cursor.getString(cursor.getColumnIndexOrThrow(lName));
            String em = cursor.getString(cursor.getColumnIndexOrThrow(email));
            String ph = cursor.getString(cursor.getColumnIndexOrThrow(phone));
            int op = cursor.getInt(cursor.getColumnIndexOrThrow(open));
            int cl = cursor.getInt(cursor.getColumnIndexOrThrow(close));
            int ac = cursor.getInt(cursor.getColumnIndexOrThrow(active));

            Employee emp = new Employee(id, first, mid, last, em, ph, op, cl,ac);
            allEmps.add(emp);
        }

        db.close();
        cursor.close();
        return allEmps;
    }

    /**
     Gets employee ID based on input in add employee
     Returns null if there is no entry in the database
     */
    private Employee getEmpIdforAddEmpOnly(String fName, String email, String phone){
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery( "select * from employee where firstName = ? AND email = ? AND phone = ?"
            , new String[]{fName,email,phone});
        if (cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_id));
            String mid = cursor.getString(cursor.getColumnIndexOrThrow(mName));
            String last = cursor.getString(cursor.getColumnIndexOrThrow(lName));
            int op = cursor.getInt(cursor.getColumnIndexOrThrow(open));
            int cl = cursor.getInt(cursor.getColumnIndexOrThrow(close));
            int ac = cursor.getInt(cursor.getColumnIndexOrThrow(active));

        Employee emp = new Employee(id, fName, mid, last, email, phone, op, cl,ac);
        cursor.close();
        db.close();

        return emp;
    }
        else {return null;}
}
    /**
     Gets employee based on id
     Returns null if there is no entry in the database
     */
    public Employee getEmpById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from employee where " + _id + " = " + id, null);
        if (cursor.moveToFirst()) {

            String first = cursor.getString(cursor.getColumnIndexOrThrow(fName));
            String mid = cursor.getString(cursor.getColumnIndexOrThrow(mName));
            String last = cursor.getString(cursor.getColumnIndexOrThrow(lName));
            String em = cursor.getString(cursor.getColumnIndexOrThrow(email));
            String ph = cursor.getString(cursor.getColumnIndexOrThrow(phone));
            int op = cursor.getInt(cursor.getColumnIndexOrThrow(open));
            int cl = cursor.getInt(cursor.getColumnIndexOrThrow(close));
            int ac = cursor.getInt(cursor.getColumnIndexOrThrow(active));

            Employee emp = new Employee(id, first, mid, last, em, ph, op, cl,ac);
            cursor.close();
            db.close();

            return emp;
        }
        else {return null;}
    }

    /** changes status from active to inactive (1 to 0)
     */
    public void removeEmp(int id){
        ContentValues values = new ContentValues();
        values.put(active, 0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(employeeTable, values, _id + " = ?", new String[]{String.valueOf(id)});

        //sets default avail to 0
        setDefNotAvail(id);

        db.close();
    }

    public void editEmp(Employee employee){
        //create values
        int id = employee.getID();
        ContentValues values = new ContentValues();
        values.put(fName, employee.getFirstName());
        values.put(mName, employee.getMiddleName());
        values.put(lName, employee.getLastName());
        values.put(email, employee.getEmail());
        values.put(phone, employee.getPhone());
        values.put(open, employee.getOpen());
        values.put(close, employee.getClose());
        values.put(active, employee.getActive());

        //open database, update values, close database
        SQLiteDatabase db = this.getWritableDatabase();
        db.update("employee", values, _id + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    //endregion

    //Avail methods
    //region

    public void addEditAvail(Avail empAvail){
        ContentValues values = new ContentValues();
        values.put(_id, empAvail.getID());
        values.put(month, empAvail.getMonth());
        values.put(year, empAvail.getYear());
        values.put(day, empAvail.getDay());
        values.put(availStatus, empAvail.getStatus());

        SQLiteDatabase db = this.getWritableDatabase();
        db.replace(availTable, null, values);

        db.close();
    }

    public Avail getAvailbyDayByEmp(int id, int mon, int yr, int d){
        //returns null if there is no entry in the database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select " + availStatus + " from " + availTable + " where " + _id + " = " + id + " and "
                + month + " = " + mon + " and " + year + " = " + yr + " and " + day + " = " + d, null);
        //if there is data
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(availStatus));
            Avail empAvail = new Avail(id, mon, yr, d, status);

            cursor.close();
            db.close();

            return empAvail;
        }
        else {return null;}
    }

    public Avail getAvailbyDayByEmp(Employee employee, LocalDate date){
        //returns null if there is no entry in the database
        int id = employee.getID();
        int d = date.getDayOfMonth();
        int mon = date.getMonthValue();
        int yr = date.getYear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select " + availStatus + " from " + availTable + " where " + _id + " = " + id + " and "
                + month + " = " + mon + " and " + year + " = " + yr + " and " + day + " = " + d, null);
        //if there is data
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(availStatus));
            Avail empAvail = new Avail(id, mon, yr, d, status);

            cursor.close();
            db.close();

            return empAvail;
        }
        else {return null;}
    }

    public void removeEmpAvail(Avail avail) {
        int id = avail.getID();
        int mon = avail.getMonth();
        int yr = avail.getYear();
        int d = avail.getMonth();

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + availTable + " where " + _id + " = " + id + " and " + month
                + " = " + mon + " and " + year  + " = " + yr + " and " + day  + " = " + d);
        db.close();
    }
    //endregion

    //DefAvail methods
    //region
    public void addDefAvail(int id){
        ContentValues values = new ContentValues();
        values.put(_id, id);

        //status are all default values
        SQLiteDatabase db = this.getWritableDatabase();
        db.replace(defAvailTable, null, values);
        db.close();
    }

    public void editDefAvail(int id, ArrayList<Integer> status){
        ContentValues values = new ContentValues();
        values.put(_id, id);

        //put each status into db
        values.put(sun, status.get(0));
        values.put(mon, status.get(1));
        values.put(tue, status.get(2));
        values.put(wed, status.get(3));
        values.put(thu, status.get(4));
        values.put(fri, status.get(5));
        values.put(sat, status.get(6));


        SQLiteDatabase db = this.getWritableDatabase();
        db.replace(defAvailTable, null, values);
        db.close();
    }

    public ArrayList<Integer> defAvailStatusList (int id){
        //list ordered Sun-Sat (indices 0-6)
        //status: 0 = not available, 1 = all day, 2 = morning, 3 = evening
        ArrayList<Integer> defAvailStatusList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from defAvail where " + _id + " = " + id, null);

        if (cursor.moveToFirst()) {
            defAvailStatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(sun)));
            defAvailStatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(mon)));
            defAvailStatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(tue)));
            defAvailStatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(wed)));
            defAvailStatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(thu)));
            defAvailStatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(fri)));
            defAvailStatusList.add(cursor.getInt(cursor.getColumnIndexOrThrow(sat)));

            cursor.close();
            db.close();

            return defAvailStatusList;
        }
        return null;
    }

    /** For use by DBhandler Only*/
    public AvailByStatus getEmpDefaultAvailByMonth(int id, LocalDate date){
        LocalDate tempDay;

        int mon = date.getMonthValue();
        int year = date.getYear();

        ArrayList<Integer> allDayAvail = new ArrayList<>();
        ArrayList<Integer> mornAvail = new ArrayList<>();
        ArrayList<Integer> eveAvail = new ArrayList<>();
        ArrayList<Integer> notAvail = new ArrayList<>();
        int status;

        int i;
        //for every day of the month, find day of week, find status, add to appropriate list
        for (i = 1; i<(date.lengthOfMonth()+1); i++){
            tempDay = LocalDate.of(year, mon, i);
            status = statusFromDayOfWeek(tempDay, defAvailStatusList(id));

            switch (status){
                case 0: //not avail
                    notAvail.add(i);
                    break;
                case 1: //all day
                    allDayAvail.add(i);
                    break;
                case 2: //morn only
                    mornAvail.add(i);
                    break;
                case 3: //all day
                    eveAvail.add(i);
                    break;
            }
        }
        return new AvailByStatus(allDayAvail, mornAvail, eveAvail, notAvail);
    }

    public void setDefNotAvail(int id) {
        //sets all default to not avail
        ContentValues defValues = new ContentValues();
        defValues.put(mon, 0);
        defValues.put(tue, 0);
        defValues.put(wed, 0);
        defValues.put(thu, 0);
        defValues.put(fri, 0);
        defValues.put(sat, 0);
        defValues.put(sun, 0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(defAvailTable, defValues, _id + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    //endregion

    /** For use by DBhandler Only*/
    public AvailByStatus getEmpChangedAvailbyMonth (int id, LocalDate date){
        int mon = date.getMonthValue();
        int yr = date.getYear();

        ArrayList<Integer> allDayAvail = new ArrayList<>();
        ArrayList<Integer> mornAvail = new ArrayList<>();
        ArrayList<Integer> eveAvail = new ArrayList<>();
        ArrayList<Integer> notAvail = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from availChanges WHERE employeeID = ? AND month = ? AND year = ?"
                , new String[]{String.valueOf(id), String.valueOf(mon), String.valueOf(yr)});

        while (cursor.moveToNext()) {
            int d = cursor.getInt(cursor.getColumnIndexOrThrow(day));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(availStatus));

            //add to appropriate list
            switch (status){
                case 0: //not avail
                    notAvail.add(d);
                    break;
                case 1: //all day
                    allDayAvail.add(d);
                    break;
                case 2: //morn only
                    mornAvail.add(d);
                    break;
                case 3: //all day
                    eveAvail.add(d);
                    break;
            }
        }

        cursor.close();
        db.close();

        return new AvailByStatus(allDayAvail, mornAvail, eveAvail, notAvail);
    }

    /** Returns if the employee is availiable for at least one shift in the given week
     * Start must be a Sunday
     */
    public boolean empIsAvailInWeek(LocalDate start, Employee employee){
        //get def avail list
        ArrayList<Integer> defAvail = new ArrayList<>(defAvailStatusList(employee.getID()));
        //get status of avail changes for the week, if nothing in db, then integer is 4
        ArrayList<Integer> changedAvail = new ArrayList<>();
        Avail temp = getAvailbyDayByEmp(employee, start);
        if (temp != null){
            if (temp.getStatus() > 0){
                return true; //the emp is avail for at least one shift
            }
            else if (defAvail.get(0) > 0){ //null means no changed avail so do what def is
                return true; //emp is avail for at least one shift
            }
        }

        for (int boa = 1; boa<7; boa++){
            temp = getAvailbyDayByEmp(employee, start.plusDays(boa));
            if (temp != null) {
                //null means no changed avail so do what def is
                if (temp.getStatus() > 0) {
                    return true; //the emp is avail for at least one shift
                }
            }
            else if (defAvail.get(0) > 0){
                return true;
            }
        }
        return false; //here if not avail for any shifts in the week
    }

    public int numberOfMonthsInRange (LocalDate today, LocalDate endDay){
        int months = 0;

        //months for number of full years
        int yearDif = (today.getYear() + 1) - (endDay.getYear()-1);
        if (yearDif == 0 || yearDif > 0){
            months = 12 + (12*yearDif);
        }
        //else no full years so continue
        months += (12 - today.getMonthValue());

        return months;
    }

    /**
     * get the newest day that there has been a schedule made for
     * returns null if today is the newest day that has been scheduled for
     */
    public LocalDate getScheduleEndDate(LocalDate today){

        int thisMonth = today.getMonthValue();
        int thisYear = today.getYear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * " + " from " + sched + " where ((" + day + " > "
                        + today.getDayOfMonth() + " AND " + month + " = " + thisMonth + " AND "
                        + year + " = " + thisYear + ") OR (" + month + " > " + thisMonth + " AND "
                        + year + " = " + thisYear + ") OR (" + year + " > " + thisYear +
                        ")) order by year DESC, month DESC, day DESC",
                null);

        if (cursor.moveToFirst()) {
            int d = cursor.getInt(cursor.getColumnIndexOrThrow(day));
            int m = cursor.getInt(cursor.getColumnIndexOrThrow(month));
            int y = cursor.getInt(cursor.getColumnIndexOrThrow(year));


            cursor.close();
            db.close();
            return LocalDate.of(y, m, d);
        }
        return null;
    }

    /** For use when generating availability for calendar
     */
    public AvailByStatus getEmpAvailByMonth(int id, LocalDate date){
        AvailByStatus defAvail = getEmpDefaultAvailByMonth(id, date);
        AvailByStatus changedAvail = getEmpChangedAvailbyMonth(id, date);

        //make list of all changed days, remove from all default lists
        ArrayList<Integer> changedDays = new ArrayList<>();
        changedDays.addAll(changedAvail.getAllDayAvail());
        changedDays.addAll(changedAvail.getMornAvail());
        changedDays.addAll(changedAvail.getEveAvail());
        changedDays.addAll(changedAvail.getNotAvail());

        //remove days from default lists
        if (!changedDays.isEmpty()){
            (defAvail.getAllDayAvail()).removeAll(changedDays);
            (defAvail.getMornAvail()).removeAll(changedDays);
            (defAvail.getEveAvail()).removeAll(changedDays);
            (defAvail.getNotAvail()).removeAll(changedDays);
        }

        //add all changed days to appropriate list
        defAvail.getAllDayAvail().addAll(changedAvail.getAllDayAvail());
        defAvail.getMornAvail().addAll(changedAvail.getMornAvail());
        defAvail.getEveAvail().addAll(changedAvail.getEveAvail());
        defAvail.getNotAvail().addAll(changedAvail.getNotAvail());

        return defAvail;
    }

    public int statusFromDayOfWeek(LocalDate date, ArrayList<Integer> defStatus){
        DayOfWeek temp = date.getDayOfWeek();
        int dayOfWeek = temp.getValue();
        int status = 4;

            //dayOfWeek follows the ISO-8601 standard, from 1 (Monday) to 7 (Sunday) --> cases
            switch (dayOfWeek) {
                case 7: //day is a Sunday so use index 0
                    status = defStatus.get(0);
                    break;
                case 1: //day is a Monday so use index 1
                    status = defStatus.get(1);
                    break;
                case 2: //day is a Tuesday so use index 2
                    status = defStatus.get(2);
                    break;
                case 3: //day is a Wednesday so use index 3
                    status = defStatus.get(3);
                    break;
                case 4: //day is a Thursday so use index 4
                    status = defStatus.get(4);
                    break;
                case 5: //day is a Friday so use index 5
                    status = defStatus.get(5);
                    break;
                case 6: //day is a Saturday so use index 6
                    status = defStatus.get(6);
                    break;
            }
            return status;
    }

    //Schedule methods
    //region

    public void addEditSched(Schedule schedule){
        ContentValues values = new ContentValues();
        values.put(day, schedule.getDay());
        values.put(month, schedule.getMonth());
        values.put(year, schedule.getYear());
        values.put(shift1, schedule.getShift1());
        values.put(shift2, schedule.getShift2());
        values.put(mornBusy, schedule.getMornBusy());
        values.put(eve1, schedule.getEve1());
        values.put(eve2, schedule.getEve2());
        values.put(eveBusy, schedule.getEveBusy());
        values.put(busy, schedule.getBusy());
        values.put(status, schedule.getStatus());

        SQLiteDatabase db = this.getWritableDatabase();
        db.replace(sched, null, values);

        db.close();
    }

    public Schedule getSchedbyDay(int d, int mon, int yr){
        //returns null if there is no entry in the database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * " + " from " + sched + " where " + day + " = "
                + d + " and " + month + " = " + mon + " and " + year + " = " + yr, null);

        //if there is data
        if (cursor.moveToFirst()) {
            int s1 = cursor.getInt(cursor.getColumnIndexOrThrow(shift1));
            int s2 = cursor.getInt(cursor.getColumnIndexOrThrow(shift2));
            int mB = cursor.getInt(cursor.getColumnIndexOrThrow(mornBusy));
            int e1 = cursor.getInt(cursor.getColumnIndexOrThrow(eve1));
            int e2 = cursor.getInt(cursor.getColumnIndexOrThrow(eve2));
            int eB = cursor.getInt(cursor.getColumnIndexOrThrow(eveBusy));
            int b = cursor.getInt(cursor.getColumnIndexOrThrow(busy));
            int s = cursor.getInt(cursor.getColumnIndexOrThrow(status));
            Schedule daySched = new Schedule(d, mon, yr, s1, s2, mB, e1, e2, eB, b, s);

            cursor.close();
            db.close();

            return daySched;
        }
        else {return null;}
    }

    public Schedule getSchedbyDay(LocalDate date){
        //returns null if there is no entry in the database
        int d = date.getDayOfMonth();
        int mon = date.getMonthValue();
        int yr = date.getYear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * " + " from " + sched + " where " + day + " = "
                + d + " and " + month + " = " + mon + " and " + year + " = " + yr, null);

        //if there is data
        if (cursor.moveToFirst()) {
            int s1 = cursor.getInt(cursor.getColumnIndexOrThrow(shift1));
            int s2 = cursor.getInt(cursor.getColumnIndexOrThrow(shift2));
            int mB = cursor.getInt(cursor.getColumnIndexOrThrow(mornBusy));
            int e1 = cursor.getInt(cursor.getColumnIndexOrThrow(eve1));
            int e2 = cursor.getInt(cursor.getColumnIndexOrThrow(eve2));
            int eB = cursor.getInt(cursor.getColumnIndexOrThrow(eveBusy));
            int b = cursor.getInt(cursor.getColumnIndexOrThrow(busy));
            int s = cursor.getInt(cursor.getColumnIndexOrThrow(status));
            Schedule daySched = new Schedule(d, mon, yr, s1, s2, mB, e1, e2, eB, b, s);

            cursor.close();
            db.close();

            return daySched;
        }
        else {return null;}
    }

    public void removeSchedbyDay(Schedule sched) {
        int d = sched.getDay();
        int mon = sched.getMonth();
        int yr = sched.getYear();

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + availTable + " where " + day + " = " + d + " and " + month
                + " = " + mon + " and " + year  + " = " + yr);
        db.close();
    }

    public ScheduleMonthByStatus getSchedMonthByStatusList(LocalDate date){
        int mon = date.getMonthValue();
        int yr = date.getYear();

        ArrayList<Integer> done = new ArrayList<>();
        ArrayList<Integer> inProgress = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * from schedule WHERE month = ? AND year = ?"
                , new String[]{String.valueOf(mon), String.valueOf(yr)});

        while (cursor.moveToNext()) {
            int d = cursor.getInt(cursor.getColumnIndexOrThrow(day));
            int st = cursor.getInt(cursor.getColumnIndexOrThrow(status));

            //add to appropriate list
            switch (st){
                case 1: //completed valid schedule
                    done.add(d);
                    break;
                case 2: //schedule started but not completed
                    inProgress.add(d);
                    break;
            }
        }

        cursor.close();
        db.close();

        return new ScheduleMonthByStatus(done, inProgress);
    }

    public int checkDefAvail(int id, LocalDate day){
        //get avail status for day of week
        return statusFromDayOfWeek(day, defAvailStatusList(id));
    }

    public int checkChangedAvail(int id, LocalDate day){
        //get avail status for day
        try {
            Avail dbAvail = getAvailbyDayByEmp(id, day.getMonthValue(), day.getYear(), day.getDayOfMonth());
            return dbAvail.getStatus();
        }
        catch (NullPointerException ignored){
            //theres nothing in the db
            return 4;
        }
    }

    //endregion

    /**returns empty list if nothing in DB*/
    public ArrayList<Schedule> empsSchedsFromDay(LocalDate today, Employee emp){

        int id = emp.getID();
        ArrayList<Schedule> schedulesWithEmp = new ArrayList<>();

        int thisMonth = today.getMonthValue();
        int thisYear = today.getYear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * " + " from " + sched + " where ((" + day + " > "
                + today.getDayOfMonth() + " AND " + month + " = " + thisMonth + " AND "
                + year + " = " + thisYear + ") OR (" + month + " > " + thisMonth + " AND "
                        + year + " = " + thisYear + ") OR (" + year + " > " + thisYear +
                        ")) AND " + id + " IN (" + shift1 + "," + shift2+ "," + mornBusy + "," + eve1
                        + "," + eve2 + "," + eveBusy + ")",
                null);

        while (cursor.moveToNext()) {
            int d = cursor.getInt(cursor.getColumnIndexOrThrow(day));
            int m = cursor.getInt(cursor.getColumnIndexOrThrow(month));
            int y = cursor.getInt(cursor.getColumnIndexOrThrow(year));
            int s1 = cursor.getInt(cursor.getColumnIndexOrThrow(shift1));
            int s2 = cursor.getInt(cursor.getColumnIndexOrThrow(shift2));
            int s3 = cursor.getInt(cursor.getColumnIndexOrThrow(mornBusy));
            int e1 = cursor.getInt(cursor.getColumnIndexOrThrow(eve1));
            int e2 = cursor.getInt(cursor.getColumnIndexOrThrow(eve2));
            int e3 = cursor.getInt(cursor.getColumnIndexOrThrow(eveBusy));
            int b = cursor.getInt(cursor.getColumnIndexOrThrow(busy));
            int s = cursor.getInt(cursor.getColumnIndexOrThrow(status));

            Schedule sched = new Schedule(d,m,y,s1,s2,s3,e1,e2,e3,b,s);
            schedulesWithEmp.add(sched);
        }
        cursor.close();
        db.close();

        return schedulesWithEmp;
    }

    public Schedule schedIfEmpOnDay(LocalDate selectedDate, Employee employee) {

        int id = employee.getID();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "select * " + " from " + sched + " where " + day + " = "
                        + selectedDate.getDayOfMonth() + " AND " + month + " = " + selectedDate.getMonthValue()
                        + " AND " + year + " = " + selectedDate.getYear() + " AND " + id + " IN ("
                        + shift1 + "," + shift2+ "," + mornBusy + "," + eve1 + "," + eve2 + "," + eveBusy + ")",
                null);

        if (cursor.moveToFirst()){
            int d = cursor.getInt(cursor.getColumnIndexOrThrow(day));
            int m = cursor.getInt(cursor.getColumnIndexOrThrow(month));
            int y = cursor.getInt(cursor.getColumnIndexOrThrow(year));
            int s1 = cursor.getInt(cursor.getColumnIndexOrThrow(shift1));
            int s2 = cursor.getInt(cursor.getColumnIndexOrThrow(shift2));
            int s3 = cursor.getInt(cursor.getColumnIndexOrThrow(mornBusy));
            int e1 = cursor.getInt(cursor.getColumnIndexOrThrow(eve1));
            int e2 = cursor.getInt(cursor.getColumnIndexOrThrow(eve2));
            int e3 = cursor.getInt(cursor.getColumnIndexOrThrow(eveBusy));
            int b = cursor.getInt(cursor.getColumnIndexOrThrow(busy));
            int s = cursor.getInt(cursor.getColumnIndexOrThrow(status));

            Schedule sched = new Schedule(d,m,y,s1,s2,s3,e1,e2,e3,b,s);
            cursor.close();
            db.close();

            return sched;
        }
        return null;
    }
}