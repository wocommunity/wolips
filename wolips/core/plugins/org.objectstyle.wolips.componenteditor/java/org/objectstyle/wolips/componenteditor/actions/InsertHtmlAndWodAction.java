package org.objectstyle.wolips.componenteditor.actions;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.actions.ActionDelegate;
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.wod.SimpleWodBinding;
import org.objectstyle.wolips.bindings.wod.SimpleWodElement;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

/**
 * <P>
 * This is the superclass of the actions that insert new components into the
 * component. Most of the guts of it are in the superclass here with the
 * configuration of the actions in the subclasses.
 * </P>
 * 
 * @author apl
 * 
 */

public abstract class InsertHtmlAndWodAction extends AbstractTemplateAction {
	/**
	 * <P>
	 * This method should return the required bindings that the component must
	 * have and so we may as well shove these in at the same time.
	 * </P>
	 */
	protected abstract Binding[] getRequiredBindings(String componentName);

	/**
	 * <P>
	 * This method will return true in the case that the component that would be
	 * inserted can contain other component content. An example of a component
	 * that can include content is WOHyperlink and an example of one that can't
	 * is WOString.
	 * </P>
	 * 
	 * <P>
	 * The subclasses of this component should override this method to be able
	 * to include content. By default they cannot.
	 * </P>
	 * 
	 * @return
	 */
	protected abstract boolean canHaveComponentContent(String componentName);

	/**
	 * Returns the InsertComponentSpecification for this component. This may
	 * open a dialog, so it should only be called from the run method.
	 * 
	 * @return the InsertComponentSpecification for this action
	 */
	protected abstract InsertComponentSpecification getComponentSpecification();

	protected String getIndentText(IDocument doc, int offset) throws BadLocationException {
		IRegion indentRegion = getIndentRegion(doc, offset);
		String indentText = doc.get(indentRegion.getOffset(), indentRegion.getLength());
		return indentText;
	}

	protected IRegion getIndentRegion(IDocument doc, int offset) throws BadLocationException {
		IRegion lineRegion = doc.getLineInformationOfOffset(offset);
		int lineStartOffset = lineRegion.getOffset();
		int lineLength = lineRegion.getLength();
		int lineEndOffset = lineStartOffset + lineLength;
		int indentOffset = -1;
		for (int i = lineStartOffset; indentOffset == -1 && i < lineEndOffset; i++) {
			char ch = doc.getChar(i);
			if (!Character.isWhitespace(ch)) {
				indentOffset = i;
			}
		}
		Region indentRegion;
		if (indentOffset == -1) {
			indentRegion = new Region(lineStartOffset, 0);
		} else {
			indentRegion = new Region(lineStartOffset, indentOffset - lineStartOffset);
		}
		return indentRegion;
	}

	protected boolean isLineEmpty(IDocument doc, int offset) throws BadLocationException {
		IRegion lineRegion = doc.getLineInformationOfOffset(offset);
		int lineStartOffset = lineRegion.getOffset();
		int lineLength = lineRegion.getLength();
		int lineEndOffset = lineStartOffset + lineLength;
		boolean isLineEmpty = true;
		for (int i = lineStartOffset; isLineEmpty && i < lineEndOffset; i++) {
			char ch = doc.getChar(i);
			if (!Character.isWhitespace(ch)) {
				isLineEmpty = false;
			}
		}
		return isLineEmpty;
	}

