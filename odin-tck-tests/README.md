# Odin TCK - Technology Compatibility Kit

Odin project to verify compliance with the JSR-330 TCK. 

## What tests we comply with:

- Odin provides the functionality covered in the TCK related to `public`, `private`, `protected` and default injection.
- Odin doesn't provide static injection functionality.

## Important notes:

- The classes for the JSR-330 TCK are from the `javax.inject` GitHub Repo https://github.com/javax-inject/javax-inject. This is licensed under the Apache License 2.0 and are Copyright of the JSR-330 Expert Group
- The TCK classes rely on JUnit 4 - hence we created a separate Gradle subproject to keep tests separate from those we have that rely on JUnit 5