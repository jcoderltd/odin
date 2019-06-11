/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.annotation.component;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
/**
 *
 * @author Camilo Gonzalez
 */
public @interface ComponentDependency {
    Class<?>[] value();
}
