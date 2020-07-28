# Odin - A Dependency Injection Library

Odin is an Object Dependency Injection library for Java applications.

[![Build Status](https://travis-ci.org/jcoderltd/odin.svg?branch=master)](https://travis-ci.org/jcoderltd/odin)
[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/jcoderltd/odin)

## Important notes:

- Odin is currently in alpha stage
- We'll be adding more documentation as we go along
- For the annotations module, support for javax.inject is provided for most features defined there.

## Documentation:

[User documentation](https://github.com/jcoderltd/odin/wiki)

## Include in a Java project using Gradle:

```java
// if you want annotations support
compile 'io.jcoder.odin:odin-annotations:0.2.4'

// if you want only the core
compile 'io.jcoder.odin:odin-core:0.2.4'
```

## Example usage:

Assume you have 2 classes `A` and `B` where `A` depends on `B`:

```java
@Singleton
public static class A {
    private final B b;

    public A(B b) {
        this.b = b;
    }
    
    @PostConstruct
    void initialize() {
        System.out.println("A is initialized");
    }
}

@Singleton
public static class B {
    @PostConstruct
    void initialize() {
        System.out.println("B is initialized");
    }
}
```
 
Then, to create an injection context that manages `A` and `B` you would do the following:

```java
import static io.jcoder.odin.annotation.builder.AnnotationAwareRegistrationBuilder.annotated;

InjectionContext context = new DefaultInjectionContext();
context.register(annotated(A.class));
context.register(annotated(B.class));
context.initialize();
```

