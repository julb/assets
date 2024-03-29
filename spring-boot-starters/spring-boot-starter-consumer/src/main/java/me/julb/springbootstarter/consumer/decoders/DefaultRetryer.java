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
package me.julb.springbootstarter.consumer.decoders;

import lombok.extern.slf4j.Slf4j;

import feign.RetryableException;
import feign.Retryer;
import me.julb.library.utility.exceptions.UnableToReachRemoteSystemException;

/**
 * A default retryer for all consumers.
 * <br>
 * @author Julb.
 */
@Slf4j
public class DefaultRetryer extends Retryer.Default {

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