	/**
	 * @see ActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		TemplateEditor te = getTemplateEditor();
		WodEditor we = getWodEditor();

		if ((null != te) && (null != we)) {
			InsertComponentSpecification ics = getComponentSpecification();

			if (ics != null) {
				SimpleWodElement wodElement = new SimpleWodElement(ics.getComponentInstanceName(), ics.getComponentName());
				Binding[] bindings = ics.getRequiredBindings();
				if (bindings != null) {
					for (Binding binding : bindings) {
						wodElement.addBinding(new SimpleWodBinding(binding.getName(), "", true));
					}
				}

				// If the component name is blank, then this is an HTML tag
				if (ics.getComponentName() == null || ics.getComponentName().length() == 0) {
					wodElement.setTagName(ics.getTagName());
				}

				SimpleWodElement htmlElement;
				if (ics.isInline()) {
					htmlElement = wodElement;
				} else {
					htmlElement = new SimpleWodElement("", "");
					htmlElement.setTagName(ics.getTagName());
				}

				Map<String, String> htmlAttributes = ics.getHtmlAttributes();
				if (htmlAttributes != null) {
					for (Map.Entry<String, String> htmlAttribute : htmlAttributes.entrySet()) {
						htmlElement.addBinding(new SimpleWodBinding(htmlAttribute.getKey(), htmlAttribute.getValue(), true));
					}
				}

				IDocument teDoc = te.getHtmlEditDocument();
				IDocument weDoc = we.getWodEditDocument();
				ITextSelection teDocTSel = (ITextSelection) te.getSourceEditor().getSelectionProvider().getSelection();

				// insert the WebObjects component into the template portion.
				try {
					ITextViewerExtension teExt = (ITextViewerExtension)te.getSourceEditor().getViewer();
					teExt.getRewriteTarget().beginCompoundChange();
					try {
						int selectionStartOffset = teDocTSel.getOffset();
						int selectionEndOffset = teDocTSel.getOffset() + teDocTSel.getLength();
	
						if (canHaveComponentContent(ics.getComponentName())) {
							int selectionStartLine = teDocTSel.getStartLine();
							int selectionEndLine = teDocTSel.getEndLine();
	
							StringWriter startTagWriter = new StringWriter();
							htmlElement.writeInlineFormat(startTagWriter, "", true, true, false, false, "$", "");
							String startTag = startTagWriter.toString();
	
							StringWriter endTagWriter = new StringWriter();
							htmlElement.writeInlineFormat(endTagWriter, "", true, false, false, true, "$", "");
							String endTag = endTagWriter.toString();
	
							String indentText = getIndentText(teDoc, selectionStartOffset);
							IRegion startLineRegion = teDoc.getLineInformationOfOffset(selectionStartOffset);
							IRegion endLineRegion = teDoc.getLineInformationOfOffset(selectionEndOffset);
	
							// MS: If the selection starts within the indent
							// area, then you're actually selecting
							// from the beginning of the line, not splitting an
							// existing line of HTML.
							int selectionLineStartOffset = (selectionStartOffset - startLineRegion.getOffset());
							boolean selectionStartedInIndent = (indentText.length() >= selectionLineStartOffset);
	
							if (selectionStartLine == selectionEndLine) {
								if (selectionEndOffset == endLineRegion.getOffset() && selectionEndOffset > 0) {
									teDoc.replace(selectionEndOffset - 1, 0, endTag);
								} else {
									teDoc.replace(selectionEndOffset, 0, endTag);
								}
	
								if (selectionStartedInIndent) {
									if (indentText.length() == 0) {
										teDoc.replace(startLineRegion.getOffset() - 1, 0, startTag);
									}
									else {
										teDoc.replace(startLineRegion.getOffset() + indentText.length(), 0, startTag);
									}
								} else {
									teDoc.replace(selectionStartOffset, 0, startTag);
								}
							} else {
								int indentEndOffset;
								String lastLineIndentText = getIndentText(teDoc, selectionEndOffset);
								int selectionLineEndOffset = (selectionEndOffset - endLineRegion.getOffset());
								if (lastLineIndentText.length() >= selectionLineEndOffset) {
									String endText = indentText + endTag + "\n";
									teDoc.replace(endLineRegion.getOffset(), 0, endText);
									indentEndOffset = 1;
								} else {
									String endText = "\n" + indentText + endTag + "\n" + indentText;
									teDoc.replace(selectionEndOffset, 0, endText);
									indentEndOffset = 2;
								}
	
								int indentStartOffset;
								if (selectionStartedInIndent) {
									indentStartOffset = 1;
									String startText = startTag + "\n" + indentText;
									teDoc.replace(startLineRegion.getOffset() + indentText.length(), 0, startText);
								} else {
									indentStartOffset = 2;
									String startText = "\n" + indentText + startTag + "\n" + indentText;
									teDoc.replace(selectionStartOffset, 0, startText);
								}
								for (int line = selectionStartLine + indentStartOffset; line <= selectionEndLine + indentEndOffset; line++) {
									int lineOffset = teDoc.getLineOffset(line);
									teDoc.replace(lineOffset, 0, "\t");
								}
							}
						} else {
							StringWriter startTagWriter = new StringWriter();
							htmlElement.writeInlineFormat(startTagWriter, null, true, true, false, true, "$", "");
							String tag = startTagWriter.toString();
							teDoc.replace(selectionStartOffset, 0, tag);
						}
					}
					finally {
						teExt.getRewriteTarget().endCompoundChange();
					}
					
					// insert the WebObjects component into the bindings
					// portion.
					if (!ics.isInline()) {
						int firstBindingValueOffset = -1;

						int offset = weDoc.getLength();
						StringWriter wodElementWriter = new StringWriter();
						if (offset > 0) {
							wodElementWriter.write("\n");
						}
						wodElement.writeWodFormat(wodElementWriter, true);
						wodElementWriter.flush();
						String wodElementStr = wodElementWriter.toString();

						weDoc.replace(offset, 0, wodElementStr);

						if (-1 != firstBindingValueOffset) {
							we.selectAndReveal(offset + firstBindingValueOffset, 0);
						} else {
							we.selectAndReveal(offset, wodElementStr.length());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					ComponenteditorPlugin.getDefault().log(e);
				}
			}
		}
	}
}
