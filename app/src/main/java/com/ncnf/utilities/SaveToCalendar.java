package com.ncnf.utilities;

import android.content.Intent;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;

import com.ncnf.socialObject.SocialObject;

public class SaveToCalendar {

    public static Intent createCalendarIntent(SocialObject s){

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, s.getName());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, s.getLocation().toString());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, s.getDescription());
        //intent.putExtra(CalendarContract.Events.ALL_DAY, true);
        intent.putExtra(CalendarContract.Events.DTSTART, s.getDate().getTime());
        //intent.putExtra(CalendarContract.Events.DTEND, hour end)*/
        return intent;
    }
}
