package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.jface.text.rules.*;

public class TagRule extends MultiLineRule {
	
	public TagRule(IToken token) {
		super("<", ">", token);
	}
	
	protected boolean sequenceDetected(ICharacterScanner scanner,char[] sequence,boolean eofAllowed) {
		if (sequence[0] == '<') {
			int c = scanner.read();
			if (c=='?' || c=='!' || c=='%') {
				scanner.unread();
				return false;
			}
		} else if (sequence[0] == '>') {
			// read previous char
			scanner.unread();
			scanner.unread();
			int c = scanner.read();
			// repair position
			scanner.read();
			
			if(c=='%') {
				return false;
			}
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
	
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		
		int c;
		boolean doubleQuoted = false;
		boolean singleQuoted = false;
		
		char[][] delimiters= scanner.getLegalLineDelimiters();
		boolean previousWasEscapeCharacter = false;	
		while ((c= scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip the escaped character.
				scanner.read();
			} else if(c=='"'){
				if(singleQuoted==false){
					doubleQuoted = !doubleQuoted;
				}
			} else if(c=='\''){
				if(doubleQuoted==false){
					singleQuoted = !singleQuoted;
				}
			} else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
				// Check if the specified end sequence has been found.
				if (doubleQuoted==false && singleQuoted==false && sequenceDetected(scanner, fEndSequence, true))
					return true;
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for (int i= 0; i < delimiters.length; i++) {
					if (c == delimiters[i][0] && sequenceDetected(scanner, delimiters[i], true)) {
						if (!fEscapeContinuesLine || !previousWasEscapeCharacter)
							return true;
					}
				}
			}
			previousWasEscapeCharacter = (c == fEscapeCharacter);
		}
		if (fBreaksOnEOF) return true;
		scanner.unread();
		return false;
	}

}
