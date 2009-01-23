/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eogenerator.core.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.objectstyle.wolips.eogenerator.core.Activator;
import org.objectstyle.wolips.eogenerator.core.runner.ExternalEOGeneratorRunner;
import org.objectstyle.wolips.eogenerator.core.runner.VelocityEOGeneratorRunner;
import org.objectstyle.wolips.preferences.Preferences;

public class EOGenerateWorkspaceJob extends WorkspaceJob {
	private IFile _eogenFile;

	private List<IEOGeneratorListener> _listeners;

	private long _creationDate;

	public EOGenerateWorkspaceJob(IFile eogenFile) {
		super("EOGenerating " + eogenFile.getName() + " ...");
		_creationDate = System.currentTimeMillis();
		_eogenFile = eogenFile;
		_listeners = new LinkedList<IEOGeneratorListener>();
		//System.out.println("EOGenerateWorkspaceJob.EOGenerateWorkspaceJob: queuing up " + this);
	}

	public long creationDate() {
		return _creationDate;
	}

	public void addListener(IEOGeneratorListener listener) {
		_listeners.add(listener);
	}

	public boolean isFile(IFile file) {
		return _eogenFile.equals(file);
	}

	public IStatus runInWorkspace(IProgressMonitor monitor) {
		for (IEOGeneratorListener listener : _listeners) {
			listener.eogeneratorStarted();
		}
		try {
			boolean commitSuicide = false;

			IJobManager jobManager = Job.getJobManager();
			Job[] jobs = jobManager.find(null);
			if (jobs != null) {
				for (Job job : jobs) {
					if (job != this && job instanceof EOGenerateWorkspaceJob && ((EOGenerateWorkspaceJob) job).isFile(_eogenFile)) {
						EOGenerateWorkspaceJob otherJob = (EOGenerateWorkspaceJob) job;
						if (otherJob.creationDate() > _creationDate) {
							//System.out.println("EOGenerateWorkspaceJob.runInWorkspace: commit suicide on " + this + " because of " + job);
							commitSuicide = true;
						} else {
							//System.out.println("EOGenerateWorkspaceJob.runInWorkspace: canceling " + job + " from " + this);
							job.cancel();
						}
					}
				}
			}

			if (commitSuicide) {
				//System.out.println("EOGenerateWorkspaceJob.runInWorkspace: committing suicide");
			} else {
				//System.out.println("EOGenerateWorkspaceJob.runInWorkspace: running " + this + " (" + _eogenFile + ")");

				StringBuffer output = new StringBuffer();
				boolean fileSucceeded = true;
				setName("EOGenerating " + _eogenFile.getName() + " ...");
				try {
					EOGeneratorModel eogenModel = EOGeneratorModel.createModelFromFile(_eogenFile);
					eogenModel.setVerbose(eogenModel.isVerbose());

					IEOGeneratorRunner runner;
					String eogeneratorPath = Preferences.getEOGeneratorPath();
					if (eogeneratorPath == null || eogeneratorPath.length() == 0 || "velocity".equalsIgnoreCase(eogeneratorPath)) {
						runner = new VelocityEOGeneratorRunner();
					} else {
						runner = new ExternalEOGeneratorRunner();
					}
					boolean showResults = runner.generate(eogenModel, output, monitor);
					if (showResults) {
						fileSucceeded = false;
					}

					_eogenFile.getProject().getFolder(new Path(eogenModel.getDestination())).refreshLocal(IResource.DEPTH_INFINITE, monitor);
					_eogenFile.getProject().getFolder(new Path(eogenModel.getSubclassDestination())).refreshLocal(IResource.DEPTH_INFINITE, monitor);
				} catch (OperationCanceledException t) {
					//System.out.println("EOGenerateWorkspaceJob.runInWorkspace: canceled operation " + _eogenFile + ", " + this);
				} catch (Throwable t) {
					fileSucceeded = false;
					output.append(t.getMessage());
					//System.out.println("EOGenerateWorkspaceJob.runInWorkspace: failed " + _eogenFile);
					Activator.getDefault().log("Failed to generate " + _eogenFile.getName() + ".", t);
				}

				String outputStr = output.toString();
				if (fileSucceeded) {
					for (IEOGeneratorListener listener : _listeners) {
						listener.eogeneratorSucceeded(_eogenFile, outputStr);
					}
				} else {
					for (IEOGeneratorListener listener : _listeners) {
						listener.eogeneratorFailed(_eogenFile, outputStr);
					}
				}
			}
			//System.out.println("EOGenerateWorkspaceJob.runInWorkspace: finished " + this);
		} finally {
			for (IEOGeneratorListener listener : _listeners) {
				listener.eogeneratorFinished();
			}
		}

		return new Status(IStatus.OK, org.objectstyle.wolips.eogenerator.core.Activator.PLUGIN_ID, IStatus.OK, "Done", null);
	}
}