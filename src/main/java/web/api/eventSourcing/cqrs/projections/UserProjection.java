package web.api.eventSourcing.cqrs.projections;

import java.util.Set;

import web.api.eventSourcing.cqrs.queries.AddressByRegionQuery;
import web.api.eventSourcing.cqrs.queries.ContactByTypeQuery;
import web.api.eventSourcing.cqrs.repository.UserReadRepository;
import web.api.eventSourcing.domain.Address;
import web.api.eventSourcing.domain.Contact;
import web.api.eventSourcing.domain.UserAddress;
import web.api.eventSourcing.domain.UserContact;

public class UserProjection {

    private UserReadRepository repository;

    public UserProjection(UserReadRepository repository) {
        this.repository = repository;
    }

    public Set<Contact> handle(ContactByTypeQuery query) throws Exception {
        UserContact userContact = repository.getUserContact(query.getUserId());
        if (userContact == null)
            throw new Exception("User does not exist.");
        return userContact.getContactByType()
            .get(query.getContactType());
    }

    public Set<Address> handle(AddressByRegionQuery query) throws Exception {
        UserAddress userAddress = repository.getUserAddress(query.getUserId());
        if (userAddress == null)
            throw new Exception("User does not exist.");
        return userAddress.getAddressByRegion()
            .get(query.getState());
    }

}
