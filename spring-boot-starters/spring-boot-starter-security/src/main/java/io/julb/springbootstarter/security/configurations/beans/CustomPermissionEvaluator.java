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
package io.julb.springbootstarter.security.configurations.beans;

import io.julb.library.utility.security.ISecurable;
import io.julb.springbootstarter.core.context.TrademarkContextHolder;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * A custom permission evaluator.
 * <P>
 * @author Julb.
 */
public class CustomPermissionEvaluator implements PermissionEvaluator {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // If the target domain object is a String, it means that it is a permission on a Domain object in general.
        if (targetDomainObject instanceof String) {
            return hasPermission(authentication, null, (String) targetDomainObject, permission);
        }

        // Otherwise, go ahead with standard checks.
        if ((authentication == null) || !(targetDomainObject instanceof ISecurable) || !(permission instanceof String)) {
            return false;
        }

        ISecurable securableObject = (ISecurable) targetDomainObject;
        return isAllowed(authentication, securableObject, permission.toString().toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }

        ISecurable securableObject = new ISecurable() {

            /**
             * {@inheritDoc}
             */
            @Override
            public String getId() {
                return targetId.toString();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getTargetType() {
                return targetType;
            }
        };

        return isAllowed(authentication, securableObject, permission.toString().toLowerCase());
    }

    /**
     * Checks if the user is allowed to do the operation on the given object.
     * @param auth the user authenticated.
     * @param securableObject the securable object.
     * @param identifier the identifier.
     * @param permission the permission to check.
     * @return <code>true</code> if the user is allowed to enter, <code>false</code> otherwise.
     */
    private boolean isAllowed(Authentication authentication, ISecurable securableObject, String permission) {
        String tm = TrademarkContextHolder.getTrademark();
        // FIXME Implement that method.
        return true;
    }
}