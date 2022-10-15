package org.objectstyle.wolips.womodeler;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;
import org.objectstyle.wolips.preferences.PreferencesPlugin;
import org.objectstyle.wolips.womodeler.preferences.PreferenceConstants;
import org.objectstyle.wolips.womodeler.server.Webserver;

public class WOModelerStartup implements IStartup {

	private Webserver _server;
	
	public void earlyStartup() {
		IPreferenceStore store = PreferencesPlugin.getDefault().getPreferenceStore();
		
	    _server = new Webserver(store.getInt(PreferenceConstants.WOMODELER_SERVER_PORT));
	    _server.addRequestHandler("/womodeler", new WOModelerRequestHandler());
	    _server.addRequestHandler("/refresh", new RefreshRequestHandler());
	    _server.addRequestHandler("/openComponent", new OpenComponentRequestHandler());
	    _server.addRequestHandler("/openJavaFile", new OpenJavaFileRequestHandler());
		
		if (PreferencesPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WOMODELER_SERVER_ENABLED)) {
			_server.start(true);
		}
	}
}
