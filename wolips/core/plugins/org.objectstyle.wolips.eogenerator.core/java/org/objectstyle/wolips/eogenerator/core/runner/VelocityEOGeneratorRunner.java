package org.objectstyle.wolips.eogenerator.core.runner;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import org.objectstyle.wolips.eogenerator.core.Activator;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.model.IEOGeneratorRunner;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel.Define;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.thirdparty.velocity.resourceloader.ResourceLoader;

public class VelocityEOGeneratorRunner implements IEOGeneratorRunner {
	public static class ConsoleLogger implements LogSystem {
		public void init(RuntimeServices runtimeservices) throws Exception {
			// DO NOTHING
		}

		public void logVelocityMessage(int i, String s) {
			System.out.println("ConsoleLogger.logVelocityMessage: " + i + ", " + s);
		}
	}

	public void generate(EOGeneratorModel eogeneratorModel, StringBuffer results) throws Exception {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, org.apache.velocity.runtime.log.NullLogSystem.class.getName());
		// velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
		// ConsoleLogger.class.getName());
		StringBuffer templatePaths = new StringBuffer();
		templatePaths.append(".");
		String templatePath = eogeneratorModel.getTemplateDir();
		if (templatePath != null) {
			templatePaths.append(",");
			templatePaths.append(new File(templatePath).getAbsolutePath());
		}
		velocityEngine.setProperty("resource.loader", "file,wolips");
		velocityEngine.setProperty("file.resource.loader", templatePaths.toString());
		velocityEngine.setProperty("wolips.resource.loader.class", ResourceLoader.class.getName());
		velocityEngine.setProperty("wolips.resource.loader.bundle", Activator.getDefault().getBundle());
		// velocityEngine.setProperty("class.resource.loader.class",
		// EOGeneratorResourceLoader.class.getName());

		velocityEngine.init();
		VelocityContext context = new VelocityContext();

		List<EOModel> models = new LinkedList<EOModel>();
		EOModelGroup modelGroup = new EOModelGroup();
		modelGroup.setPrefix(eogeneratorModel.getPrefix());
		modelGroup.setEOGenericRecordClassName(eogeneratorModel.getDefineValueNamed("EOGenericRecord"));
		for (EOModelReference modelRef : eogeneratorModel.getModels()) {
			String modelPath = modelRef.getPath(null);
			File modelFile = new File(modelPath);
			if (!modelFile.isAbsolute()) {
				modelFile = new File(eogeneratorModel.getProject().getLocation().toFile(), modelPath);
			}
			EOModel model = modelGroup.loadModelFromURL(modelFile.toURL());
			models.add(model);
		}
		for (EOModelReference modelRef : eogeneratorModel.getRefModels()) {
			String modelPath = modelRef.getPath(null);
			File modelFile = new File(modelPath);
			if (!modelFile.isAbsolute()) {
				modelFile = new File(eogeneratorModel.getProject().getLocation().toFile(), modelPath);
			}
			modelGroup.loadModelFromURL(modelFile.toURL());
		}
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		modelGroup.resolve(failures);
		modelGroup.verify(failures);

		File destination = new File(eogeneratorModel.getDestination());
		if (!destination.isAbsolute()) {
			destination = new File(eogeneratorModel.getProject().getLocation().toFile(), eogeneratorModel.getDestination());
		}
		if (!destination.exists()) {
			if (!destination.mkdirs()) {
				throw new IOException("Failed to create destination '" + destination + "'.");
			}
		}
		File subclassDestination = new File(eogeneratorModel.getSubclassDestination());
		if (!subclassDestination.isAbsolute()) {
			subclassDestination = new File(eogeneratorModel.getProject().getLocation().toFile(), eogeneratorModel.getSubclassDestination());
		}
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
					String superclassTemplateName = eogeneratorModel.getJavaTemplate("_Entity.java");
					VelocityEOGeneratorRunner.writeTemplate(velocityEngine, context, superclassTemplateName, superclassFile);

					File subclassFolder = subclassFile.getParentFile();
					if (!subclassFolder.exists()) {
						if (!subclassFolder.mkdirs()) {
							throw new IOException("Unable to make subclass folder '" + superclassFolder + "'.");
						}
					}
					if (!subclassFile.exists()) {
						String subclassTemplateName = eogeneratorModel.getSubclassJavaTemplate("Entity.java");
						VelocityEOGeneratorRunner.writeTemplate(velocityEngine, context, subclassTemplateName, subclassFile);
					}
				}
			}
		}
	}

	public static void writeTemplate(VelocityEngine engine, VelocityContext context, String templateName, File outputFile) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		System.out.println("VelocityEOGeneratorRunner.writeTemplate: " + outputFile.getCanonicalFile());
		Template template = engine.getTemplate(templateName);
		if (!outputFile.getParentFile().exists()) {
			if (!outputFile.getParentFile().mkdirs()) {
				throw new IOException("Unable to create the folder " + outputFile.getParentFile() + ".");
			}
		}

		if (!outputFile.exists()) {
			System.out.println("EOGenerator.writeTemplate: writing the first time ...");
			FileWriter newFileWriter = new FileWriter(outputFile);
			BufferedWriter newFileBufferedWriter = new BufferedWriter(newFileWriter);
			try {
				template.merge(context, newFileBufferedWriter);
			} finally {
				newFileBufferedWriter.close();
			}
		} else {
			ByteArrayOutputStream newFileContentsStream = new ByteArrayOutputStream();
			Writer newFileContentsWriter = new OutputStreamWriter(newFileContentsStream);
			try {
				template.merge(context, newFileContentsWriter);
			} finally {
				newFileContentsWriter.close();
			}
			byte[] newFileContents = newFileContentsStream.toByteArray();
//			for (int i = 0; i < newFileContents.length; i++) {
//				System.out.println("EOGenerator.writeTemplate: " + i + ": " + (char) newFileContents[i]);
//			}

			/*
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] newFileDigest = md5.digest(newFileContents);

			md5.reset();
			FileInputStream existingFileStream = new FileInputStream(outputFile);
			try {
				System.out.println("EOGenerator.writeTemplate: existing = " + outputFile.length());
				byte[] buffer = new byte[2048];
				while (existingFileStream.available() > 0) {
					int bytesRead = existingFileStream.read(buffer);
					md5.digest(buffer, 0, bytesRead);
					//System.out.println("EOGenerator.writeTemplate: " + bytesRead);
				}
			} finally {
				existingFileStream.close();
			}
			byte[] existingFileDigest = md5.digest();

			boolean templateChanged = false;
			for (int i = 0; !templateChanged && i < existingFileDigest.length; i++) {
				System.out.println("EOGenerator.writeTemplate: " + i + "=" + newFileDigest[i] + "," + existingFileDigest[i]);
				templateChanged = (newFileDigest[i] != existingFileDigest[i]);
			}
			*/
			boolean templateChanged = true;
			if (templateChanged) {
				FileOutputStream newFileStream = new FileOutputStream(outputFile);
				BufferedOutputStream newFileBufferedStream = new BufferedOutputStream(newFileStream);
				try {
					newFileBufferedStream.write(newFileContents);
				} finally {
					newFileBufferedStream.close();
				}
			} else {
				System.out.println("EOGenerator.writeTemplate: skipping " + outputFile);
			}
		}
	}
}
