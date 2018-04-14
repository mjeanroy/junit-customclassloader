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

package com.github.mjeanroy.junit4.customclassloader.impl;

import com.github.mjeanroy.junit4.customclassloader.ClassLoaderHolder;

/**
 * Implementation of {@link ClassLoaderHolder} that will load {@link BlackListClassLoader} instance.
 */
public class BlackListClassLoaderHolder implements ClassLoaderHolder {
	/**
	 * The {@link BlackListClassLoader} instance.
	 */
	private final BlackListClassLoader classLoader;

	/**
	 * Create the holder.
	 */
	public BlackListClassLoaderHolder() {
		this.classLoader = new BlackListClassLoader(Thread.currentThread().getContextClassLoader());
	}

	@Override
	public void beforeTest() {
	}

	@Override
	public void afterTest() {
		classLoader.clear();
	}

	@Override
	public ClassLoader get() {
		return classLoader;
	}

	/**
	 * Clear class blacklist.
	 */
	public void clearBlackList() {
		classLoader.clear();
	}

	/**
	 * Add blacklisted class.
	 *
	 * @param name Class name.
	 */
	public void addToBlackList(String name) {
		classLoader.add(name);
	}
}
