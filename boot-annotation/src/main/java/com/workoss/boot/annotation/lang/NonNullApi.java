package com.workoss.boot.annotation.lang;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.*;

/**
 * NonNullApi
 * @author workoss
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierDefault({ ElementType.METHOD, ElementType.PARAMETER })
public @interface NonNullApi {

}
