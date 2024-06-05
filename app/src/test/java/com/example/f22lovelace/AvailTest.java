package com.example.f22lovelace;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.f22lovelace.classes.Avail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AvailTest {

    @Test
    public void testGetters(){
        Avail empAvail = new Avail(1, 10, 2023, 31, 1);

        assertEquals(1, empAvail.getID());
        assertEquals(10, empAvail.getMonth());
        assertEquals(31, empAvail.getDay());
        assertEquals(2023, empAvail.getYear());
        assertEquals(1, empAvail.getStatus());
    }

    @Test
    public void testSetters(){
        Avail empAvail = new Avail(1, 10, 2023, 31, 1);

        empAvail.setId(5);
        empAvail.setMonth(2);
        empAvail.setYear(2022);
        empAvail.setDay(20);
        empAvail.setStatus(2);

        assertEquals(5, empAvail.getID());
        assertEquals(2, empAvail.getMonth());
        assertEquals(2022, empAvail.getYear());
        assertEquals(20, empAvail.getDay());
        assertEquals(2, empAvail.getStatus());
    }
}
