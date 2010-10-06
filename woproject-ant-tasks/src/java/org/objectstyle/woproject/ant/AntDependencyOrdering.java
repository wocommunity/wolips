package org.objectstyle.woproject.ant;

import org.objectstyle.woenvironment.frameworks.DependencyOrdering;

public class AntDependencyOrdering extends DependencyOrdering<AntDependency> {

  public AntDependencyOrdering() {
		super(false);
	}

	@Override
  protected void addWOProject(AntDependency dependency) {
    // DO NOTHING
  }

}
