package com.ncnf.socialObject;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.utilities.DateAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventDB {

    private final Map<String, SocialObject> events = new HashMap<>();

    public static final UUID uuid1 = UUID.randomUUID();
    public static final UUID uuid2 = UUID.randomUUID();
    public static final UUID uuid3 = UUID.randomUUID();
    public static final UUID uuid4 = UUID.randomUUID();
    public static final UUID uuid5 = UUID.randomUUID();
    public static final UUID uuid6 = UUID.randomUUID();
    public static final UUID uuid7 = UUID.randomUUID();
    public static final UUID uuid8 = UUID.randomUUID();
    public static final UUID uuid9 = UUID.randomUUID();
    public static final UUID uuid10 = UUID.randomUUID();

    public static final UUID ownerID = UUID.randomUUID();
    public static final String ownerEmail = "test@email.com";

    private static final Event event1 = new Event(ownerID.toString(), "EPFL event", (new DateAdapter(2021, 03, 30)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event2 = new Event(ownerID.toString(), "Carmen", (new DateAdapter(2021, 04, 24)).getDate(), new GeoPoint(46.5338, 6.5914), "Renens Salle de Concerts, 1020 Renens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event3 = new Event(ownerID.toString(), "Concert", (new DateAdapter(2021, 04, 10)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event4 = new Event(ownerID.toString(), "UNIL event", (new DateAdapter(2021, 04, 24)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event5 = new Event(ownerID.toString(), "Exposition", (new DateAdapter(2021, 04, 24)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0,ownerEmail);
    private static final Event event6 = new Event(ownerID.toString(), "EPFL event", (new DateAdapter(2021, 04, 24)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event7 = new Event(ownerID.toString(), "Romeo & Juliet", (new DateAdapter(2021, 04, 24)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event8 = new Event(ownerID.toString(), "Swan Lake", (new DateAdapter(2021, 04, 24)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event9 = new Event(ownerID.toString(), "Barmen", (new DateAdapter(2021, 04, 24)).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
    private static final Event event10 = new Event(ownerID.toString(), "La flute enchantée", new DateAdapter(2021, 04, 01).getDate(), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, ownerEmail);
//    private static final Event event2 = new Event(, ownerID, (new DateAdapter(2021, 04, 24)).getDate().toString(), new Location(46.517789, 6.636917, "Avenue du Théâtre 12, 1002 Lausanne"), "Carmen opera function", SocialObject.Type.Opera, 0, 0, new PublicOrganizer("Opéra de Lausanne"), "swan_lake");
//    private static final Event event3 = new Event("", (new DateAdapter(2021, 04, 24)).getDate().toString(), new Location(46.52210876137052, 6.628016637001172, "Rue de Genève 12, 1003 Lausanne"), "SocialObject description goes here", SocialObject.Type.Concert, 0, 0, new PublicOrganizer("Salle Métropole"), "swan_lake");
//    private static final Event event4 = new Event("", new DateAdapter().getDate().toString(), new Location(46.52132870748288, 6.57909276639799, "1015 Lausanne"), "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, new PublicOrganizer("UNIL"), "swan_lake");
//    private static final Event event5 = new Event("", new DateAdapter(2021, 04, 12).getDate().toString(), new Location(46.51794768231773, 6.625629946258326, "Place de la Gare 16, 1003 Lausanne"), "SocialObject description goes here", SocialObject.Type.Museum, 0, 0, new PublicOrganizer("Musée Cantonal des Beaux-Arts"), "art");
//    private static final Event event6 = new Event("", new DateAdapter(2021, 04, 10).getDate().toString(), new Location(46.518689, 6.568067, "Rolex Learning Center, 1015 Ecublens"), "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, new PublicOrganizer("EPFL"), "rolex");
//    private static final Event event7 = new Event("", new DateAdapter(2021, 04, 11).getDate().toString(), new Location(46.51263429446572, 6.612488611335794, "Avenue Gustave Doret, 1007 Lausanne"), "SocialObject description goes here", SocialObject.Type.Theatre, 0, 0, new PublicOrganizer("Théâtre Vidy-Lausanne"), "swan_lake");
//    private static final Event event8 = new Event(", new DateAdapter(2021, 04, 25).getDate().toString(), new Location(46.517789, 6.636917, "Avenue du Théâtre 12, 1002 Lausanne"), "SocialObject description goes here", SocialObject.Type.Ballet, 0, 0, new PublicOrganizer("Opéra de Lausanne"), "swan_lake");
//    private static final Event event9 = new Event("Concert", new DateAdapter(2021, 04, 16).getDate().toString(), new Location(46.52210876137052, 6.628016637001172, "Rue de Genève 12, 1003 Lausanne"), "SocialObject description goes here", SocialObject.Type.Concert, 0, 0, new PublicOrganizer("Salle Métropole"), "swan_lake");
//    private static final Event event10 = new Event("La flute enchantée", new DateAdapter(2021, 04, 01).getDate().toString(), new Location(46.517789, 6.636917, "Avenue du Théâtre 12, 1002 Lausanne"), "SocialObject description goes here", SocialObject.Type.Opera, 0, 0, new PublicOrganizer("Opéra de Lausanne"), "swan_lake");


    public EventDB() {
        event1.setUuid(uuid1);
        event1.addTag(new Tag("\uD83D\uDCF1", "Technology"));
        event1.addTag(new Tag("\uD83D\uDCF1", "Conference"));
        event1.addTag(new Tag("\uD83D\uDCF1", "Artificial Intelligence"));
        events.put(event1.getUuid().toString(), event1);

        event2.setUuid(uuid2);
        event2.addTag(new Tag("\uD83D\uDCF1", "Opera"));
        event2.addTag(new Tag("\uD83D\uDCF1", "Singing"));
        event2.addTag(new Tag("\uD83D\uDCF1", "Art"));
        events.put(event2.getUuid().toString(), event2);

        event3.setUuid(uuid3);
        event3.addTag(new Tag("\uD83D\uDCF1", "Concert"));
        event3.addTag(new Tag("\uD83D\uDCF1", "Rock"));
        event3.addTag(new Tag("\uD83D\uDCF1", "Singing"));
        events.put(event3.getUuid().toString(), event3);

        event4.setUuid(uuid4);
        event4.addTag(new Tag("\uD83D\uDCF1", "Technology"));
        event4.addTag(new Tag("\uD83D\uDCF1", "Conference"));
        event4.addTag(new Tag("\uD83D\uDCF1", "BitCoin"));
        events.put(event4.getUuid().toString(), event4);

        event5.setUuid(uuid5);
        event5.addTag(new Tag("\uD83D\uDCF1", "Museum"));
        event5.addTag(new Tag("\uD83D\uDCF1", "Exposition"));
        event5.addTag(new Tag("\uD83D\uDCF1", "Art"));
        events.put(event5.getUuid().toString(), event5);

        event6.setUuid(uuid6);
        event6.addTag(new Tag("\uD83D\uDCF1", "Technology"));
        event6.addTag(new Tag("\uD83D\uDCF1", "Conference"));
        event6.addTag(new Tag("\uD83D\uDCF1", "Big Data"));
        events.put(event6.getUuid().toString(), event6);

        event7.setUuid(uuid7);
        event7.addTag(new Tag("\uD83D\uDCF1", "Theatre"));
        event7.addTag(new Tag("\uD83D\uDCF1", "Art"));
        events.put(event7.getUuid().toString(), event7);

        event8.setUuid(uuid8);
        event8.addTag(new Tag("\uD83D\uDCF1", "Ballet"));
        event8.addTag(new Tag("\uD83D\uDCF1", "Dance"));
        event8.addTag(new Tag("\uD83D\uDCF1", "Art"));
        events.put(event8.getUuid().toString(), event8);

        event9.setUuid(uuid9);
        event9.addTag(new Tag("\uD83D\uDCF1", "Concert"));
        event9.addTag(new Tag("\uD83D\uDCF1", "Pop"));
        event9.addTag(new Tag("\uD83D\uDCF1", "Singing"));
        events.put(event9.getUuid().toString(), event9);

        event10.setUuid(uuid10);
        event10.addTag(new Tag("\uD83D\uDCF1", "Opera"));
        event10.addTag(new Tag("\uD83D\uDCF1", "Singing"));
        event10.addTag(new Tag("\uD83D\uDCF1", "Art"));
        events.put(event10.getUuid().toString(), event10);
    }

    public SocialObject getSocialObj(String uuid) {
        return events.get(uuid);
    }

    public List<SocialObject> toList() {
        List<SocialObject> ret = new ArrayList<>();
        for (SocialObject e : events.values()) {
            ret.add(e);
        }
        return ret;
    }

}