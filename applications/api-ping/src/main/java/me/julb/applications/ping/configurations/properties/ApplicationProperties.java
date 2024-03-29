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

package me.julb.applications.ping.configurations.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The local configuration properties.
 * <br>
 * @author Julb.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    //@formatter:off
     /**
     * The local attribute.
     * -- GETTER --
     * Getter for {@link #local} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #local} property.
     * @param local the value to set.
     */
     //@formatter:on
    private TargetProperties local;

    //@formatter:off
     /**
     * The remotes attribute.
     * -- GETTER --
     * Getter for {@link #remotes} property.
     * @return the value.
     * -- SETTER --
     * Setter for {@link #remotes} property.
     * @param remotes the value to set.
     */
     //@formatter:on
    private List<TargetProperties> remotes = new ArrayList<>();

    /**
     * Gets the targets to ping (local and remotes).
     * @return the targets to ping.
     */
    public Collection<TargetProperties> getTargets() {
        List<TargetProperties> targets = new ArrayList<>();
        if (local != null) {
            targets.add(local);
        }
        if (CollectionUtils.isNotEmpty(remotes)) {
            targets.addAll(remotes);
        }
        return targets;
    }
}
