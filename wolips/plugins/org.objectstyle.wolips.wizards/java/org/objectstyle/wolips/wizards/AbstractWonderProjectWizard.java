package org.objectstyle.wolips.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.objectstyle.wolips.core.resources.internal.types.project.ProjectPatternsets;
import org.objectstyle.wolips.templateengine.TemplateDefinition;
import org.objectstyle.wolips.templateengine.TemplateEngine;

/**
 *
 */
public abstract class AbstractWonderProjectWizard extends AbstractProjectWizard {

	protected void addComponentDefinition(String templateFolder, TemplateEngine engine, String path, String name) {
		File wo = new File(path + File.separator + "Components" + File.separator + name + ".wo");
		wo.mkdirs();
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".html.vm", path + File.separator + "Components" + File.separator  + name + ".wo", name + ".html", name + ".html"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".wod.vm", path + File.separator + "Components" + File.separator  + name + ".wo", name + ".wod", name + ".wod"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".woo.vm", path + File.separator + "Components" + File.separator  + name + ".wo", name + ".woo", name + ".woo"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".api.vm", path + File.separator + "Components", name + ".api", name + ".api"));
		engine.addTemplate(new TemplateDefinition(templateFolder + "/" + name + ".java.vm", path + File.separator + "Sources", name + ".java", name + ".java"));
	}

	protected void prepare(String path) {
		File components = new File(path + File.separator + "Components");
		components.mkdirs();
		File src = new File(path + File.separator + "Sources");
		src.mkdirs();
		File resources = new File(path + File.separator + "Resources");
		resources.mkdirs();
		File wsresources = new File(path + File.separator + "WebServerResources");
		wsresources.mkdirs();
		File libraries = new File(path + File.separator + "Libraries");
		libraries.mkdirs();
		File bin = new File(path + File.separator + "bin");
		bin.mkdirs();
		File ant = new File(path + File.separator + ProjectPatternsets.ANT_FOLDER_NAME);
		ant.mkdirs();

		writeString("WebServerResources/**/*\n", new File(ant, "wsresources.include.patternset"));
		writeString("Components/**/*.wo\nComponents/**/*.api\nResources/**/*\n", new File(ant, "resources.include.patternset"));
	}

	private void writeString(String string, File file) {
		try {
			File parentDir = file.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			OutputStream os = new FileOutputStream(file);
			os.write(string.getBytes());
			os.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
