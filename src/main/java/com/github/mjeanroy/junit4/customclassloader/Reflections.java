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

import static java.util.Collections.emptyList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Static Reflections Utilities.
 */
final class Reflections {

	// Ensure non instantiation.
	private Reflections() {
	}

	/**
	 * Find annotation on given class (and go up in the superclass hierarchy if needed).
	 *
	 * @param klass The base class.
	 * @param annotationClass The annotation class to look for.
	 * @param message The error message to throw if annotation cannot be found.
	 * @param <T> Annotation Type.
	 * @return The annotation instance.
	 * @throws IllegalStateException If annotation cannot be found in the class hierarchy.
	 */
	static <T extends Annotation> T findAnnotation(Class<?> klass, Class<T> annotationClass, String message) {
		T annotation = findAnnotation(klass, annotationClass);
		if (annotation == null) {
			throw new IllegalStateException(message);
		}

		return annotation;
	}

	/**
	 * Instantiate class using the default empty constructor (must be public).
	 *
	 * @param klass The base class.
	 * @param <T> Class Type.
	 * @return The class instance.
	 * @throws IllegalStateException If class cannot be instantiated.
	 */
	static <T> T newInstance(Class<T> klass) {
		try {
			return klass.newInstance();
		} catch (IllegalAccessException | InstantiationException ex) {
			throw new IllegalStateException("Cannot instantiate class " + klass.getName() + ". Please ensure there is a public empty constructor.", ex);
		}
	}

	/**
	 * Find all fields annotated with given annotation class (go up in the class hierarchy if needed).
	 *
	 * @param klass The base class.
	 * @param annotationClass The annotation class.
	 * @param <T> Annotation Type.
	 * @return The list of all fields annotated.
	 */
	static <T extends Annotation> List<Field> findFieldsAnnotatedWith(Class<?> klass, Class<T> annotationClass) {
		if (klass == null) {
			return emptyList();
		}

		// Analyse the current class.
		List<Field> fields = new ArrayList<>();
		for (Field field : klass.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotationClass)) {
				fields.add(field);
			}
		}

		// Go up in the hierarchy.
		fields.addAll(findFieldsAnnotatedWith(klass.getSuperclass(), annotationClass));

		return fields;
	}

	/**
	 * Update value of given field on given class instance.
	 *
	 * @param target The target instance.
	 * @param field The field to set value.
	 * @param value The value to set on given field.
	 */
	static void setter(Object target, Field field, Object value) {
		boolean wasAccessible = field.isAccessible();
		try {
			if (!wasAccessible) {
				field.setAccessible(true);
			}

			field.set(target, value);
		}
		catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		finally {
			if (!wasAccessible) {
				field.setAccessible(false);
			}
		}
	}

	/**
	 * Find annotation on given class (and go up in the superclass hierarchy if needed).
	 *
	 * @param klass The base class.
	 * @param annotationClass The annotation class to look for.
	 * @param <T> Annotation Type.
	 * @return The annotation instance, {@code null} if class is not annotated.
	 */
	private static <T extends Annotation> T findAnnotation(Class<?> klass, Class<T> annotationClass) {
		Class<?> current = klass;
		while (current != null) {
			T annotation = current.getAnnotation(annotationClass);
			if (annotation != null) {
				return annotation;
			}

			current = current.getSuperclass();
		}

		return null;
	}
}
