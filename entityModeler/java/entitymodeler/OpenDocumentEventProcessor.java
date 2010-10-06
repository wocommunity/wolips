package entitymodeler;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.objectstyle.wolips.eomodeler.actions.OpenEntityModelerAction;

public class OpenDocumentEventProcessor implements Listener {
	private ArrayList<String> _filesToOpen = new ArrayList<String>(1);

	public void handleEvent(Event event) {
		System.out.println("OpenDocumentEventProcessor.handleEvent: " + event.text);
		if (event.text != null) {
			_filesToOpen.add(event.text);
		}
	}

	public synchronized void openFiles() {
		if (_filesToOpen.isEmpty()) {
			return;
		}

		String[] filePaths = _filesToOpen.toArray(new String[_filesToOpen.size()]);
		_filesToOpen.clear();

		for (String path : filePaths) {
			System.out.println("OpenDocumentEventProcessor.openFiles: " + path);
			try {
				ApplicationWorkbenchAdvisor.openModelPath(path);
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}