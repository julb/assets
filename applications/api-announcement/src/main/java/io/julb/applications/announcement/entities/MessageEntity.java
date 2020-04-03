package io.julb.applications.announcement.entities;

import io.julb.library.dto.simple.content.ContentCreationDTO;
import io.julb.library.dto.simple.content.ContentDTO;
import io.julb.library.dto.simple.content.ContentPatchDTO;
import io.julb.library.dto.simple.content.ContentUpdateDTO;
import io.julb.library.utility.validator.constraints.Description;
import io.julb.library.utility.validator.constraints.MimeType;
import io.julb.springbootstarter.mapping.annotations.ObjectMappingFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The message entity.
 * <P>
 * @author Julb.
 */
@ObjectMappingFactory(creation = ContentCreationDTO.class, patch = ContentPatchDTO.class, read = ContentDTO.class, update = ContentUpdateDTO.class)
@Getter
@Setter
public class MessageEntity {

    //@formatter:off
     /**
     * The mimeType attribute.
     * -- GETTER --
     * Getter for {@link #mimeType} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #mimeType} property.
     * @param mimeType the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @MimeType
    private String mimeType;

    //@formatter:off
     /**
     * The content attribute.
     * -- GETTER --
     * Getter for {@link #content} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #content} property.
     * @param content the value to set.
     */
     //@formatter:on
    @NotNull
    @NotBlank
    @Description
    private String content;
}
