package com.alexstyl.specialdates.events.peopleevents;

import com.alexstyl.specialdates.DisplayName;
import com.alexstyl.specialdates.contact.Contact;
import com.alexstyl.specialdates.date.ContactEvent;
import com.alexstyl.specialdates.date.Date;
import com.alexstyl.specialdates.events.namedays.NameCelebrations;
import com.alexstyl.specialdates.events.namedays.NamedayLocale;
import com.alexstyl.specialdates.events.namedays.NamedayPreferences;
import com.alexstyl.specialdates.events.namedays.calendar.NamedayCalendar;
import com.alexstyl.specialdates.events.namedays.calendar.resource.NamedayCalendarProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeopleNamedaysCalculator {

    private final NamedayPreferences namedayPreferences;
    private final NamedayCalendarProvider namedayCalendarProvider;
    private final DeviceContactsQuery deviceContactsQuery;

    public PeopleNamedaysCalculator(NamedayPreferences namedayPreferences,
                                    NamedayCalendarProvider namedayCalendarProvider,
                                    DeviceContactsQuery deviceContactsQuery
    ) {
        this.namedayPreferences = namedayPreferences;
        this.namedayCalendarProvider = namedayCalendarProvider;
        this.deviceContactsQuery = deviceContactsQuery;
    }

    public List<ContactEvent> loadDeviceStaticNamedays() {
        List<ContactEvent> namedayEvents = new ArrayList<>();
        Set<Long> contactIDs = new HashSet<>();

        List<Contact> contacts = deviceContactsQuery.getAllContacts();
        for (Contact contact : contacts) {
            long contactID = contact.getContactID();
            if (contactIDs.contains(contactID)) {
                continue;
            }
            contactIDs.add(contactID);

            DisplayName displayName = contact.getDisplayName();
            HashSet<Date> namedays = new HashSet<>();
            for (String firstName : displayName.getFirstNames()) {
                NameCelebrations nameDays = getNamedaysOf(firstName);
                if (nameDays.containsNoDate()) {
                    continue;
                }
                int namedaysCount = nameDays.size();
                for (int i = 0; i < namedaysCount; i++) {
                    Date date = nameDays.getDate(i);
                    if (namedays.contains(date)) {
                        continue;
                    }
                    ContactEvent event = new ContactEvent(EventType.NAMEDAY, date, contact);
                    namedayEvents.add(event);
                    namedays.add(date);
                }
            }
        }

        return namedayEvents;
    }

    public List<ContactEvent> loadSpecialNamedays() {
        List<ContactEvent> namedayEvents = new ArrayList<>();
        Set<Long> contactIDs = new HashSet<>();

        for (Contact contact : deviceContactsQuery.getAllContacts()) {
            long contactID = contact.getContactID();
            if (contactIDs.contains(contactID)) {
                continue;
            }
            contactIDs.add(contactID);
            for (String firstName : contact.getDisplayName().getFirstNames()) {
                NameCelebrations nameDays = getSpecialNamedaysOf(firstName);
                if (nameDays.containsNoDate()) {
                    continue;
                }

                int namedaysCount = nameDays.size();
                for (int i = 0; i < namedaysCount; i++) {
                    Date date = nameDays.getDate(i);
                    ContactEvent nameday = new ContactEvent(EventType.NAMEDAY, date, contact);
                    namedayEvents.add(nameday);
                }
            }
        }
        return namedayEvents;
    }

    private NameCelebrations getNamedaysOf(String given) {
        NamedayCalendar namedayCalendar = getNamedayCalendar();
        return namedayCalendar.getNormalNamedaysFor(given);
    }

    private NameCelebrations getSpecialNamedaysOf(String firstName) {
        NamedayCalendar namedayCalendar = getNamedayCalendar();
        return namedayCalendar.getSpecialNamedaysFor(firstName);
    }

    public NamedayCalendar getNamedayCalendar() {
        NamedayLocale locale = namedayPreferences.getSelectedLanguage();
        int todayYear = Date.today().getYear();
        return namedayCalendarProvider.loadNamedayCalendarForLocale(locale, todayYear);
    }
}
