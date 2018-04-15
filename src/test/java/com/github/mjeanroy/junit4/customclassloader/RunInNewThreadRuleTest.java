package com.github.mjeanroy.junit4.customclassloader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runner.Description.createTestDescription;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class RunInNewThreadRuleTest {

	@Test
	public void it_should_run_test_in_new_thread() throws Throwable {
		final Thread testThread = Thread.currentThread();
		final ClassLoaderHolder classLoaderHolder = spy(new BlackListClassLoaderHolder());
		final RunInNewThreadRule runInNewThreadRule = new RunInNewThreadRule(classLoaderHolder);
		final Description description = createTestDescription(RunInNewThreadRuleTest.class, "it_should_run_test_in_new_thread");
		final Statement statement = mock(Statement.class);

		final Answer<Object> answer = new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				assertThat(Thread.currentThread()).isNotSameAs(testThread);
				assertThat(Thread.currentThread().getName()).isEqualTo("JUnit{" + description.getDisplayName() + "}");
				assertThat(Thread.currentThread().getContextClassLoader()).isExactlyInstanceOf(BlackListClassLoader.class);
				return null;
			}
		};

		doAnswer(answer).when(statement).evaluate();

		Statement result = runInNewThreadRule.apply(statement, description);

		assertThat(result).isNotNull();
		verifyZeroInteractions(classLoaderHolder);

		result.evaluate();
		verify(classLoaderHolder).get();
	}

	@Test(expected = CustomRuntimeException.class)
	public void it_should_propagate_exception() throws Throwable {
		final ClassLoaderHolder classLoaderHolder = spy(new BlackListClassLoaderHolder());
		final RunInNewThreadRule runInNewThreadRule = new RunInNewThreadRule(classLoaderHolder);
		final Description description = createTestDescription(RunInNewThreadRuleTest.class, "it_should_run_test_in_new_thread");
		final Statement statement = mock(Statement.class);
		doThrow(CustomRuntimeException.class).when(statement).evaluate();

		Statement result = runInNewThreadRule.apply(statement, description);
		result.evaluate();
	}

	@SuppressWarnings("serial")
	private static class CustomRuntimeException extends RuntimeException {
	}
}
