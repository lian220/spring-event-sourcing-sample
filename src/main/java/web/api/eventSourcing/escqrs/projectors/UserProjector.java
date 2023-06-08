package web.api.eventSourcing.escqrs.projectors;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import web.api.eventSourcing.cqrs.repository.UserReadRepository;
import web.api.eventSourcing.domain.Address;
import web.api.eventSourcing.domain.Contact;
import web.api.eventSourcing.domain.UserAddress;
import web.api.eventSourcing.domain.UserContact;
import web.api.eventSourcing.es.events.Event;
import web.api.eventSourcing.es.events.UserAddressAddedEvent;
import web.api.eventSourcing.es.events.UserAddressRemovedEvent;
import web.api.eventSourcing.es.events.UserContactAddedEvent;
import web.api.eventSourcing.es.events.UserContactRemovedEvent;

public class UserProjector {

    UserReadRepository readRepository = new UserReadRepository();

    public UserProjector(UserReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public void project(String userId, List<Event> events) {

        for (Event event : events) {
            if (event instanceof UserAddressAddedEvent)
                apply(userId, (UserAddressAddedEvent) event);
            if (event instanceof UserAddressRemovedEvent)
                apply(userId, (UserAddressRemovedEvent) event);
            if (event instanceof UserContactAddedEvent)
                apply(userId, (UserContactAddedEvent) event);
            if (event instanceof UserContactRemovedEvent)
                apply(userId, (UserContactRemovedEvent) event);
        }

    }

    public void apply(String userId, UserAddressAddedEvent event) {
        Address address = new Address(event.getCity(), event.getState(), event.getPostCode());
        UserAddress userAddress = Optional.ofNullable(readRepository.getUserAddress(userId))
            .orElse(new UserAddress());
        Set<Address> addresses = Optional.ofNullable(userAddress.getAddressByRegion()
            .get(address.getState()))
            .orElse(new HashSet<>());
        addresses.add(address);
        userAddress.getAddressByRegion()
            .put(address.getState(), addresses);
        readRepository.addUserAddress(userId, userAddress);
    }

    public void apply(String userId, UserAddressRemovedEvent event) {
        Address address = new Address(event.getCity(), event.getState(), event.getPostCode());
        UserAddress userAddress = readRepository.getUserAddress(userId);
        if (userAddress != null) {
            Set<Address> addresses = userAddress.getAddressByRegion()
                .get(address.getState());
            if (addresses != null)
                addresses.remove(address);
            readRepository.addUserAddress(userId, userAddress);
        }
    }

    public void apply(String userId, UserContactAddedEvent event) {
        Contact contact = new Contact(event.getContactType(), event.getContactDetails());
        UserContact userContact = Optional.ofNullable(readRepository.getUserContact(userId))
            .orElse(new UserContact());
        Set<Contact> contacts = Optional.ofNullable(userContact.getContactByType()
            .get(contact.getType()))
            .orElse(new HashSet<>());
        contacts.add(contact);
        userContact.getContactByType()
            .put(contact.getType(), contacts);
        readRepository.addUserContact(userId, userContact);
    }

    public void apply(String userId, UserContactRemovedEvent event) {
        Contact contact = new Contact(event.getContactType(), event.getContactDetails());
        UserContact userContact = readRepository.getUserContact(userId);
        if (userContact != null) {
            Set<Contact> contacts = userContact.getContactByType()
                .get(contact.getType());
            if (contacts != null)
                contacts.remove(contact);
            readRepository.addUserContact(userId, userContact);
        }
    }
}
