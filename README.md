## junit-customclassloader

[![Build Status](https://travis-ci.org/mjeanroy/junit-customclassloader.svg?branch=master)](https://travis-ci.org/mjeanroy/junit-customclassloader)

### Introduction

Run your test using a custom classloader!

### Installation

**Maven**

```xml
<dependency>
  <groupId>com.github.mjeanroy</groupId>
  <artifactId>junit-customclassloader</artifactId>
  <version>0.1.0</version>
  <scope>test</scope>
</dependency>
```

### How to use

Run your test with `CustomClassLoaderRunner` and add `RunWithClassLoader` annotation to specify the classloader
to use in your unit tests!

For example, here a unit test that will run only on Windows:

```java
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mjeanroy.junit4.customclassloader.BlackListClassLoaderHolder;
import com.github.mjeanroy.junit4.customclassloader.CustomClassLoaderRunner;
import com.github.mjeanroy.junit4.customclassloader.RunWithClassLoader;

@RunWith(CustomClassLoaderRunner.class)
@RunWithClassLoader(BlackListClassLoaderHolder.class)
public class MyUnitTest {
    @Test
    public void it_should_run_with_custom_class_loader() {
        // Do your test
    }
}
```

### Why?

Sometimes, you may have to write code that depends on classpath detection: think about a library where
you want to load a different implementation if a library is available in the classpath.

In this case, you will probably have this kind of function:

```java
public final class MyFactory {
  private MyFactory() {
  }

  public static MyLib create() {
    if (isAvailable("com.org.my.optional.dependency")) {
      return new MyOptionalLibImpl();
    }

    return new MyDefaultImpl();
  }
  
  private static boolean isAvailable(String className) {
    try {
      Class.forName(className, false, Thread.currentThread().getContextClassLoader());
      return true;
    } catch(ClassNotFoundException ex) {
      return false;
    }
  }
}
```

Then, it is hard to unit test this function: you have to create a fake classloader to test both case.
Here is how you can do that with `junit-customclassloader`:

```java
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.mjeanroy.junit4.customclassloader.BlackListClassLoader;
import com.github.mjeanroy.junit4.customclassloader.BlackListClassLoaderHolder;
import com.github.mjeanroy.junit4.customclassloader.CustomClassLoaderRunner;
import com.github.mjeanroy.junit4.customclassloader.RunWithClassLoader;
import com.github.mjeanroy.junit4.customclassloader.TestClassLoader;

@RunWith(CustomClassLoaderRunner.class)
@RunWithClassLoader(BlackListClassLoaderHolder.class)
public class MyUnitTest {
  @Test
  public void it_should_load_default_impl() {
    getClassLoader().add("com.org.my.optional.dependency");
    Assert.asserEquals(MyFactory.create().getClass(), MyDefaultImpl.class);
  }

  private BlackListClassLoader getClassLoader() {
    // With the CustomClassLoaderRunner, each test is runned in its own thread, so it is safe
    // to get the current thread classloader like this.
    return (BlackListClassLoader) Thread.currentThread().getContextClassLoader();
  }
}
```

### License

MIT License.

### Contributing

If you found a bug or you thing something is missing, feel free to contribute and submit an issue or a pull request.
