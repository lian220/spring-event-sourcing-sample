package web.api.eventSourcing.es.service;

import java.util.List;

import web.api.eventSourcing.domain.Address;
import web.api.eventSourcing.domain.Contact;
import web.api.eventSourcing.domain.User;
import web.api.eventSourcing.es.events.Event;
import web.api.eventSourcing.es.events.UserAddressAddedEvent;
import web.api.eventSourcing.es.events.UserAddressRemovedEvent;
import web.api.eventSourcing.es.events.UserContactAddedEvent;
import web.api.eventSourcing.es.events.UserContactRemovedEvent;
import web.api.eventSourcing.es.events.UserCreatedEvent;
import web.api.eventSourcing.es.repository.EventStore;

public class UserUtility {

    public static User recreateUserState(EventStore store, String userId) {
        User user = null;

        List<Event> events = store.getEvents(userId);
        for (Event event : events) {
            if (event instanceof UserCreatedEvent) {
                UserCreatedEvent e = (UserCreatedEvent) event;
                user = new User(e.getUserId(), e.getFirstName(), e.getLastName());
            }
            if (event instanceof UserAddressAddedEvent) {
                UserAddressAddedEvent e = (UserAddressAddedEvent) event;
                Address address = new Address(e.getCity(), e.getState(), e.getPostCode());
                if (user != null)
                    user.getAddresses()
                        .add(address);
            }
            if (event instanceof UserAddressRemovedEvent) {
                UserAddressRemovedEvent e = (UserAddressRemovedEvent) event;
                Address address = new Address(e.getCity(), e.getState(), e.getPostCode());
                if (user != null)
                    user.getAddresses()
                        .remove(address);
            }
            if (event instanceof UserContactAddedEvent) {
                UserContactAddedEvent e = (UserContactAddedEvent) event;
                Contact contact = new Contact(e.getContactType(), e.getContactDetails());
                if (user != null)
                    user.getContacts()
                        .add(contact);
            }
            if (event instanceof UserContactRemovedEvent) {
                UserContactRemovedEvent e = (UserContactRemovedEvent) event;
                Contact contact = new Contact(e.getContactType(), e.getContactDetails());
                if (user != null)
                    user.getContacts()
                        .remove(contact);
            }
        }

        return user;
    }

}
