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

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ClassLoader} implementation that will explicitly fail when blacklisted class are loaded.
 * This is especially useful when implementing tests where tested methods relies on classpath detection.
 */
public class BlackListClassLoader extends ClassLoader {
	/**
	 * The blacklist set of classes.
	 */
	private final Set<String> blacklist;

	/**
	 * The parent classloader, everything will be delegated to this classloader, except for blacklisted classes.
	 */
	private final ClassLoader parent;

	/**
	 * Create the classloader.
	 *
	 * @param parent The parent classloader.
	 */
	BlackListClassLoader(ClassLoader parent) {
		this.parent = parent;
		this.blacklist = new HashSet<>();
	}

	@Override
	public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
		// Only use custom classloader for classes from these packages
		if (blacklist.contains(name)) {
			// The `findClass` method will throw ClassNotFoundException.
			super.findClass(name);
		}

		// Otherwise load from the parent classloader
		return parent.loadClass(name);
	}

	/**
	 * Clear the entire blacklist.
	 */
	public void clear() {
		this.blacklist.clear();
	}

	/**
	 * Add blacklisted class.
	 *
	 * @param name Class name (fully qualified name).
	 */
	public void add(String name) {
		this.blacklist.add(name);
	}
}
