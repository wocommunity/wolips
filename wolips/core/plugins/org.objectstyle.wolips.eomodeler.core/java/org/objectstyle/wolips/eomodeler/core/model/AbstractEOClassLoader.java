package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.Bundle;

public abstract class AbstractEOClassLoader implements IEOClassLoaderFactory {
	private static Map<String, Reference<ClassLoader>> CLASSLOADER_CACHE;

	static {
		AbstractEOClassLoader.CLASSLOADER_CACHE = new HashMap<String, Reference<ClassLoader>>();
	}

	public ClassLoader createClassLoaderForModel(EOModel model) throws EOModelException {
		try {
			// System.out.println("AbstractEOClassLoader.createClassLoaderForModel: "
			// + model.getName() + " ...");
			Set<URL> classpathSet = new LinkedHashSet<URL>();
			fillInModelClasspath(model, classpathSet);
			fillInDevelopmentClasspath(classpathSet);
			Bundle bundle = InternalPlatform.getDefault().getBundle("org.objectstyle.wolips.eomodeler.core");
			URL wosqlJarUrl = bundle.getEntry("/lib/EOFSQLUtils.jar");
			if (wosqlJarUrl != null) {
				classpathSet.add(wosqlJarUrl);
			}
			URL wo53sqlJarUrl = bundle.getEntry("/lib/EOFSQLUtils53.jar");
			if (wo53sqlJarUrl != null) {
				classpathSet.add(wo53sqlJarUrl);
			}
			URL wo56sqlJarUrl = bundle.getEntry("/lib/EOFSQLUtils56.jar");
			if (wo56sqlJarUrl != null) {
				classpathSet.add(wo56sqlJarUrl);
			}
			StringBuffer webobjectsClasspath = new StringBuffer();
			Iterator<URL> classpathIter = classpathSet.iterator();
			while (classpathIter.hasNext()) {
				URL classpathUrl = classpathIter.next();
				webobjectsClasspath.append(File.pathSeparator);
				webobjectsClasspath.append(classpathUrl.getPath());
			}
			System.setProperty("com.webobjects.classpath", webobjectsClasspath.toString());
			System.setProperty("NSProjectBundleEnabled", "true");
			ClassLoader eomodelClassLoader = createEOModelClassLoader(model, classpathSet);
			return eomodelClassLoader;
		} catch (Exception e) {
			throw new EOModelException("Failed to create EOF class loader.", e);
		}
	}

	protected synchronized ClassLoader createEOModelClassLoader(EOModel model, Set<URL> classpathUrlSet) {
		URLClassLoader classLoader = null;
		String cacheKey = getCacheKey(model, classpathUrlSet);
		Reference classLoaderReference = AbstractEOClassLoader.CLASSLOADER_CACHE.get(cacheKey);
		if (classLoaderReference != null) {
			classLoader = (URLClassLoader) classLoaderReference.get();
			if (classLoader != null) {
				LinkedHashSet<URL> previousURLSet = new LinkedHashSet<URL>();
				for (URL previousURL : classLoader.getURLs()) {
					previousURLSet.add(previousURL);
				}
				if (!previousURLSet.equals(classpathUrlSet)) {
					classLoader = null;
				}
			}
		}
		if (classLoader == null) {
			if (classpathUrlSet.size() == 1) {
				classLoader = null;
			} else {
				URL[] classpathUrls = classpathUrlSet.toArray(new URL[classpathUrlSet.size()]);
				classLoader = URLClassLoader.newInstance(classpathUrls);
				AbstractEOClassLoader.CLASSLOADER_CACHE.put(cacheKey, new SoftReference<ClassLoader>(classLoader));
			}
		}
		return classLoader;
	}

	protected String getCacheKey(EOModel model, Set<URL> classpathUrlSet) {
		return model.getName() + "_" + String.valueOf(classpathUrlSet.hashCode());
	}

	protected abstract void fillInModelClasspath(EOModel model, Set<URL> classpathUrls) throws Exception;

	protected abstract void fillInDevelopmentClasspath(Set<URL> classpathUrls) throws Exception;
}
