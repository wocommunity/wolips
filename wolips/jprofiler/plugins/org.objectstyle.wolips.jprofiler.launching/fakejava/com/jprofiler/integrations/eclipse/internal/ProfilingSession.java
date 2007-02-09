package com.jprofiler.integrations.eclipse.A;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class ProfilingSession {

    public static ProfilingSession A(ILaunchConfigurationDelegate delegate, ILaunchConfiguration configuration) throws CoreException {
    	return null;
    }
    
    public Map getModifiedAttributesMap(Map map) {
    	return null;
    }

	public boolean sendProfilingRequest() {
		return false;
	}

	public ILaunchConfiguration getModifiedLaunchConfiguration(ILaunchConfiguration sourceConfiguration) throws CoreException {
		return null;
	}

}
