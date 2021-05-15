package com.ncnf.utilities;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

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

}
