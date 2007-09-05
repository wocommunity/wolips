package tk.eclipse.plugin.htmleditor.editors;

import org.eclipse.swt.graphics.RGB;

/** Defines initial colors used in editors. */
public interface IHTMLColorConstants {
	RGB HTML_COMMENT = new RGB( 63,  95, 191);
	RGB PROC_INSTR   = new RGB(128, 128, 128);
	RGB STRING       = new RGB( 42,   0, 255);
	RGB DEFAULT      = new RGB(  0,   0,   0);
	RGB TAG          = new RGB( 63, 127, 127);
  RGB WO_TAG       = new RGB(  3,  67,  67);
  RGB ATTRIBUTE    = new RGB(127,   0, 127);
	RGB SCRIPT       = new RGB(184,  93,   0);
	RGB CSS_PROP     = new RGB(  0,   0, 255);
	RGB CSS_COMMENT  = new RGB( 63,  95, 191);
	RGB CSS_VALUE    = new RGB(  0, 128,   0);
	RGB FOREGROUND   = new RGB(  0,   0,   0);
	RGB BACKGROUND   = new RGB(255, 255, 255);
	RGB JAVA_COMMENT = new RGB(  0, 128,   0);
	RGB JAVA_STRING  = new RGB(  0,   0, 255);
	RGB JAVA_KEYWORD = new RGB(128,   0, 128);
}
