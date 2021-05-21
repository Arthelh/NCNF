package com.ncnf.utilities;

import android.util.Log;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(DateAdapter.toDate(""), null);
    }

    @Test
    public void toDateWorks(){
        String date = "2000/01/25 12:45";
        LocalDateTime dateTime = DateAdapter.toDate(date);
        System.out.println(Integer.toString(dateTime.getYear()));
        assertTrue(dateTime.getYear() == 2000);
        assertTrue(dateTime.getMonth() == Month.JANUARY);
        assertTrue(dateTime.getDayOfMonth() == 25);
        assertTrue(dateTime.getHour() == 12);
        assertTrue(dateTime.getMinute() == 45);
    }
}
