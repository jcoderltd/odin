/**
 * <p>
 * This package shows an example usage of Odin using constructor and setter injection:
 * 
 * <p>
 * We'll assume that we have a system that has two singleton services, ServiceA and ServiceB where both services depend
 * on each other. In this case, constructor injection for both services would not be possible due to the cyclic
 * dependency.
 * 
 * <p>
 * {@link io.jcoder.odin.examples.component.setter.ServiceA} uses setter injection and
 * {@link io.jcoder.odin.examples.component.setter.ServiceB} uses constructor injection
 * 
 * <p>
 * For simplicity, the implementation of both services is bare-bones as this is just to show the usage of Odin to
 * connect the instances.
 */
package io.jcoder.odin.examples.component.setter;