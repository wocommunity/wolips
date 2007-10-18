package org.objectstyle.wolips.wodclipse.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.IWodModel;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.document.WodBindingNameHyperlink;
import org.objectstyle.wolips.wodclipse.core.document.WodBindingValueHyperlink;
import org.objectstyle.wolips.wodclipse.core.document.WodElementTypeHyperlink;

public class WodElementHyperlinkDetector implements IHyperlinkDetector {
	private WodEditor _editor;

	public WodElementHyperlinkDetector(WodEditor editor) {
		_editor = editor;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		List<IHyperlink> hyperlinks = new LinkedList<IHyperlink>();
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
								if (element.isTypeWithin(region)) {
									WodElementTypeHyperlink typeHyperlink = WodElementTypeHyperlink.toElementTypeHyperlink(element, cache);
									if (typeHyperlink != null) {
										hyperlinks.add(typeHyperlink);
									}
								}
								for (IWodBinding binding : element.getBindings()) {
									if (binding.isNameWithin(region)) {
										WodBindingNameHyperlink bindingHyperlink = WodBindingNameHyperlink.toBindingNameHyperlink(element, binding.getName(), cache);
										if (bindingHyperlink != null) {
											hyperlinks.add(bindingHyperlink);
										}
									}
									else if (binding.isValueWithin(region)) {
										WodBindingValueHyperlink bindingHyperlink = WodBindingValueHyperlink.toBindingValueHyperlink(element, binding.getName(), cache);
										if (bindingHyperlink != null) {
											hyperlinks.add(bindingHyperlink);
										}
									}
								}
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
		} else {
			hyperlinksArray = hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
		}
		return hyperlinksArray;
	}
}
