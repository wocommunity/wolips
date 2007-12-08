package org.objectstyle.wolips.wooeditor.utils;

public class WooUtils {
	private WooUtils() {

	}

	public static String encodingNameFromObjectiveC(
			String objectiveCEncodingName) {
		if (objectiveCEncodingName.equals("NSISOLatin1StringEncoding"))
			return "ISO-8859-1";
		if (objectiveCEncodingName.equals("NSMacOSRomanStringEncoding"))
			return "MacRoman";
		if (objectiveCEncodingName.equals("NSASCIIStringEncoding"))
			return "US-ASCII";
		if (objectiveCEncodingName.equals("NSNEXTSTEPStringEncoding"))
			return "ISO-8859-1";
		if (objectiveCEncodingName.equals("NSJapaneseEUCStringEncoding"))
			return "EUC_JP";
		if (objectiveCEncodingName.equals("NSUTF8StringEncoding"))
			return "UTF-8";
		if (objectiveCEncodingName.equals("NSSymbolStringEncoding"))
			return "MacSymbol";
		if (objectiveCEncodingName.equals("NSNonLossyASCIIStringEncoding"))
			return "US-ASCII";
		if (objectiveCEncodingName.equals("NSShiftJISStringEncoding"))
			return "SJIS";
		if (objectiveCEncodingName.equals("NSISOLatin2StringEncoding"))
			return "ISO-8859-2";
		if (objectiveCEncodingName.equals("NSUnicodeStringEncoding"))
			return "Unicode";
		if (objectiveCEncodingName.equals("NSWindowsCP1251StringEncoding"))
			return "Cp1251";
		if (objectiveCEncodingName.equals("NSWindowsCP1252StringEncoding"))
			return "Cp1252";
		if (objectiveCEncodingName.equals("NSWindowsCP1253StringEncoding"))
			return "Cp1253";
		if (objectiveCEncodingName.equals("NSWindowsCP1254StringEncoding"))
			return "Cp1254";
		if (objectiveCEncodingName.equals("NSWindowsCP1250StringEncoding"))
			return "Cp1250";
		if (objectiveCEncodingName.equals("NSISO2022JPStringEncoding"))
			return "ISO2022JP";
		else
			return objectiveCEncodingName;
	}

}
