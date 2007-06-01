package org.objectstyle.wolips.wodclipse.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;

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
					for (IWodElement element : model.getElements()) {
						Position typePosition = element.getElementTypePosition();
						if (typePosition != null && typePosition.getOffset() < region.getOffset() && typePosition.getOffset() + typePosition.getLength() > region.getOffset()) {
							Region elementRegion = new Region(typePosition.getOffset(), typePosition.getLength());
							WodHyperlink hyperlink = new WodHyperlink(elementRegion, element.getElementType(), cache);
							hyperlinks.add(hyperlink);
						}
					}
				}
			}
		} catch (Exception e) {
			Activator.getDefault().log(e);
		}
		return hyperlinks.toArray(new WodHyperlink[hyperlinks.size()]);
	}

	private class WodHyperlink implements IHyperlink {
		private WodParserCache _cache;

		private IRegion _region;

		private String _elementType;

		public WodHyperlink(IRegion region, String elementType, WodParserCache cache) {
			_region = region;
			_elementType = elementType;
			_cache = cache;
		}

		public IRegion getHyperlinkRegion() {
			return _region;
		}

		public String getTypeLabel() {
			return null;
		}

		public String getHyperlinkText() {
			return null;
		}

		public void open() {
			try {
				IType type = WodReflectionUtils.findElementType(_cache.getJavaProject(), _elementType, false, _cache);
				if (type != null) {
					IJavaElement element = type.getPrimaryElement();
					if (element != null) {
						JavaUI.revealInEditor(JavaUI.openInEditor(element), element);
					}
				}
			} catch (Exception ex) {
				Activator.getDefault().log(ex);
			}
		}

	}

}
