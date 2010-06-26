package org.objectstyle.woproject.maven2.wolifecycle;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Before;
import org.junit.Test;

public class TestAbstractPackageMojo extends AbstractMojoTestCase {

    private static final File TEST_POM = new File(getBasedir(),
	    "src/test/resources/unit/wolifecycle-basic-test/pom.xml");

    protected AbstractPackageMojo mojo;

    @Override
    @Before
    protected void setUp() throws Exception {
	super.setUp();

	mojo = (AbstractPackageMojo) lookupMojo("package-woapplication",
		TEST_POM);
    }

    @Test
    public void testClassifierAsString() throws Exception {
	assertThat(mojo.getClassifierAsString(), is("-someClassifier"));
    }

    @Test
    public void testFinalNameWithClassifier() throws Exception {
	assertThat(mojo.getArtifactFile().getName(),
		is("foo-1.0-SNAPSHOT-someClassifier.woapplication"));
    }

    @Test
    public void testFinalNameWithEmptyClassifier() throws Exception {
	mojo.setClassifier(null);

	assertThat(mojo.getArtifactFile().getName(),
		is("foo-1.0-SNAPSHOT.woapplication"));
    }

    @Test
    public void testNullClassifierAsString() throws Exception {
	mojo.setClassifier(null);

	assertThat(mojo.getClassifierAsString(), is(""));
    }
}
