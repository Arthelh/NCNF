package com.example.bootcamp;
import org.junit.Test;

import static org.junit.Assert.*;


public class DateTest {

    @Test
    public void dateThrowsExceptionOnIllegalYear() {
        assertThrows(IllegalArgumentException.class, () -> new Date(2000, 11, 11));
    }

    @Test
    public void dateThrowsExceptionOnWrongFebDate() {
        assertThrows(IllegalArgumentException.class, () -> new Date(2021, 2, 29));
    }

    @Test
    public void dateThrowsNoExceptionOnRightLeapYearDate() {
        Date date = new Date(2024, 2, 29);
        assertEquals(date.getDay(), 29);
    }

    @Test
    public void dateThrowsNoExceptionOnWrongShortMonthDate() {
        assertThrows(IllegalArgumentException.class, () -> new Date(2021, 4, 31));
    }



}
