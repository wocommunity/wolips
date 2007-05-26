package tk.eclipse.plugin.htmleditor.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * @author Naoki Takezoe
 */
public abstract class AbstractCharacterPairMatcher implements ICharacterPairMatcher {
	
	private int fAnchor;
	private boolean enable;
	
	private Map<String, String> pairMap = new HashMap<String, String>();
	
	private List<String> startBlock = new ArrayList<String>();
	private List<String> endBlock   = new ArrayList<String>();
	private List<String> quote      = new ArrayList<String>();
	
	private char delimiter = '=';
	
	public void setDelimiter(char c){
		this.delimiter = c;
	}
	
	public void addQuoteCharacter(char c){
		quote.add(String.valueOf(c));
	}
	
	protected String getSource(IDocument document){
		return document.get();
	}
	
	public void addBlockCharacter(char start, char end){
		pairMap.put(String.valueOf(start), String.valueOf(end));
		pairMap.put(String.valueOf(end), String.valueOf(start));
		startBlock.add(String.valueOf(start));
		endBlock.add(String.valueOf(end));
	}
	
	public void setEnable(boolean enable){
		this.enable = enable;
	}
	
	public void dispose() {
	}

	public void clear() {
	}
	
	public IRegion match(IDocument document, int offset) {
		if(offset < 0 || offset >= document.getLength() || !enable){
			return null;
		}
		
		String text = getSource(document);
		
		try {
			if(offset < document.getLength()){
				char c = text.charAt(offset);
				if(isEndBlock(c)){
					int place = getPrevPlace(text, c, offset-1);
					if(place >= 0){
						fAnchor = ICharacterPairMatcher.LEFT;
						return new Region(place, 1);
					}
				}
				if(isQuoteCharacter(c)){
					String substr = text.substring(0, offset);
					int stoffset = substr.lastIndexOf(c);
					int eqoffset = substr.lastIndexOf(delimiter);
					if(stoffset > eqoffset){
						int place = substr.lastIndexOf(c, offset - 1);
						if(place >= 0){
							fAnchor = ICharacterPairMatcher.LEFT;
							return new Region(place, 1);
						}
					}
				}
			}
			if(offset > 0){
				char c = text.charAt(offset - 1);
				if(isStartBlock(c)){
					int place = getNextPlace(text, c, offset+1);
					if(place >= 0){
						fAnchor = ICharacterPairMatcher.RIGHT;
						return new Region(place, 1);
					}
				}
				if(isQuoteCharacter(c)){
					String substr = text.substring(0, offset - 1);
					int stoffset = substr.lastIndexOf(c);
					int eqoffset = substr.lastIndexOf(delimiter);
					if(stoffset < eqoffset){
						int place = text.indexOf(c, offset);
						if(place >= 0){
							fAnchor = ICharacterPairMatcher.RIGHT;
							return new Region(place, 1);
						}
					}
				}
			}
		} catch(Exception ex){
			HTMLPlugin.logException(ex);
			setEnable(false);
		}
		
		fAnchor = -1;
		return null;
	}
	
	private int getPrevPlace(String source, char c, int offset){
		char pair = getPairCharacter(c);
		int nest = 0;
		for(int i=offset;i>=0;i--){
			char nc = source.charAt(i);
			if(nc==c){
				nest++;
			} else if(nc==pair){
				if(nest==0){
					return i;
				} else {
					nest--;
				}
			}
		}
		return -1;
	}
	
	private int getNextPlace(String source, char c, int offset){
		char pair = getPairCharacter(c);
		int nest = 0;
		for(int i=offset;i<source.length();i++){
			char nc = source.charAt(i);
			if(nc==c){
				nest++;
			} else if(nc==pair){
				if(nest==0){
					return i;
				} else {
					nest--;
				}
			}
		}
		return -1;
	}
	
	private boolean isStartBlock(char c){
		return startBlock.contains(String.valueOf(c));
	}
	
	private boolean isEndBlock(char c){
		return endBlock.contains(String.valueOf(c));
	}
	
	private boolean isQuoteCharacter(char c){
		return quote.contains(String.valueOf(c));
	}
	
	private char getPairCharacter(char c){
		String pair = pairMap.get(String.valueOf(c));
		if(pair==null){
			return 0;
		}
		return pair.charAt(0);
	}
	
	public int getAnchor() {
		return fAnchor;
	}
}
