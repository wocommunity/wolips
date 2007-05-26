package tk.eclipse.plugin.htmleditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.internal.core.UpdateCore;
import org.eclipse.update.internal.ui.UpdateUI;
import org.osgi.framework.BundleContext;

import tk.eclipse.plugin.htmleditor.views.IPaletteContributer;
import tk.eclipse.plugin.jseditor.launch.JavaScriptLaunchUtil;
import tk.eclipse.plugin.jspeditor.editors.IJSPFilter;
import tk.eclipse.plugin.jspeditor.editors.ITLDLocator;


/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Naoki Takezoe
 * @author Tom Wickham-Jones
 */
public class HTMLPlugin extends AbstractUIPlugin {
	
	//The shared instance.
	private static HTMLPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	//Color Provider
	private ColorProvider colorProvider;
	
	public static final String ICON_HTML     = "_icon_html";
	public static final String ICON_XML      = "_icon_xml";
	public static final String ICON_JSP      = "_icon_jsp";
	public static final String ICON_CSS      = "_icon_css";
	public static final String ICON_WEB      = "_icon_web";
	public static final String ICON_FILE     = "_icon_file";
	public static final String ICON_TAG      = "_icon_tag";
	public static final String ICON_ATTR     = "_icon_attribute";
	public static final String ICON_VALUE    = "_icon_value";
	public static final String ICON_FOLDER   = "_icon_folder";
	public static final String ICON_BUTTON   = "_icon_button";
	public static final String ICON_TEXT     = "_icon_text";
	public static final String ICON_RADIO    = "_icon_radio";
	public static final String ICON_CHECK    = "_icon_check";
	public static final String ICON_SELECT   = "_icon_select";
	public static final String ICON_TEXTAREA = "_icon_textarea";
	public static final String ICON_TABLE    = "_icon_table";
	public static final String ICON_COLUMN   = "_icon_column";
	public static final String ICON_LABEL    = "_icon_label";
	public static final String ICON_PASS     = "_icon_pass";
	public static final String ICON_LIST     = "_icon_list";
	public static final String ICON_PANEL    = "_icon_panel";
	public static final String ICON_LINK     = "_icon_link";
	public static final String ICON_HIDDEN   = "_icon_hidden";
	public static final String ICON_OUTPUT   = "_icon_output";
	public static final String ICON_CSS_RULE = "_icon_css_rule";
	public static final String ICON_CSS_PROP = "_icon_css_prop";
	public static final String ICON_PROPERTY = "_icon_property";
	public static final String ICON_FORWARD  = "_icon_forward";
	public static final String ICON_BACKWARD = "_icon_backword";
	public static final String ICON_REFRESH  = "_icon_refresh";
	public static final String ICON_RUN      = "_icon_run";
	public static final String ICON_TAG_HTML = "_icon_html";
	public static final String ICON_TITLE    = "_icon_title";
	public static final String ICON_FORM     = "_icon_form";
	public static final String ICON_IMAGE    = "_icon_image";
	public static final String ICON_COMMENT  = "_icon_comment";
	public static final String ICON_BODY     = "_icon_body";
	public static final String ICON_DOCTYPE  = "_icon_doctype";
	public static final String ICON_ELEMENT  = "_icon_element";
	public static final String ICON_ATTLIST  = "_icon_attlist";
	public static final String ICON_NOTATE   = "_icon_notate";
	public static final String ICON_ENTITY   = "_icon_entity";
	public static final String ICON_FUNCTION = "_icon_function";
	public static final String ICON_VARIABLE = "_icon_variable";
	public static final String ICON_CLASS    = "_icon_class";
	public static final String ICON_TEMPLATE = "_icon_template";
	public static final String ICON_JAVASCRIPT = "_icon_javascript";
	public static final String ICON_XSD      = "_icon_xsd";
	public static final String ICON_DTD      = "_icon_dtd";
	public static final String ICON_PALETTE  = "_icon_palette";
	public static final String ICON_ERROR    = "_icon_error";
	public static final String ICON_JAR      = "_icon_jar";
	public static final String ICON_JAR_EXT  = "_icon_jar_ext";
	public static final String ICON_INTERFACE = "_icon_interface";
	public static final String ICON_PACKAGE  = "_icon_package";
	
