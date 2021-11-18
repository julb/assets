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

package me.julb.springbootstarter.core.context.mpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Mapped Param context adapter.
 * <br>
 * @author Julb.
 */
public class MPCAdapter {

    /**
     * The thread local.
     */
    private static ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>() {

        /**
         * {@inheritDoc}
         */
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }

    };

    /**
     * Put a context value (the <code>val</code> parameter) as identified with the <code>key</code> parameter.
     * @param key the key.
     * @param val the value.
     */
    public void put(String key, String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, String>();
            threadLocal.set(map);
        }
        map.put(key, val);
    }

    /**
     * Get the value identified by the <code>key</code> parameter.
     * @param key the key.
     * @return the value identified by the key.
     */
    public String get(String key) {
        Map<String, String> map = threadLocal.get();
        if ((map != null) && (key != null)) {
            return map.get(key);
        } else {
            return null;
        }
    }

    /**
     * Remove the value identified by the <code>key</code> parameter.
     * @param key the key.
     */
    public void remove(String key) {
        Map<String, String> map = threadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }

    /**
     * Clear all entries in the context.
     */
    public void clear() {
        Map<String, String> map = threadLocal.get();
        if (map != null) {
            map.clear();
            threadLocal.remove();
        }
    }

    /**
     * Returns the keys in the context.
     * @return the keys in the context.
     */
    public Set<String> getKeys() {
        Map<String, String> map = threadLocal.get();
        if (map != null) {
            return map.keySet();
        } else {
            return null;
        }
    }

    /**
     * Return a copy of the current thread's context map. Returned value may be null.
     * @return a copy of the current thread context map.
     */
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> oldMap = threadLocal.get();
        if (oldMap != null) {
            return new HashMap<String, String>(oldMap);
        } else {
            return null;
        }
    }

    /**
     * Sets the context map.
     * @param contextMap the context map.
     */
    public void setContextMap(Map<String, String> contextMap) {
        threadLocal.set(new HashMap<String, String>(contextMap));
    }

}
