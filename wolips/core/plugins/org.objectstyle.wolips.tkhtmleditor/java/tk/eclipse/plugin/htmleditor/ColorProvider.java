package tk.eclipse.plugin.htmleditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorProvider {
	
	private Map colorTable = new HashMap(10);
	private Map tokenTable = new HashMap(10);	
	IPreferenceStore store;
	
	public ColorProvider(IPreferenceStore store) {
		this.store = store;
	}
	
	public IToken getToken(String prefKey){
	   Token token = (Token) tokenTable.get(prefKey);
	   if (token == null){
		  String colorName = store.getString(prefKey);
		  RGB rgb = StringConverter.asRGB(colorName);
		  token = new Token(new TextAttribute(getColor(rgb)));
		  tokenTable.put(prefKey, token);
	   }
	   return token;
	}
	
	public void dispose(){
		Iterator e = colorTable.values().iterator();
		while (e.hasNext()){
			((Color) e.next()).dispose();
		}
	}
	
	public Color getColor(String prefKey){
		  String colorName = store.getString(prefKey);
		  RGB rgb = StringConverter.asRGB(colorName);
		  return getColor(rgb);
	}
	
	private Color getColor(RGB rgb) {
		Color color = (Color) colorTable.get(rgb);
		if (color == null){
		   color = new Color(Display.getCurrent(), rgb);
		   colorTable.put(rgb, color);
		}
		return color;
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event){
	   Token token = (Token) tokenTable.get(event.getProperty());
	   return (token != null);
	}

	public void handlePreferenceStoreChanged(PropertyChangeEvent event){
	   String prefKey = event.getProperty();
	   Token token = (Token) tokenTable.get(prefKey);
	   if (token != null){
		  String colorName = store.getString(prefKey);
		  RGB rgb = StringConverter.asRGB(colorName);
		  token.setData(new TextAttribute(getColor(rgb)));
	   }
	}
}
