/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Mickael Jeanroy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.mjeanroy.junit4.customclassloader;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

/**
 * A JUnit rule that will try to inject {@link ClassLoader} or {@link ClassLoaderHolder}
 * instances to the test class instance (using the {@link TestClassLoader} annotation).
 */
class ClassLoaderInjectionRule extends ExternalResource implements TestRule {
	/**
	 * The test class instance.
	 */
	private final Object target;

	/**
	 * The classloader holder instance.
	 */
	private final ClassLoaderHolder classLoaderHolder;

	/**
	 * Create the rule.
	 *
	 * @param target Test class instance.
	 * @param classLoaderHolder Class loader holder instance.
	 */
	ClassLoaderInjectionRule(Object target, ClassLoaderHolder classLoaderHolder) {
		this.target = target;
		this.classLoaderHolder = classLoaderHolder;
	}

	@Override
	protected void before() {
		injectClassLoader();
	}

	@Override
	protected void after() {
		cleanUpClassLoaderInjection();
	}

	/**
	 * Inject {@link ClassLoader} or {@link ClassLoaderHolder} on all fields annotated
	 * with {@link TestClassLoader} annotation.
	 */
	private void injectClassLoader() {
		List<Field> fields = Reflections.findFieldsAnnotatedWith(target.getClass(), TestClassLoader.class);
		for (Field field : fields) {
			injectClassLoaderField(field);
		}
	}

	/**
	 * Write {@link ClassLoader} or {@link ClassLoaderHolder} to the field
	 * given in parameter.
	 *
	 * If injection is not possible because of wrong type, an {@link IllegalStateException} is thrown.
	 *
	 * @throws IllegalStateException If injection cannot be done.
	 */
	private void injectClassLoaderField(Field field) {
		Class<?> type = field.getType();

		if (ClassLoader.class.isAssignableFrom(type)) {
			Reflections.setter(target, field, Thread.currentThread().getContextClassLoader());
		}
		else if (ClassLoaderHolder.class.isAssignableFrom(type)) {
			Reflections.setter(target, field, classLoaderHolder);
		}
		else {
			throw new IllegalStateException("Cannot set field '" + field.getName() + "', it should be an instance of '" + ClassLoader.class.getName() + "' or '" + ClassLoaderHolder.class.getName() + "'.");
		}
	}

	/**
	 * Set fields value annotated with {@link TestClassLoader} to {@code null}.
	 */
	private void cleanUpClassLoaderInjection() {
		List<Field> fields = Reflections.findFieldsAnnotatedWith(target.getClass(), TestClassLoader.class);
		for (Field field : fields) {
			Reflections.setter(target, field, null);
		}
	}
}
