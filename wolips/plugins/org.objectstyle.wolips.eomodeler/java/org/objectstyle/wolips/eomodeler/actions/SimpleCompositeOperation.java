package org.objectstyle.wolips.eomodeler.actions;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.ICompositeOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.objectstyle.wolips.eomodeler.Activator;

public class SimpleCompositeOperation extends AbstractOperation implements ICompositeOperation {
	private List<IUndoableOperation> _operations;

	public SimpleCompositeOperation(String label) {
		super(label);
		_operations = new LinkedList<IUndoableOperation>();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		ExecutionException pendingException = null;
		boolean completed = false;
		List<IUndoableOperation> completedOperations = new LinkedList<IUndoableOperation>();
		for (IUndoableOperation operation : _operations) {
			completed = false;
			try {
				IStatus status = operation.execute(monitor, info);
				if (status.getSeverity() == IStatus.OK) {
					completed = true;
					completedOperations.add(operation);
				}
			}
			catch (ExecutionException e) {
				pendingException = e;
			}
			if (!completed) {
				break;
			}
		}
		
		IStatus status;
		if (!completed) {
			for (IUndoableOperation operation : completedOperations) {
				operation.undo(monitor, info);
			}
			if (pendingException != null) {
				throw pendingException;
			}
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Failed", null);
		}
		else {
			status = Status.OK_STATUS;
		}
		return status;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for (IUndoableOperation operation : _operations) {
			status = operation.redo(monitor, info);
			if (status.getSeverity() == IStatus.ERROR) {
				break;
			}
		}
		return status;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		IStatus status = Status.OK_STATUS;
		for (IUndoableOperation operation : _operations) {
			status = operation.undo(monitor, info);
			if (status.getSeverity() == IStatus.ERROR) {
				break;
			}
		}
		return status;
	}

	public void add(IUndoableOperation operation) {
		_operations.add(operation);
	}

	public void remove(IUndoableOperation operation) {
		_operations.remove(operation);
	}
	
	@Override
	public void addContext(IUndoContext context) {
		super.addContext(context);
		for (IUndoableOperation operation : _operations) {
			operation.addContext(context);
		}
	}
	
	@Override
	public void removeContext(IUndoContext context) {
		super.addContext(context);
		for (IUndoableOperation operation : _operations) {
			operation.removeContext(context);
		}
	}
	
	@Override
	public boolean canExecute() {
		boolean canExecute = super.canExecute();
		for (IUndoableOperation operation : _operations) {
			canExecute &= operation.canExecute();
		}
		return canExecute;
	}
	
	@Override
	public boolean canUndo() {
		boolean canUndo = super.canUndo();
		for (IUndoableOperation operation : _operations) {
			canUndo &= operation.canUndo();
		}
		return canUndo;
	}
	
	@Override
	public boolean canRedo() {
		boolean canRedo = super.canRedo();
		for (IUndoableOperation operation : _operations) {
			canRedo &= operation.canRedo();
		}
		return canRedo;
	}
	
	@Override
	public void dispose() {
		for (IUndoableOperation operation : _operations) {
			operation.dispose();
		}
		super.dispose();
	}
}
