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

import java.lang.reflect.Field;
import java.util.List;

import com.github.mjeanroy.junit4.customclassloader.fixtures.ChildClassAnnotated;
import com.github.mjeanroy.junit4.customclassloader.fixtures.ClassNotAnnotated;
import com.github.mjeanroy.junit4.customclassloader.fixtures.CustomFieldAnnotation;
import com.github.mjeanroy.junit4.customclassloader.fixtures.ParentClassAnnotated;
import com.github.mjeanroy.junit4.customclassloader.fixtures.CustomAnnotation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ReflectionsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void it_should_find_annotation_on_class() {
		CustomAnnotation annotation = Reflections.findAnnotation(ParentClassAnnotated.class, CustomAnnotation.class, "foo");
		assertThat(annotation).isNotNull();
		assertThat(annotation.id()).isZero();
	}

	@Test
	public void it_should_find_annotation_on_super_class() {
		CustomAnnotation annotation = Reflections.findAnnotation(ChildClassAnnotated.class, CustomAnnotation.class, "foo");
		assertThat(annotation).isNotNull();
		assertThat(annotation.id()).isZero();
	}

	@Test
	public void it_should_not_find_annotation() {
		String message = "foo";

		thrown.expect(IllegalStateException.class);
		thrown.expectMessage(message);

		Reflections.findAnnotation(ClassNotAnnotated.class, CustomAnnotation.class, "foo");
	}

	@Test
	public void it_should_instantiate_class() {
		ClassNotAnnotated instance = Reflections.newInstance(ClassNotAnnotated.class);
		assertThat(instance).isNotNull();
	}

	@Test
	public void it_should_get_all_fields_annotated_with() {
		List<Field> fields = Reflections.findFieldsAnnotatedWith(ChildClassAnnotated.class, CustomFieldAnnotation.class);
		assertThat(fields).hasSize(2);
		assertThat(fields.get(0).getName()).isEqualTo("field11");
		assertThat(fields.get(1).getName()).isEqualTo("field01");
	}

	@Test
	public void it_should_set_field_value() throws Exception {
		Field field = ChildClassAnnotated.class.getDeclaredField("field11");
		String value = "foobar";
		ChildClassAnnotated target = new ChildClassAnnotated();

		Reflections.setter(target, field, value);

		assertThat(target.getField11()).isEqualTo(value);
	}
}
