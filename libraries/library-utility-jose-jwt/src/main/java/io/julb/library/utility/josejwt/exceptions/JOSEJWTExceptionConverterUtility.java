/**
 * MIT License
 *
 * Copyright (c) 2017-2020 Julb
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
package io.julb.library.utility.josejwt.exceptions;

import io.julb.library.utility.exceptions.BadRequestException;
import io.julb.library.utility.exceptions.BaseException;
import io.julb.library.utility.exceptions.UnauthorizedException;
import io.julb.library.utility.josejwt.exceptions.badrequest.MissingAudienceInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.badrequest.MissingExpirationInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.badrequest.MissingIssuerInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.badrequest.TokenNotParseableJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.ExpiredTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.InvalidAudienceInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.InvalidIssuerInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.InvalidSignatureInTokenJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.UnresolvableKeyJOSEJWTException;
import io.julb.library.utility.josejwt.exceptions.unauthorized.UnsupportedKeyTypeJOSEJWTException;

/**
 * The base exception for JOSE/JWT library.
 * <P>
 * @author Julb.
 */
public final class JOSEJWTExceptionConverterUtility {

    /**
     * Converts a {@link JOSEJWTException} to a {@link BaseException}.
     * @param e the origin.
     * @return the corresponding exception.
     */
    public static BaseException convertJOSEJWTException(JOSEJWTException e) {
        // Unauthorized.

        //@formatter:off
        if (ExpiredTokenJOSEJWTException.class.equals(e.getClass())
            || InvalidAudienceInTokenJOSEJWTException.class.equals(e.getClass())
            || InvalidIssuerInTokenJOSEJWTException.class.equals(e.getClass())
            || InvalidSignatureInTokenJOSEJWTException.class.equals(e.getClass())
            || UnresolvableKeyJOSEJWTException.class.equals(e.getClass())
            || UnsupportedKeyTypeJOSEJWTException.class.equals(e.getClass())) {
            throw new UnauthorizedException(e);
        }
        //@formatter:on

        // Invalid token format

        //@formatter:off
        if (TokenNotParseableJOSEJWTException.class.equals(e.getClass())
            || MissingAudienceInTokenJOSEJWTException.class.equals(e.getClass())
            || MissingExpirationInTokenJOSEJWTException.class.equals(e.getClass())
            || MissingIssuerInTokenJOSEJWTException.class.equals(e.getClass())) {
            throw new BadRequestException(e);
        }
        //@formatter:on

        // Others.
        throw new UnauthorizedException(e);
    }
}