  public static final String PREF_COLOR_ATTRIBUTE  = "_pref_color_attribute";
	public static final String PREF_COLOR_TAG        = "_pref_color_tag";
	public static final String PREF_COLOR_COMMENT    = "_pref_color_comment";
	public static final String PREF_COLOR_STRING     = "_pref_color_string";
	public static final String PREF_COLOR_DOCTYPE    = "_pref_color_doctype";
	public static final String PREF_COLOR_SCRIPT     = "_pref_color_scriptlet";
  public static final String PREF_COLOR_OGNL       = "_pref_color_ognl";
  public static final String PREF_COLOR_DYNAMIC    = "_pref_color_dynamic";
	public static final String PREF_COLOR_CSSPROP    = "_pref_color_cssprop";
	public static final String PREF_COLOR_CSSCOMMENT = "_pref_color_csscomment";
	public static final String PREF_COLOR_CSSVALUE   = "_pref_color_cssvalue";
	public static final String PREF_EDITOR_TYPE      = "_pref_editor_type";
	public static final String PREF_DTD_URI          = "_pref_dtd_uri";
	public static final String PREF_DTD_PATH         = "_pref_dtd_path";
	public static final String PREF_DTD_CACHE        = "_pref_dtd_cache";
	public static final String PREF_ASSIST_AUTO      = "_pref_assist_auto";
	public static final String PREF_ASSIST_CHARS     = "_pref_assist_chars";
	public static final String PREF_ASSIST_TIMES     = "_pref_assist_times";
	public static final String PREF_ASSIST_CLOSE     = "_pref_assist_close";
	public static final String PREF_PALETTE_ITEMS    = "_pref_palette_items";
	public static final String PREF_USE_SOFTTAB      = "_pref_use_softtab";
	public static final String PREF_SOFTTAB_WIDTH    = "_pref_softtab_width";
	public static final String PREF_COLOR_BG         = "AbstractTextEditor.Color.Background";
	public static final String PREF_COLOR_BG_DEF     = "AbstractTextEditor.Color.Background.SystemDefault";
	public static final String PREF_COLOR_FG         = "__pref_color_foreground";
	public static final String PREF_TLD_URI          = "__pref_tld_uri";
	public static final String PREF_TLD_PATH         = "__pref_tld_path";
	public static final String PREF_JSP_COMMENT      = "__pref_jsp_comment";
	public static final String PREF_JSP_KEYWORD      = "__pref_jsp_keyword";
	public static final String PREF_JSP_STRING       = "__pref_jsp_string";
	public static final String PREF_PAIR_CHAR        = "__pref_pair_character";
	public static final String PREF_SHOW_XML_ERRORS  = "__pref_show_xml_errors";
	public static final String PREF_COLOR_JSSTRING   = "__pref_color_jsstring";
	public static final String PREF_COLOR_JSKEYWORD  = "__pref_color_jskeyword";
	public static final String PREF_COLOR_JSCOMMENT  = "__pref_color_jscomment";
	public static final String PREF_CUSTOM_ATTRS     = "__pref_custom_attributes";
	public static final String PREF_CUSTOM_ELEMENTS  = "__pref_custom_elements";
	public static final String PREF_TASK_TAGS        = "__pref_task_tags";
	public static final String PREF_ENABLE_CLASSNAME = "__pref_enable_classname";
	public static final String PREF_CLASSNAME_ATTRS  = "__pref_classname_attrs";
	public static final String PREF_SCHEMA_MAPPINGS  = "__pref_schema_mappings";
	
	
	public static final String[] SUPPORTED_IMAGE_TYPES = {
			"gif","png","jpg","jpeg","bmp"
	};
	
