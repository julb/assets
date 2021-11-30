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

package me.julb.functions;

import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.test.context.TestPropertySource;

import me.julb.library.dto.simple.value.ValueDTO;
import me.julb.library.utility.constants.Integers;
import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * The function to generate UUID.
 * <br>
 * @author Julb.
 */
@TestPropertySource(properties = { "spring.sleuth.function.enabled=false" })
public class GenerateUUIDFunctionTest extends AbstractBaseTest {
    /**
     * The function catalog.
     */
    @Autowired
    private FunctionCatalog functionCatalog;

    /**
     * Unit test method.
     */
    @Test
    public void whenInvokingFunction_thenReturnUUID()
        throws Exception {
        Supplier<ValueDTO> function = functionCatalog.lookup("generateUUIDFunction");
        ValueDTO uuid = function.get();
        Assertions.assertNotNull(uuid);
        Assertions.assertNotNull(uuid.getValue());
        Assertions.assertEquals(Integers.THIRTY_TWO, uuid.getValue().length());
        Assertions.assertEquals(uuid.getValue(), uuid.getValue().toLowerCase());
        Assertions.assertTrue(Pattern.matches("^[0-9a-f]+$", uuid.getValue()));
    }
}
