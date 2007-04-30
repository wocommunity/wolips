package tk.eclipse.plugin.htmleditor.template;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.swt.graphics.Image;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * 
 * @author Naoki Takezoe
 */
public class HTMLTemplateAssistProcessor extends TemplateCompletionProcessor {

	protected Template[] getTemplates(String contextTypeId) {
		HTMLTemplateManager manager = HTMLTemplateManager.getInstance();
		return manager.getTemplateStore().getTemplates();
	}

	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		HTMLTemplateManager manager = HTMLTemplateManager.getInstance();
		return manager.getContextTypeRegistry().getContextType(HTMLContextType.CONTEXT_TYPE);
	}

	protected Image getImage(Template template) {
		return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_TEMPLATE);
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {

		ITextSelection selection= (ITextSelection) viewer.getSelectionProvider().getSelection();

		// adjust offset to end of normalized selection
		if (selection.getOffset() == offset)
			offset= selection.getOffset() + selection.getLength();

		String prefix= extractPrefix(viewer, offset);
		Region region= new Region(offset - prefix.length(), prefix.length());
		TemplateContext context= createContext(viewer, region);
		if (context == null)
			return new ICompletionProposal[0];

		context.setVariable("selection", selection.getText()); // name of the selection variables {line, word}_selection //$NON-NLS-1$

		Template[] templates= getTemplates(context.getContextType().getId());

		List matches= new ArrayList();
		for (int i= 0; i < templates.length; i++) {
			Template template= templates[i];
			try {
				context.getContextType().validate(template.getPattern());
			} catch (TemplateException e) {
				continue;
			}
			if (template.getName().startsWith(prefix) && 
					template.matches(prefix, context.getContextType().getId()))
				matches.add(createProposal(template, context, (IRegion) region, getRelevance(template, prefix)));
		}

		return (ICompletionProposal[]) matches.toArray(new ICompletionProposal[matches.size()]);
	}
	
}
