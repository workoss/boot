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
     * <p> Defaults to the entity name.
     */
    String name() default "";

    /**
     * (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
    String catalog() default "";

    /**
     * (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    String schema() default "";

    /**
     * (Optional) Unique constraints that are to be placed on
     * the table. These are only used if table generation is in
     * effect. These constraints apply in addition to any constraints
     * specified by the <code>Column</code> and <code>JoinColumn</code>
     * annotations and constraints entailed by primary key mappings.
     * <p> Defaults to no additional constraints.
     */
    UniqueConstraint[] uniqueConstraints() default {};

    /**
     * (Optional) Indexes for the table.  These are only used if
     * table generation is in effect.  Note that it is not necessary
     * to specify an index for a primary key, as the primary key
     * index will be created automatically.
     *
     * @since 2.1
     */
    Index[] indexes() default {};
}

