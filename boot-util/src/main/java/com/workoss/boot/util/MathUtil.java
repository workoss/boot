/*
 * The MIT License
 * Copyright © 2020-2021 workoss
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.workoss.boot.util;

/**
 * @author workoss
 */
public class MathUtil {

	/**
	 * Fast method of finding the next power of 2 greater than or equal to the supplied
	 * value.
	 * <p>
	 * This method will do runtime bounds checking and call
	 * {@link #findNextPositivePowerOfTwo(int)} if within a valid range.
	 * @param value from which to search for next power of 2
	 * @return The next power of 2 or the value itself if it is a power of 2.
	 * <p>
	 * Special cases for return values are as follows:
	 * <ul>
	 * <li>{@code <= 0} -> 1</li>
	 * <li>{@code >= 2^30} -> 2^30</li>
	 * </ul>
	 */
	public static final int safeFindNextPositivePowerOfTwo(int value) {
		return value <= 0 ? 1 : value >= 0x40000000 ? 0x40000000 : findNextPositivePowerOfTwo(value);
	}

	private static int findNextPositivePowerOfTwo(final int value) {
		assert value > Integer.MIN_VALUE && value < 0x40000000;
		// ceil(log<sub>2</sub>(x)) = {@code 32 - numberOfLeadingZeros(x - 1)}
		return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
	}

}
