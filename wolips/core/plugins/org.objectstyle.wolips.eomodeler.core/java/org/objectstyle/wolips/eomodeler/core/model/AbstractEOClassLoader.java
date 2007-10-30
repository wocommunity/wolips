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
	private static Map<Set<URL>, Reference<ClassLoader>> CLASSLOADER_CACHE;

	static {
		AbstractEOClassLoader.CLASSLOADER_CACHE = new HashMap<Set<URL>, Reference<ClassLoader>>();
	}

	public ClassLoader createClassLoaderForModel(EOModel model) throws EOModelException {
		try {
			Set<URL> classpathSet = new LinkedHashSet<URL>();
			fillInModelClasspath(model, classpathSet);
			fillInDevelopmentClasspath(classpathSet);
			Bundle bundle = InternalPlatform.getDefault().getBundle("org.objectstyle.wolips.eomodeler.core");
			URL sqlJarUrl = bundle.getEntry("/lib/EntityModelerSQL.jar");
			if (sqlJarUrl != null) {
				classpathSet.add(sqlJarUrl);
			}
			StringBuffer webobjectsClasspath = new StringBuffer();
			Iterator classpathIter = classpathSet.iterator();
			while (classpathIter.hasNext()) {
				URL classpathUrl = (URL) classpathIter.next();
				webobjectsClasspath.append(File.pathSeparator);
				webobjectsClasspath.append(classpathUrl.getPath());
				// System.out.println("ClasspathUtils.createEOModelClassLoader:
				// " +
				// classpathUrl);
			}
			System.setProperty("com.webobjects.classpath", webobjectsClasspath.toString());
			ClassLoader eomodelClassLoader = createEOModelClassLoader(classpathSet);
			return eomodelClassLoader;
		} catch (Exception e) {
			throw new EOModelException("Failed to create EOF class loader.", e);
		}
	}

	protected synchronized ClassLoader createEOModelClassLoader(Set<URL> classpathUrlSet) {
		ClassLoader classLoader = null;
		Reference classLoaderReference = AbstractEOClassLoader.CLASSLOADER_CACHE.get(classpathUrlSet);
		if (classLoaderReference != null) {
			classLoader = (ClassLoader) classLoaderReference.get();
		}
		if (classLoader == null) {
			if (classpathUrlSet.size() == 1) {
				classLoader = null;
			}
			else {
				URL[] classpathUrls = classpathUrlSet.toArray(new URL[classpathUrlSet.size()]);
				//classLoader = URLClassLoader.newInstance(classpathUrls, AbstractEOClassLoader.class.getClassLoader());
				classLoader = URLClassLoader.newInstance(classpathUrls);
				AbstractEOClassLoader.CLASSLOADER_CACHE.put(classpathUrlSet, new SoftReference<ClassLoader>(classLoader));
			}
		}
		return classLoader;
	}

	protected abstract void fillInModelClasspath(EOModel model, Set<URL> classpathUrls) throws Exception;

	protected abstract void fillInDevelopmentClasspath(Set<URL> classpathUrls) throws Exception;
}
