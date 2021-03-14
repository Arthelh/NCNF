package com.ncnf.event;

<<<<<<< HEAD:app/src/main/java/ncnf/event/Event.java
import java.util.Date;

import ncnf.organizer.Organizer;
=======
import com.ncnf.organizer.Organizer;
>>>>>>> main:app/src/main/java/com/ncnf/event/Event.java

public interface Event {
    Organizer getOrganizer();
    String getName();
    String getUID();
    EventType getType();
    String getData();
    Date getDate();
}
