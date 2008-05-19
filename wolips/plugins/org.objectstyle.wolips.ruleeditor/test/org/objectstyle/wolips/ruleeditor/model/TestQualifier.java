package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestQualifier {
	@Test(expected = IllegalArgumentException.class)
	public void qualifierForInvalidClassName() throws Exception {
		Qualifier.forClassName("invalidClassName");
	}

	@Test(expected = IllegalArgumentException.class)
	public void qualifierForInvalidDisplayName() throws Exception {
		Qualifier.forDisplayName("invalidDisplayName");
	}

	@Test
	public void qualifierForNullClassName() throws Exception {
		assertThat(Qualifier.forClassName(null), is(Qualifier.KEY_VALUE));
	}

	@Test
	public void qualifierForNullDisplayName() throws Exception {
		assertThat(Qualifier.forDisplayName(null), is(Qualifier.KEY_VALUE));
	}

	@Test
	public void qualifierForValidClassName() throws Exception {
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EOKeyValueQualifier"), is(Qualifier.KEY_VALUE));
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EOAndQualifier"), is(Qualifier.AND));
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EOOrQualifier"), is(Qualifier.OR));
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EONotQualifier"), is(Qualifier.NOT));
	}

	@Test
	public void qualifierForValidDisplayName() throws Exception {
		assertThat(Qualifier.forDisplayName(""), is(Qualifier.KEY_VALUE));
		assertThat(Qualifier.forDisplayName("and"), is(Qualifier.AND));
		assertThat(Qualifier.forDisplayName("or"), is(Qualifier.OR));
		assertThat(Qualifier.forDisplayName("not"), is(Qualifier.NOT));
	}
}
