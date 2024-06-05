package com.example.f22lovelace;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.f22lovelace.classes.Avail;
import com.example.f22lovelace.classes.AvailByStatus;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import com.example.f22lovelace.classes.Schedule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseTest {
    private DBHandler dbHandler;
//emp tests failing due to phone number formatting change
    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();

        context.deleteDatabase("restaurantDB");
        dbHandler = new DBHandler(context);
    }

    @After
    public void closeDB(){
        dbHandler.close();
    }

    //create employees
    Employee emp = new Employee(1,"Jordan", "Bothwell", "Peterson",
            "jbpeterson@fake.com", "1234567890", 0, 1, 1);
    Employee emp2 = new Employee(2, "Jamil", null, "Meetwood",
            "jmeetwood@fake.com", "2345678901", 1, 0, 0);


    @Test
    public void shouldAddEmp() {

        dbHandler.addEmp(emp);

        //make info into lists to test that they are the same as what we get from the database
        List<Object> shouldBeEmpInfo = Arrays.asList(emp.getID(), emp.getFirstName(), emp.getMiddleName(),
                emp.getLastName(), emp.getEmail(), emp.getPhone(), emp.getOpen(), emp.getClose(), emp.getActive());

        //ensure both employees added in correct order
        ArrayList<Employee> allEmps = dbHandler.listActiveEmps();
        List<Object> empInfo = Arrays.asList(allEmps.get(0).getID(), allEmps.get(0).getFirstName(), allEmps.get(0).getMiddleName(),
                allEmps.get(0).getLastName(), allEmps.get(0).getEmail(), allEmps.get(0).getPhone(),
                allEmps.get(0).getOpen(), allEmps.get(0).getClose(), allEmps.get(0).getActive());

        //check defAvail added
        ArrayList<Integer> defStatus = dbHandler.defAvailStatusList(emp.getID());
        ArrayList<Integer> checkStatus = new ArrayList<>();

        int i;
        for (i = 0; i<7; i++){
            checkStatus.add(0);
        }

        assertEquals(shouldBeEmpInfo, empInfo);
        assertEquals(checkStatus, defStatus);

    }

    @Test
    public void findOneEmp() {

        dbHandler.addEmp(emp);
        dbHandler.addEmp(emp2);

        //ensure retrieving one employee by ID works
        Employee gotEmp = dbHandler.getEmpById(2);

        List<Object> shouldBeEmp = Arrays.asList(2, "Jamil", null, "Meetwood",
                "jmeetwood@fake.com", "2345678901", 1, 0, 0);
        List<Object> theEmp = Arrays.asList(gotEmp.getID(), gotEmp.getFirstName(), gotEmp.getMiddleName(),
                gotEmp.getLastName(), gotEmp.getEmail(), gotEmp.getPhone(), gotEmp.getOpen(), gotEmp.getClose()
                , gotEmp.getActive());

        assertEquals(shouldBeEmp, theEmp);
    }

    @Test
    public void deleteOneEmp(){
        dbHandler.addEmp(emp);
        dbHandler.removeEmp(emp.getID());

        Employee empDB = dbHandler.getEmpById(1);

        //check defAvail changed to 0
        ArrayList<Integer> defStatus = dbHandler.defAvailStatusList(emp.getID());
        ArrayList<Integer> checkStatus = new ArrayList<>();

        int i;
        for (i = 0; i<7; i++){
            checkStatus.add(0);
        }

        assertEquals(checkStatus, defStatus);
        assertEquals(0, empDB.getActive());
    }

    @Test
    public void editEmp(){
        dbHandler.addEmp(emp);
        dbHandler.addEmp(emp2);

        Employee editedEmp = new Employee(2, "Jamil", "Logan", "Meetwood",
                "jmeetwood@fake.com", "2345678901",1 ,0, 0);

        dbHandler.editEmp(editedEmp);

        Employee gotEmp = dbHandler.getEmpById(2);
        List<Object> empInfo = Arrays.asList(gotEmp.getID(), gotEmp.getFirstName(), gotEmp.getMiddleName(),
                gotEmp.getLastName(), gotEmp.getEmail(), gotEmp.getPhone(), gotEmp.getOpen(),
                gotEmp.getClose(), gotEmp.getActive());

        List<Object> shouldBeEmp = Arrays.asList(2, "Jamil", "Logan", "Meetwood",
                "jmeetwood@fake.com", "2345678901", 1, 0, 0);

        assertEquals(shouldBeEmp, empInfo);
    }

    @Test
    public void shouldListAllEmps(){
        dbHandler.addEmp(emp);
        dbHandler.addEmp(emp2);

        ArrayList<Employee> allEmps = dbHandler.listAllEmps();

        List<Object> shouldBeEmpInfo = Arrays.asList(emp.getID(), emp.getFirstName(), emp.getMiddleName(),
                emp.getLastName(), emp.getEmail(), emp.getPhone(), emp.getOpen(), emp.getClose());
        List<Object> shouldBeEmp2Info = Arrays.asList(emp2.getID(), emp2.getFirstName(), emp2.getMiddleName(),
                emp2.getLastName(), emp2.getEmail(), emp2.getPhone(), emp2.getOpen(), emp2.getClose());
        List<Object> empInfo = Arrays.asList(allEmps.get(0).getID(), allEmps.get(0).getFirstName(), allEmps.get(0).getMiddleName(),
                allEmps.get(0).getLastName(), allEmps.get(0).getEmail(), allEmps.get(0).getPhone(), allEmps.get(0).getOpen(), allEmps.get(0).getClose());
        List<Object> emp2Info = Arrays.asList(allEmps.get(1).getID(), allEmps.get(1).getFirstName(), allEmps.get(1).getMiddleName(),
                allEmps.get(1).getLastName(), allEmps.get(1).getEmail(), allEmps.get(1).getPhone(), allEmps.get(1).getOpen(), allEmps.get(1).getClose());

        assertEquals(shouldBeEmpInfo, empInfo);
        assertEquals(shouldBeEmp2Info, emp2Info);
    }

    @Test
    public void shouldListActiveEmps(){
        dbHandler.addEmp(emp);
        dbHandler.addEmp(emp2);

        ArrayList<Employee> allEmps = dbHandler.listActiveEmps();

        List<Object> shouldBeEmpInfo = Arrays.asList(emp.getID(), emp.getFirstName(), emp.getMiddleName(),
                emp.getLastName(), emp.getEmail(), emp.getPhone(), emp.getOpen(), emp.getClose());
        List<Object> empInfo = Arrays.asList(allEmps.get(0).getID(), allEmps.get(0).getFirstName(), allEmps.get(0).getMiddleName(),
                allEmps.get(0).getLastName(), allEmps.get(0).getEmail(), allEmps.get(0).getPhone(), allEmps.get(0).getOpen(), allEmps.get(0).getClose());

        assertEquals(shouldBeEmpInfo, empInfo);
        assertEquals(1, allEmps.size());
    }

    @Test
    public void shouldAddAvail(){
        dbHandler.addEmp(emp);

        Avail avail = new Avail(emp.getID(), 1, 2022, 1, 3);

        dbHandler.addEditAvail(avail);
        List<Object> shouldBeAvail = Arrays.asList(1,1,2022,1,3);

        Avail dbAvail = dbHandler.getAvailbyDayByEmp(emp.getID(), 1,2022,1);

        List<Object> theAvail = Arrays.asList(dbAvail.getID(),dbAvail.getMonth(),dbAvail.getYear(),dbAvail.getDay(),dbAvail.getStatus());
        assertEquals(shouldBeAvail,theAvail);
    }

    @Test
    public void shouldEditAvail(){
        dbHandler.addEmp(emp);

        Avail avail = new Avail(emp.getID(), 1, 2022, 1, 3);
        avail.setStatus(1);
        dbHandler.addEditAvail(avail);
        List<Object> shouldBeAvail = Arrays.asList(1,1,2022,1,1);

        Avail dbAvail = dbHandler.getAvailbyDayByEmp(emp.getID(), 1,2022,1);

        List<Object> theAvail = Arrays.asList(dbAvail.getID(),dbAvail.getMonth(),dbAvail.getYear(),dbAvail.getDay(),dbAvail.getStatus());
        assertEquals(shouldBeAvail,theAvail);
    }

    @Test
    public void shouldDeleteAvail(){
        dbHandler.addEmp(emp);
        Avail avail = new Avail(emp.getID(), 1, 2022, 1, 3);
        dbHandler.removeEmpAvail(avail);
    }

    @Test
    public void shouldEditDefAvail(){
        dbHandler.addEmp(emp);

        ArrayList<Integer> changeStatusTo = new ArrayList<>();
        int i;
        for (i = 0; i<7; i++){
            changeStatusTo.add(2);
        }

        dbHandler.editDefAvail(emp.getID(), changeStatusTo);

        ArrayList<Integer> dbStatus = dbHandler.defAvailStatusList(emp.getID());
        ArrayList<Integer> checkStatus = new ArrayList<>();

        for (i = 0; i<7; i++){
            checkStatus.add(2);
        }

        assertEquals(checkStatus, dbStatus);
    }

    @Test
    public void testGetEmpDefaultAvailByMonth(){
        dbHandler.addEmp(emp);
        LocalDate date = LocalDate.of(2022,1,1);
        List<Integer> stati = Arrays.asList(0,1,2,3,0,1,2);
        ArrayList<Integer> changeStatusTo = new ArrayList<>(stati);
        dbHandler.editDefAvail(emp.getID(), changeStatusTo);

        //make should be
        List<Integer> allDays = Arrays.asList(3,7,10,14,17,21,24,28,31);
        List<Integer> morns = Arrays.asList(1,4,8,11,15,18,22,25,29);
        List<Integer> eves = Arrays.asList(5,12,19,26);
        List<Integer> nots = Arrays.asList(2,6,9,13,16,20,23,27,30);
        ArrayList<Integer> sbAllDay = new ArrayList<>(allDays);
        ArrayList<Integer> sbMorn = new ArrayList<>(morns);
        ArrayList<Integer> sbEve = new ArrayList<>(eves);
        ArrayList<Integer> sbNot = new ArrayList<>(nots);

        //get from db
        AvailByStatus db = dbHandler.getEmpDefaultAvailByMonth(emp.getID(), date);

        assertEquals(sbAllDay, db.getAllDayAvail());
        assertEquals(sbMorn, db.getMornAvail());
        assertEquals(sbEve, db.getEveAvail());
        assertEquals(sbNot, db.getNotAvail());

        /* Should be lists for 1Jan2022
        0: 2,6,9,13,16,20,23,27,30
        1: 3,7,10,14,17,21,24,28,31
        2: 1,4,8,11,15,18,22,25,29
        3: 5,12,19,26
         */

    }

    @Test
    public void testgetEmpChangedAvailbyMonth(){
        dbHandler.addEmp(emp);
        LocalDate date = LocalDate.of(2022,1,18);

        Avail avail = new Avail(emp.getID(), 1, 2022, 1, 3);
        Avail avail1 = new Avail(emp.getID(), 1, 2022, 2, 1);
        Avail avail2 = new Avail(emp.getID(), 1, 2022, 3, 2);
        Avail avail3 = new Avail(emp.getID(), 1, 2022, 4, 0);
        Avail avail4 = new Avail(emp.getID(), 1, 2022, 5, 1);
        Avail avail5 = new Avail(emp.getID(), 1, 2022, 8, 0);


        dbHandler.addEditAvail(avail); dbHandler.addEditAvail(avail1); dbHandler.addEditAvail(avail2);
        dbHandler.addEditAvail(avail3); dbHandler.addEditAvail(avail4); dbHandler.addEditAvail(avail5);

        //make should be
        List<Integer> allDays = Arrays.asList(2, 5);
        List<Integer> morns = Arrays.asList(3);
        List<Integer> eves = Arrays.asList(1);
        List<Integer> nots = Arrays.asList(4,8);
        ArrayList<Integer> sbAllDay = new ArrayList<>(allDays);
        ArrayList<Integer> sbMorn = new ArrayList<>(morns);
        ArrayList<Integer> sbEve = new ArrayList<>(eves);
        ArrayList<Integer> sbNot = new ArrayList<>(nots);

        //get from db
        AvailByStatus db = dbHandler.getEmpChangedAvailbyMonth(emp.getID(), date);

        assertEquals(sbAllDay, db.getAllDayAvail());
        assertEquals(sbMorn, db.getMornAvail());
        assertEquals(sbEve, db.getEveAvail());
        assertEquals(sbNot, db.getNotAvail());
    }

    @Test
    public void testGetEmpAvailByMonth(){
        dbHandler.addEmp(emp);
        LocalDate date = LocalDate.of(2022,1,1);

        //change defaults
        List<Integer> stati = Arrays.asList(0,1,2,3,0,1,2);
        ArrayList<Integer> changeStatusTo = new ArrayList<>(stati);
        dbHandler.editDefAvail(emp.getID(), changeStatusTo);

        //make changes
        Avail avail = new Avail(emp.getID(), 1, 2022, 1, 3);
        Avail avail1 = new Avail(emp.getID(), 1, 2022, 2, 1);
        Avail avail2 = new Avail(emp.getID(), 1, 2022, 3, 2);
        Avail avail3 = new Avail(emp.getID(), 1, 2022, 4, 0);
        Avail avail4 = new Avail(emp.getID(), 1, 2022, 5, 1);
        Avail avail5 = new Avail(emp.getID(), 1, 2022, 8, 0);

        dbHandler.addEditAvail(avail); dbHandler.addEditAvail(avail1); dbHandler.addEditAvail(avail2);
        dbHandler.addEditAvail(avail3); dbHandler.addEditAvail(avail4); dbHandler.addEditAvail(avail5);


        //make should be
        List<Integer> allDays = Arrays.asList(7,10,14,17,21,24,28,31,2,5);
        List<Integer> morns = Arrays.asList(11,15,18,22,25,29,3);
        List<Integer> eves = Arrays.asList(12,19,26,1);
        List<Integer> nots = Arrays.asList(6,9,13,16,20,23,27,30,4,8);
        ArrayList<Integer> sbAllDay = new ArrayList<>(allDays);
        ArrayList<Integer> sbMorn = new ArrayList<>(morns);
        ArrayList<Integer> sbEve = new ArrayList<>(eves);
        ArrayList<Integer> sbNot = new ArrayList<>(nots);

        //get from db
        AvailByStatus db = dbHandler.getEmpAvailByMonth(emp.getID(), date);

        assertEquals(sbAllDay, db.getAllDayAvail());
        assertEquals(sbMorn, db.getMornAvail());
        assertEquals(sbEve, db.getEveAvail());
        assertEquals(sbNot, db.getNotAvail());
    }

    @Test
    public void shouldAddSched(){
        Schedule sched = new Schedule(12,12,2222,1,2,3,
                4,5,6,7,8);
        dbHandler.addEditSched(sched);

        List<Integer> shouldBe = Arrays.asList(12,12,2222,1,2,3,4,5,6,7,8);
        Schedule dbSched = dbHandler.getSchedbyDay(12,12,2222);
        List<Integer> dbSchedule = Arrays.asList(dbSched.getDay(), dbSched.getMonth(), dbSched.getYear(),
                dbSched.getShift1(), dbSched.getShift2(), dbSched.getMornBusy(), dbSched.getEve1(),
                dbSched.getEve2(), dbSched.getEveBusy(), dbSched.getBusy(), dbSched.getStatus());

        assertEquals(shouldBe, dbSchedule);
    }

    @Test
    public void checkDups(){
        dbHandler.addEmp(emp);
        dbHandler.addEmp(emp2);

        Employee emp3 = new Employee(2, "Jamil", null, "Meetwood",
                "jm@fake.com", "1231231234", 1, 0, 1);
        dbHandler.addEmp(emp3);

        ArrayList<Employee> dups = dbHandler.checkDupEmp(emp2);

        List<Object> shouldBeEmp = Arrays.asList(2, "Jamil", null, "Meetwood",
                "jmeetwood@fake.com", "2345678901", 1, 0, 0);
        List<Object> shouldBeEmp2 = Arrays.asList(3, "Jamil", null, "Meetwood",
                "jm@fake.com", "1231231234", 1, 0, 1);

        List<Object> dbemp1 = Arrays.asList(dups.get(0).getID(), dups.get(0).getFirstName(), dups.get(0).getMiddleName(),
                dups.get(0).getLastName(), dups.get(0).getEmail(), dups.get(0).getPhone(), dups.get(0).getOpen(), dups.get(0).getClose()
                , dups.get(0).getActive());
        List<Object> dbemp2 = Arrays.asList(dups.get(1).getID(), dups.get(1).getFirstName(), dups.get(1).getMiddleName(),
                dups.get(1).getLastName(), dups.get(1).getEmail(), dups.get(1).getPhone(), dups.get(1).getOpen(), dups.get(1).getClose()
                , dups.get(1).getActive());

        assertEquals(shouldBeEmp, dbemp1);
        assertEquals(shouldBeEmp2, dbemp2);

    }
}
