package ncnf.event;

import java.util.Date;
import java.util.UUID;

import ncnf.organizer.Organizer;

public class PublicEvent implements Event {

    private final Organizer organiser;
    private final EventType eventType;
    private final String name;
    private final String uniqueID;
    private final Date date;
    //temporary placeholder for event content
    private String data;

    public PublicEvent(Organizer organiser, EventType type, String name, String data, Date date){
        this.organiser = organiser;
        this.eventType = type;
        this.name = name;
        this.data = data;
        uniqueID = UUID.randomUUID().toString();
        this.date = date;
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

    @Override
    public Date getDate() {
        return date;
    }

    public void setData(String data){
        this.data = data;
    }


}
