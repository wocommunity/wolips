package tk.eclipse.plugin.csseditor.editors;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Naoki Takezoe
 */
public class CSSDefinition {
  public static List<CSSProperty> PROPERTIES;

  static {
    List<CSSProperty> cssProperties = new LinkedList<CSSProperty>();
    CSSProperty textIndent = new CSSProperty("text-indent");
    textIndent.addValueType(new CSSSizeValueType());
    textIndent.addValueType("inherit");
    cssProperties.add(textIndent);

    CSSProperty textAlign = new CSSProperty("text-align");
    textAlign.addValueType("left");
    textAlign.addValueType("right");
    textAlign.addValueType("center");
    textAlign.addValueType("justify");
    textAlign.addValueType("inherit");
    textAlign.addValueType("\"string\"");
    cssProperties.add(textAlign);

    CSSProperty textDecoration = new CSSProperty("text-decoration");
    textDecoration.addValueType("none");
    textDecoration.addValueType("underline");
    textDecoration.addValueType("overline");
    textDecoration.addValueType("line-through");
    textDecoration.addValueType("blink");
    textDecoration.addValueType("inherit");
    cssProperties.add(textDecoration);

    CSSProperty textShadow = new CSSProperty("text-shadow");
    textShadow.addValueType("none");
    textShadow.addValueType(new CSSColorValueType());
    textShadow.addValueType(new CSSCompositeValueType(new CSSColorValueType(), new CSSSizeValueType(), new CSSSizeValueType()));
    textShadow.addValueType(new CSSCompositeValueType(new CSSColorValueType(), new CSSSizeValueType(), new CSSSizeValueType(), new CSSSizeValueType()));
    textShadow.addValueType("inherit");
    cssProperties.add(textShadow);

    CSSProperty letterSpacing = new CSSProperty("letter-spacing");
    letterSpacing.addValueType("normal");
    letterSpacing.addValueType(new CSSSizeValueType());
    letterSpacing.addValueType("inherit");
    cssProperties.add(letterSpacing);

    CSSProperty wordSpacing = new CSSProperty("word-spacing");
    wordSpacing.addValueType("normal");
    wordSpacing.addValueType(new CSSSizeValueType());
    wordSpacing.addValueType("inherit");
    cssProperties.add(wordSpacing);

    CSSProperty textTransform = new CSSProperty("text-transform");
    textTransform.addValueType("captialize");
    textTransform.addValueType("uppercase");
    textTransform.addValueType("lowercase");
    textTransform.addValueType("none");
    textTransform.addValueType("inherit");
    cssProperties.add(textTransform);

    CSSProperty whiteSpace = new CSSProperty("white-space");
    whiteSpace.addValueType("normal");
    whiteSpace.addValueType("pre");
    whiteSpace.addValueType("nowrap");
    whiteSpace.addValueType("inherit");
    cssProperties.add(whiteSpace);

    CSSProperty color = new CSSProperty("color");
    color.addValueType(new CSSColorValueType());
    color.addValueType("inherit");
    cssProperties.add(color);

    CSSProperty backgroundColor = new CSSProperty("background-color");
    backgroundColor.addValueType(new CSSColorValueType());
    backgroundColor.addValueType("transparent");
    backgroundColor.addValueType("inherit");
    cssProperties.add(backgroundColor);

    CSSProperty backgroundImage = new CSSProperty("background-image");
    backgroundImage.addValueType("url(\"xxx\")");
    backgroundImage.addValueType("none");
    backgroundImage.addValueType("inherit");
    cssProperties.add(backgroundImage);

    CSSProperty backgroundRepeat = new CSSProperty("background-repeat");
    backgroundRepeat.addValueType("repeat");
    backgroundRepeat.addValueType("repeat-x");
    backgroundRepeat.addValueType("repeat-y");
    backgroundRepeat.addValueType("no-repeat");
    backgroundRepeat.addValueType("inherit");
    cssProperties.add(backgroundRepeat);

    CSSProperty backgroundAttachment = new CSSProperty("background-attachment");
    backgroundAttachment.addValueType("scroll");
    backgroundAttachment.addValueType("fixed");
    backgroundAttachment.addValueType("inherit");
    cssProperties.add(backgroundAttachment);

    CSSEnumValueType vertical = new CSSEnumValueType("top", "center", "bottom");
    CSSEnumValueType horizontal = new CSSEnumValueType("left", "center", "right");
    CSSProperty backgroundPosition = new CSSProperty("background-position");
    backgroundPosition.addValueType(new CSSSizeValueType());
    backgroundPosition.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType()));
    backgroundPosition.addValueType(vertical);
    backgroundPosition.addValueType(horizontal);
    backgroundPosition.addValueType(new CSSCompositeValueType(vertical, horizontal));
    backgroundPosition.addValueType(new CSSCompositeValueType(horizontal, vertical));
    backgroundPosition.addValueType("inherit");
    cssProperties.add(backgroundPosition);

    CSSProperty background = new CSSProperty("background");
    CSSEnumValueType backgroundTypes = new CSSEnumValueType(backgroundColor, backgroundImage, backgroundRepeat, backgroundAttachment, backgroundPosition);
    background.addValueType(backgroundTypes);
    background.addValueType(new CSSCompositeValueType(backgroundTypes, backgroundTypes));
    background.addValueType(new CSSCompositeValueType(backgroundTypes, backgroundTypes, backgroundTypes));
    background.addValueType(new CSSCompositeValueType(backgroundTypes, backgroundTypes, backgroundTypes, backgroundTypes));
    background.addValueType(new CSSCompositeValueType(backgroundTypes, backgroundTypes, backgroundTypes, backgroundTypes, backgroundTypes));
    cssProperties.add(background);

    CSSProperty paddingLeft = new CSSProperty("padding-left");
    paddingLeft.addValueType(new CSSSizeValueType());
    paddingLeft.addValueType("inherit");
    cssProperties.add(paddingLeft);

    CSSProperty paddingRight = new CSSProperty("padding-right");
    paddingRight.addValueType(new CSSSizeValueType());
    paddingRight.addValueType("inherit");
    cssProperties.add(paddingRight);

    CSSProperty paddingTop = new CSSProperty("padding-top");
    paddingTop.addValueType(new CSSSizeValueType());
    paddingTop.addValueType("inherit");
    cssProperties.add(paddingTop);

    CSSProperty paddingBottom = new CSSProperty("padding-bottom");
    paddingBottom.addValueType(new CSSSizeValueType());
    paddingBottom.addValueType("inherit");
    cssProperties.add(paddingBottom);

    CSSProperty padding = new CSSProperty("padding");
    padding.addValueType(new CSSSizeValueType());
    padding.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType()));
    padding.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType(), new CSSSizeValueType()));
    padding.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType(), new CSSSizeValueType(), new CSSSizeValueType()));
    padding.addValueType("inherit");
    cssProperties.add(padding);

    CSSEnumValueType borderWidth = new CSSEnumValueType();
    borderWidth.addValueType("thin");
    borderWidth.addValueType("medium");
    borderWidth.addValueType("thick");
    borderWidth.addValueType(new CSSSizeValueType());

    CSSProperty borderLeft = new CSSProperty("border-left");
    borderLeft.addValueType(borderWidth);
    borderLeft.addValueType("inherit");
    cssProperties.add(borderLeft);

    CSSProperty borderRight = new CSSProperty("border-right");
    borderRight.addValueType(borderWidth);
    borderRight.addValueType("inherit");
    cssProperties.add(borderRight);

    CSSProperty borderTop = new CSSProperty("border-top");
    borderTop.addValueType(borderWidth);
    borderTop.addValueType("inherit");
    cssProperties.add(borderTop);

    CSSProperty borderBottom = new CSSProperty("border-bottom");
    borderBottom.addValueType(borderWidth);
    borderBottom.addValueType("inherit");
    cssProperties.add(borderBottom);

    CSSEnumValueType borderStyleTypes = new CSSEnumValueType("none", "hidden", "dotted", "dashed", "solid", "double", "groove", "ridge", "inset", "outset");
    CSSProperty borderStyle = new CSSProperty("border-style");
    borderStyle.addValueType(borderStyleTypes);
    borderStyle.addValueType("inherit");
    cssProperties.add(borderStyle);

    CSSEnumValueType borderTypes = new CSSEnumValueType(borderWidth, borderStyleTypes, new CSSColorValueType());
    borderTypes.addValueType(new CSSColorValueType());
    CSSProperty border = new CSSProperty("border");
    border.addValueType(borderTypes);
    border.addValueType(new CSSCompositeValueType(borderTypes, borderTypes));
    border.addValueType(new CSSCompositeValueType(borderTypes, borderTypes, borderTypes));
    border.addValueType(new CSSCompositeValueType(borderTypes, borderTypes, borderTypes, borderTypes));
    border.addValueType("inherit");
    cssProperties.add(border);

    CSSProperty marginLeft = new CSSProperty("margin-left");
    marginLeft.addValueType(new CSSSizeValueType());
    marginLeft.addValueType("inherit");
    cssProperties.add(marginLeft);

    CSSProperty marginRight = new CSSProperty("margin-right");
    marginRight.addValueType(new CSSSizeValueType());
    marginRight.addValueType("inherit");
    cssProperties.add(marginRight);

    CSSProperty marginTop = new CSSProperty("margin-top");
    marginTop.addValueType(new CSSSizeValueType());
    marginTop.addValueType("inherit");
    cssProperties.add(marginTop);

    CSSProperty marginBottom = new CSSProperty("margin-bottom");
    marginBottom.addValueType(new CSSSizeValueType());
    marginBottom.addValueType("inherit");
    cssProperties.add(marginBottom);

    CSSProperty margin = new CSSProperty("margin");
    margin.addValueType(new CSSSizeValueType());
    margin.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType()));
    margin.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType(), new CSSSizeValueType()));
    margin.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType(), new CSSSizeValueType(), new CSSSizeValueType()));
    margin.addValueType("inherit");
    cssProperties.add(margin);

    CSSProperty fontFamily = new CSSProperty("font-family");
    fontFamily.addValueType("serif");
    fontFamily.addValueType("sans-serif");
    fontFamily.addValueType("cursive");
    fontFamily.addValueType("fantasy");
    fontFamily.addValueType("monospace");
    fontFamily.addValueType("Helvetica");
    fontFamily.addValueType("Arial");
    fontFamily.addValueType("Geneva");
    cssProperties.add(fontFamily);

    CSSProperty fontStyle = new CSSProperty("font-style");
    fontStyle.addValueType("normal");
    fontStyle.addValueType("italic");
    fontStyle.addValueType("oblique");
    fontStyle.addValueType("inherit");
    cssProperties.add(fontStyle);

    CSSProperty fontWeight = new CSSProperty("font-weight");
    fontWeight.addValueType("normal");
    fontWeight.addValueType("bold");
    fontWeight.addValueType("bolder");
    fontWeight.addValueType("lighter");
    fontWeight.addValueType("100");
    fontWeight.addValueType("200");
    fontWeight.addValueType("300");
    fontWeight.addValueType("400");
    fontWeight.addValueType("500");
    fontWeight.addValueType("600");
    fontWeight.addValueType("700");
    fontWeight.addValueType("800");
    fontWeight.addValueType("900");
    fontWeight.addValueType("inherit");
    cssProperties.add(fontWeight);

    CSSProperty fontVariant = new CSSProperty("font-variant");
    fontVariant.addValueType("normal");
    fontVariant.addValueType("small-caps");
    fontVariant.addValueType("inherit");
    cssProperties.add(fontVariant);

    CSSProperty fontStretch = new CSSProperty("font-stretch");
    fontStretch.addValueType("normal");
    fontStretch.addValueType("wider");
    fontStretch.addValueType("narrower");
    fontStretch.addValueType("ultra-condensed");
    fontStretch.addValueType("extra-condensed");
    fontStretch.addValueType("condensed");
    fontStretch.addValueType("semi-condensed");
    fontStretch.addValueType("semi-expanded");
    fontStretch.addValueType("expanded");
    fontStretch.addValueType("extra-expanded");
    fontStretch.addValueType("ultra-expanded");
    fontStretch.addValueType("inherit");
    cssProperties.add(fontStretch);

    CSSEnumValueType absoluteSize = new CSSEnumValueType("xx-small", "x-small", "small", "medium", "large", "x-large", "xx-large");
    CSSEnumValueType relativeSize = new CSSEnumValueType("larger", "smaller");
    
    CSSProperty fontSizeAdjust = new CSSProperty("font-size-adjust");
    fontSizeAdjust.addValueType("none");
    fontSizeAdjust.addValueType(new CSSSizeValueType()); // this should actually just be a number
    fontSizeAdjust.addValueType("inherit");
    cssProperties.add(fontSizeAdjust);

    CSSProperty fontSize = new CSSProperty("font-size");
    fontSize.addValueType(new CSSSizeValueType());
    fontSize.addValueType(absoluteSize);
    fontSize.addValueType(relativeSize);
    fontSize.addValueType("inherit");
    cssProperties.add(fontSize);

    CSSEnumValueType fontStyles = new CSSEnumValueType(fontStyle, fontVariant, fontWeight, fontSize, fontFamily);
    fontStyles.addValueType("caption");
    fontStyles.addValueType("icon");
    fontStyles.addValueType("menu");
    fontStyles.addValueType("message-box");
    fontStyles.addValueType("small-caption");
    fontStyles.addValueType("status-bar");
    
    CSSProperty font = new CSSProperty("font");
    font.addValueType(fontStyles);
    font.addValueType(new CSSEnumValueType(fontStyles, fontStyles));
    font.addValueType(new CSSEnumValueType(fontStyles, fontStyles, fontStyles));
    font.addValueType(new CSSEnumValueType(fontStyles, fontStyles, fontStyles, fontStyles));
    font.addValueType(new CSSEnumValueType(fontStyles, fontStyles, fontStyles, fontStyles, fontStyles));
    font.addValueType("inherit");
    cssProperties.add(font);

    CSSProperty borderLeftWidth = new CSSProperty("border-left-width");
    borderLeftWidth.addValueType(borderWidth);
    borderLeftWidth.addValueType("inherit");
    cssProperties.add(borderLeftWidth);

    CSSProperty borderRightWidth = new CSSProperty("border-right-width");
    borderRightWidth.addValueType(borderWidth);
    borderRightWidth.addValueType("inherit");
    cssProperties.add(borderRightWidth);

    CSSProperty borderTopWidth = new CSSProperty("border-top-width");
    borderTopWidth.addValueType(borderWidth);
    borderTopWidth.addValueType("inherit");
    cssProperties.add(borderTopWidth);

    CSSProperty borderBottomWidth = new CSSProperty("border-bottom-width");
    borderBottomWidth.addValueType(borderWidth);
    borderBottomWidth.addValueType("inherit");
    cssProperties.add(borderBottomWidth);

    CSSProperty borderLeftColor = new CSSProperty("border-left-color");
    borderLeftColor.addValueType(new CSSColorValueType());
    borderLeftColor.addValueType("inherit");
    cssProperties.add(borderLeftColor);

    CSSProperty borderRightColor = new CSSProperty("border-right-color");
    borderRightColor.addValueType(new CSSColorValueType());
    borderRightColor.addValueType("inherit");
    cssProperties.add(borderRightColor);

    CSSProperty borderTopColor = new CSSProperty("border-top-color");
    borderTopColor.addValueType(new CSSColorValueType());
    borderTopColor.addValueType("inherit");
    cssProperties.add(borderTopColor);

    CSSProperty borderBottomColor = new CSSProperty("border-bottom-color");
    borderBottomColor.addValueType(new CSSColorValueType());
    borderBottomColor.addValueType("inherit");
    cssProperties.add(borderBottomColor);

    CSSProperty borderLeftStyle = new CSSProperty("border-left-style");
    borderLeftStyle.addValueType(borderStyleTypes);
    borderLeftStyle.addValueType("inherit");
    cssProperties.add(borderLeftStyle);

    CSSProperty borderRightStyle = new CSSProperty("border-right-style");
    borderRightStyle.addValueType(borderStyleTypes);
    borderRightStyle.addValueType("inherit");
    cssProperties.add(borderRightStyle);

    CSSProperty borderTopStyle = new CSSProperty("border-top-style");
    borderTopStyle.addValueType(borderStyleTypes);
    borderTopStyle.addValueType("inherit");
    cssProperties.add(borderTopStyle);

    CSSProperty borderBottomStyle = new CSSProperty("border-bottom-style");
    borderBottomStyle.addValueType(borderStyleTypes);
    borderBottomStyle.addValueType("inherit");
    cssProperties.add(borderBottomStyle);

    CSSProperty display = new CSSProperty("display");
    display.addValueType("inline");
    display.addValueType("block");
    display.addValueType("list-item");
    display.addValueType("run-in");
    display.addValueType("compact");
    display.addValueType("marker");
    display.addValueType("table");
    display.addValueType("inline-table");
    display.addValueType("table-row-group");
    display.addValueType("table-header-group");
    display.addValueType("table-footer-group");
    display.addValueType("table-row");
    display.addValueType("table-column-group");
    display.addValueType("table-column");
    display.addValueType("table-cell");
    display.addValueType("table-caption");
    display.addValueType("none");
    display.addValueType("inherit");
    cssProperties.add(display);

    CSSProperty position = new CSSProperty("position");
    position.addValueType("static");
    position.addValueType("fixed");
    position.addValueType("relative");
    position.addValueType("absolute");
    position.addValueType("inherit");
    cssProperties.add(position);

    CSSProperty top = new CSSProperty("top");
    top.addValueType(new CSSSizeValueType());
    top.addValueType("auto");
    top.addValueType("inherit");
    cssProperties.add(top);

    CSSProperty bottom = new CSSProperty("bottom");
    bottom.addValueType(new CSSSizeValueType());
    bottom.addValueType("auto");
    bottom.addValueType("inherit");
    cssProperties.add(bottom);

    CSSProperty left = new CSSProperty("left");
    left.addValueType(new CSSSizeValueType());
    left.addValueType("auto");
    left.addValueType("inherit");
    cssProperties.add(left);

    CSSProperty right = new CSSProperty("right");
    right.addValueType(new CSSSizeValueType());
    right.addValueType("auto");
    right.addValueType("inherit");
    cssProperties.add(right);

    CSSProperty cssfloat = new CSSProperty("float");
    cssfloat.addValueType("left");
    cssfloat.addValueType("right");
    cssfloat.addValueType("none");
    cssfloat.addValueType("inherit");
    cssProperties.add(cssfloat);

    CSSProperty clear = new CSSProperty("clear");
    clear.addValueType("none");
    clear.addValueType("left");
    clear.addValueType("right");
    clear.addValueType("both");
    clear.addValueType("inherit");
    cssProperties.add(clear);

    CSSProperty zIndex = new CSSProperty("z-index");
    zIndex.addValueType("auto");
    zIndex.addValueType("##");
    zIndex.addValueType("inherit");
    cssProperties.add(zIndex);

    CSSProperty direction = new CSSProperty("direction");
    direction.addValueType("ltr");
    direction.addValueType("rtl");
    direction.addValueType("inherit");
    cssProperties.add(direction);

    CSSProperty unicodeBidi = new CSSProperty("unicode-bidi");
    unicodeBidi.addValueType("normal");
    unicodeBidi.addValueType("embed");
    unicodeBidi.addValueType("bidi-override");
    unicodeBidi.addValueType("inherit");
    cssProperties.add(unicodeBidi);

    CSSProperty width = new CSSProperty("width");
    width.addValueType(new CSSSizeValueType());
    width.addValueType("auto");
    width.addValueType("inherit");
    cssProperties.add(width);

    CSSProperty minWidth = new CSSProperty("min-width");
    minWidth.addValueType(new CSSSizeValueType());
    minWidth.addValueType("inherit");
    cssProperties.add(minWidth);

    CSSProperty maxWidth = new CSSProperty("max-width");
    maxWidth.addValueType(new CSSSizeValueType());
    maxWidth.addValueType("none");
    maxWidth.addValueType("inherit");
    cssProperties.add(maxWidth);

    CSSProperty height = new CSSProperty("height");
    height.addValueType(new CSSSizeValueType());
    height.addValueType("auto");
    height.addValueType("inherit");
    cssProperties.add(height);

    CSSProperty minHeight = new CSSProperty("min-height");
    minHeight.addValueType(new CSSSizeValueType());
    minHeight.addValueType("inherit");
    cssProperties.add(minHeight);

    CSSProperty maxHeight = new CSSProperty("max-height");
    maxHeight.addValueType(new CSSSizeValueType());
    maxHeight.addValueType("none");
    maxHeight.addValueType("inherit");
    cssProperties.add(maxHeight);

    CSSProperty lineHeight = new CSSProperty("line-height");
    lineHeight.addValueType("normal");
    lineHeight.addValueType(new CSSSizeValueType());
    lineHeight.addValueType("inherit");
    cssProperties.add(lineHeight);

    CSSProperty verticalAlign = new CSSProperty("vertical-align");
    verticalAlign.addValueType("baseline");
    verticalAlign.addValueType("sub");
    verticalAlign.addValueType("super");
    verticalAlign.addValueType("top");
    verticalAlign.addValueType("text-top");
    verticalAlign.addValueType("middle");
    verticalAlign.addValueType("bottom");
    verticalAlign.addValueType("text-bottom");
    verticalAlign.addValueType(new CSSSizeValueType());
    verticalAlign.addValueType("inherit");
    cssProperties.add(verticalAlign);

    CSSProperty overflow = new CSSProperty("overflow");
    overflow.addValueType("visible");
    overflow.addValueType("hidden");
    overflow.addValueType("scroll");
    overflow.addValueType("auto");
    overflow.addValueType("inherit");
    cssProperties.add(overflow);

    CSSProperty clip = new CSSProperty("clip");
    clip.addValueType("rect(##, ##, ##, ##)");
    clip.addValueType("auto");
    clip.addValueType("inherit");
    cssProperties.add(clip);

    CSSProperty visibility = new CSSProperty("visibility");
    visibility.addValueType("visible");
    visibility.addValueType("hidden");
    visibility.addValueType("collapse");
    visibility.addValueType("inherit");
    cssProperties.add(visibility);

    CSSProperty captionSide = new CSSProperty("caption-side");
    captionSide.addValueType("top");
    captionSide.addValueType("bottom");
    captionSide.addValueType("left");
    captionSide.addValueType("right");
    captionSide.addValueType("inherit");
    cssProperties.add(captionSide);

    CSSProperty tableLayout = new CSSProperty("table-layout");
    tableLayout.addValueType("auto");
    tableLayout.addValueType("fixed");
    tableLayout.addValueType("inherit");
    cssProperties.add(tableLayout);

    CSSProperty borderCollapse = new CSSProperty("border-collapse");
    borderCollapse.addValueType("collapse");
    borderCollapse.addValueType("separate");
    borderCollapse.addValueType("inherit");
    cssProperties.add(borderCollapse);

    CSSProperty borderSpacing = new CSSProperty("border-spacing");
    borderSpacing.addValueType(new CSSSizeValueType());
    borderSpacing.addValueType(new CSSCompositeValueType(new CSSSizeValueType(), new CSSSizeValueType()));
    borderSpacing.addValueType("inherit");
    cssProperties.add(borderSpacing);

    CSSProperty emptyCells = new CSSProperty("empty-cells");
    emptyCells.addValueType("show");
    emptyCells.addValueType("hide");
    emptyCells.addValueType("inherit");
    cssProperties.add(emptyCells);

    CSSProperty content = new CSSProperty("content");
    content.addValueType("\"xxx\"");
    cssProperties.add(content);

    CSSProperty quotes = new CSSProperty("quotes");
    quotes.addValueType("'\"' '\"'");
    quotes.addValueType("none");
    quotes.addValueType("inherit");
    cssProperties.add(quotes);

    CSSEnumValueType listStyleTypes = new CSSEnumValueType("disc", "circle", "square", "decimal", "decimal-leading-zero", "lower-roman", "upper-roman", "lower-greek", "lower-alpha", "lower-latin", "upper-alpha", "upper-latin", "hebrew", "armenian", "georgian", "cjk-ideographic", "hiragena", "katakana", "hiragana-iroha", "none");
    CSSProperty listStyleType = new CSSProperty("list-style-type");
    listStyleType.addValueType(listStyleTypes);
    listStyleType.addValueType("inherit");
    cssProperties.add(listStyleType);

    CSSProperty listStyleImage = new CSSProperty("list-style-image");
    listStyleImage.addValueType("url(\"xxx\")");
    listStyleImage.addValueType("none");
    listStyleImage.addValueType("inherit");
    cssProperties.add(listStyleImage);

    CSSProperty listStylePosition = new CSSProperty("list-style-position");
    listStylePosition.addValueType("inside");
    listStylePosition.addValueType("outside");
    listStylePosition.addValueType("inherit");
    cssProperties.add(listStylePosition);

    CSSEnumValueType listStyles = new CSSEnumValueType(listStyleType, listStyleImage, listStylePosition);
    CSSProperty listStyle = new CSSProperty("list-style");
    listStyle.addValueType(listStyles);
    listStyle.addValueType(new CSSCompositeValueType(listStyles, listStyles));
    listStyle.addValueType(new CSSCompositeValueType(listStyles, listStyles, listStyles));
    listStyle.addValueType("inherit");
    cssProperties.add(listStyle);

    CSSProperty markerOffset = new CSSProperty("marker-offset");
    markerOffset.addValueType(new CSSSizeValueType());
    markerOffset.addValueType("auto");
    markerOffset.addValueType("inherit");
    cssProperties.add(markerOffset);

    CSSProperty cursor = new CSSProperty("cursor");
    cursor.addValueType("url(\"xxx\")");
    cursor.addValueType("auto");
    cursor.addValueType("crosshair");
    cursor.addValueType("default");
    cursor.addValueType("pointer");
    cursor.addValueType("move");
    cursor.addValueType("e-resize");
    cursor.addValueType("ne-resize");
    cursor.addValueType("nw-resize");
    cursor.addValueType("n-resize");
    cursor.addValueType("se-resize");
    cursor.addValueType("sw-resize");
    cursor.addValueType("s-resize");
    cursor.addValueType("w-resize");
    cursor.addValueType("text");
    cursor.addValueType("wait");
    cursor.addValueType("help");
    cursor.addValueType("inherit");
    cssProperties.add(cursor);

    CSSProperty outlineWidth = new CSSProperty("outline-width");
    outlineWidth.addValueType(borderWidth);
    outlineWidth.addValueType("inherit");
    cssProperties.add(outlineWidth);

    CSSProperty outlineColor = new CSSProperty("outline-color");
    outlineColor.addValueType(new CSSColorValueType());
    outlineColor.addValueType("inherit");
    cssProperties.add(outlineColor);

    CSSProperty outlineStyle = new CSSProperty("outline-style");
    outlineStyle.addValueType(borderStyleTypes);
    outlineStyle.addValueType("inherit");
    cssProperties.add(outlineStyle);

    CSSEnumValueType outlineTypes = new CSSEnumValueType(outlineColor, outlineWidth, outlineStyle);
    CSSProperty outline = new CSSProperty("outline");
    outline.addValueType(outlineTypes);
    outline.addValueType(new CSSCompositeValueType(outlineTypes, outlineTypes));
    outline.addValueType(new CSSCompositeValueType(outlineTypes, outlineTypes, outlineTypes));
    outline.addValueType("inherit");
    cssProperties.add(outline);

    CSSDefinition.PROPERTIES = cssProperties;
  }

  //	static {
  //		// sort by length
  //		Arrays.sort(CSS_KEYWORDS,CSSProperty textIndent = new Comparator(){
  //			public int compare(Object o1, Object o2){
  //				CSSProperty info1 = (CSSProperty)o1; cssProperties.add(info);
  //				CSSProperty info2 = (CSSProperty)o2; cssProperties.add(info);
  //				if(info1.getReplaceString().length() > info2.getReplaceString().length()){
  //					return -1;
  //				}
  //				if(info1.getReplaceString().length() < info2.getReplaceString().length()){
  //					return 1;
  //				}
  //				return 0;
  //			}
  //		});
  //	}
}
