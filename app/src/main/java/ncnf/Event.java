package ncnf;

public interface Event {
    Organizer getOrganizer();
    String getName();
    String getUID();
    EventType getType();
    String getData();
}
