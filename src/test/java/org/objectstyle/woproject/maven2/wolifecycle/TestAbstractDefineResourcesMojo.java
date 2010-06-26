package org.objectstyle.woproject.maven2.wolifecycle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestAbstractDefineResourcesMojo extends AbstractMojoTestCase {
    private static final File TEST_POM = new File(getBasedir(),
	    "src/test/resources/unit/wolifecycle-basic-test/pom.xml");

    protected List<Resource> resources;

    protected Resource mockResource;

    protected static final String RESOURCES_DIRECTORY = "src/main/resources";

    protected AbstractDefineResourcesMojo mojo;

    @Override
    @Before
    protected void setUp() throws Exception {
	super.setUp();

	mojo = (AbstractDefineResourcesMojo) lookupMojo(
		"define-woapplication-resources", TEST_POM);

	resources = new ArrayList<Resource>();

	mockResource = new Resource();

	mockResource.setDirectory(RESOURCES_DIRECTORY);

	resources.add(mockResource);
    }

    @Test
    public void testCreateNewResourceIfCannotFindOne() throws Exception {
	Resource resource = AbstractDefineResourcesMojo.findOrCreateResource(
		resources, RESOURCES_DIRECTORY);

	assertThat(resource, is(mockResource));
    }

    @Test
    public void testDefaultFullTargetPath() throws Exception {
	mojo.setClassifier(null);

	String path = mojo.getFullTargetPath("test");

	assertThat(path, is("../foo-1.0-SNAPSHOT.woa/Contents/test"));
    }

    @Test
    public void testDirectoryForNewResource() throws Exception {
	String resourceDirectoryNotAvailable = "src/main/another-folder";

	Resource resource = AbstractDefineResourcesMojo.findOrCreateResource(
		resources, resourceDirectoryNotAvailable);

	assertThat(resource.getDirectory(), is(resourceDirectoryNotAvailable));
    }

    @Test
    public void testDontFlattenHiddenResources() throws Exception {
	mojo.setFlattingResources(true);

	File file = Mockito.spy(new File(".svn"));

	Mockito.stub(file.isHidden()).toReturn(true);

	assertThat(mojo.includeResourcesRecursively(file), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionForNullResourceDirectory() throws Exception {
	try {
	    AbstractDefineResourcesMojo.findOrCreateResource(resources, null);

	    fail("Resources directory cannot be null. Must throw an exception.");
	} catch (IllegalArgumentException exception) {
	    // Funcionou corretamente
	}

    }

    @Test
    public void testFindAlreadyDefinedResource() throws Exception {
	Resource resource = AbstractDefineResourcesMojo.findOrCreateResource(
		resources, "src/main/another-folder");

	assertThat(resource, not(mockResource));
	assertThat(resource, notNullValue());
    }

    @Test
    public void testFlattenResourcesForNullInitialization() throws Exception {
	mojo.setFlattingResources(null);

	assertThat(mojo.flattenResources(), is(false));
    }

    @Test
    public void testFullTargetDirectoryNotIncludingVersion() throws Exception {
	mojo = Mockito.spy(mojo);

	Mockito.stub(mojo.includesVersionInArtifactName()).toReturn(false);

	String path = mojo.getFullTargetPath("folder");

	assertThat(path, is("../bar.woa/Contents/folder"));
    }

    @Test
    public void testFullTargetPathWithClassifier() throws Exception {
	String path = mojo.getFullTargetPath("test");

	assertThat(path,
		is("../foo-1.0-SNAPSHOT-someClassifier.woa/Contents/test"));
    }

    @Test
    public void testFullTargetPathWithFinalName() throws Exception {
	mojo.setFinalName("foo-bar-project");
	mojo.setClassifier(null);

	String path = mojo.getFullTargetPath("test");

	assertThat(path, is("../foo-bar-project.woa/Contents/test"));
    }
}
