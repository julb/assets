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

package io.julb.springbootstarter.core.context.mpc;

import java.util.Map;

/**
 * The MPC utility class to set variable in the context.
 * <P>
 * @author Julb.
 */
public final class MPC {

    /**
     * The mpcAdapter attribute.
     */
    static MPCAdapter mpcAdapter;

    /**
     * Default constructor.
     */
    private MPC() {
        super();
    }

    static {
        mpcAdapter = new MPCAdapter();
    }

    /**
     * Put a context value (the <code>val</code> parameter) as identified with the <code>key</code> parameter.
     * @param key the key.
     * @param val the value.
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public static void put(String key, String val)
        throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        mpcAdapter.put(key, val);
    }

    /**
     * Get the value identified by the <code>key</code> parameter.
     * @param key the key.
     * @return the value.
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public static String get(String key)
        throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }

        return mpcAdapter.get(key);
    }

    /**
     * Remove the value identified by the <code>key</code> parameter.
     * @param key the key.
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public static void remove(String key)
        throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        mpcAdapter.remove(key);
    }

    /**
     * Clear all entries in the MPC of the underlying implementation.
     */
    public static void clear() {
        mpcAdapter.clear();
    }

    /**
     * Return a copy of the current thread's context map, with keys and values of type String. Returned value may be null.
     * @return A copy of the current thread's context map. May be null.
     */
    public static Map<String, String> getCopyOfContextMap() {
        return mpcAdapter.getCopyOfContextMap();
    }

    /**
     * Set the current thread's context map by first clearing any existing map and then copying the map passed as parameter. The context map passed as parameter must only contain keys and values of type String.
     * @param contextMap must contain only keys and values of type String
     */
    public static void setContextMap(Map<String, String> contextMap) {
        mpcAdapter.setContextMap(contextMap);
    }
}
