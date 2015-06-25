package org.objectstyle.wolips.pbserver;

import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.objectstyle.wolips.pbserver.preferences.PreferenceConstants;
import org.objectstyle.wolips.preferences.PreferencesPlugin;

public class PBServerStartup implements IStartup {

	private PBServer myServer;

	public void earlyStartup() {
		IPreferenceStore store = PreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(PreferenceConstants.PBSERVER_ENABLED)) {
			myServer = new PBServer();
			try {
				int port = store.getInt(PreferenceConstants.PBSERVER_PORT);
				myServer.start(port);
			} catch (IOException e) {
				e.printStackTrace(System.out);
			}
		}
	}
}
