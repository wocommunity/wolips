package org.objectstyle.wolips.eogenerator.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.core.runtime.Path;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel.Define;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;

public class EOGenerator {
	public static class ConsoleLogger implements LogSystem {
		public void init(RuntimeServices runtimeservices) throws Exception {
			// DO NOTHING
		}

		public void logVelocityMessage(int i, String s) {
			System.out.println("ConsoleLogger.logVelocityMessage: " + i + ", " + s);
		}
	}

	public static void generate(EOGeneratorModel eogeneratorModel) throws Exception {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, org.apache.velocity.runtime.log.NullLogSystem.class.getName());
		// velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
		// ConsoleLogger.class.getName());
		velocityEngine.setProperty("resource.loader", "file,class");
		StringBuffer templatePaths = new StringBuffer();
		templatePaths.append(".");
		String templatePath = eogeneratorModel.getTemplateDir();
		if (templatePath != null) {
			templatePaths.append(",");
			templatePaths.append(new File(templatePath).getAbsolutePath());
		}
		velocityEngine.setProperty("resource.loader", "file,class");
		velocityEngine.setProperty("file.resource.loader", templatePaths.toString());
		velocityEngine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());

		velocityEngine.init();
		VelocityContext context = new VelocityContext();

		List<EOModel> models = new LinkedList<EOModel>();
		EOModelGroup modelGroup = new EOModelGroup();
		modelGroup.setPrefix(eogeneratorModel.getPrefix());
		modelGroup.setEOGenericRecordClassName(eogeneratorModel.getDefineValueNamed("EOGenericRecord"));
		for (EOModelReference modelRef : eogeneratorModel.getModels()) {
			String modelPath = modelRef.getPath(null);
			File modelFile = new File(modelPath);
			EOModel model = modelGroup.loadModelFromURL(modelFile.toURL());
			models.add(model);
		}
		for (EOModelReference modelRef : eogeneratorModel.getRefModels()) {
			String modelPath = modelRef.getPath(null);
			File modelFile = new File(modelPath);
			modelGroup.loadModelFromURL(modelFile.toURL());
		}
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		modelGroup.resolve(failures);
		modelGroup.verify(failures);

		File destination = new File(eogeneratorModel.getDestination());
		if (!destination.exists()) {
			if (!destination.mkdirs()) {
				throw new IOException("Failed to create destination '" + destination + "'.");
			}
		}
		File subclassDestination = new File(eogeneratorModel.getSubclassDestination());
		if (!subclassDestination.exists()) {
			if (!subclassDestination.mkdirs()) {
				throw new IOException("Failed to create subclass destination '" + subclassDestination + "'.");
			}
		}

		context.put("eogeneratorModel", eogeneratorModel);
		for (Define define : eogeneratorModel.getDefines()) {
			context.put(define.getName(), define.getValue());
		}
		for (EOModel model : models) {
			System.out.println("Generating " + model.getName() + " ...");
			context.put("model", model);

			for (EOEntity entity : model.getEntities()) {
				System.out.println("Generating " + model.getName() + "." + entity.getName() + " ...");
				context.put("entity", entity);

				String classNameWithPackage = entity.getClassName();
				if (classNameWithPackage != null) {
					String prefixClassNameWithPackage = entity.getPrefixClassName();

					String subclassFilePath = classNameWithPackage.replace('.', '/') + ".java";
					File subclassFile = new File(subclassDestination, subclassFilePath);

					String superclassFilePath = prefixClassNameWithPackage.replace('.', '/') + ".java";
					File superclassFile = new File(destination, superclassFilePath);

					context.put("className", classNameWithPackage);
					context.put("prefixClassName", prefixClassNameWithPackage);
					context.put("packageName", entity.getPackageName());
					context.put("classNameWithoutPackage", entity.getClassNameWithoutPackage());
					context.put("prefixClassNameWithoutPackage", entity.getPrefixClassNameWithoutPackage());
					File superclassFolder = superclassFile.getParentFile();
					if (!superclassFolder.exists()) {
						if (!superclassFolder.mkdirs()) {
							throw new IOException("Unable to make superclass folder '" + superclassFolder + "'.");
						}
					}
					EOGenerator.writeTemplate(velocityEngine, context, "_Entity.java.vm", superclassFile);

					File subclassFolder = subclassFile.getParentFile();
					if (!subclassFolder.exists()) {
						if (!subclassFolder.mkdirs()) {
							throw new IOException("Unable to make subclass folder '" + superclassFolder + "'.");
						}
					}
					if (!subclassFile.exists()) {
						EOGenerator.writeTemplate(velocityEngine, context, "Entity.java.vm", subclassFile);
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		EOGeneratorModel eogeneratorModel = new EOGeneratorModel();
		eogeneratorModel.addModel(new EOModelReference(new Path("/Users/mschrag/Documents/workspace/MDTask/MDTask.eomodeld")));
		eogeneratorModel.addModel(new EOModelReference(new Path("/Users/mschrag/Documents/workspace/MDTAccounting/MDTAccounting.eomodeld")));
		eogeneratorModel.addRefModel(new EOModelReference(new Path("/Users/mschrag/Documents/workspace/ERPrototypes/Resources/erprototypes.eomodeld")));
		eogeneratorModel.setPrefix("_");
		eogeneratorModel.setDestination("/tmp/src");
		eogeneratorModel.setSubclassDestination("/tmp/src");
		eogeneratorModel.setDefine("EOGenericRecord", "er.extensions.ERXGenericRecord");

		EOGenerator.generate(eogeneratorModel);
	}

	public static void writeTemplate(VelocityEngine engine, VelocityContext context, String templateName, File outputFile) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		Template template = engine.getTemplate(templateName);
		if (!outputFile.getParentFile().exists()) {
			if (!outputFile.getParentFile().mkdirs()) {
				throw new IOException("Unable to create the folder " + outputFile.getParentFile() + ".");
			}
		}
		FileWriter outputWriter = new FileWriter(outputFile);
		try {
			template.merge(context, outputWriter);
		} finally {
			outputWriter.close();
		}
	}
}
