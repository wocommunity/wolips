package tk.eclipse.plugin.htmleditor.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import tk.eclipse.plugin.htmleditor.HTMLUtil;
import tk.eclipse.plugin.xmleditor.editors.DTDResolver;
import tk.eclipse.plugin.xmleditor.editors.IDTDResolver;

public class XMLNewWizardPage extends WizardNewFileCreationPage {
	
	// for DTD
	private boolean useDTD = false;
	private String publicID = "";
	private String systemID = "";
	// for XSD
	private boolean useXSD = false;
	private String schemaURI = "";
	
	private String documentRoot = "";
	
	
	public XMLNewWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
	}
	
	public void setSchemaInfo(boolean useDTD,String publicID,String systemID,
			boolean useXSD,String schemaURI,String documentRoot){
		this.useDTD       = useDTD;
		this.publicID     = publicID;
		this.systemID     = systemID;
		this.documentRoot = documentRoot;
		this.useXSD       = useXSD;
		this.schemaURI    = schemaURI;
	}
	
	public IFile getFile(){
		IPath newFilePath = getContainerFullPath().append(getFileName());
		return createFileHandle(newFilePath);
	}
	
	@Override
  protected InputStream getInitialContents() {
		// charset encoding
		String projectName = getContainerFullPath().segment(0);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String charset = HTMLUtil.getProjectCharset(project);
//		try {
//			String projectName = getContainerFullPath().segment(0);
//			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//			charset = project.getDefaultCharset();
//		} catch(CoreException ex){
//		}
		// generate XML
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"");
		if(charset!=null){
			sb.append(" encoding=\"" + charset + "\"");
		}
		sb.append("?>\n");
		
		// DOCTYPE decl
		if(useDTD){
			if(!publicID.equals("") || !systemID.equals("")){
				sb.append("<!DOCTYPE ");
				sb.append(documentRoot);
				sb.append(" PUBLIC");
				sb.append(" \"" + publicID + "\"");
				sb.append(" \"" + systemID + "\"");
				sb.append(">\n");
			}
		}
		
		// generate the root tag
		if(useDTD || useXSD){
			if(!documentRoot.equals("")){
				sb.append("<").append(documentRoot);
				if(useXSD){
					String namespace = getTargetNamespace();
					if(namespace!=null){
						sb.append("\txmlns=\"" + namespace + "\"\n");
						sb.append("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
						sb.append("\txsi:schemaLocation=\"" + namespace + " " + schemaURI + "\"");
					} else {
						sb.append("\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
						sb.append("\txsi:noNamespaceSchemaLocation=\"" + schemaURI + "\"");
					}
				}
				sb.append(">\n");
				sb.append("</").append(documentRoot).append(">\n");
			}
		}
		
		return new ByteArrayInputStream(sb.toString().getBytes());
	}
	
	/** Returns a target namespace of XML Schema */
	private String getTargetNamespace(){
		try {
			DTDResolver resolver = new DTDResolver(new IDTDResolver[0],
					getFile().getLocation().makeAbsolute().toFile().getParentFile());
			InputStream in = resolver.getInputStream(this.schemaURI);
			if(in!=null){
				SchemaGrammar grammer = (SchemaGrammar)new XMLSchemaLoader().loadGrammar(
						new XMLInputSource(null,null,null,in,null));
				return grammer.getTargetNamespace();
			}
		} catch(Exception ex){
		}
		return null;
	}
}
