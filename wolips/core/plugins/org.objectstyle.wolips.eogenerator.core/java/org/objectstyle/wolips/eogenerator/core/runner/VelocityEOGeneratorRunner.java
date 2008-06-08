package org.objectstyle.wolips.eogenerator.core.runner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.apache.velocity.tools.generic.ListTool;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.eogenerator.core.Activator;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel;
import org.objectstyle.wolips.eogenerator.core.model.EOModelReference;
import org.objectstyle.wolips.eogenerator.core.model.IEOGeneratorRunner;
import org.objectstyle.wolips.eogenerator.core.model.EOGeneratorModel.Define;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelRenderContext;
import org.objectstyle.wolips.eomodeler.core.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.core.model.IEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.core.utils.BooleanUtils;
import org.objectstyle.wolips.thirdparty.velocity.WOLipsVelocityUtils;
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

	private boolean _insideEclipse;

	public VelocityEOGeneratorRunner() {
		this(true);
	}

	public VelocityEOGeneratorRunner(boolean insideEclipse) {
		_insideEclipse = insideEclipse;
	}

	public boolean generate(EOGeneratorModel eogeneratorModel, StringBuffer results) throws Exception {
		return generate(eogeneratorModel, results, null);
	}

	public boolean generate(EOGeneratorModel eogeneratorModel, StringBuffer results, EOModelGroup preloadedModelGroup) throws Exception {
		boolean showResults = false;

		String superclassTemplateName = eogeneratorModel.getJavaTemplate();
		String subclassTemplateName = eogeneratorModel.getSubclassJavaTemplate();

		boolean eogeneratorJava14 = eogeneratorModel.isJava14();
		if (eogeneratorJava14) {
			if (superclassTemplateName == null || superclassTemplateName.length() == 0) {
				superclassTemplateName = "_Entity14.java";
			}
			if (subclassTemplateName == null || subclassTemplateName.length() == 0) {
				subclassTemplateName = "Entity14.java";
			}
		} else {
			if (superclassTemplateName == null || superclassTemplateName.length() == 0) {
				superclassTemplateName = "_Entity.java";
			}
			if (subclassTemplateName == null || subclassTemplateName.length() == 0) {
				subclassTemplateName = "Entity.java";
			}
		}

		VelocityEngine velocityEngine = WOLipsVelocityUtils.createVelocityEngine("EOGenerator", Activator.getDefault().getBundle(), eogeneratorModel.getTemplateDir(), eogeneratorModel.getProjectPath(), _insideEclipse, ResourceLoader.class);
		VelocityContext context = new VelocityContext();

		List<EOModel> models = new LinkedList<EOModel>();
		EOModelRenderContext renderContext = new EOModelRenderContext();
		try {
			String prefix = eogeneratorModel.getPrefix();
			if (prefix != null) {
				renderContext.setPrefix(prefix);
			}
			renderContext.setSuperclassPackage(eogeneratorModel.getSuperclassPackage());
			String eogenericRecordClassName = eogeneratorModel.getDefineValueNamed("EOGenericRecord");
			if (eogenericRecordClassName != null) {
				renderContext.setEOGenericRecordClassName(eogenericRecordClassName);
			}
			renderContext.setJavaClientCommon(eogeneratorModel.isJavaClientCommon() != null && eogeneratorModel.isJavaClientCommon().booleanValue());
			renderContext.setJavaClient(eogeneratorModel.isJavaClient() != null && eogeneratorModel.isJavaClient().booleanValue());
			EOModelRenderContext.setRenderContext(renderContext);

			EOModelGroup modelGroup;
			if (preloadedModelGroup == null) {
				modelGroup = new EOModelGroup();
				
				Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();

				if (BooleanUtils.isTrue(eogeneratorModel.isLoadModelGroup())) {
					for (EOModelReference eomodelReference : eogeneratorModel.getModels()) {
						EOModelGroup generatingModelGroup = new EOModelGroup(); 
						URL modelURL = getModelURL(eogeneratorModel, eomodelReference);
						IEOModelGroupFactory.Utility.loadModelGroup(modelURL, generatingModelGroup, failures, true, modelURL, new NullProgressMonitor());
						EOModel generatingModel = generatingModelGroup.getEditingModel();
						models.add(generatingModel);
						
						for (EOModel model : generatingModelGroup.getModels()) {
							if (!modelGroup.containsModelNamed(model.getName())) {
								modelGroup.addModel(model);
							}
						}
					}
				}
				else {
					loadModels(eogeneratorModel, modelGroup, eogeneratorModel.getModels(), models);
					loadModels(eogeneratorModel, modelGroup, eogeneratorModel.getRefModels(), new LinkedList<EOModel>());

					modelGroup.resolve(failures);
					modelGroup.verify(failures);
				}

				for (EOModelVerificationFailure failure : failures) {
					if (!failure.isWarning()) {
						results.append("Error: " + failure.getMessage() + "\n");
						showResults = true;
					}
				}
			} else {
				modelGroup = preloadedModelGroup;
				for (EOModelReference modelRef : eogeneratorModel.getModels()) {
					String modelName = modelRef.getName();
					EOModel model = modelGroup.getModelNamed(modelName);
					if (model == null) {
						throw new RuntimeException("There was no model named '" + modelName + "' in this model group.");
					}
					models.add(model);
				}
			}

			File superclassDestination = new File(eogeneratorModel.getDestination());
			if (!superclassDestination.isAbsolute()) {
				IPath projectPath = eogeneratorModel.getProjectPath();
				if (projectPath != null) {
					superclassDestination = new File(projectPath.toFile(), eogeneratorModel.getDestination());
				}
			}
			if (!superclassDestination.exists()) {
				if (!superclassDestination.mkdirs()) {
					throw new IOException("Failed to create destination '" + superclassDestination + "'.");
				}
			}

			File subclassDestination = new File(eogeneratorModel.getSubclassDestination());
			if (!subclassDestination.isAbsolute()) {
				IPath projectPath = eogeneratorModel.getProjectPath();
				if (projectPath != null) {
					subclassDestination = new File(projectPath.toFile(), eogeneratorModel.getSubclassDestination());
				}
			}
			if (!subclassDestination.exists()) {
				if (!subclassDestination.mkdirs()) {
					throw new IOException("Failed to create subclass destination '" + subclassDestination + "'.");
				}
			}

			// String filePathTemplate = eogeneratorModel.getFilenameTemplate();
			// if (filePathTemplate == null || filePathTemplate.trim().length()
			// == 0) {
			// }

			context.put("eogeneratorModel", eogeneratorModel);
			context.put("date", new Date());
			context.put("calendar", Calendar.getInstance());
			for (Define define : eogeneratorModel.getDefines()) {
				context.put(define.getName(), define.getValue());
			}
			context.put("list", new ListTool());
			String extension = eogeneratorModel.getExtension();
			for (EOModel model : models) {
				// System.out.println("Generating " + model.getName() + " ...");
				context.put("model", model);

				for (EOEntity entity : model.getEntities()) {
					// System.out.println("Generating " + model.getName() + "."
					// + entity.getName() + " ...");
					context.put("entity", entity);

					String classNameWithPackage = renderContext.getClassNameForEntity(entity);
					boolean eogenericRecord = classNameWithPackage == null || classNameWithPackage.endsWith("GenericRecord");
					if (entity.isGenerateSource() && !eogenericRecord) {
						String prefixClassNameWithPackage = entity.getPrefixClassName();
						context.put("className", classNameWithPackage);
						context.put("prefixClassName", prefixClassNameWithPackage);
						context.put("packageName", entity.getPackageName());
						context.put("classNameWithoutPackage", entity.getClassNameWithoutPackage());
						context.put("prefixClassNameWithoutPackage", entity.getPrefixClassNameWithoutPackage());

						String superclassFileTemplate;
						// StringWriter superclassFilePathWriter = new
						// StringWriter();
						// velocityEngine.evaluate(context,
						// superclassFilePathWriter, "LOG",
						// superclassFileTemplate);
						if (BooleanUtils.isFalse(eogeneratorModel.isPackageDirs())) {
							superclassFileTemplate = entity.getPrefixClassNameWithoutPackage();
						}
						else {
							superclassFileTemplate = prefixClassNameWithPackage.toString().replace('.', '/');
						}

						String superclassFilePath = superclassFileTemplate + "." + extension;
						File superclassFile = new File(superclassDestination, superclassFilePath);
						File superclassFolder = superclassFile.getParentFile();
						if (!superclassFolder.exists()) {
							if (!superclassFolder.mkdirs()) {
								throw new IOException("Unable to make superclass folder '" + superclassFolder + "'.");
							}
						}
						WOLipsVelocityUtils.writeTemplate(velocityEngine, context, superclassTemplateName, superclassFile);

						String subclassFileTemplate;
						// StringWriter subclassFilePathWriter = new
						// StringWriter();
						// velocityEngine.evaluate(context,
						// subclassFilePathWriter, "LOG", subclassFileTemplate);
						if (BooleanUtils.isFalse(eogeneratorModel.isPackageDirs())) {
							subclassFileTemplate = entity.getClassNameWithoutPackage();
						}
						else {
							subclassFileTemplate = classNameWithPackage.toString().replace('.', '/');
						}

						String subclassFilePath = subclassFileTemplate + "." + extension;
						File subclassFile = new File(subclassDestination, subclassFilePath);
						File subclassFolder = subclassFile.getParentFile();
						if (!subclassFolder.exists()) {
							if (!subclassFolder.mkdirs()) {
								throw new IOException("Unable to make subclass folder '" + superclassFolder + "'.");
							}
						}
						if (!subclassFile.exists()) {
							WOLipsVelocityUtils.writeTemplate(velocityEngine, context, subclassTemplateName, subclassFile);
						}
					}
				}
			}
		} finally {
			EOModelRenderContext.clearRenderContext();
		}
		return showResults;
	}

	public URL getModelURL(EOGeneratorModel eogeneratorModel, EOModelReference modelRef) throws MalformedURLException {
		String modelPath = modelRef.getPath((IPath) null);
		File modelFile = new File(modelPath);
		if (!modelFile.isAbsolute()) {
			IPath projectPath = eogeneratorModel.getProjectPath();
			if (projectPath != null) {
				modelFile = new File(projectPath.toFile(), modelPath);
			}
		}
		return modelFile.toURL();
	}

	public void loadModels(EOGeneratorModel eogeneratorModel, EOModelGroup modelGroup, List<EOModelReference> modelReferences, List<EOModel> loadedModels) throws MalformedURLException, IOException, EOModelException {
		for (EOModelReference modelRef : modelReferences) {
			URL modelURL = getModelURL(eogeneratorModel, modelRef);
			EOModel model = modelGroup.loadModelFromURL(modelURL);
			loadedModels.add(model);
		}
	}
}
