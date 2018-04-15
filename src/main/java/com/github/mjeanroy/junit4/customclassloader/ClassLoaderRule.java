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

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

/**
 * A JUnit rule that will perform initialization and cleanup before
 * and after each tests.
 */
class ClassLoaderRule extends ExternalResource implements TestRule {
	/**
	 * The classloader holder.
	 */
	private final ClassLoaderHolder classLoaderHolder;

	/**
	 * Create the rule.
	 *
	 * @param classLoaderHolder The classloader holder.
	 */
	ClassLoaderRule(ClassLoaderHolder classLoaderHolder) {
		this.classLoaderHolder = classLoaderHolder;
	}

	@Override
	protected void before() {
		classLoaderHolder.beforeTest();
	}

	@Override
	protected void after() {
		classLoaderHolder.afterTest();
	}
}
