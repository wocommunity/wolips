package org.objectstyle.woproject.maven2.wolifecycle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.junit.Before;
import org.junit.Test;

public class TestAbstractDefineResourcesMojo {
	protected List<Resource> resources;

	protected Resource mockResource;

	protected static final String RESOURCES_DIRECTORY = "src/main/resources";

	@Test
	public void createNewResourceIfCannotFindOne() throws Exception {
		Resource resource = AbstractDefineResourcesMojo.findOrCreateResource(resources, RESOURCES_DIRECTORY);

		assertThat(resource, is(mockResource));
	}

	@Test
	public void directoryForNewResource() throws Exception {
		String resourceDirectoryNotAvailable = "src/main/another-folder";

		Resource resource = AbstractDefineResourcesMojo.findOrCreateResource(resources, resourceDirectoryNotAvailable);

		assertThat(resource.getDirectory(), is(resourceDirectoryNotAvailable));
	}

	@Test(expected = IllegalArgumentException.class)
	public void exceptionForNullResourceDirectory() throws Exception {
		AbstractDefineResourcesMojo.findOrCreateResource(resources, null);
	}

	@Test
	public void findAlreadyDefinedResource() throws Exception {
		Resource resource = AbstractDefineResourcesMojo.findOrCreateResource(resources, "src/main/another-folder");

		assertThat(resource, not(mockResource));
		assertThat(resource, notNullValue());
	}

	@Before
	public void setup() {
		resources = new ArrayList<Resource>();

		mockResource = new Resource();

		mockResource.setDirectory(RESOURCES_DIRECTORY);

		resources.add(mockResource);
	}
}
