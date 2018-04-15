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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A rule that will execute unit test method in a new thread.
 * This is particularly important since each thread will be started with a
 * new class loader.
 */
class RunInNewThreadRule implements TestRule {
	/**
	 * The class loader holder that will return the class loader to use
	 * for each unit test method.
	 */
	private final ClassLoaderHolder classLoaderHolder;

	/**
	 * Create the rule.
	 *
	 * @param classLoaderHolder The class loader holder.
	 */
	RunInNewThreadRule(ClassLoaderHolder classLoaderHolder) {
		this.classLoaderHolder = classLoaderHolder;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		return new RunInNewThreadStatement(classLoaderHolder, base, description);
	}

	/**
	 * A specialized implementation of {@link Statement} that will run base
	 * statement in a new thread.
	 */
	private static class RunInNewThreadStatement extends Statement {
		/**
		 * The class loader holder.
		 */
		private final ClassLoaderHolder classLoaderHolder;

		/**
		 * The base statement.
		 */
		private final Statement statement;

		/**
		 * The base description.
		 */
		private final Description description;

		// Volatile because we need visibility between threads.
		/**
		 * The exception that may be thrown in unit test and that will need
		 * to be propagated.
		 */
		private volatile Throwable throwable;

		RunInNewThreadStatement(ClassLoaderHolder classLoaderHolder, Statement statement, Description description) {
			this.classLoaderHolder = classLoaderHolder;
			this.statement = statement;
			this.description = description;
		}

		@Override
		public void evaluate() throws Throwable {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						statement.evaluate();
					} catch (Throwable throwable) {
						RunInNewThreadStatement.this.throwable = throwable;
					}
				}
			});

			// Set the thread name, can be interesting for debugging.
			thread.setName("JUnit{" + description.getDisplayName() + "}");

			// Use the custom classloader.
			thread.setContextClassLoader(classLoaderHolder.get());

			// Now, we can start the thread!
			thread.start();

			try {
				thread.join();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}

			rethrow();
		}

		private void rethrow() throws Throwable {
			if (throwable != null) {
				throw throwable;
			}
		}
	}
}
