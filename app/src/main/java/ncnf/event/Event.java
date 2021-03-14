package ncnf.event;

import java.util.Date;

import ncnf.organizer.Organizer;

public interface Event {
    Organizer getOrganizer();
    String getName();
    String getUID();
    EventType getType();
    String getData();
    Date getDate();
}
