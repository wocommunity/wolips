package org.objectstyle.wolips.goodies.core.mac;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.resources.refresh.RefreshProvider;
import org.objectstyle.wolips.goodies.core.mac.jna.CoreServices.CoreServicesWrapper;

public class MacRefreshProvider extends RefreshProvider {
	private MacRefreshMonitor _refreshMonitor;

	@Override
	public synchronized IRefreshMonitor installMonitor(IResource resource, IRefreshResult result) {
		IRefreshMonitor refreshMonitor;
		CoreServicesWrapper coreServices = new CoreServicesWrapper();
		if (coreServices.SystemVersion() >= 0x1050) {
			if (_refreshMonitor == null) {
				_refreshMonitor = new MacRefreshMonitor();
			}
			_refreshMonitor.monitor(resource, result);
			refreshMonitor = _refreshMonitor;
		} else {
			refreshMonitor = super.createPollingMonitor(resource);
		}
		return refreshMonitor;
	}

}
