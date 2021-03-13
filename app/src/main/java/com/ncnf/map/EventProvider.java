package com.ncnf.map;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class EventProvider {

    @Inject
    public EventProvider() {}

    public List<Event> getAll() {
        ArrayList<Event> list = new ArrayList<>();
        list.add(new Event("Math Conference, EPFL, 1pm March 3rd", 46.5191f, 6.5668f));
        list.add(new Event("Les Noces de Figaro, Lausanne Opera, 7pm March 12th", 46.5180f, 6.6369f));
        list.add(new Event("Caravan Palace, Les Docks, 9pm March 12th", 46.5224f, 6.6193f));
        list.add(new Event("Cours de danse, Espace Arsenic, 3pm March 13th", 46.5227f, 6.6216f));
        return list;
    }

}
