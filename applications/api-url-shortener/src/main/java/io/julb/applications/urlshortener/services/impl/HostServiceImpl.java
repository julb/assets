/**
 * MIT License
 *
 * Copyright (c) 2017-2019 Julb
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

package io.julb.applications.urlshortener.services.impl;

import io.julb.applications.urlshortener.services.HostService;
import io.julb.library.dto.simple.value.ValueDTO;
import io.julb.library.utility.validator.constraints.DNS;
import io.julb.springbootstarter.core.configs.ConfigSourceService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * The host service implementation.
 * <P>
 * @author Julb.
 */
@Service
@Validated
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class HostServiceImpl implements HostService {

    /**
     * The config source service.
     */
    @Autowired
    private ConfigSourceService configSourceService;

    // ------------------------------------------ Read methods.

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValueDTO> findAll() {
        List<String> availableHosts = getAvailableHosts();
        return availableHosts.stream().map(ValueDTO::new).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(@NotNull @DNS String host) {
        return getAvailableHosts().contains(host);
    }

    // ------------------------------------------ Write methods.

    // ------------------------------------------ Private methods.

    /**
     * Gets the available hosts.
     * @return the available hosts.
     */
    private List<String> getAvailableHosts() {
        String[] hosts = configSourceService.getTypedProperty("url.shortener.hosts", String[].class);
        List<String> hostsAsList = new ArrayList<String>();
        if (ArrayUtils.isNotEmpty(hosts)) {
            for (String host : hosts) {
                hostsAsList.add(StringUtils.lowerCase(host));
            }
            Collections.sort(hostsAsList);
        }
        return hostsAsList;
    }

}