	private static Map<String, String> innerDTD = new LinkedHashMap<String, String>();
	static {
		innerDTD.put("http://java.sun.com/j2ee/dtds/web-app_2_2.dtd","/DTD/web-app_2_2.dtd");
		innerDTD.put("http://java.sun.com/dtd/web-app_2_3.dtd","/DTD/web-app_2_3.dtd");
		innerDTD.put("http://java.sun.com/j2ee/dtds/web-jsptaglibrary_1_1.dtd","/DTD/web-jsptaglibrary_1_1.dtd");
		innerDTD.put("http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd","/DTD/web-jsptaglibrary_1_2.dtd");
		innerDTD.put("XMLSchema.dtd","/DTD/XMLSchema.dtd");
		innerDTD.put("datatypes.dtd","/DTD/datatypes.dtd");
		
		innerDTD.put("http://java.sun.com/xml/ns/j2ee","/XSD/web-app_2_4.xsd");
		innerDTD.put("j2ee_1_4.xsd","/XSD/j2ee_1_4.xsd");
		innerDTD.put("j2ee_web_services_1_1.xsd","/XSD/j2ee_web_services_1_1.xsd");
		innerDTD.put("j2ee_web_services_client_1_1.xsd","/XSD/j2ee_web_services_client_1_1.xsd");
		innerDTD.put("jsp_2_0.xsd","/XSD/jsp_2_0.xsd");
		innerDTD.put("jspxml.xsd","/XSD/jspxml.xsd");
		innerDTD.put("web-app_2_4.xsd","/XSD/web-app_2_4.xsd");
		innerDTD.put("web-jsptaglibrary_2_0.xsd","/XSD/web-jsptaglibrary_2_0.xsd");
		innerDTD.put("xml.xsd","/XSD/xml.xsd");
	}
	
	private static Map<String, String> innerTLD = new LinkedHashMap<String, String>();
	static {
		innerTLD.put("http://java.sun.com/jstl/core_rt","/TLD/c-1_0-rt.tld");
		innerTLD.put("http://java.sun.com/jstl/core","/TLD/c-1_0.tld");
		innerTLD.put("http://java.sun.com/jsp/jstl/core","/TLD/c.tld");
		innerTLD.put("http://java.sun.com/jstl/fmt_rt","/TLD/fmt-1_0-rt.tld");
		innerTLD.put("http://java.sun.com/jstl/fmt","/TLD/fmt-1_0.tld");
		innerTLD.put("http://java.sun.com/jsp/jstl/fmt","/TLD/fmt.tld");
//		innerTLD.put("http://java.sun.com/jsp/jstl/functions","/TLD/fn.tld");
		innerTLD.put("http://java.sun.com/jstl/sql_rt","/TLD/sql-1_0-rt.tld");
		innerTLD.put("http://java.sun.com/jstl/sql","/TLD/sql-1_0.tld");
		innerTLD.put("http://java.sun.com/jsp/jstl/sql","/TLD/sql.tld");
		innerTLD.put("http://java.sun.com/jstl/xml_rt","/TLD/x-1_0-rt.tld");
		innerTLD.put("http://java.sun.com/jstl/xml","/TLD/x-1_0.tld");
		innerTLD.put("http://java.sun.com/jsp/jstl/xml","/TLD/x.tld");
	}
	
