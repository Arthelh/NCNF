package com.ncnf.socialObject;

import android.content.Intent;
import android.provider.CalendarContract;

public class Add2Calendar {

    public static Intent createCalendarIntent(SocialObject s){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, s.getName());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, s.getLocation().toString());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, s.getDescription());
        intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        //TODO if hour is added
        /*intent.putExtra(CalendarContract.Events.DTSTART, hour start )
        intent.putExtra(CalendarContract.Events.DTEND, hour end)*/
        return intent;
    }
}
