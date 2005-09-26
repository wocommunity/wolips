package org.objectstyle.wolips.core.resources.builder;

import java.util.Map;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractDeltaCleanBuilder implements IDeltaBuilder, ICleanBuilder {
  public boolean handleClassesDelta(IResourceDelta _delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    handleClasses(_delta.getResource(), _progressMonitor, _buildCache);
    return false;
  }

  public boolean handleWoappResourcesDelta(IResourceDelta _delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    handleWoappResources(_delta.getResource(), _progressMonitor, _buildCache);
    return false;
  }

  public boolean handleWebServerResourcesDelta(IResourceDelta _delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    handleWebServerResources(_delta.getResource(), _progressMonitor, _buildCache);
    return false;
  }

  public boolean handleOtherDelta(IResourceDelta _delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    handleOther(_delta.getResource(), _progressMonitor, _buildCache);
    return false;
  }

  public boolean classpathChanged(IResourceDelta _delta, IProgressMonitor _progressMonitor, Map _buildCache) {
    handleClasspath(_delta.getResource(), _progressMonitor, _buildCache);
    return false;
  }
}
