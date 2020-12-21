package me.julb.applications.helloworld.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.helloworld.configurations.properties.CustomConfigurationProperties;
import me.julb.applications.helloworld.services.dto.UserDTO;
import me.julb.library.dto.simple.message.MessageDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.springbootstarter.core.context.TrademarkContextHolder;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;
import me.julb.springbootstarter.web.utility.PageUtility;

/**
 * The hello controller.
 * <P>
 * @author Julb.
 */
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloController {

    /**
     * The app properties.
     */
    @Autowired
    private CustomConfigurationProperties properties;

    /**
     * The message source.
     */
    @Autowired
    private MessageSource messageSource;

    // ------------------------------------------ Read methods.

    /**
     * Gets the hello message.
     * @param searchable the search request.
     * @param pageable the pageable information.
     * @return the hello message.
     */
    @GetMapping
    @OpenApiPageable
    @OpenApiSearchable
    @Operation(summary = "Say hello to everyone !")
    public Page<MessageDTO> sayHello(Searchable searchable, Pageable pageable) {
        LOGGER.info("Receiving hello request: {}, {}.", searchable, pageable);
        List<MessageDTO> messages = new ArrayList<MessageDTO>();
        for (String name : properties.getNames()) {
            messages.add(new MessageDTO(messageSource.getMessage("hello", new String[] {name}, LocaleContextHolder.getLocale())));
        }
        return PageUtility.of(messages, pageable);
    }

    /**
     * Gets the hello message.
     * @param name the name.
     * @return the hello message.
     */
    @GetMapping("/{name}")
    @Operation(summary = "Say hello to someone !")
    @Parameters({@Parameter(name = "name", description = "User name")})
    public MessageDTO sayHelloTo(@Size(max = 3) @Pattern(regexp = "^[0-9]{1,3}$") @PathVariable("name") String name) {
        LOGGER.info("Current trademark: {}.", TrademarkContextHolder.getTrademark());
        return new MessageDTO(messageSource.getMessage("hello", new String[] {name}, LocaleContextHolder.getLocale()));
    }

    // ------------------------------------------ Write methods.

    /**
     * Sings the hello message.
     * @param user the user.
     * @return the song message.
     */
    @PostMapping()
    @Operation(summary = "Sing hello to someone !")
    public MessageDTO singHelloTo(@RequestBody @Parameter(description = "User to sing to.", required = true) UserDTO user) {
        return new MessageDTO(messageSource.getMessage("hello", new String[] {user.getName()}, LocaleContextHolder.getLocale()));
    }
}
