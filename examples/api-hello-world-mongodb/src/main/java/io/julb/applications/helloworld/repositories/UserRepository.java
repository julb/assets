package io.julb.applications.helloworld.repositories;

import io.julb.applications.helloworld.entities.UserEntity;
import io.julb.springbootstarter.persistence.mongodb.repositories.MongoSpecificationExecutor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The paging and sorting repository.
 * <P>
 * @author Julb.
 */
public interface UserRepository extends MongoRepository<UserEntity, String>, MongoSpecificationExecutor<UserEntity> {

    /**
     * Checks if a user exists by its name.
     * @param name the name.
     * @return <code>true</code> if exists, <code>false</code> otherwise.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Finds the list of users having given tag.
     * @param tag the tag.
     * @param pageable the pageable.
     * @return the paged list of users.
     */
    Page<UserEntity> findByTags(String tag, Pageable pageable);

    /**
     * Checks if a user has specified timezone.
     * @param timezone the timezone.
     * @param pageable the pageable.
     * @return <code>true</code> if exists, <code>false</code> otherwise.
     */
    Page<UserEntity> findByPreferences_Timezone(String timezone, Pageable pageable);

    /**
     * Finds the list of users having given label code.
     * @param code the code.
     * @param pageable the pageable.
     * @return the paged list of users.
     */
    Page<UserEntity> findByLabels_Code(String code, Pageable pageable);
}