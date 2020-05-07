package com.workoss.boot.plugin.mybatis.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface UniqueConstraint {

    /** (Optional) Constraint name.  A provider-chosen name will be chosen
     * if a name is not specified.
     *
     * @since 2.0
     */
    String name() default "";

    /** (Required) An array of the column names that make up the constraint. */
    String[] columnNames();
}
