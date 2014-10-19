package edu.hastings.hastingscollege.model;

import java.util.HashMap;
import java.util.List;

public class EventModel {

    public String eventName;
    public String eventWeek;
    public String[] eventDays;
    public List<List<HashMap<String, String>>> eventsOfDay;

    public EventModel(String name, String week, String[] days, List<List<HashMap<String, String>>> dailyEvents) {
        this.eventName = name;
        this.eventWeek = week;
        this.eventDays = days;
        this.eventsOfDay = dailyEvents;
    }
}
