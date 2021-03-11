package ncnf.main.map;

import java.util.ArrayList;

public class PlaceholderEvent {

    private String name;
    private float latitude;
    private float longitude;

    public PlaceholderEvent(String name, float latitude, float longitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public static ArrayList<PlaceholderEvent> getTestEvents(){
        ArrayList<PlaceholderEvent> list = new ArrayList<>();
        //list.add(new PlaceholderEvent("Roméo et Juliette, Theatre du Leman", 46.2101f, 6.1510f));
        list.add(new PlaceholderEvent("Math Conference, EPFL, 1pm March 3rd", 46.5191f, 6.5668f));
        list.add(new PlaceholderEvent("Les Noces de Figaro, Lausanne Opera, 7pm March 12th", 46.5180f, 6.6369f));
        list.add(new PlaceholderEvent("Caravan Palace, Les Docks, 9pm March 12th", 46.5224f, 6.6193f));
        list.add(new PlaceholderEvent("Cours de danse, Espace Arsenic, 3pm March 13th", 46.5227f, 6.6216f));

        return list;
    }

}
