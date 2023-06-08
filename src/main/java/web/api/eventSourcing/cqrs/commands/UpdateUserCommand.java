package web.api.eventSourcing.cqrs.commands;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import web.api.eventSourcing.domain.Address;
import web.api.eventSourcing.domain.Contact;

@Data
@AllArgsConstructor
public class UpdateUserCommand {

    private String userId;
    private Set<Address> addresses = new HashSet<>();
    private Set<Contact> contacts = new HashSet<>();

}
