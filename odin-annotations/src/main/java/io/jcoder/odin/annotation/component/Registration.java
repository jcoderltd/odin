/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.component;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
/**
 *
 * @author Camilo Gonzalez
 */
public @interface Registration {

}
