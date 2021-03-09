package ncnf.event;

import java.util.UUID;

import ncnf.organizer.Organizer;

public class PublicEvent implements Event {

    private final Organizer organiser;
    private final EventType eventType;
    private final String name;
    private final String uniqueID;
    //temporary placeholder for event content
    private String data;

    public PublicEvent(Organizer organiser, EventType type, String name, String data){
        this.organiser = organiser;
        this.eventType = type;
        this.name = name;
        this.data = data;
        uniqueID = UUID.randomUUID().toString();
    }


    @Override
    public Organizer getOrganizer() {
        return organiser;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUID() {
        return uniqueID;
    }

    @Override
    public EventType getType() {
        return eventType;
    }

    @Override
    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data = data;
    }


}
