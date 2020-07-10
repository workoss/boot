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
package com.workoss.boot.plugin.mybatis.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {

	/**
	 * (Optional) The name of the table.
	 * <p>
	 * Defaults to the entity name.
	 */
	String name() default "";

	/**
	 * (Optional) The catalog of the table.
	 * <p>
	 * Defaults to the default catalog.
	 */
	String catalog() default "";

	/**
	 * (Optional) The schema of the table.
	 * <p>
	 * Defaults to the default schema for user.
	 */
	String schema() default "";

	/**
	 * (Optional) Unique constraints that are to be placed on the table. These are only
	 * used if table generation is in effect. These constraints apply in addition to any
	 * constraints specified by the <code>Column</code> and <code>JoinColumn</code>
	 * annotations and constraints entailed by primary key mappings.
	 * <p>
	 * Defaults to no additional constraints.
	 */
	UniqueConstraint[] uniqueConstraints() default {};

	/**
	 * (Optional) Indexes for the table. These are only used if table generation is in
	 * effect. Note that it is not necessary to specify an index for a primary key, as the
	 * primary key index will be created automatically.
	 *
	 * @since 2.1
	 */
	Index[] indexes() default {};

}
