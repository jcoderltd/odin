/*
 * Copyright 2019 - JCoder Ltd
 */
package io.jcoder.odin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Retention(RetentionPolicy.RUNTIME) 
@Qualifier
public @interface SampleQualifier {
}
