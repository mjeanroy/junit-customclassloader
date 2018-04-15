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

/**
 * Template implementation of {@link ClassLoaderHolder}.
 * The only method to implement is {@link ClassLoaderHolder#get()}.
 * Note that the parent classloader can be obtained using the {@link #getParentClassLoader()} method.
 */
public abstract class AbstractClassLoaderHolder implements ClassLoaderHolder {
	/**
	 * The parent {@link ClassLoader} instance.
	 */
	private final ClassLoader parentClassLoader;

	/**
	 * Create the holder.
	 */
	protected AbstractClassLoaderHolder() {
		this.parentClassLoader = Thread.currentThread().getContextClassLoader();
	}

	@Override
	public void beforeTest() {
	}

	@Override
	public void afterTest() {
	}

	/**
	 * Get the parent classloader, detected during holder creation.
	 *
	 * @return Parent class loader.
	 */
	public ClassLoader getParentClassLoader() {
		return parentClassLoader;
	}
}
