package org.objectstyle.wolips.thirdparty.velocity;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.MessageDigest;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.eclipse.core.runtime.IPath;
import org.objectstyle.wolips.thirdparty.velocity.resourceloader.ResourceLoader;
import org.osgi.framework.Bundle;

public class WOLipsVelocityUtils {

	/**
	 * Creates a configured VelocityEngine.
	 * 
	 * @param templateFamilyName
	 *            the name of the template family (the common folder name that
	 *            contains the templates)
	 * @param templateBundle
	 *            the bundle of the calling plugin
	 * @param customTemplatePath
	 *            the custom template path to load with
	 * @param projectPath
	 *            the project path to prepend if the customTemplatePath is
	 *            relative
	 * @param insideEclipse
	 *            if true, this engine should load resources from eclipse
	 *            bundles
	 * @param resourceLoaderClass
	 *            the custom resource loader if not running inside eclipse
	 * @return an initialized VelocityEngine
	 * @throws Exception
	 *             if velocity engine creation fails
	 */
	public static VelocityEngine createVelocityEngine(String templateFamilyName, Bundle templateBundle, String customTemplatePath, IPath projectPath, boolean insideEclipse, Class resourceLoaderClass) throws Exception {
		StringBuffer templatePaths = new StringBuffer();
		templatePaths.append(".");
		if (customTemplatePath != null && customTemplatePath.length() > 0) {
			templatePaths.append(",");
			File templateFolder = new File(customTemplatePath);
			if (!templateFolder.isAbsolute() && projectPath != null) {
				templateFolder = new File(projectPath.toFile(), customTemplatePath);
			}
			templatePaths.append(templateFolder.getAbsolutePath());
		}

		templatePaths.append(",");
		templatePaths.append(new File("/Library/Application Support/WOLips/" + templateFamilyName).getAbsolutePath());
		templatePaths.append(",");
		templatePaths.append(new File(System.getProperty("user.home"), "Documents and Settings/Application Data/WOLips/" + templateFamilyName).getAbsolutePath());
		templatePaths.append(",");
		templatePaths.append(new File(System.getProperty("user.home"), "Documents and Settings/AppData/Local/WOLips/" + templateFamilyName).getAbsolutePath());
		templatePaths.append(",");
		templatePaths.append(new File(System.getProperty("user.home"), "Library/Application Support/WOLips/" + templateFamilyName).getAbsolutePath());

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, org.apache.velocity.runtime.log.NullLogSystem.class.getName());
		velocityEngine.setProperty("resource.loader", "file,wolips");
		velocityEngine.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
		velocityEngine.setProperty("file.resource.loader.path", templatePaths.toString());
		if (insideEclipse) {
			velocityEngine.setProperty("wolips.resource.loader.class", ResourceLoader.class.getName());
			if (templateBundle != null) {
				velocityEngine.setProperty("wolips.resource.loader.bundle", templateBundle);
			}
		} else if (resourceLoaderClass != null) {
			velocityEngine.setProperty("wolips.resource.loader.class", resourceLoaderClass.getName());
		}
		velocityEngine.init();

		return velocityEngine;
	}

	public static String writeTemplateToString(VelocityEngine engine, VelocityContext context, String templateName, ByteArrayOutputStream newFileContentsStream) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		Template template;
		try {
			template = engine.getTemplate(templateName);
		} catch (ResourceNotFoundException e) {
			throw new Exception("Failed to load the template '" + templateName + "'.  Check your model's eogen file to make sure that it specifies the correct template folder and template names.");
		}

		Writer newFileContentsWriter = new OutputStreamWriter(newFileContentsStream);
		try {
			template.merge(context, newFileContentsWriter);
		} finally {
			newFileContentsWriter.close();
		}
		String newFileContentsStr = newFileContentsStream.toString();
		if (newFileContentsStr != null) {
			if (newFileContentsStr.contains("<%")) {
				throw new IOException("You are attempting to use an old EOGenerator template with Velocity EOGenerator.");
			} else if (newFileContentsStr.contains("<wo:")) {
				throw new IOException("You are attempting to use a JavaEOGenerator template with Velocity EOGenerator.");
			}
		}
		return newFileContentsStr;
	}

	public static String writeTemplateToString(VelocityEngine engine, VelocityContext context, String templateName) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		return writeTemplateToString(engine, context, templateName, new ByteArrayOutputStream());
	}

	public static void writeTemplate(VelocityEngine engine, VelocityContext context, String templateName, File outputFile) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		ByteArrayOutputStream newFileContentsStream = new ByteArrayOutputStream();
		String newFileContentsStr = WOLipsVelocityUtils.writeTemplateToString(engine, context, templateName, newFileContentsStream);
		if (!outputFile.getParentFile().exists()) {
			if (!outputFile.getParentFile().mkdirs()) {
				throw new IOException("Unable to create the folder " + outputFile.getParentFile() + ".");
			}
		}
		boolean templateChanged = true;
		if (!outputFile.exists()) {
			FileWriter newFileWriter = new FileWriter(outputFile);
			BufferedWriter newFileBufferedWriter = new BufferedWriter(newFileWriter);
			try {
				newFileBufferedWriter.write(newFileContentsStr);
			} finally {
				newFileBufferedWriter.close();
			}
		} else {
			byte[] newFileContents = newFileContentsStream.toByteArray();
			if (outputFile.exists()) {
				FileInputStream fis = new FileInputStream(outputFile);
				int bytesRemaining = (int) outputFile.length();
				if (bytesRemaining == newFileContents.length) {
					byte[] oldFileContents;
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream((int) outputFile.length());
						byte[] buf = new byte[4096];
						while (bytesRemaining > 0) {
							int bytesRead = fis.read(buf, 0, Math.min(buf.length, bytesRemaining));
							bos.write(buf, 0, bytesRead);
							bytesRemaining -= bytesRead;
						}
						oldFileContents = bos.toByteArray();
					} finally {
						fis.close();
					}

					MessageDigest md5 = MessageDigest.getInstance("MD5");
					byte[] oldMD5 = md5.digest(oldFileContents);
					md5.reset();
					byte[] newMD5 = md5.digest(newFileContents);
					md5.reset();

					if (oldMD5.length == newMD5.length) {
						templateChanged = false;
						for (int i = 0; !templateChanged && i < oldMD5.length; i++) {
							if (oldMD5[i] != newMD5[i]) {
								templateChanged = true;
							}
						}
					}
				}
			}

			if (templateChanged) {
				FileOutputStream newFileStream = new FileOutputStream(outputFile);
				BufferedOutputStream newFileBufferedStream = new BufferedOutputStream(newFileStream);
				try {
					newFileBufferedStream.write(newFileContents);
				} finally {
					newFileBufferedStream.close();
				}
			} else {
				// System.out.println("EOGenerator.writeTemplate: skipping " +
				// outputFile);
			}
		}
	}
}
