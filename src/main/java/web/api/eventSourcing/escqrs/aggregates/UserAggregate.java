package web.api.eventSourcing.escqrs.aggregates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import web.api.eventSourcing.cqrs.commands.CreateUserCommand;
import web.api.eventSourcing.cqrs.commands.UpdateUserCommand;
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
import web.api.eventSourcing.es.service.UserUtility;

public class UserAggregate {

    private EventStore writeRepository;

    public UserAggregate(EventStore repository) {
        this.writeRepository = repository;
    }

    public List<Event> handleCreateUserCommand(CreateUserCommand command) {
        UserCreatedEvent event = new UserCreatedEvent(command.getUserId(), command.getFirstName(), command.getLastName());
        writeRepository.addEvent(command.getUserId(), event);
        return Arrays.asList(event);
    }

    public List<Event> handleUpdateUserCommand(UpdateUserCommand command) {
        User user = UserUtility.recreateUserState(writeRepository, command.getUserId());
        List<Event> events = new ArrayList<>();

        List<Contact> contactsToRemove = user.getContacts()
            .stream()
            .filter(c -> !command.getContacts()
                .contains(c))
            .collect(Collectors.toList());
        for (Contact contact : contactsToRemove) {
            UserContactRemovedEvent contactRemovedEvent = new UserContactRemovedEvent(contact.getType(), contact.getDetail());
            events.add(contactRemovedEvent);
            writeRepository.addEvent(command.getUserId(), contactRemovedEvent);
        }

        List<Contact> contactsToAdd = command.getContacts()
            .stream()
            .filter(c -> !user.getContacts()
                .contains(c))
            .collect(Collectors.toList());
        for (Contact contact : contactsToAdd) {
            UserContactAddedEvent contactAddedEvent = new UserContactAddedEvent(contact.getType(), contact.getDetail());
            events.add(contactAddedEvent);
            writeRepository.addEvent(command.getUserId(), contactAddedEvent);
        }

        List<Address> addressesToRemove = user.getAddresses()
            .stream()
            .filter(a -> !command.getAddresses()
                .contains(a))
            .collect(Collectors.toList());
        for (Address address : addressesToRemove) {
            UserAddressRemovedEvent addressRemovedEvent = new UserAddressRemovedEvent(address.getCity(), address.getState(), address.getPostcode());
            events.add(addressRemovedEvent);
            writeRepository.addEvent(command.getUserId(), addressRemovedEvent);
        }

        List<Address> addressesToAdd = command.getAddresses()
            .stream()
            .filter(a -> !user.getAddresses()
                .contains(a))
            .collect(Collectors.toList());
        for (Address address : addressesToAdd) {
            UserAddressAddedEvent addressAddedEvent = new UserAddressAddedEvent(address.getCity(), address.getState(), address.getPostcode());
            events.add(addressAddedEvent);
            writeRepository.addEvent(command.getUserId(), addressAddedEvent);
        }

        return events;
    }

}
