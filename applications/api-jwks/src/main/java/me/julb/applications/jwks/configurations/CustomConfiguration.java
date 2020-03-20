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
package me.julb.applications.jwks.configurations;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import me.julb.applications.jwks.configurations.caching.CacheConstants;
import me.julb.applications.jwks.configurations.properties.ApplicationProperties;

/**
 * The custom configuration.
 * <P>
 * @author Julb.
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableCaching
@Slf4j
public class CustomConfiguration {

    /**
     * The cache manager.
     */
    @Autowired
    private CacheManager cacheManager;

    /**
     * This method is triggered when the properties are refreshed.
     */
    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefreshConfiguration() {
        LOGGER.info("Configuration change detected. Clearing all caches.");
        cacheManager.getCache(CacheConstants.KEYSET_KEYS_CACHE_KEY).clear();
        cacheManager.getCache(CacheConstants.KEYSET_KEY_CACHE_KEY).clear();
    }

}
