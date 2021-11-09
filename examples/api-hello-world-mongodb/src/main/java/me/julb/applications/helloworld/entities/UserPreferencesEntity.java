package me.julb.applications.helloworld.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * The user preferences entity.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
public class UserPreferencesEntity {

    //@formatter:off
     /**
     * The timezone attribute.
     * -- GETTER --
     * Getter for {@link #timezone} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #timezone} property.
     * @param timezone the value to set.
     */
     //@formatter:on
    private String timezone;
}