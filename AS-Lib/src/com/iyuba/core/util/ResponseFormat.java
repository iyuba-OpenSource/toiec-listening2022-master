package com.iyuba.core.util;

/**
 * Created by iyuba on 2018/11/6.
 */

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 接口返回的数据格式，当前限定取值：{@link #JSON}或{@link #XML}
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ResponseFormat {

    String JSON = "json";

    String XML = "xml";

    String value() default "";
}
