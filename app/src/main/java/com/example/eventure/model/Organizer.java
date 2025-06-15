package com.example.eventure.model;

import java.util.List;

public class Organizer extends User {
    private List<Event> myEvents;

    public List<Event> getMyEvents() {
        return myEvents;
    }

    public void setMyEvents(List<Event> myEvents) {
        this.myEvents = myEvents;
    }
}
