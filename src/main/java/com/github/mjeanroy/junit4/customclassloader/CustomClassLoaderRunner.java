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

import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * The runner that will load a custom classloader for unit tests.
 */
public class CustomClassLoaderRunner extends BlockJUnit4ClassRunner {

	/**
	 * The classloader holder that will be able to load a custom classloader.
	 */
	private final CustomClassLoaderHolder classLoaderHolder;

	/**
	 * Create the JUnit runner.
	 *
	 * @param testClass The tested class.
	 * @throws InitializationError If an error occurs during initialization.
	 */
	public CustomClassLoaderRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		this.classLoaderHolder = Reflections.newInstance(findAnnotation(testClass).value());
	}

	@Override
	public void run(RunNotifier notifier) {
		// Run test in a new thread, required to execute it on a custom loader.
		Thread thread = new Thread(new JunitRunnable(notifier));
		thread.setContextClassLoader(classLoaderHolder.get());
		thread.start();

		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> testRules = super.getTestRules(target);
		testRules.add(new ClassLoaderRule(classLoaderHolder));
		testRules.add(new ClassLoaderInjectionRule(target, classLoaderHolder));
		return testRules;
	}

	/**
	 * Find the {@link RunWithClassLoader} annotation, that will be used to create the choose the custom classloader holder
	 * to use for the test suite.
	 *
	 * @param testClass The tested class.
	 * @return The annotation instance.
	 */
	private static RunWithClassLoader findAnnotation(Class<?> testClass) {
		return Reflections.findAnnotation(testClass, RunWithClassLoader.class, "Try to instantiate custom classloader, but cannot find @RunWithClassLoader annotation, please specify it.");
	}

	/**
	 * The {@link Runnable} that will run the unit test in the newly created thread.
	 */
	private class JunitRunnable implements Runnable {
		/**
		 * The {@link RunNotifier} instance.
		 */
		private final RunNotifier notifier;

		/**
		 * Create the runnable.
		 *
		 * @param notifier The {@link RunNotifier} instance.
		 */
		private JunitRunnable(RunNotifier notifier) {
			this.notifier = notifier;
		}

		@Override
		public void run() {
			CustomClassLoaderRunner.super.run(notifier);
		}
	}
}
