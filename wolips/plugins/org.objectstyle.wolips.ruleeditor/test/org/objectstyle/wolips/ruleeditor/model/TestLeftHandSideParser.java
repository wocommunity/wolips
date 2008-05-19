package org.objectstyle.wolips.ruleeditor.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TestLeftHandSideParser {
	protected LeftHandSideParser parser;

	@Test
	public void parseEmptyQualifier() {
		Map<String, Object> result = parser.parse("");

		assertThat(result.isEmpty(), is(true));

		result = parser.parse("     ");

		assertThat(result.isEmpty(), is(true));
	}

	@Test
	public void parseNotQualifier() {
		String conditions = "not(task = 'edit')";

		Map<String, Object> result = parser.parse(conditions);

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EONotQualifier"));
		assertThat(result.get(AbstractQualifierElement.KEY_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.VALUE_KEY), nullValue());

		Collection<QualifierElement> qualifiers = (Collection<QualifierElement>) result.get(AbstractQualifierElement.QUALIFIERS_KEY);

		assertThat(qualifiers.size(), is(1));
	}

	@Test
	public void parseNullQualifier() {
		Map<String, Object> result = parser.parse(null);

		assertThat(result.isEmpty(), is(true));
	}

	@Test
	public void parseOneQualifier() {

		String conditions = "(task = 'edit')";

		Map<String, Object> result = parser.parse(conditions);

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOKeyValueQualifier"));
		assertThat((String) result.get(AbstractQualifierElement.KEY_KEY), is("task"));
		assertThat((String) result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), is("isEqualTo"));
		assertThat((String) result.get(AbstractQualifierElement.VALUE_KEY), is("edit"));
		assertThat(result.get(AbstractQualifierElement.QUALIFIERS_KEY), nullValue());
	}

	@Test
	public void parseOneQualifierWithoutParenthesis() {
		String conditions = "task = 'edit'";

		Map<String, Object> result = parser.parse(conditions);

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOKeyValueQualifier"));
		assertThat((String) result.get(AbstractQualifierElement.KEY_KEY), is("task"));
		assertThat((String) result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), is("isEqualTo"));
		assertThat((String) result.get(AbstractQualifierElement.VALUE_KEY), is("edit"));
		assertThat(result.get(AbstractQualifierElement.QUALIFIERS_KEY), nullValue());
	}

	@Test
	public void parseThreeQualifiers() {
		String conditions = "(task = query and (entity.name = Action or propertyKey = status))";

		Map<String, Object> result = parser.parse(conditions);

		Collection<QualifierElement> qualifiers = (Collection<QualifierElement>) result.get(AbstractQualifierElement.QUALIFIERS_KEY);

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOAndQualifier"));
		assertThat(result.get(AbstractQualifierElement.KEY_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.VALUE_KEY), nullValue());

		assertThat(qualifiers.size(), is(2));

		for (QualifierElement qualifierElement : qualifiers) {
			if (qualifierElement.getQualifiers() != null) {
				assertThat(qualifierElement.getQualifiers().size(), is(2));
			}
		}
	}

	@Test
	public void parseThreeQualifiersInferringThePrecedence() throws Exception {
		String conditions = "task = query and entity.name = Action or propertyKey = status";

		Map<String, Object> result = parser.parse(conditions);

		Collection<QualifierElement> qualifiers = (Collection<QualifierElement>) result.get(AbstractQualifierElement.QUALIFIERS_KEY);

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOOrQualifier"));
		assertThat(result.get(AbstractQualifierElement.KEY_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.VALUE_KEY), nullValue());

		assertThat(qualifiers.size(), is(2));

		QualifierElement qualifier = (QualifierElement) qualifiers.toArray()[0];

		assertThat(qualifier.getAssignmentClassName(), is("com.webobjects.eocontrol.EOAndQualifier"));
		assertThat(qualifier.getQualifiers().size(), is(2));
	}

	@Test
	public void parseThreeQualifiersWithoutParenthesis() {
		String conditions = "task = query and entity.name = Action and propertyKey = status";

		Map<String, Object> result = parser.parse(conditions);

		Collection<QualifierElement> qualifiers = (Collection<QualifierElement>) result.get(AbstractQualifierElement.QUALIFIERS_KEY);

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOAndQualifier"));
		assertThat(result.get(AbstractQualifierElement.KEY_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.VALUE_KEY), nullValue());

		assertThat(qualifiers.size(), is(3));
	}

	@Test
	public void parseTwoQualifiers() {
		String conditions = "(task = 'edit' or task = 'delete')";

		Map<String, Object> result = parser.parse(conditions);

		assertThat((String) result.get(AbstractRuleElement.CLASS_KEY), is("com.webobjects.eocontrol.EOOrQualifier"));
		assertThat(result.get(AbstractQualifierElement.KEY_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.SELECTOR_NAME_KEY), nullValue());
		assertThat(result.get(AbstractQualifierElement.VALUE_KEY), nullValue());

		Collection<QualifierElement> qualifiers = (Collection<QualifierElement>) result.get(AbstractQualifierElement.QUALIFIERS_KEY);

		assertThat(qualifiers.size(), is(2));
	}

	@Before
	public void setUp() {
		parser = new LeftHandSideParser();
	}

}
