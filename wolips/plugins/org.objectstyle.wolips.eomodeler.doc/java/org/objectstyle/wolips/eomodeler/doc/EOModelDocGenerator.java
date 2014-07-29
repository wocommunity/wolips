package org.objectstyle.wolips.eomodeler.doc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;

public class EOModelDocGenerator {
	public static class ConsoleLogger implements LogSystem {
		public void init(RuntimeServices runtimeservices) throws Exception {
			// DO NOTHING
		}

		public void logVelocityMessage(int i, String s) {
			System.out.println("ConsoleLogger.logVelocityMessage: " + i + ", " + s);
		}
	}

	public static void generate(EOModelGroup modelGroup, File outputFolder, File templatePath, String entityURLTemplate) throws Exception {
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(modelGroup.getClass().getClassLoader());
		try {
			VelocityEngine velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, org.apache.velocity.runtime.log.NullLogSystem.class.getName());
			// velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
			// ConsoleLogger.class.getName());
			StringBuffer templatePaths = new StringBuffer();
			templatePaths.append(".");
			if (templatePath != null) {
				templatePaths.append(",");
				templatePaths.append(templatePath.getAbsolutePath());
			}
			velocityEngine.setProperty("resource.loader", "file,class");
			velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
			velocityEngine.setProperty("file.resource.loader.path", templatePaths.toString());
			velocityEngine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
	
			velocityEngine.init();

			VelocityContext context = new VelocityContext();
	
			context.put("modelGroup", modelGroup);
			EOModelDocGenerator.writeTemplate(velocityEngine, context, "eomodeldoc.css.vm", new File(outputFolder, "eomodeldoc.css"));
			EOModelDocGenerator.writeTemplate(velocityEngine, context, "eomodeldoc.js.vm", new File(outputFolder, "eomodeldoc.js"));
			EOModelDocGenerator.writeTemplate(velocityEngine, context, "prototype.js.vm", new File(outputFolder, "prototype.js"));
			EOModelDocGenerator.writeTemplate(velocityEngine, context, "index.html.vm", new File(outputFolder, "index.html"));
			EOModelDocGenerator.writeTemplate(velocityEngine, context, "indexContent.html.vm", new File(outputFolder, "content.html"));
			EOModelDocGenerator.writeTemplate(velocityEngine, context, "indexOverview.html.vm", new File(outputFolder, "overview.html"));
			EOModelDocGenerator.writeTemplate(velocityEngine, context, "indexModels.html.vm", new File(outputFolder, "models.html"));
			for (EOModel model : modelGroup.getModels()) {
				System.out.println("Generating " + model.getName() + " ...");
				context.put("model", model);
				EOModelDocGenerator.writeTemplate(velocityEngine, context, "modelOverview.html.vm", new File(outputFolder, model.getName() + "/overview.html"));
				EOModelDocGenerator.writeTemplate(velocityEngine, context, "modelContent.html.vm", new File(outputFolder, model.getName() + "/content.html"));
	
				for (EOEntity entity : model.getEntities()) {
					System.out.println("Generating " + model.getName() + "." + entity.getName() + " ...");
					context.put("entity", entity);
					if (entityURLTemplate != null) {
						String className = entity.getClassName();
						if (className != null && className.length() > 0) {
							StringWriter entityURLWriter = new StringWriter();
							VelocityContext entityURLContext = new VelocityContext();
							entityURLContext.put("entity", entity);
							entityURLContext.put("model", model);
							Velocity.evaluate(entityURLContext, entityURLWriter, "entityURL", entityURLTemplate);
							context.put("entityURL", entityURLWriter.toString());
						}
					}
					EOModelDocGenerator.writeTemplate(velocityEngine, context, "entityContent.html.vm", new File(outputFolder, model.getName() + "/entities/" + entity.getName() + ".html"));
				}
	
				for (EOStoredProcedure storedProcedure : model.getStoredProcedures()) {
					System.out.println("Generating " + model.getName() + "." + storedProcedure.getName() + " ...");
					context.put("storedProcedure", storedProcedure);
					EOModelDocGenerator.writeTemplate(velocityEngine, context, "storedProcedureContent.html.vm", new File(outputFolder, model.getName() + "/storedProcedures/" + storedProcedure.getName() + ".html"));
				}
			}
		} finally {
			thread.setContextClassLoader(loader);
		}

		System.out.println("Done: " + new File(outputFolder, "index.html"));
	}

	public static void main(String[] args) throws Exception {
		// String userHomeWOLipsPath = System.getProperty("user.home") +
		// File.separator + "Library" + File.separator + "WOLips";
		// URL url = null;
		// url = FileLocator.resolve(Activator.getDefault().getBundle().);
		// String templatePaths = userHomeWOLipsPath + ", ";
		// Path path = new Path(url.getPath());
		// templatePaths = templatePaths +
		// path.append("templates").toOSString();
		// velocityEngine.setProperty("resource.loader", "wolips");
		// velocityEngine.setProperty("wolips.resource.loader.class",
		// org.objectstyle.wolips.thirdparty.velocity.resourceloader.ResourceLoader.class.getName());
		// velocityEngine.setProperty("wolips.resource.loader.bundle",
		// Activator.getDefault().getBundle());
		// velocityEngine.setProperty("jar.resource.loader.path", "jar:" +
		// TemplateEnginePlugin.getDefault().getBundle().getResource("plugin.xml").getFile());
		EOModelGroup modelGroup = new EOModelGroup();
		modelGroup.loadModelFromURL(new File("/Users/mschrag/Documents/workspace/ERPrototypes/Resources/erprototypes.eomodeld").toURL());
		modelGroup.loadModelFromURL(new File("/Users/mschrag/Documents/workspace/MDTAccounting/MDTAccounting.eomodeld").toURL());
		modelGroup.loadModelFromURL(new File("/Users/mschrag/Documents/workspace/MDTask/MDTask.eomodeld").toURL());
		modelGroup.resolve(new HashSet<EOModelVerificationFailure>());

		File outputFolder = new File("/tmp/eomodeldoc");

		EOModelDocGenerator.generate(modelGroup, outputFolder, null, null);
	}

	public static void writeTemplate(VelocityEngine engine, VelocityContext context, String templateName, File outputFile) throws Exception {
		try {
			Template template = engine.getTemplate(templateName, "UTF-8");
			if (!outputFile.getParentFile().exists()) {
				if (!outputFile.getParentFile().mkdirs()) {
					throw new IOException("Unable to create the folder " + outputFile.getParentFile() + ".");
				}
			}
			Writer outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
			try {
				template.merge(context, outputWriter);
			} finally {
				outputWriter.close();
			}
		} catch (Exception e) {
			throw new Exception("Failed to generate '" + outputFile + "' with template '" + templateName + "'.", e);
		}
	}
}
