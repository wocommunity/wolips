package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestSelector {
	@Test(expected = IllegalArgumentException.class)
	public void selectorForInvalidName() throws Exception {
		Selector.forName("invalidName");
	}

	@Test(expected = IllegalArgumentException.class)
	public void selectorForInvalidOperator() throws Exception {
		Selector.forOperator("invalidOperator");
	}

	@Test
	public void selectorForNullName() throws Exception {
		assertThat(Selector.forName(null), is(Selector.EQUAL));
	}

	@Test
	public void selectorForNullOperator() throws Exception {
		assertThat(Selector.forOperator(null), is(Selector.EQUAL));
	}

	@Test
	public void selectorForValidName() throws Exception {
		assertThat(Selector.forName("isEqualTo"), is(Selector.EQUAL));
		assertThat(Selector.forName("isNotEqualTo"), is(Selector.NOT_EQUAL));
		assertThat(Selector.forName("isLessThan"), is(Selector.LESS_THAN));
		assertThat(Selector.forName("isLessThanOrEqualTo"), is(Selector.LESS_THAN_OR_EQUAL));
		assertThat(Selector.forName("isGreaterThan"), is(Selector.GREATER_THAN));
		assertThat(Selector.forName("isGreaterThanOrEqualTo"), is(Selector.GREATER_THAN_OR_EQUAL));
		assertThat(Selector.forName("isLike"), is(Selector.LIKE));
	}

	@Test
	public void selectorForValidOperator() throws Exception {
		assertThat(Selector.forOperator("="), is(Selector.EQUAL));
		assertThat(Selector.forOperator("!="), is(Selector.NOT_EQUAL));
		assertThat(Selector.forOperator("<"), is(Selector.LESS_THAN));
		assertThat(Selector.forOperator("<="), is(Selector.LESS_THAN_OR_EQUAL));
		assertThat(Selector.forOperator(">"), is(Selector.GREATER_THAN));
		assertThat(Selector.forOperator(">="), is(Selector.GREATER_THAN_OR_EQUAL));
		assertThat(Selector.forOperator("like"), is(Selector.LIKE));
	}
}
