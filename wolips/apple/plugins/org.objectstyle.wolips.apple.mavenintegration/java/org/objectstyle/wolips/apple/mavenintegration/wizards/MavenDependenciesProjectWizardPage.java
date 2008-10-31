/* ====================================================================
 *
 * The ObjectStyle Group Software License, Version 1.0
 *
 * Copyright (c) 2004 The ObjectStyle Group,
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne"
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */
/*Portions of this code are Copyright Apple Inc. 2008 and licensed under the
ObjectStyle Group Software License, version 1.0.  This license from Apple
applies solely to the actual code contributed by Apple and to no other code.
No other license or rights are granted by Apple, explicitly, by implication,
by estoppel, or otherwise.  All rights reserved.*/
package org.objectstyle.wolips.apple.mavenintegration.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.maven.ide.eclipse.wizards.MavenDependenciesWizardPage;
import org.objectstyle.wolips.wizards.Messages;

/**
 * Wizard page to add dependencies to a Maven project
 */
public class MavenDependenciesProjectWizardPage extends MavenDependenciesWizardPage {

  /**
   */
  public MavenDependenciesProjectWizardPage() {
    setTitle(Messages.getString("MavenDependenciesProjectWizardPage.title"));
    setDescription(Messages.getString("MavenDependenciesProjectWizardPage.description"));
    setPageComplete(true);
    setDependencies(new Dependency[0]);
  }

  @Override
  public void createControl(Composite parent) {
    super.createControl(parent);
//    Control[] controls = parent.getChildren();
//    printAllControls(controls); //Debug
    setPageComplete(true);
    ((WOMavenApplicationProjectWizard) this.getWizard()).setCurrentDependencies(this.getCombinedDependencies());
  }

  /**
   * @return list of dependencies
   */
  @SuppressWarnings("unchecked")
  public List getCombinedDependencies() {

    //TODO: fix default runtime version
    ArrayList updatedList = new ArrayList(defaultDependencies("5.4.2-SNAPSHOT"));
    updatedList.addAll(new ArrayList(Arrays.asList(this.getDependencies())));

    return updatedList;
  }

  /**
   * @param version
   * @return dependency list
   */
  public static List<Dependency> defaultDependencies(String version) {
    ArrayList<Dependency> list = new ArrayList<Dependency>();
    String aVersion = version;
    String groupID = "com.webobjects";
    if (aVersion == null || aVersion.length() < 0) {
      aVersion = "5.5-SNAPSHOT";
    }
    list.add(createDependency("JavaXML", groupID, aVersion));
    list.add(createDependency("JavaFoundation", groupID, aVersion));
    list.add(createDependency("JavaWebObjects", groupID, aVersion));
    list.add(createDependency("JavaWOExtensions", groupID, aVersion));
    list.add(createDependency("JavaEOControl", groupID, aVersion));
    list.add(createDependency("JavaEOAccess", groupID, aVersion));
    list.add(createDependency("JavaJDBCAdaptor", groupID, aVersion));

    return list;
  }

  /**
   * @param artifactID
   * @param groupID
   * @param version
   * @return Dependency
   */
  public static Dependency createDependency(String artifactID, String groupID, String version) {
    Dependency dep = new Dependency();
    dep.setArtifactId(artifactID);
    dep.setGroupId(groupID);
    dep.setVersion(version);

    return dep;
  }

  /**
   * Debug controls
   * @param controls
   */
  static void printAllControls(Control[] controls) {
    System.out.println("---------------");
    for (Control c : controls) {
      if (c instanceof Composite) {
        printAllControls(((Composite) c).getChildren());
      }
      else {
        System.out.println("Control: " + c.getClass().getName() + " other:" + c.toString());
      }
    }
  }

  /**
   *
   */
  protected void addDefaultDependencies() {
    List dependencies = AbstractMavenProjectWizard.defaultDependencies(null);

  }
}
