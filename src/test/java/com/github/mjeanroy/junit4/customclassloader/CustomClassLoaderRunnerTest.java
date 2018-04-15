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

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;

public class CustomClassLoaderRunnerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void it_should_create_runner_with_appropriate_rules() throws Exception {
		CustomClassLoaderRunner runner = new CustomClassLoaderRunner(TestFixture.class);
		TestFixture target = new TestFixture();
		List<TestRule> rules = runner.getTestRules(target);

		assertThat(rules).isNotEmpty();
		assertThat(rules.get(0).getClass()).isEqualTo(ClassLoaderInjectionRule.class);
		assertThat(rules.get(1).getClass()).isEqualTo(ClassLoaderRule.class);
		assertThat(rules.get(2).getClass()).isEqualTo(RunInNewThreadRule.class);
	}

	@Test
	public void it_should_fail_to_instantiate_runner_without_appropriate_annotation() throws Exception {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Try to instantiate custom classloader, but cannot find @RunWithClassLoader annotation, please specify it.");
		new CustomClassLoaderRunner(TestFixtureWithoutAnnotation.class);
	}

	@RunWithClassLoader(BlackListClassLoaderHolder.class)
	public static class TestFixture {
		@Test
		public void test() {
		}
	}

	public static class TestFixtureWithoutAnnotation {
		@Test
		public void test() {
		}
	}
}
