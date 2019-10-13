/**
 * <p>
 * This package shows a basic example usage of Odin using the following scenario:
 * 
 * <p>
 * We'll assume that we have a system that handles <code>User</code> data and that we have 2 layers right now, a service
 * layer that represents our business logic, and a repository layer that helps with the storage/retrieval of users from
 * a datastore.
 * 
 * <p>
 * For simplicity, the implementation of both layers is bare-bones as this is just to show the usage of Odin to connect
 * the components from the service to the repository.
 * 
 * <p>
 * As such, we have:
 * <ul>
 * <li><code>{@link io.jcoder.odin.examples.basic.UserService}</code>: Represents our service layer for the
 * <code>User</code></li>
 * <li><code>{@link io.jcoder.odin.examples.basic.UserRepository}</code>: Represents our repository layer for the
 * <code>User</code></li>
 * <li><code>{@link io.jcoder.odin.examples.basic.UserApp}</code>: Represents the entry point to our example
 * application</li>
 * </ul>
 */
package io.jcoder.odin.examples.basic;