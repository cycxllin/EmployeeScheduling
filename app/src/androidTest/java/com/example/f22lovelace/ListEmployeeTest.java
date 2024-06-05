package com.example.f22lovelace;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.example.f22lovelace.classes.DBHandler;
import com.example.f22lovelace.classes.Employee;
import java.util.Arrays;
import java.util.List;

public class ListEmployeeTest {
    private DBHandler dbHandler;

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

    @Test
    public void listEmps() {
        //create employees and add to db
        Employee emp = new Employee(1, "Jordan", "Bothwell", "Peterson",
                "jbpeterson@fake.com", "1234567890", 0, 1, 1);
        Employee emp2 = new Employee(2, "Jamil", null, "Meetwood",
                "jmeetwood@fake.com", "2345678901", 1, 0, 0);
        dbHandler.addEmp(emp);
        dbHandler.addEmp(emp2);

    }


}
