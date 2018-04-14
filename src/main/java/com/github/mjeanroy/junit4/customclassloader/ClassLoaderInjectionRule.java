package com.github.mjeanroy.junit4.customclassloader;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

class ClassLoaderInjectionRule extends ExternalResource implements TestRule {
	private final Object target;
	private final CustomClassLoaderHolder classLoaderHolder;

	ClassLoaderInjectionRule(Object target, CustomClassLoaderHolder classLoaderHolder) {
		this.target = target;
		this.classLoaderHolder = classLoaderHolder;
	}

	@Override
	protected void before() {
		injectClassLoader();
	}

	@Override
	protected void after() {
		cleanUpClassLoaderInjection();
	}

	private void injectClassLoader() {
		List<Field> fields = Reflections.findFieldsAnnotatedWith(target.getClass(), TestClassLoader.class);

		for (Field field : fields) {
			injectClassLoaderField(field);
		}
	}

	private void injectClassLoaderField(Field field) {
		Class<?> type = field.getType();

		if (ClassLoader.class.isAssignableFrom(type)) {
			Reflections.setter(target, field, classLoaderHolder.get());
		}
		else if (CustomClassLoaderHolder.class.isAssignableFrom(type)) {
			Reflections.setter(target, field, classLoaderHolder);
		}
		else {
			throw new IllegalStateException("Cannot set field '" + field.getName() + "', it should be an instance of '" + ClassLoader.class.getName() + "' or '" + CustomClassLoaderHolder.class.getName() + "'.");
		}
	}

	private void cleanUpClassLoaderInjection() {
		List<Field> fields = Reflections.findFieldsAnnotatedWith(target.getClass(), TestClassLoader.class);
		for (Field field : fields) {
			Reflections.setter(target, field, null);
		}
	}
}
