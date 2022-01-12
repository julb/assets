/**
 * MIT License
 *
 * Copyright (c) 2017-2021 Julb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.julb.applications.jwks.controllers;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.julb.applications.jwks.services.IKeyService;
import me.julb.library.utility.validator.constraints.JSONWebKeyId;
import me.julb.library.utility.validator.constraints.JSONWebKeysetName;

/**
 * The keyset controller.
 * <br>
 * @author Julb.
 */
@RestController
@Validated
@RequestMapping(path = "/keysets/{keysetName:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
public class KeyController {

    /**
     * The keyset service.
     */
    @Autowired
    private IKeyService keyService;

    // ------------------------------------------ Read methods.

    /**
     * Finds the keyset by its name.
     * @param keysetName the keyset name.
     * @return a JSON representation of the keyset.
     */
    @Operation(summary = "gets the jwks representation of this keyset")
    @GetMapping(path = {"", "/keys"})
    @ResponseBody
    public Mono<String> findKeysetByName(@NotNull @JSONWebKeysetName @PathVariable String keysetName) {
        return keyService.findAll(keysetName);
    }

    /**
     * Finds the keyset's key by its name and the ID.
     * @param keysetName the keyset name.
     * @param keyId the key ID.
     * @return a JSON representation of the key.
     */
    @Operation(summary = "gets the jwk representation of this key")
    @GetMapping(path = "/keys/{keyId:.+}")
    @ResponseBody
    public Mono<String> findKeyById(@NotNull @JSONWebKeysetName @PathVariable String keysetName, @NotNull @JSONWebKeyId @PathVariable String keyId) {
        return keyService.findByKeyId(keysetName, keyId);
    }

    // ------------------------------------------ Write methods.
}
