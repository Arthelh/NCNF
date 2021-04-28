package com.ncnf.user;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

public class UserLocation {

    private GeoPoint geoPoint;
    private @ServerTimestamp String timeStamp;

    private User user;

    public UserLocation(GeoPoint gp, String ts, User us) {
        geoPoint = gp;
        timeStamp = ts;
        user = us;
    }

    public UserLocation() {

    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "geoPoint=" + geoPoint +
                ", timeStamp='" + timeStamp + '\'' +
                ", user=" + user +
                '}';
    }
}
