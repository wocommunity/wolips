package tk.eclipse.plugin.htmleditor.editors;

public class HTMLHyperlinkInfo {
	
	private Object obj;
	private int offset;
	private int length;
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	public Object getObject() {
		return obj;
	}

	public void setObject(Object obj) {
		this.obj = obj;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
