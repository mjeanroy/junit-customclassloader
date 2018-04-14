package com.github.mjeanroy.junit4.customclassloader.it;

import com.github.mjeanroy.junit4.customclassloader.CustomClassLoaderRunner;
import com.github.mjeanroy.junit4.customclassloader.RunWithClassLoader;
import com.github.mjeanroy.junit4.customclassloader.TestClassLoader;
import com.github.mjeanroy.junit4.customclassloader.fixtures.ChildClassAnnotated;
import com.github.mjeanroy.junit4.customclassloader.fixtures.ParentClassAnnotated;
import com.github.mjeanroy.junit4.customclassloader.impl.BlackListClassLoaderHolder;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CustomClassLoaderRunner.class)
@RunWithClassLoader(BlackListClassLoaderHolder.class)
public class TestWithCustomClassLoader {

	@TestClassLoader
	private BlackListClassLoaderHolder customClassLoader;

	@Test(expected = ClassNotFoundException.class)
	public void it_should_fail_if_class_is_blacklisted() throws Exception {
		String name = ChildClassAnnotated.class.getName();
		customClassLoader.addToBlackList(name);
		loadClass(name);
	}

	@Test
	public void it_should_not_fail_if_class_is_not_blacklisted() throws Exception {
		String name = ParentClassAnnotated.class.getName();
		loadClass(name);
	}

	private void loadClass(String name) throws Exception {
		Class.forName(name, false, Thread.currentThread().getContextClassLoader());
	}
}
