package com.ncnf.event;
import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static org.junit.Assert.*;
public class EventCalculatorTest {

    @Test
    public void eventRelevanceCalculatorWorks() {

        PublicEvent event1 = new PublicEvent("EPFL event", (new DateAdapter(2021, 03, 30)).getDate(), new Location(46.518689, 6.568067, "Rolex Learning Center, 1015 Ecublens"), "Event description goes here", EventType.Conference, 0, 0, new PublicOrganizer("EPFL"), "rolex");
        PublicEvent event2 = new PublicEvent("Carmen", (new DateAdapter(2021, 04, 24)).getDate(), new Location(46.517789, 6.636917, "Avenue du Théâtre 12, 1002 Lausanne"), "Carmen opera function", EventType.Opera, 0, 0, new PublicOrganizer("Opéra de Lausanne"), "swan_lake");
        PublicEvent event3 = new PublicEvent("Concert", (new DateAdapter(2021, 04, 24)).getDate(), new Location(46.52210876137052, 6.628016637001172, "Rue de Genève 12, 1003 Lausanne"), "Event description goes here", EventType.Concert, 0, 0, new PublicOrganizer("Salle Métropole"), "swan_lake");

        event1.addTag(new Tag("\uD83D\uDCF1", "Technology"));
        event1.addTag(new Tag("\uD83D\uDCF1", "Conference"));
        event1.addTag(new Tag("\uD83D\uDCF1", "Artificial Intelligence"));

        event2.addTag(new Tag("\uD83D\uDCF1", "Opera"));
        event2.addTag(new Tag("\uD83D\uDCF1", "Singing"));
        event2.addTag(new Tag("\uD83D\uDCF1", "Art"));

        event3.addTag(new Tag("\uD83D\uDCF1", "Art"));

        List<Event> l = new ArrayList<>();
        l.add(event1);
        l.add(event2);
        l.add(event3);

        EventRelevanceCalculator rc = new EventRelevanceCalculator(l);

        assertTrue(rc.getSortedList().get(0).equals(event2) || rc.getSortedList().get(1).equals(event2));
        assertTrue(rc.getSortedList().get(0).equals(event3) || rc.getSortedList().get(1).equals(event3));
        assertTrue(rc.getSortedList().get(2).equals(event1));
    }


}
