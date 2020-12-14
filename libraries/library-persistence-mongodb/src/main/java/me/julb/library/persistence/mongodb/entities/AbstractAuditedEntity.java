package me.julb.library.persistence.mongodb.entities;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import me.julb.library.utility.validator.constraints.DateTimeISO8601;

/**
 * Description of an base entity.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
@ToString
public abstract class AbstractAuditedEntity {

    //@formatter:off
     /**
     * The createdAt attribute.
     * -- GETTER --
     * Getter for {@link #createdAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #createdAt} property.
     * @param createdAt the value to set.
     */
     //@formatter:on
    @NotNull
    @DateTimeISO8601
    private String createdAt;

    //@formatter:off
     /**
     * The lastUpdatedAt attribute.
     * -- GETTER --
     * Getter for {@link #lastUpdatedAt} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #lastUpdatedAt} property.
     * @param lastUpdatedAt the value to set.
     */
     //@formatter:on
    @NotNull
    @DateTimeISO8601
    private String lastUpdatedAt;

}