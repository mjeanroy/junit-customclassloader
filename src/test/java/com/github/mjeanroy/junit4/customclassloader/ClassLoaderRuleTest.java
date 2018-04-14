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

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;

public class ClassLoaderRuleTest {

	@Test
	public void it_should_run_class_loader_holder_initialization() {
		CustomClassLoaderHolder holder = mock(CustomClassLoaderHolder.class);
		ClassLoaderRule rule = new ClassLoaderRule(holder);
		rule.before();
		verify(holder).beforeTest();
	}

	@Test
	public void it_should_run_class_loader_holder_cleanup() {
		CustomClassLoaderHolder holder = mock(CustomClassLoaderHolder.class);
		ClassLoaderRule rule = new ClassLoaderRule(holder);
		rule.after();
		verify(holder).afterTest();
	}

	@Test
	public void it_should_run_class_loader_holder_initialization_and_cleanup() throws Throwable {
		CustomClassLoaderHolder holder = mock(CustomClassLoaderHolder.class);
		ClassLoaderRule rule = new ClassLoaderRule(holder);
		Description description = Description.createTestDescription(ClassLoaderRuleTest.class, "it_should_run_class_loader_holder_initialization_and_cleanup");
		Statement statement = mock(Statement.class);

		Statement result = rule.apply(statement, description);

		verifyZeroInteractions(holder);
		verifyZeroInteractions(statement);

		result.evaluate();

		InOrder inOrder = inOrder(holder, statement);
		inOrder.verify(holder).beforeTest();
		inOrder.verify(statement).evaluate();
		inOrder.verify(holder).afterTest();
	}
}
