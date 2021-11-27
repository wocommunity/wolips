package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestQualifier {
	@Test(expected = IllegalArgumentException.class)
	public void qualifierForInvalidClassName() throws Exception {
		Qualifier.forClassName("invalidClassName");
	}

	@Test
	public void qualifierForNullClassName() throws Exception {
		assertThat(Qualifier.forClassName(null), is(Qualifier.KEY_VALUE));
	}

	@Test
	public void qualifierForValidClassName() throws Exception {
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EOKeyValueQualifier"), is(Qualifier.KEY_VALUE));
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EOKeyComparisonQualifier"), is(Qualifier.KEY_COMPARISON));
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EOAndQualifier"), is(Qualifier.AND));
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EOOrQualifier"), is(Qualifier.OR));
		assertThat(Qualifier.forClassName("com.webobjects.eocontrol.EONotQualifier"), is(Qualifier.NOT));
	}
}
