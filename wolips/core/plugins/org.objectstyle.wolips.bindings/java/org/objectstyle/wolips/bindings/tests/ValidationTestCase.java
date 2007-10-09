package org.objectstyle.wolips.bindings.tests;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.objectstyle.wolips.bindings.api.ApiModel;
import org.objectstyle.wolips.bindings.api.ApiModelException;
import org.objectstyle.wolips.bindings.api.Validation;

public class ValidationTestCase extends TestCase {
	public void testPierreValidationFailure() throws ApiModelException {
		String api = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<wodefinitions>" +
		"	<wo class=\"SPRegistrationErrorConditional\" wocomponentcontent=\"true\">" +
		"		<binding name=\"attributeTypeName\"/>" +
		"		<binding name=\"addressType\"/>" +
		"		<binding name=\"key\"/>" +

		"		<validation message=\"The error conditional must include either an &quot;attributeTypeName&quot; or an &quot;addressType&quot; / &quot;key&quot; pair.\">" +
		"			<and>" +
		"				<unbound name=\"attributeTypeName\"/>" +
		"				<or>" +
		"					<unbound name=\"addressType\"/>" +
		"					<unbound name=\"key\"/>" +
		"				</or>" +
		"			</and>" +
		"		</validation>" +
		"	</wo>" +
		"</wodefinitions>";
		StringReader sr = new StringReader(api);
		ApiModel model = new ApiModel(sr);
		
		Map bindings = new HashMap();
		bindings.put("attributeTypeName", "\"value\"");
		Validation[] failedValidations = model.getWo().getFailedValidations(bindings);
		assertEquals(0, failedValidations.length);

		bindings.clear();
		bindings.put("addressType", "currentAddressType");
		bindings.put("key", "currentKey");
		failedValidations = model.getWo().getFailedValidations(bindings);
		assertEquals(0, failedValidations.length);

		for (int validationNum = 0; validationNum < failedValidations.length; validationNum++) {
			Validation validation = failedValidations[validationNum];
			System.out.println("ValidationTestCase.testTest: " + validation.getMessage());
		}
	}
}
