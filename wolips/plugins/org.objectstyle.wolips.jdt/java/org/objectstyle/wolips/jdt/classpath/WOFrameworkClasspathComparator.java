package org.objectstyle.wolips.jdt.classpath;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

public class WOFrameworkClasspathComparator implements Comparator<IClasspathEntry> {
	private static Set<String> _wonderFrameworks;

	private static Set<String> _systemFrameworks;

	private IClasspathEntry[] _entries;

	static {
		_wonderFrameworks = new HashSet<String>();
		_wonderFrameworks.add("Ajax");
		_wonderFrameworks.add("AjaxLook");
		_wonderFrameworks.add("BTBusinessLogic");
		_wonderFrameworks.add("DRGrouping");
		_wonderFrameworks.add("DerbyPlugIn");
		_wonderFrameworks.add("ERAttachment");
		_wonderFrameworks.add("ERCaching");
		_wonderFrameworks.add("ERCalendar");
		_wonderFrameworks.add("ERCaptcha");
		_wonderFrameworks.add("ERChangeNotificationJMS");
		_wonderFrameworks.add("ERChronic");
		_wonderFrameworks.add("ERCoreBusinessLogic");
		_wonderFrameworks.add("ERDirectToWeb");
		_wonderFrameworks.add("ERExcelLook");
		_wonderFrameworks.add("ERExtensions");
		_wonderFrameworks.add("ERIMAdaptor");
		_wonderFrameworks.add("ERIndexing");
		_wonderFrameworks.add("ERJGroupsSynchronizer");
		_wonderFrameworks.add("ERJars");
		_wonderFrameworks.add("ERJavaMail");
		_wonderFrameworks.add("ERMiniUglyLook");
		_wonderFrameworks.add("ERNeutralLook");
		_wonderFrameworks.add("EROpenID");
		_wonderFrameworks.add("ERPlot");
		_wonderFrameworks.add("ERPrototypes");
		_wonderFrameworks.add("ERRest");
		_wonderFrameworks.add("ERSelenium");
		_wonderFrameworks.add("ERTaggable");
		_wonderFrameworks.add("ERWorkerChannel");
		_wonderFrameworks.add("ExcelGenerator");
		_wonderFrameworks.add("FrontBasePlugIn");
		_wonderFrameworks.add("JChronic");
		_wonderFrameworks.add("JavaMemoryAdaptor");
		_wonderFrameworks.add("JavaMonitorFramework");
		_wonderFrameworks.add("JavaMonitorSupport");
		_wonderFrameworks.add("JavaRESTAdaptor");
		_wonderFrameworks.add("JavaWOExtensions");
		_wonderFrameworks.add("PostgresqlPlugIn");
		_wonderFrameworks.add("SVGObjects");
		_wonderFrameworks.add("Validity");
		_wonderFrameworks.add("WOLips");
		_wonderFrameworks.add("WOOgnl");
		_wonderFrameworks.add("WOPayPal");
		_wonderFrameworks.add("WRReporting");
		_wonderFrameworks.add("YUI");

		_systemFrameworks = new HashSet<String>();
		_systemFrameworks.add("JavaDTWGeneration");
		_systemFrameworks.add("JavaDirectToWeb");
		_systemFrameworks.add("JavaEOAccess");
		_systemFrameworks.add("JavaEOApplication");
		_systemFrameworks.add("JavaEOCocoa");
		_systemFrameworks.add("JavaEOControl");
		_systemFrameworks.add("JavaEODistribution");
		_systemFrameworks.add("JavaEOGeneration");
		_systemFrameworks.add("JavaEOInterface");
		_systemFrameworks.add("JavaEOInterfaceCocoa");
		_systemFrameworks.add("JavaEOInterfaceSwing");
		_systemFrameworks.add("JavaEOProject");
		_systemFrameworks.add("JavaEORuleSystem");
		_systemFrameworks.add("JavaEOTool");
		_systemFrameworks.add("JavaFoundation");
		_systemFrameworks.add("JavaJDBCAdaptor");
		_systemFrameworks.add("JavaJNDIAdaptor");
		_systemFrameworks.add("JavaWOJSPServlet");
		_systemFrameworks.add("JavaWebObjects");
		_systemFrameworks.add("JavaWebServicesClient");
		_systemFrameworks.add("JavaWebServicesGeneration");
		_systemFrameworks.add("JavaWebServicesSupport");
		_systemFrameworks.add("JavaXML");
	}

	public WOFrameworkClasspathComparator(IClasspathEntry[] entries) {
		_entries = entries;
	}

	protected int originalPositionOf(IClasspathEntry entry) {
		int originalPosition = -1;
		for (originalPosition = 0; originalPosition < _entries.length; originalPosition++) {
			if (_entries[originalPosition] == entry) {
				break;
			}
		}
		return originalPosition;
	}

	protected int valueForFrameworkNamed(String frameworkName, IClasspathEntry entry) {
		int value;
		if (_systemFrameworks.contains(frameworkName)) {
			value = 2000;
		}
		else if (_wonderFrameworks.contains(frameworkName)) {
			value = 1000;
		}
		else {
			value = originalPositionOf(entry);
		}
		return value;
	}

	public int compare(IClasspathEntry o1, IClasspathEntry o2) {
		IPath p1 = o1.getPath();
		String type1 = p1.segment(0);
		IPath p2 = o2.getPath();
		String type2 = p2.segment(0);
		int value1;
		int value2;
		if ("org.eclipse.jdt.launching.JRE_CONTAINER".equals(type1)) {
			value1 = 1;
			value2 = -1;
		} else if ("org.eclipse.jdt.launching.JRE_CONTAINER".equals(type2)) {
			value1 = -1;
			value2 = 1;
		} else if (WOFrameworkClasspathContainer.ID.equals(type1) && WOFrameworkClasspathContainer.ID.equals(type2)) {
			String framework1 = p1.segment(1);
			String framework2 = p2.segment(1);
			value1 = valueForFrameworkNamed(framework1, o1);
			value2 = valueForFrameworkNamed(framework2, o2);
		} else if (WOFrameworkClasspathContainer.ID.equals(type1)) {
			value1 = 1;
			value2 = -1;
		} else if (WOFrameworkClasspathContainer.ID.equals(type2)) {
			value1 = -1;
			value2 = 1;
		} else {
			value1 = originalPositionOf(o1);
			value2 = originalPositionOf(o2);
		}

		int comparison;
		if (value1 < value2) {
			comparison = -1;
		} else if (value1 > value2) {
			comparison = 1;
		} else {
			comparison = 0;
		}
		return comparison;
	}
}