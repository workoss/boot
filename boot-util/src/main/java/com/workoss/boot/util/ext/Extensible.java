package com.workoss.boot.util.ext;

import java.lang.annotation.*;

/**
 * @author: workoss
 * @date: 2018-11-09 09:39
 * @version:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Extensible {
    /**
     * 指定自定义扩展文件名称，默认就是全类名
     *
     * @return 自定义扩展文件名称
     */
    String file() default "";

    /**
     * 扩展类是否使用单例，默认使用
     *
     * @return 是否使用单例
     */
    boolean singleton() default true;

    /**
     * 扩展类是否需要编码，默认不需要
     *
     * @return 是否需要编码
     */
    boolean coded() default false;

}
