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

package me.julb.springbootstarter.core.context;

import me.julb.springbootstarter.core.context.mpc.MPC;

/**
 * The trademark context holder.
 * <br>
 * @author Julb.
 */
public final class TrademarkContextHolder {

    /**
     * The Trademark key in the MPC.
     */
    private static final String TM = "tm";

    /**
     * Gets the trademark.
     * @return the trademark stored in the context.
     */
    public static String getTrademark() {
        return MPC.get(TM);
    }

    /**
     * Sets the trademark.
     * @param trademark the trademark.
     */
    public static void setTrademark(String trademark) {
        MPC.put(TM, trademark);
    }

    /**
     * Unsets the trademark.
     */
    public static void unsetTrademark() {
        MPC.remove(TM);
    }

}
