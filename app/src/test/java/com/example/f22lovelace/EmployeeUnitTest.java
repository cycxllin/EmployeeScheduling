package com.example.f22lovelace;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.f22lovelace.classes.Employee;


public class EmployeeUnitTest {
    //create employee for testing
    Employee emp = new Employee(1, "Jordan", "Bothwell", "Peterson",
            "jbpeterson@fake.com", "1234567890", 0, 1, 0);

    @Test
    public void testGetters(){
        assertEquals(1, emp.getID());
        assertEquals("Jordan", emp.getFirstName());
        assertEquals("Bothwell", emp.getMiddleName());
        assertEquals("Peterson", emp.getLastName());
        assertEquals("jbpeterson@fake.com", emp.getEmail());
        assertEquals("1234567890", emp.getPhone());
        assertEquals("Jordan Bothwell Peterson", emp.getFullName());
        assertEquals(0, emp.getOpen());
        assertEquals(1, emp.getClose());
        assertEquals(1, emp.getActive());
    }

    @Test
    public void testSetters(){
        emp.setID(1);
        emp.setFirstName("Clary");
        emp.setMiddleName("Buffaunt");
        emp.setLastName("Mak");
        emp.setEmail("cbmak@fake.com");
        emp.setPhone("0987654321");
        emp.setOpen(1);
        emp.setClose(0);
        emp.setActive(0);

        assertEquals(1, emp.getID());
        assertEquals("Clary", emp.getFirstName());
        assertEquals("Buffaunt", emp.getMiddleName());
        assertEquals("Mak", emp.getLastName());
        assertEquals("cbmak@fake.com", emp.getEmail());
        assertEquals("0987654321", emp.getPhone());
        assertEquals(1, emp.getOpen());
        assertEquals(0, emp.getClose());
        assertEquals(0, emp.getActive());
    }
}
