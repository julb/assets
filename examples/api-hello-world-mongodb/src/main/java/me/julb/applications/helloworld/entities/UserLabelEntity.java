package me.julb.applications.helloworld.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The user label entity.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "code")
public class UserLabelEntity {

    //@formatter:off
     /**
     * The code attribute.
     * -- GETTER --
     * Getter for {@link #code} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #code} property.
     * @param code the value to set.
     */
     //@formatter:on
    private String code;

    //@formatter:off
     /**
     * The color attribute.
     * -- GETTER --
     * Getter for {@link #color} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #color} property.
     * @param color the value to set.
     */
     //@formatter:on
    private String color;

}