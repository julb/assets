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
package me.julb.springbootstarter.security.utilities;

import org.apache.commons.lang3.StringUtils;

/**
 * The role utility.
 * <br>
 * @author Julb.
 */
public class RoleUtility {

    /**
     * The role prefix.
     */
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Returns the authority name from the role name.
     * @param roleName the role name.
     * @return the authority name.
     */
    public static final String toAuthorityName(String roleName) {
        return StringUtils.join(ROLE_PREFIX, roleName.toUpperCase());
    }

    /**
     * Returns <code>true</code> if the authority is a role.
     * @param authorityName the authority name.
     * @return <code>true</code> if the authority is a role.
     */
    public static final Boolean isRole(String authorityName) {
        return authorityName.startsWith(ROLE_PREFIX);
    }
}
