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
package io.julb.springbootstarter.consumer.decoders;

import io.julb.library.utility.exceptions.UnableToReachRemoteSystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.RetryableException;
import feign.Retryer;

/**
 * A default retryer for all consumers.
 * <P>
 * @author Julb.
 */
public class DefaultRetryer extends Retryer.Default {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRetryer.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void continueOrPropagate(RetryableException exception) {
        try {
            super.continueOrPropagate(exception);
            LOGGER.info("Retrying....");
        } catch (RetryableException e) {
            LOGGER.warn("Retries exhausted.");
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new UnableToReachRemoteSystemException(cause);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultRetryer clone() {
        return new DefaultRetryer();
    }
}