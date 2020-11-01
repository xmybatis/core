package com.github.xmybatis.core.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE) 
@Retention(RUNTIME)
public @interface Table  {
    /**
     * (Optional) The name of the table.
     * <p> Defaults to the entity name.
     */
    String value() default "";
    
    

    /** (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
    String catalog() default "";

    /** (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    String schema() default "";
    //String value() default "";
    String alias() default "";
    LeftJoin[] leftJoin() default {};
}
