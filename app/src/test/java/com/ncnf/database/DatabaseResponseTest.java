package com.ncnf.database;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseResponseTest {

    boolean isSuccessful = true;
    Object result = new Object();
    Exception exception = new Exception();
    DatabaseResponse res = new DatabaseResponse(isSuccessful, result, exception);

    @Test
    public void getExceptionReturnsSavedException() {
        assertThat("Response exception matches", res.getException(), is(exception));
    }

    @Test
    public void isSuccessfulReturnsSavedStatus() {
        assertThat("Response success matches", res.isSuccessful() == isSuccessful);
    }

    @Test
    public void getResultReturnsSavedResult() {
        assertThat("Response result matches", res.getResult().equals(result));
    }

}
