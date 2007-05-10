package org.objectstyle.wolips.componenteditor.actions;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

/**
 * <P>This class is used in the dialogue box for the user to be able
 * to identify a new name for the inserted component instance.  It
 * will ensure that no bad characters are used in the component name.
 * </P> 
 * @author apl
 *
 */

public class ComponentInstanceNameVerifyListener implements VerifyListener
{

	public boolean isValidCharacter(char ch)
	{
		if((ch>=0x41) && (ch<=0x5A))
			return true;
		
		if((ch>=0x61) && (ch<=0x7A))
			return true;
		
		if((ch>=0x30) && (ch<=0x39))
			return true;
		
		return false;
	}
	
	public void verifyText(VerifyEvent e)
	{
		if((null!=e.text) && e.doit) {
			for(int i=0;(i<e.text.length())&&e.doit;i++)
			{
				char ch = e.text.charAt(i);
				
				if(!isValidCharacter(ch))
					e.doit = false;
			}
		}		
	}
}
