package org.objectstyle.wolips.componenteditor.actions;

import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.*;
import org.eclipse.core.runtime.*;

import org.eclipse.ui.actions.*;
import org.eclipse.ui.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.text.*;

import org.objectstyle.wolips.componenteditor.*;
import org.objectstyle.wolips.componenteditor.part.*;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public abstract class InsertComponentAction extends ActionDelegate implements IEditorActionDelegate
{
	
	protected IEditorPart activeEditor = null;
	
/**
 * <P>This method should return the required bindings
 * that the component must have and so we may as well
 * shove these in at the same time.</P>
 */
	
	public Collection getRequiredBindings() { return null; }
	
/**
 * <P>This is a standard suffix for the component
 * names.  For example, you might like your string
 * components to generally have "String" at the
 * end.</P>
 */
	
	public abstract String getComponentInstanceNameSuffix();
	
/**
 * <P>This is the name of the component that will be
 * inserted.  Some examples of standard component
 * named might be <TT>WOString</TT>, <TT>WOForm</TT>
 * etc...</P>
 */
	
	public abstract String getComponentName();
	
/**
 * <P>This method will return true in the case that the
 * component that would be inserted can contain other
 * component content.  An example of a component that
 * can include content is WOHyperlink and an example of
 * one that can't is WOString.</P>
 * 
 * <P>The subclasses of this component should override
 * this method to be able to include content.  By 
 * default they cannot.</P>
 * @return 
 */	

	public boolean canHaveComponentContent() { return false; }
	
    /**
     * @see ActionDelegate#run(IAction)
     */
    public void run(IAction action) {
    	if(null!=activeEditor)
    	{
    		ComponentEditorPart cep = (ComponentEditorPart) activeEditor;
    		HtmlWodTab hwt = cep.htmlWodTab();
    		
    		if(null!=hwt)
    		{
    			TemplateEditor te = hwt.templateEditor();
    			WodEditor we = hwt.wodEditor();
    			
    		    IDocument teDoc = te.getHtmlEditDocument();
    		    IDocument weDoc = we.getWodEditDocument();
    		    ITextSelection teDocTSel = (ITextSelection) te.getSourceEditor().getSelectionProvider().getSelection(); 
    		    
    		    InsertComponentSpecification ics = new InsertComponentSpecification(getComponentName());
    		    ics.setRequiredBindings(getRequiredBindings());
    		    ics.setComponentInstanceNameSuffix(getComponentInstanceNameSuffix());
    		    
    		    IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    		    InsertComponentDialogue dialog = new InsertComponentDialogue(ww.getShell(), ics);
				dialog.open();

				if(null!=ics.getComponentInstanceName())
				{
    		    
// insert the WebObjects component into the template portion.
    		    
					{
						String partBefore = null;
						String partAfter = null;

						if(canHaveComponentContent())
						{
							partBefore = "\n<WEBOBJECT NAME=\""+ics.getComponentInstanceName()+"\">";
							partAfter = "</WEBOBJECT>\n";
						}
						else
						{
							partBefore = "<WEBOBJECT NAME=\""+ics.getComponentInstanceName()+"\"></WEBOBJECT>";
						}

						int offset = teDocTSel.getOffset();

						try { teDoc.replace(offset, 0, partBefore); }
						catch(BadLocationException ble) {
							throw new Error("unable to insert the WO part before into the template",ble); // difference exception.
						}

						if(null!=partAfter)
						{
							offset += partBefore.length();
							offset += teDocTSel.getLength();

							try { teDoc.replace(offset, 0, partAfter); }
							catch(BadLocationException ble) {
								throw new Error("unable to insert the WO part after into the template",ble); // use a different exception?
							}
						}
					}

//					insert the WebObjects component into the bindings portion.

					{
						int firstBindingValueOffset = -1;
						StringBuffer sb = new StringBuffer();

						sb.append("\n\n");
						sb.append(ics.getComponentInstanceName());
						sb.append(":");
						sb.append(ics.getComponentName());
						sb.append(" {\n");

						Collection bindings = ics.getRequiredBindings();

						if(null!=bindings)
						{
							Iterator bindingsI = bindings.iterator();

							while(bindingsI.hasNext())
							{
								String binding = (String) bindingsI.next();

								sb.append(binding);
								sb.append("=");
								
								if(-1==firstBindingValueOffset)
									firstBindingValueOffset = sb.length();
								
								sb.append(";\n");
							}
						}

						sb.append("}\n");

						int offset = weDoc.getLength();
						String association = sb.toString();

						try { weDoc.replace(offset,0,association); }
						catch(BadLocationException ble) {
							throw new Error("unable to insert the WO association into the wod file.",ble);
						}

						if(-1!=firstBindingValueOffset)
							we.selectAndReveal(offset+firstBindingValueOffset, 0);
						else
							we.selectAndReveal(offset, association.length());
					}
				}
    		}
    	}
    }

    /**
     * @see IEditorActionDelegate#setActiveEditor(IAction, IEditorPart)
     */
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    	activeEditor = targetEditor;
    }

    
}
