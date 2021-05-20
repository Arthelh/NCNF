package com.ncnf.utilities.event;

import com.ncnf.models.Event;
import com.ncnf.models.EventTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EventRelevanceCalculator {

    private final List<Event> events = new ArrayList<>();
    private final List<Event> sortedList;

    public EventRelevanceCalculator(List<Event> l) {

        for (Event e : l) {
            if (e != null)
                this.events.add((Event) e);
        }
        sortedList = new ArrayList<>(sortedList());
    }

    private List<Event> sortedList() {
        List<Event> l = new ArrayList<>();
        Map<EventTag, Integer> m = new HashMap<>();

        for (Event e : events) {
            List<EventTag> eventTags = e.getEventTags();

            if (!eventTags.isEmpty()) {
                EventTag maxEventTag = eventTags.get(0);
                int maxOccurrences = 0;

                for (int i = 0; i < eventTags.size(); ++i) {
                    int thisTagOcc = 0;
                    EventTag thisEventTag = eventTags.get(i);
                    for (Event e2 : events) {
                        if (!e2.equals(e) && e2.getEventTags().contains(thisEventTag)) {
                            thisTagOcc += 1;
                        }
                    }

                    if (thisTagOcc > maxOccurrences) {
                        maxEventTag = thisEventTag;
                        maxOccurrences = thisTagOcc;
                    }
                }

                m.put(maxEventTag, maxOccurrences);
            }
        }

        if (m.isEmpty())
            return new ArrayList<>(events);

        List<Integer> occurrences = new ArrayList<>(new HashSet<>(m.values()));
        Collections.sort(occurrences, Collections.reverseOrder());
        for (Integer i : occurrences) {
            for (EventTag t : m.keySet()) {
                if (m.get(t).equals(i)) {
                    for (Event e : events) {
                        if (e.getEventTags().contains(t) && !l.contains(e)) {
                            l.add(e);
                        }
                    }
                }
            }
        }

        return l;
    }

    public List<Event> getSortedList() {
        return sortedList;
    }

}
