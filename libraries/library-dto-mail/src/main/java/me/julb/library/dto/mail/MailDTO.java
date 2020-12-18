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
package me.julb.library.dto.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO to send a mail.
 * <P>
 * @author Julb.
 */
@Getter
@Setter
public class MailDTO {

    /**
     * The sender of the mail.
     */
    @Email
    @NotBlank
    private String from;

    /**
     * TO recipients of the email.
     */
    private Collection<@Email @NotBlank String> tos = new ArrayList<>();

    /**
     * CC recipients of the email.
     */
    private Collection<@Email @NotBlank String> ccs = new ArrayList<>();

    /**
     * BCC recipients of the email.
     */
    private Collection<@Email @NotBlank String> bccs = new ArrayList<>();

    /**
     * Subject of the email.
     */
    @NotNull
    @NotBlank
    private String subject;

    /**
     * HTML Body (content) of the email.
     */
    @NotNull
    @NotBlank
    private String html;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        //@formatter:off
        return String.format("From: <%s>, Tos: <%s>, Ccs: <%s>, Bcc: <%s>, Subject: %s", 
            from,
            Arrays.toString(tos.toArray()),
            Arrays.toString(ccs.toArray()),
            Arrays.toString(bccs.toArray()),
            subject
        );
        //@formatter:on
    }

}
