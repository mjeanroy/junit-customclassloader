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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ClassLoaderInjectionRuleTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void it_should_inject_class_loader_holder_before_test() {
		ClassLoaderHolder holder = mock(ClassLoaderHolder.class);
		CustomClassLoaderHolderInjection target = new CustomClassLoaderHolderInjection();
		ClassLoaderInjectionRule rule = new ClassLoaderInjectionRule(target, holder);

		rule.before();

		assertThat(target.holder).isSameAs(holder);
	}

	@Test
	public void it_should_inject_class_loader_before_test() {
		ClassLoaderHolder holder = mock(ClassLoaderHolder.class);
		CustomClassLoaderInjection target = new CustomClassLoaderInjection();
		ClassLoaderInjectionRule rule = new ClassLoaderInjectionRule(target, holder);

		rule.before();

		assertThat(target.classLoader).isSameAs(Thread.currentThread().getContextClassLoader());
	}

	@Test
	public void it_should_fail_to_inject_class_with_inappropriate_type() {
		ClassLoaderHolder holder = mock(ClassLoaderHolder.class);
		CustomClassWithInappropriateType target = new CustomClassWithInappropriateType();
		ClassLoaderInjectionRule rule = new ClassLoaderInjectionRule(target, holder);

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Cannot set field 'foo', it should be an instance of 'java.lang.ClassLoader' or 'com.github.mjeanroy.junit4.customclassloader.ClassLoaderHolder'.");

		rule.before();
	}

	@Test
	public void it_should_clean_loader_holder_after_test() {
		ClassLoaderHolder holder = mock(ClassLoaderHolder.class);
		CustomClassLoaderHolderInjection target = new CustomClassLoaderHolderInjection();
		target.holder = holder;
		ClassLoaderInjectionRule rule = new ClassLoaderInjectionRule(target, holder);

		rule.after();

		assertThat(target.holder).isNull();
	}

	@Test
	public void it_should_clean_loader_after_test() {
		ClassLoader classLoader = mock(ClassLoader.class);
		ClassLoaderHolder holder = mock(ClassLoaderHolder.class);
		CustomClassLoaderInjection target = new CustomClassLoaderInjection();
		target.classLoader = classLoader;
		ClassLoaderInjectionRule rule = new ClassLoaderInjectionRule(target, holder);

		rule.after();

		assertThat(target.classLoader).isNull();
		verifyZeroInteractions(holder);
	}

	@Test
	public void it_should_inject_class_loader_and_clean_it_test() throws Throwable {
		final ClassLoaderHolder holder = mock(ClassLoaderHolder.class);
		final CustomClassLoaderAndHolderInjection target = new CustomClassLoaderAndHolderInjection();
		final ClassLoaderInjectionRule rule = new ClassLoaderInjectionRule(target, holder);

		Answer<Object> evaluateAnswer = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertThat(target.classLoader).isSameAs(Thread.currentThread().getContextClassLoader());
				assertThat(target.holder).isSameAs(holder);
				return null;
			}
		};

		Statement statement = mock(Statement.class);
		doAnswer(evaluateAnswer).when(statement).evaluate();

		Description description = Description.createTestDescription(ClassLoaderInjectionRuleTest.class, "it_should_inject_class_loader_and_clean_it_test");
		Statement result = rule.apply(statement, description);
		verifyZeroInteractions(holder);

		result.evaluate();

		assertThat(target.classLoader).isNull();
		assertThat(target.holder).isNull();
	}

	private static class CustomClassLoaderHolderInjection {
		@TestClassLoader
		private ClassLoaderHolder holder;
	}

	private static class CustomClassLoaderInjection {
		@TestClassLoader
		private ClassLoader classLoader;
	}

	private static class CustomClassLoaderAndHolderInjection {
		@TestClassLoader
		private ClassLoader classLoader;

		@TestClassLoader
		private ClassLoaderHolder holder;
	}

	private static class CustomClassWithInappropriateType {
		@TestClassLoader
		private boolean foo;
	}
}
