package org.objectstyle.wolips.launching;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;

public class ConsoleLineTracker implements IConsoleLineTracker {

	private boolean urlFound;

	private IConsole currentConsole;

	private boolean webserverURLFound;

	private boolean webserverConnect;

	public ConsoleLineTracker() {
		super();
	}

	public void init(IConsole console) {
		// process type
		String openBrowser = null;
		try {
			openBrowser = console
					.getProcess()
					.getLaunch()
					.getLaunchConfiguration()
					.getAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_OPEN_IN_BROWSER,
							"false");
		} catch (CoreException e) {
			LaunchingPlugin.getDefault().getPluginLogger().log(e);
		}
		String webServerConnectString = null;
		try {
			webServerConnectString = console
					.getProcess()
					.getLaunch()
					.getLaunchConfiguration()
					.getAttribute(
							WOJavaLocalApplicationLaunchConfigurationDelegate.ATTR_WOLIPS_LAUNCH_WEBSERVER_CONNECT,
							"false");
		} catch (CoreException e) {
			LaunchingPlugin.getDefault().getPluginLogger().log(e);
		}

		if (openBrowser != null && openBrowser.equals("true")) {
			urlFound = false;
			webserverConnect = webServerConnectString != null
					&& webServerConnectString.equals("true");
			webserverURLFound = false;
			this.currentConsole = console;
		} else {
			urlFound = true;
		}

	}

	public void lineAppended(IRegion line) {
		if (urlFound) {
			return;
		}
		int offset = line.getOffset();
		int length = line.getLength();
		String text = null;
		try {
			text = currentConsole.getDocument().get(offset, length);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (text != null && text.startsWith("http://")) {
			final String urlString = text;
			if (!webserverConnect && !webserverURLFound) {
				webserverURLFound = true;
				return;
			}
			urlFound = true;
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					URL url;
					try {
						url = new URL(urlString);
						IWorkbenchBrowserSupport browserSupport = WebBrowserUIPlugin
								.getInstance().getWorkbench()
								.getBrowserSupport();
						IWebBrowser browser = browserSupport
								.createBrowser(
										IWorkbenchBrowserSupport.LOCATION_BAR
												| IWorkbenchBrowserSupport.NAVIGATION_BAR,
										null, null, null);
						browser.openURL(url);
					} catch (MalformedURLException e1) {
						LaunchingPlugin.getDefault().getPluginLogger().log(e1);
					} catch (PartInitException e) {
						LaunchingPlugin.getDefault().getPluginLogger().log(e);
					}

				}

			});

		}
	}

	public void dispose() {
		this.currentConsole = null;
	}
}