	/**
	 * The constructor.
	 */
	public HTMLPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("tk.eclipse.plugin.htmleditor.HTMLPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}
	
	public String getPluginId(){
		return getBundle().getSymbolicName();
	}
	
	public ColorProvider getColorProvider(){
		return this.colorProvider;
	}
	
	public static Map<String, String> getInnerDTD(){
		return innerDTD;
	}
	
	public static Map<String, String> getInnerTLD(){
		return innerTLD;
	}
	
	/**
	 * This method is called upon plug-in activation
	 */
	@Override
  public void start(BundleContext context) throws Exception {
		super.start(context);
		colorProvider = new ColorProvider(getPreferenceStore());
		
		// activate org.eclipse.update.core plugin, and enable proxy settings
		UpdateCore.getPlugin();
		UpdateUI.getDefault();
	}
	
	@Override
  protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(ICON_HTML,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/html.png")));
		reg.put(ICON_XML,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/xml.png")));
		reg.put(ICON_JSP,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/jsp.png")));
		reg.put(ICON_CSS,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/css.png")));
		reg.put(ICON_WEB,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/web.gif")));
		reg.put(ICON_FILE,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/file.gif")));
		reg.put(ICON_TAG,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/tag.gif")));
		reg.put(ICON_ATTR,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/attribute.gif")));
		reg.put(ICON_VALUE,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/value.gif")));
		reg.put(ICON_FOLDER,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/folder.gif")));
		reg.put(ICON_BUTTON,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/button.gif")));
		reg.put(ICON_TEXT,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/text.gif")));
		reg.put(ICON_RADIO,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/radio.gif")));
		reg.put(ICON_CHECK,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/checkbox.gif")));
		reg.put(ICON_SELECT,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/select.gif")));
		reg.put(ICON_TEXTAREA,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/textarea.gif")));
		reg.put(ICON_TABLE,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/table.gif")));
		reg.put(ICON_COLUMN,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/column.gif")));
		reg.put(ICON_LABEL,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/label.gif")));
		reg.put(ICON_PASS,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/password.gif")));
		reg.put(ICON_LIST,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/list.gif")));
		reg.put(ICON_PANEL,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/panel.gif")));
		reg.put(ICON_LINK,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/link.gif")));
		reg.put(ICON_HIDDEN,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/hidden.gif")));
		reg.put(ICON_OUTPUT,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/output.gif")));
		reg.put(ICON_CSS_RULE,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/css_rule.gif")));
		reg.put(ICON_CSS_PROP,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/css_prop.gif")));
		reg.put(ICON_PROPERTY,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/properties.gif")));
		reg.put(ICON_FORWARD,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/forward.gif")));
		reg.put(ICON_BACKWARD,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/backward.gif")));
		reg.put(ICON_REFRESH,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/refresh.gif")));
		reg.put(ICON_RUN,ImageDescriptor.createFromURL(getBundle().getEntry("/icons/run.gif")));
		reg.put(ICON_BODY, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/body.gif")));
		reg.put(ICON_FORM, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/form.gif")));
		reg.put(ICON_TAG_HTML, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/html.gif")));
		reg.put(ICON_IMAGE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/image.gif")));
		reg.put(ICON_TITLE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/title.gif")));
		reg.put(ICON_COMMENT, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/comment.gif")));
		reg.put(ICON_DOCTYPE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/doctype.gif")));
		reg.put(ICON_ENTITY, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/entity.gif")));
		reg.put(ICON_ATTLIST, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/attlist.gif")));
		reg.put(ICON_ELEMENT, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/element.gif")));
		reg.put(ICON_NOTATE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/notation.gif")));
		reg.put(ICON_FUNCTION, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/function.gif")));
		reg.put(ICON_VARIABLE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/var.gif")));
		reg.put(ICON_CLASS, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/class.gif")));
		reg.put(ICON_TEMPLATE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/template.gif")));
		reg.put(ICON_JAVASCRIPT, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/javascript.gif")));
		reg.put(ICON_XSD, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/xsd.gif")));
		reg.put(ICON_DTD, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/dtd.gif")));
		reg.put(ICON_PALETTE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/palette.gif")));
		reg.put(ICON_ERROR, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/error.gif")));
		reg.put(ICON_JAR, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/jar.gif")));
		reg.put(ICON_JAR_EXT, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/jar_ext.gif")));
		reg.put(ICON_INTERFACE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/interface.gif")));
		reg.put(ICON_PACKAGE, ImageDescriptor.createFromURL(getBundle().getEntry("/icons/package.gif")));
	}
	
	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
  public void stop(BundleContext context) throws Exception {
		JavaScriptLaunchUtil.removeLibraries();
		colorProvider.dispose();
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static HTMLPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = HTMLPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * Open the alert dialog.
	 * @param message message
	 */
	public static void openAlertDialog(String message){
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(),SWT.NULL|SWT.ICON_ERROR);
		box.setMessage(message);
		box.setText(getResourceString("ErrorDialog.Caption"));
		box.open();
	}
	
	/**
	 * Generates a message from a template and parameters.
	 * Replace template {0}{1}.. with parametersÅB
	 * 
	 * @param message message
	 * @param params  parameterd
	 * @return generated message
	 */
	public static String createMessage(String message,String[] params){
		for(int i=0;i<params.length;i++){
			message = message.replaceAll("\\{"+i+"\\}",params[i]);
		}
		return message;
	}
	
	/**
	 * Logging debug information.
	 * 
	 * @param message message
	 */
	public static void logDebug(String message){
		ILog log = getDefault().getLog();
		IStatus status = new Status(IStatus.INFO,getDefault().getPluginId(),0,message,null);
		log.log(status);
	}
	
	/**
	 * Logging error information.
	 * 
	 * @param message message
	 */
	public static void logError(String message){
		ILog log = getDefault().getLog();
		IStatus status = new Status(IStatus.ERROR,getDefault().getPluginId(),0,message,null);
		log.log(status);
	}
	
	/**
	 * Logging exception information.
	 * 
	 * @param ex exception
	 */
	public static void logException(Throwable ex){
		ILog log = getDefault().getLog();
		IStatus status = null;
		if(ex instanceof CoreException){
			status = ((CoreException)ex).getStatus();
		} else {
			status = new Status(IStatus.ERROR,getDefault().getPluginId(),0,ex.toString(),ex);
		}
		log.log(status);
		
		// TODO debug
		ex.printStackTrace();
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// Methods for extention points
	/////////////////////////////////////////////////////////////////////////////////
	
	private String[] noValidationNatureIds;
	
	public String[] getNoValidationNatureId(){
		if(noValidationNatureIds==null){
			List<String> list = new ArrayList<String>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(getPluginId() + ".noValidationNatures");
			IExtension[] extensions = point.getExtensions();
			for(int i=0;i<extensions.length;i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if ("noValidationNature".equals(elements[j].getName())) {
						String natureId = elements[j].getAttribute("natureId");
						list.add(natureId);
					}
				}
			}
			noValidationNatureIds = list.toArray(new String[list.size()]);
		}
		return noValidationNatureIds;
	}
	
	/**
	 * Returns contributed IFileAssistProcessor.
	 */
	public IFileAssistProcessor[] getFileAssistProcessors(){
		List<IFileAssistProcessor> list = loadContributedClasses("fileAssistProcessor", "processor");
		return list.toArray(new IFileAssistProcessor[list.size()]);
	}

	
	/** This contains URI and ICustomTagConverterContributer */
	private HashMap<String, ICustomTagConverterContributer> converterContributers = null;
	
	/**
	 * Returns contributed ICustomTagConverterContributer.
	 */
	public ICustomTagConverterContributer getCustomTagContributer(String uri){
		try {
			if(converterContributers==null){
				converterContributers = new HashMap<String, ICustomTagConverterContributer>();
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint point = registry.getExtensionPoint(getPluginId() + ".customTagConverter");
				IExtension[] extensions = point.getExtensions();
				for(int i=0;i<extensions.length;i++){
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if ("contributer".equals(elements[j].getName())) {
							String contributerUri = elements[j].getAttribute("uri");
							ICustomTagConverterContributer contributer = (ICustomTagConverterContributer) elements[j].createExecutableExtension("class");
							converterContributers.put(contributerUri,contributer);
						}
					}
				}
			}
			return converterContributers.get(uri);
		} catch(Exception ex){
			logException(ex);
			return null;
		}
	}
	
	/** List of ICustomTagAttributeAssist */
	private List<ICustomTagAttributeAssist> customTagAttrAssists = null;

	/**
	 * Returns contributed ICustomTagAttributeAssist.
	 */
	public ICustomTagAttributeAssist[] getCustomTagAttributeAssists(){
		if(customTagAttrAssists==null){
			customTagAttrAssists = loadContributedClasses("customTagAttributeAssist", "customTagAttributeAssist");
		}
		return customTagAttrAssists.toArray(new ICustomTagAttributeAssist[customTagAttrAssists.size()]);
	}
	
	/** List of IHyperlinkProvider */
	private List<IHyperlinkProvider> hyperlinkProviders = null;
	
	/**
	 * Returns contributed IHyperlinkProvider.
	 */
	public IHyperlinkProvider[] getHyperlinkProviders(){
		if(hyperlinkProviders==null){
			hyperlinkProviders = loadContributedClasses("hyperlinkProvider", "provider");
		}
		return hyperlinkProviders.toArray(new IHyperlinkProvider[hyperlinkProviders.size()]);
	}
	
	/** List of IPaletteContributer */
	private HashMap<String, IPaletteContributer> palette = null;
	
	/**
	 * Returns contributed IPaletteContributer which was registered as specified group name.
	 */
	public IPaletteContributer getPaletteContributer(String group){
		if(palette==null){
			loadPalleteContributer();
		}
		return palette.get(group);
	}
	
	/**
	 * Returns group names of contributed IPaletteContributer.
	 */
	public String[] getPaletteContributerGroups(){
		if(palette==null){
			loadPalleteContributer();
		}
		return palette.keySet().toArray(new String[0]);
	}
	
	/**
	 * Load informations of IPaletteContributer.
	 */
	private void loadPalleteContributer(){
		try {
			palette = new HashMap<String, IPaletteContributer>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(getPluginId() + ".paletteItem");
			IExtension[] extensions = point.getExtensions();
			for(int i=0;i<extensions.length;i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if ("contributer".equals(elements[j].getName())) {
						String group = elements[j].getAttribute("name");
						IPaletteContributer contributer = (IPaletteContributer) elements[j].createExecutableExtension("class");
						palette.put(group,contributer);
					}
				}
			}
		} catch(Exception ex){
			logException(ex);
		}
	}
	
	/** This contains URI and ICustomTagConverterContributer */
	private HashMap<String, ICustomTagValidatorContributer> validatorContributers = null;

	/**
	 * Returns contributed <code>ICustomTagValidatorContributer</code>.
	 */
	public ICustomTagValidatorContributer getCustomTagValidatorContributer(String uri){
		try {
			if(validatorContributers==null){
				validatorContributers = new HashMap<String, ICustomTagValidatorContributer>();
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint point = registry.getExtensionPoint(getPluginId() + ".customTagValidator");
				IExtension[] extensions = point.getExtensions();
				for(int i=0;i<extensions.length;i++){
					IConfigurationElement[] elements = extensions[i].getConfigurationElements();
					for (int j = 0; j < elements.length; j++) {
						if ("contributer".equals(elements[j].getName())) {
							String contributerUri = elements[j].getAttribute("uri");
							ICustomTagValidatorContributer validator = (ICustomTagValidatorContributer) elements[j].createExecutableExtension("class");
							validatorContributers.put(contributerUri,validator);
						}
					}
				}
			}
			return validatorContributers.get(uri);
		} catch(Exception ex){
			logException(ex);
			return null;
		}
	}
	
	/** List of ITLDLocator */
	private HashSet<ITLDLocator> tldlocators = null;

	/**
	 * Returns the array of contributed <code>ITLDLocator</code>s.
	 */
	public ITLDLocator[] getTLDLocatorContributions(){
		if(tldlocators == null){
			loadTLDLocatorContributions();
		}
		return tldlocators.toArray(new ITLDLocator[tldlocators.size()]);
	}

	private void loadTLDLocatorContributions() {
		try {
			tldlocators = new HashSet<ITLDLocator>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(getPluginId() + ".tldLocator");
			IExtension[] extensions = point.getExtensions();
			for(int i=0;i<extensions.length;i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if ("contributer".equals(elements[j].getName())) {
						//String group = elements[j].getAttribute("name");
						ITLDLocator contributer = (ITLDLocator) elements[j].createExecutableExtension("class");
						tldlocators.add(contributer);
					}
				}
			}
		} catch(Exception ex){
			logException(ex);
		}
	}
	
	
	/*
	 * Collect Filters contributed by other plugins
	 * @since 2.0.5
	 */
	private IJSPFilter[] jspFilters = null;
	
	/**
	 * Returns the array of contributed <code>IJSPFilter</code>s.
	 * 
	 * @since 2.0.5
	 */
	public IJSPFilter[] getJSPFilters() {
		if (jspFilters != null) {
			return jspFilters;
		}
		
		List<IJSPFilter> filters = loadContributedClasses("pagefilter", "jspfilter");
		jspFilters = filters.toArray(new IJSPFilter[filters.size()]);

		return jspFilters;
	}
	
	/**
	 * @since 2.0.5
	 */
	@SuppressWarnings("unchecked")
  private static <T> List<T> loadContributedClasses(String extPointId, String elementName){
		List<T> result = new ArrayList<T>();
		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(getDefault().getPluginId() + "." + extPointId);
			IExtension[] extensions = point.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if (elementName.equals(elements[j].getName())) {
						result.add((T)elements[j].createExecutableExtension("class"));
					}
				}
			}
		} catch (Exception ex) {
			logException(ex);
		}
		return result;
	}
	
	
}
