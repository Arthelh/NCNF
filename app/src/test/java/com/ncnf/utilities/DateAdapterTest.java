package com.ncnf.utilities;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class DateAdapterTest {

    @Test
    public void gettersWork() {
        DateAdapter d = new DateAdapter(2021, 2,13);
        assertEquals(d.getYear(), 2021);
        assertEquals(d.getDay(), 13);
        assertEquals(d.getMonth(), 2);

        LocalDateTime date = LocalDateTime.now();
        DateAdapter d2 = new DateAdapter(date);

        Calendar calendar = Calendar.getInstance();

        assertEquals(d2.getHour(), calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(d2.getMinute(), calendar.get(Calendar.MINUTE));

        assertEquals(d2.getDate(), date);

    }

    @Test
    public void toDateReturnsNull() {
        assertNull(DateAdapter.toDate(""));
    }

    @Test
    public void toDateWorks(){
        String date = "2000/01/25 12:45";
        LocalDateTime dateTime = DateAdapter.toDate(date);
        System.out.println(dateTime.getYear());
        assertEquals(2000, dateTime.getYear());
        assertSame(dateTime.getMonth(), Month.JANUARY);
        assertEquals(25, dateTime.getDayOfMonth());
        assertEquals(12, dateTime.getHour());
        assertEquals(45, dateTime.getMinute());
    }
}
