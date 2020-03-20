package io.julb.applications.helloworld.entities;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * The user entity.
 * <P>
 * @author Julb.
 */
@Document(collection = "users")
@Getter
@Setter
public class UserEntity {

    //@formatter:off
     /**
     * The id attribute.
     * -- GETTER --
     * Getter for {@link #id} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #id} property.
     * @param id the value to set.
     */
     //@formatter:on
    @Id
    private String id;

    //@formatter:off
     /**
     * The name attribute.
     * -- GETTER --
     * Getter for {@link #name} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #name} property.
     * @param name the value to set.
     */
     //@formatter:on
    @Indexed(unique = true)
    private String name;

    //@formatter:off
     /**
     * The tags attribute.
     * -- GETTER --
     * Getter for {@link #tags} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #tags} property.
     * @param tags the value to set.
     */
     //@formatter:on
    private List<String> tags;

    //@formatter:off
     /**
     * The attributes attribute.
     * -- GETTER --
     * Getter for {@link #attributes} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #attributes} property.
     * @param attributes the value to set.
     */
     //@formatter:on
    private Map<String, String> attributes;

    //@formatter:off
     /**
     * The user preferences attribute.
     * -- GETTER --
     * Getter for {@link #preferences} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #preferences} property.
     * @param preferences the value to set.
     */
     //@formatter:on
    private UserPreferencesEntity preferences = new UserPreferencesEntity();

    //@formatter:off
     /**
     * The labels attribute.
     * -- GETTER --
     * Getter for {@link #labels} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #labels} property.
     * @param labels the value to set.
     */
     //@formatter:on
    private Set<UserLabelEntity> labels;

}