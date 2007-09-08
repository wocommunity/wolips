package org.objectstyle.wolips.wodclipse.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;
import org.objectstyle.wolips.wodclipse.core.model.WodHyperlink;

public class WodElementHyperlinkDetector implements IHyperlinkDetector {
	private WodEditor _editor;

	public WodElementHyperlinkDetector(WodEditor editor) {
		_editor = editor;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		List<WodHyperlink> hyperlinks = new LinkedList<WodHyperlink>();
		try {
			IFileEditorInput input = (IFileEditorInput) _editor.getEditorInput();
			if (input != null) {
				IFile file = input.getFile();
				WodParserCache cache = WodParserCache.parser(file);
				IWodModel model = cache.getWodModel();
				if (model != null) {
					List<IWodElement> wodElements = model.getElements();
					if (wodElements != null) {
						for (IWodElement element : wodElements) {
							if (element.isWithin(region)) {
								hyperlinks.add(element.toWodHyperlink(cache));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Activator.getDefault().log(e);
		}
		IHyperlink[] hyperlinksArray;
		if (hyperlinks.size() == 0) {
			hyperlinksArray = null;
		}
		else {
			hyperlinksArray = hyperlinks.toArray(new WodHyperlink[hyperlinks.size()]);
		}
		return hyperlinksArray;
	}
}
