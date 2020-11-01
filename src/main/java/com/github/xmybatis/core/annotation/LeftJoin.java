package com.github.xmybatis.core.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
//@Repeatable(LeftJoins.class)
public @interface LeftJoin {
	String joinTable() ;
	String as() default "";
	String on();
	//String[] value();
}
