/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;

@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Scope
/**
 *
 * @author Camilo Gonzalez
 */
public @interface RequestScoped {
}
