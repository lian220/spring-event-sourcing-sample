package web.api.eventSourcing.cqrs.aggregates;

import web.api.eventSourcing.cqrs.commands.CreateUserCommand;
import web.api.eventSourcing.cqrs.commands.UpdateUserCommand;
import web.api.eventSourcing.cqrs.repository.UserWriteRepository;
import web.api.eventSourcing.domain.User;

public class UserAggregate {

    private UserWriteRepository writeRepository;

    public UserAggregate(UserWriteRepository repository) {
        this.writeRepository = repository;
    }

    public User handleCreateUserCommand(CreateUserCommand command) {
        User user = new User(command.getUserId(), command.getFirstName(), command.getLastName());
        writeRepository.addUser(user.getUserid(), user);
        return user;
    }

    public User handleUpdateUserCommand(UpdateUserCommand command) {
        User user = writeRepository.getUser(command.getUserId());
        user.setAddresses(command.getAddresses());
        user.setContacts(command.getContacts());
        writeRepository.addUser(user.getUserid(), user);
        return user;
    }

}
