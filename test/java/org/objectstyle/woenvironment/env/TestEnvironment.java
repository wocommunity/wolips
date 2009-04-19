package org.objectstyle.woenvironment.env;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Environment.class)
public class TestEnvironment {
	@Test
	public void environmentVariablesWithEmptyLine() throws Exception {
		PowerMockito.mockStatic(Environment.class);

		Process mockProcess = Mockito.mock(Process.class);

		Mockito.when(Environment.osProcess()).thenReturn(mockProcess);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("property1=value1\n");
		builder.append("\n");
		builder.append("property2=value2");
		
		InputStream mockInputStream = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));
		
		Mockito.doReturn(mockInputStream).when(mockProcess).getInputStream();

		Environment environment = new Environment();

		Properties result = environment.getEnvVars();
		
		assertThat(result.size(), is(2));
	}
}
