package me.julb.applications.helloworld.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.helloworld.entities.UserEntity;
import me.julb.applications.helloworld.repositories.UserRepository;
import me.julb.library.dto.simple.message.MessageDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.library.utility.validator.constraints.Identifier;
import me.julb.springbootstarter.persistence.mongodb.specifications.SearchSpecification;

/**
 * The hello controller.
 * <P>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    /**
     * The repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The message source.
     */
    @Autowired
    private MessageSource messageSource;

    // ------------------------------------------ Read methods.

    /**
     * Finds all users.
     * @param searchable the search parameters.
     * @param pageable the pageable information.
     * @return the paged list of users.
     */
    @GetMapping
    public Page<UserEntity> findAll(Searchable searchable, Pageable pageable) {
        return userRepository.findAll(new SearchSpecification<UserEntity>(searchable), pageable);
    }

    /**
     * Finds a user by its ID.
     * @param id the ID.
     * @return the user.
     */
    @GetMapping("/{id}")
    public UserEntity findOne(@PathVariable("id") @Identifier String id) {
        return userRepository.findById(id).get();
    }

    /**
     * Says hello to a user by its ID.
     * @param id the ID.
     * @return the message.
     */
    @GetMapping("/{id}/hello")
    public MessageDTO sayHelloTo(@PathVariable("id") @Identifier String id) {
        UserEntity userEntity = userRepository.findById(id).get();
        return new MessageDTO(messageSource.getMessage("hello", new String[] {userEntity.getName()}, LocaleContextHolder.getLocale()));
    }

    // ------------------------------------------ Write methods.

    /**
     * Delete a user by its ID.
     * @param id the ID.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") @Identifier String id) {
        userRepository.deleteById(id);
    }

}
