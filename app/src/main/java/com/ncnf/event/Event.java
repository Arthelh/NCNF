package com.ncnf.event;

import java.time.LocalDateTime;
import com.ncnf.organizer.Organizer;

public interface Event {
    Organizer getOrganizer();
    String getName();
    String getUID();
    EventType getType();
    String getData();
    LocalDateTime getDate();
}
