package me.julb.applications.helloworld.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.helloworld.configurations.properties.CustomConfigurationProperties;
import me.julb.applications.helloworld.entities.UserEntity;
import me.julb.applications.helloworld.entities.UserLabelEntity;
import me.julb.applications.helloworld.repositories.UserRepository;
import me.julb.library.utility.identifier.IdentifierUtility;

/**
 * The initialize data controller.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
public class InitializeDataController {

    /**
     * The app properties.
     */
    @Autowired
    private CustomConfigurationProperties properties;

    /**
     * The repository.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Initializes the data.
     */
    @GetMapping("/init")
    @Transactional
    public void init() {
        userRepository.deleteAll();
        for (String name : properties.getNames()) {
            UserEntity user = new UserEntity();
            user.setId(IdentifierUtility.generateId());
            user.setName(name);

            user.setTags(new ArrayList<String>());
            user.getTags().add("tag1");
            user.getTags().add("tag2");
            user.getTags().add("tag3");
            user.getTags().add(name);

            user.setAttributes(new HashMap<String, String>());
            user.getAttributes().put("attr1", "value1");
            user.getAttributes().put("attr2", "value2");
            user.getAttributes().put("attr3", "value3");

            user.getPreferences().setTimezone("Europe/Paris");

            user.setLabels(new HashSet<UserLabelEntity>());

            for (int i = 1; i <= 5; i++) {
                UserLabelEntity label = new UserLabelEntity();
                label.setCode(name + i);
                label.setColor("#00000" + i);
                user.getLabels().add(label);
            }

            userRepository.save(user);

            if (name.equalsIgnoreCase("david")) {
                // throw new RuntimeException();
            }
        }

        // Exists.
        boolean aliceExists = userRepository.existsByNameIgnoreCase("alice");
        System.out.println("Exists: " + aliceExists);

        // Exists.
        Page<UserEntity> findByTags = userRepository.findByTags("tag1", PageRequest.of(0, 20));
        System.out.println("Tags: " + findByTags);

        // Exists.
        Page<UserEntity> findByAlice = userRepository.findByTags("Alice", PageRequest.of(0, 20));
        System.out.println("Tags: " + findByAlice);

        // Exists.
        Page<UserEntity> findByPrefs = userRepository.findByPreferences_Timezone("Europe/Paris", PageRequest.of(0, 20));
        System.out.println("Prefs: " + findByPrefs);

        // Exists.
        Page<UserEntity> findByLabels = userRepository.findByLabels_Code("Alice2", PageRequest.of(0, 20));
        System.out.println("Labels: " + findByLabels);
    }

    /**
     * Deletes the data.
     */
    @GetMapping("/delete")
    public void delete() {
        userRepository.deleteAll();
    }
}
