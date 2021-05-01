package com.ncnf.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EventRelevanceCalculator {

    private final List<Event> events = new ArrayList<>();
    private final List<Social> sortedList;

    public EventRelevanceCalculator(List<Social> l) {

        for (Social e : l) {
            if (e != null)
                this.events.add((Event) e);
        }
        sortedList = new ArrayList<>(sortedList());
    }

    private List<Social> sortedList() {
        List<Social> l = new ArrayList<>();
        Map<Tag, Integer> m = new HashMap<>();

        for (Event e : events) {
            List<Tag> tags = e.getTags();

            if (!tags.isEmpty()) {
                Tag maxTag = tags.get(0);
                int maxOccurrences = 0;

                for (int i = 0; i < tags.size(); ++i) {
                    int thisTagOcc = 0;
                    Tag thisTag = tags.get(i);
                    for (Event e2 : events) {
                        if (!e2.equals(e) && e2.getTags().contains(thisTag)) {
                            thisTagOcc += 1;
                        }
                    }

                    if (thisTagOcc > maxOccurrences) {
                        maxTag = thisTag;
                        maxOccurrences = thisTagOcc;
                    }
                }

                m.put(maxTag, maxOccurrences);
            }
        }

        List<Integer> occurrences = new ArrayList<>(new HashSet<>(m.values()));
        Collections.sort(occurrences, Collections.reverseOrder());
        for (Integer i : occurrences) {
            for (Tag t : m.keySet()) {
                if (m.get(t).equals(i)) {
                    for (Event e : events) {
                        if (e.getTags().contains(t) && !l.contains(e)) {
                            l.add(e);
                        }
                    }
                }
            }
        }

        return l;
    }

    public List<Social> getSortedList() {
        return sortedList;
    }

}
