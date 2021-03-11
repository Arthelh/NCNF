package ncnf.organizer;

public class PublicOrganizer implements Organizer{
    private final String name;

    public PublicOrganizer(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

}
