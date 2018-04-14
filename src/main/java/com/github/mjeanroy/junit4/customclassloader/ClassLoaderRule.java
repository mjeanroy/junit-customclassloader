package com.github.mjeanroy.junit4.customclassloader;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

class ClassLoaderRule extends ExternalResource implements TestRule {
	private final ClassLoaderHolder classLoaderHolder;

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
