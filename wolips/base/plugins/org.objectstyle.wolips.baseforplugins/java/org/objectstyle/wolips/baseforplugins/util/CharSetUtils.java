package org.objectstyle.wolips.baseforplugins.util;

import java.util.Arrays;
import java.util.List;

public class CharSetUtils {
	
  public static final String ENCODING_UTF8 = "UTF-8";
  public static final String ENCODING_UTF16 = "UTF-16";
  public static final String ENCODING_MACROMAN = "MacRoman";
  public static final String ENCODING_MACSYMBOL = "MacSymbol";
  public static final String ENCODING_US_ASCII = "US-ASCII";
  public static final String ENCODING_JAPANESE_EUC = "EUC_JP";
  public static final String ENCODING_JAPANESE_ISO2022 = "ISO2022JP";
  public static final String ENCODING_JAPANESE_SHIFTJIS = "SJIS";
  public static final String ENCODING_ISO_LATIN1 = "ISO-8859-1";
  public static final String ENCODING_ISO_LATIN2 = "ISO-8859-2";
  public static final String ENCODING_UNICODE = "Unicode";
  public static final String ENCODING_WINDOWS_CP1250 = "Cp1250";
  public static final String ENCODING_WINDOWS_CP1251 = "Cp1251";
  public static final String ENCODING_WINDOWS_CP1252 = "Cp1252";
  public static final String ENCODING_WINDOWS_CP1253 = "Cp1253";
  public static final String ENCODING_WINDOWS_CP1254 = "Cp1254";
  
  private CharSetUtils() { 
	  /* Should not be instantiated */
  }
  
  public static List<String> defaultCharsetEncodingNames() {
	  return Arrays.asList(
			  ENCODING_UTF8,
			  ENCODING_UTF16,
			  ENCODING_US_ASCII,
			  ENCODING_ISO_LATIN1,
			  ENCODING_ISO_LATIN2,
			  ENCODING_JAPANESE_EUC,
			  ENCODING_JAPANESE_ISO2022,
			  ENCODING_JAPANESE_SHIFTJIS);
  }

  public static String encodingNameFromObjectiveC(final String encodingName) {
    if (encodingName.equals("NSISOLatin1StringEncoding"))
      return ENCODING_ISO_LATIN1;
    if (encodingName.equals("NSMacOSRomanStringEncoding"))
      return ENCODING_MACROMAN;
    if (encodingName.equals("NSASCIIStringEncoding"))
      return ENCODING_US_ASCII;
    if (encodingName.equals("NSNEXTSTEPStringEncoding"))
      return ENCODING_ISO_LATIN1;
    if (encodingName.equals("NSJapaneseEUCStringEncoding"))
      return ENCODING_JAPANESE_EUC;
    if (encodingName.equals("NSUTF8StringEncoding"))
      return ENCODING_UTF8;
    if (encodingName.equals("NSUTF16StringEncoding"))
        return ENCODING_UTF16;
    if (encodingName.equals("NSSymbolStringEncoding"))
      return ENCODING_MACSYMBOL;
    if (encodingName.equals("NSNonLossyASCIIStringEncoding"))
      return ENCODING_US_ASCII;
    if (encodingName.equals("NSShiftJISStringEncoding"))
      return ENCODING_JAPANESE_SHIFTJIS;
    if (encodingName.equals("NSISOLatin2StringEncoding"))
      return ENCODING_ISO_LATIN2;
    if (encodingName.equals("NSUnicodeStringEncoding"))
      return ENCODING_UNICODE;
    if (encodingName.equals("NSWindowsCP1251StringEncoding"))
      return ENCODING_WINDOWS_CP1251;
    if (encodingName.equals("NSWindowsCP1252StringEncoding"))
      return ENCODING_WINDOWS_CP1252;
    if (encodingName.equals("NSWindowsCP1253StringEncoding"))
      return ENCODING_WINDOWS_CP1253;
    if (encodingName.equals("NSWindowsCP1254StringEncoding"))
      return ENCODING_WINDOWS_CP1254;
    if (encodingName.equals("NSWindowsCP1250StringEncoding"))
      return ENCODING_WINDOWS_CP1250;
    if (encodingName.equals("NSISO2022JPStringEncoding"))
      return ENCODING_JAPANESE_ISO2022;
    
    return encodingName;
  }

}
