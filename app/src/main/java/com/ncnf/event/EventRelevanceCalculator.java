package com.ncnf.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class EventRelevanceCalculator {

    private final List<PublicEvent> events = new ArrayList<>();
    private final List<Event> sortedList;

    public EventRelevanceCalculator(List<Event> l) {

        for(Event e : l) {
            if(e != null)
                this.events.add((PublicEvent) e);
        }
        sortedList = new ArrayList<>(sortedList());
    }

    private List<Event> sortedList() {

        List<Event> l = new ArrayList<>();
        Map<Tag, Integer> m = new HashMap<>();

        for(PublicEvent e : events) {
            List<Tag> tags = e.getTags();

            if(!tags.isEmpty()) {

                Tag maxTag = tags.get(0);
                int maxOccurences = 0;

                for(int i = 0; i < tags.size(); ++i) {

                    int thisTagOcc = 0;
                    Tag thisTag = tags.get(i);
                    for(PublicEvent e2 : events) {

                        if(!e2.equals(e) && e2.getTags().contains(thisTag)) {
                            thisTagOcc += 1;
                        }
                    }

                    if(thisTagOcc > maxOccurences) {
                        maxTag = thisTag;
                        maxOccurences = thisTagOcc;
                    }
                }

                m.put(maxTag, maxOccurences);
            }
        }

        List<Integer> occurences = new ArrayList<>(new HashSet<>(m.values()));
        Collections.sort(occurences, Collections.reverseOrder());

        for(Integer i : occurences) {
            for(Tag t : m.keySet()) {
                if(m.get(t) == i) {
                    for(PublicEvent e : events) {
                        if(e.getTags().contains(t) && !l.contains(e)) {
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
