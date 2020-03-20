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

package me.julb.springbootstarter.web.utility;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * The page utility to build paged responses from standard list.
 * <P>
 * @author Julb.
 */
public final class PageUtility {

    /**
     * Builds a page from a full list of content, paginated from pageable information.
     * @param <T> the type of objects.
     * @param content the content.
     * @param pageable the pageable.
     * @return the page.
     */
    public static <T> Page<T> of(List<T> content, Pageable pageable) {
        int total = content.size();

        if (pageable.isPaged()) {
            List<T> subList;
            if (pageable.getOffset() < total) {
                int fromIndex = Long.valueOf(pageable.getOffset()).intValue();
                int toIndex = Long.valueOf(Math.min(total, pageable.getOffset() + pageable.getPageSize())).intValue();
                subList = content.subList(fromIndex, toIndex);
            } else {
                subList = new ArrayList<T>();
            }
            return new PageImpl<T>(subList, pageable, total);
        } else {
            return new PageImpl<T>(content, pageable, total);
        }
    }

}
