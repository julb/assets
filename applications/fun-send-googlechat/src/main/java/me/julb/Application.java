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
package me.julb;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeHint;

import me.julb.library.dto.googlechat.GoogleChatMessageDTO;
import me.julb.library.utility.validator.constraints.GoogleChatRoom;
import me.julb.library.utility.validator.constraints.GoogleChatText;
import me.julb.library.utility.validator.constraints.GoogleChatThreadKey;
import me.julb.springbootstarter.googlechat.repositories.impl.GoogleChatTextBodyDTO;

/**
 * Main class to launch the application.
 * <br>
 * @author Julb.
 */
@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class})
@NativeHint(
    jdkProxies = {
        @JdkProxyHint(types = {Pattern.class}),
        @JdkProxyHint(types = {Size.class})
    },
    types = @TypeHint(types = {
        GoogleChatRoom.class,
        GoogleChatText.class,
        GoogleChatThreadKey.class
    }),
    serializables = @SerializationHint(types = {GoogleChatMessageDTO.class, GoogleChatTextBodyDTO.class})
)
public class Application {

    /**
     * Method to launch the application.
     * @param args the arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
