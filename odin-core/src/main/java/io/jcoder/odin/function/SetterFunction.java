/*
 * Copyright 2018 - JCoder Ltd
 */
package io.jcoder.odin.function;

import java.io.Serializable;

/**
 * Represents a setter function that has a single parameter of type O belonging to a class of type T.
 *
 * <p>
 * Assuming class T has a method <code>void setString(String x)</code>, a reference to the setString method can be used
 * as a {@link SetterFunction}, for example, T::setString
 *
 * @author Camilo Gonzalez
 */
public interface SetterFunction<T, O> extends Serializable {

    void set(T setterOwner, O parameter);

}
