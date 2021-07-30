/*
 * Copyright 2019-2021 workoss (https://www.workoss.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
	 * <li>{@code <= 0} - 1</li>
	 * <li>{@code >= 2^30} - 2^30</li>
	 * </ul>
	 */
	public static int safeFindNextPositivePowerOfTwo(int value) {
		return value <= 0 ? 1 : value >= 0x40000000 ? 0x40000000 : findNextPositivePowerOfTwo(value);
	}

	private static int findNextPositivePowerOfTwo(final int value) {
		assert value > Integer.MIN_VALUE && value < 0x40000000;
		// ceil(log<sub>2</sub>(x)) = {@code 32 - numberOfLeadingZeros(x - 1)}
		return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
	}

}
