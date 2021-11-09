package me.julb.applications.helloworld.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.helloworld.consumers.ApiHelloWorldFeignClient;
import me.julb.library.dto.simple.message.MessageDTO;
import me.julb.library.utility.data.search.Searchable;
import me.julb.springbootstarter.consumer.utility.SearchableAndPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiPageable;
import me.julb.springbootstarter.web.annotations.openapi.OpenApiSearchable;

/**
 * The hello controller.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloController {

    /**
     * The feign client to say hello.
     */
    @Autowired
    private ApiHelloWorldFeignClient apiHelloWorldFeignClient;

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
    public Page<MessageDTO> sayHello(Searchable searchable, Pageable pageable) {
        return apiHelloWorldFeignClient.sayHello(SearchableAndPageable.of(searchable, pageable));
    }

    /**
     * Gets the hello message.
     * @param name the name.
     * @return the hello message.
     */
    @GetMapping("/{name}")
    public MessageDTO sayHelloTo(@PathVariable("name") String name) {
        return apiHelloWorldFeignClient.sayHelloTo(name);
    }

    // ------------------------------------------ Write methods.
}
