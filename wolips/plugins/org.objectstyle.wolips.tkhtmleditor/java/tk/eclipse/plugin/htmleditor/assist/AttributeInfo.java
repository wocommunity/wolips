package tk.eclipse.plugin.htmleditor.assist;

import java.util.ArrayList;

public class AttributeInfo {

	private String attributeName;
	private boolean hasValue;
	private int attributeType;
	private boolean required = false;
	private String description;
	private ArrayList values = new ArrayList();
	
	public static final int NONE       = 0;
	public static final int ALIGN      = 1;
	public static final int VALIGN     = 2;
	public static final int INPUT_TYPE = 3;
	public static final int CSS        = 4;
	public static final int FILE       = 5;
	public static final int ID         = 6;
	public static final int IDREF      = 7;
	public static final int IDREFS     = 8;
	
	/**
	 * The constructor.
	 * 
	 * @param attributeName attribute name
	 * @param hasValue      this attribute has value or not
	 */
	public AttributeInfo(String attributeName,boolean hasValue){
		this(attributeName,hasValue,NONE);
	}
	
	/**
	 * The constructor.
	 * 
	 * @param attributeName attribute name
	 * @param hasValue      this attribute has value or not
	 * @param attributeType attribute type
	 */
	public AttributeInfo(String attributeName,boolean hasValue,int attributeType){
		this(attributeName,hasValue,attributeType,false);
	}
	
	/**
	 * The constructor.
	 * 
	 * @param attributeName attribute name
	 * @param hasValue      this attribute has value or not
	 * @param attributeType attribute type
	 * @param required      this attribute is required or not
	 */
	public AttributeInfo(String attributeName,boolean hasValue,int attributeType,boolean required){
		this.attributeName = attributeName;
		this.hasValue      = hasValue;
		this.attributeType = attributeType;
		this.required      = required;
	}
	
	public int getAttributeType(){
		return this.attributeType;
	}
	
	public void setAttributeType(int type){
		this.attributeType = type;
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public boolean hasValue(){
		return this.hasValue;
	}
	
	public boolean isRequired(){
		return this.required;
	}
	
	public void addValue(String value){
	    this.values.add(value);
	}
	
	public String[] getValues(){
	    return (String[])this.values.toArray(new String[this.values.size()]);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
