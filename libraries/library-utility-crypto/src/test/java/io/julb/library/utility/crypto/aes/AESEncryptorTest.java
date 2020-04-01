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

package io.julb.library.utility.crypto.aes;

import org.junit.Assert;
import org.junit.Test;

/**
 * This class enables encryption and decryption of a {@link String} using AES256.
 * <P>
 * @author Julb.
 */
public class AESEncryptorTest {

    /**
     * Test method.
     */
    @Test
    public void whenEncryptingDecryptingSameKey_thenItWorks() {
        String rawText = "textToEncrypt";
        String key = "aaaabbbbccccddddaaaabbbbccccdddd";
        String salt = "aaaabbbbccccaaaabbbbccccaaaabbbb";

        String cipheredText = AESEncryptor.encrypt(rawText, key, salt);
        String plainText = AESEncryptor.decrypt(cipheredText, key, salt);
        Assert.assertEquals(rawText, plainText);
    }

    /**
     * Test method.
     */
    @Test(expected = IllegalStateException.class)
    public void whenEncryptingDecryptingDifferentKey_thenThrow() {
        String rawText = "textToEncrypt";
        String key = "aaaabbbbccccddddaaaabbbbccccdddd";
        String salt = "aaaabbbbccccaaaabbbbccccaaaabbbb";

        String falseKey = "bbbbccccddddaaaabbbbccccddddaaaa";
        String falseSalt = "bbbbccccddddaaaabbbbccccddddaaaa";

        String cipheredText = AESEncryptor.encrypt(rawText, key, salt);
        AESEncryptor.decrypt(cipheredText, falseKey, falseSalt);
    }

}
