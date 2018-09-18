package org.fit.pdfdom;

import lombok.Data;
import org.apache.pdfbox.text.TextPosition;

/**
 * This class represents a style of a text box.
 *
 * @author radek
 */
@Data
public class BoxStyle {
    public static final String defaultColor = "#000000";
    public static final String defaultFontWeight = "normal";
    public static final String defaultFontStyle = "normal";
    public static final String defaultPosition = "absolute";
    public static final String transparentColor = "rgba(0,0,0,0)";

    private String units;

    //font
    private String fontFamily;
    private float fontSize;
    private String fontWeight;
    private String fontStyle;
    private float lineHeight;
    private float wordSpacing;
    private float letterSpacing;
    private String color;
    private String strokeColor;
    //position
    private String position;
    private float left;
    private float top;
    private TextPosition tp;

    /**
     * Creates a new style using the specified units for lengths.
     *
     * @param units Units used for lengths (e.g. 'pt')
     */
    public BoxStyle(String units) {
        this.units = new String(units);
        fontFamily = null;
        fontSize = 0;
        fontWeight = null;
        fontStyle = null;
        lineHeight = 0;
        wordSpacing = 0;
        letterSpacing = 0;
        color = null;
        position = null;
        left = 0;
        top = 0;
    }

    public BoxStyle(BoxStyle src) {
        this.units = new String(src.units);
        fontFamily = src.fontFamily == null ? null : new String(src.fontFamily);
        fontSize = src.fontSize;
        fontWeight = src.fontWeight == null ? null : new String(src.fontWeight);
        fontStyle = src.fontStyle == null ? null : new String(src.fontStyle);
        lineHeight = src.lineHeight;
        wordSpacing = src.wordSpacing;
        letterSpacing = src.letterSpacing;
        color = src.color == null ? null : new String(src.color);
        position = src.position == null ? null : new String(src.position);
        left = src.left;
        top = src.top;
        strokeColor = src.strokeColor;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        if (position != null && !position.equals(defaultPosition))
            appendString(ret, "position", position);
        appendLength(ret, "top", top);
        appendLength(ret, "left", left);
        appendLength(ret, "line-height", lineHeight);
        if (fontFamily != null)
            appendString(ret, "font-family", fontFamily);
        if (fontSize != 0)
            appendLength(ret, "font-size", fontSize);
        if (fontWeight != null && !defaultFontWeight.equals(fontWeight))
            appendString(ret, "font-weight", fontWeight);
        if (fontStyle != null && !defaultFontStyle.equals(fontStyle))
            appendString(ret, "font-style", fontStyle);
        if (wordSpacing != 0)
            appendLength(ret, "word-spacing", wordSpacing);
        if (letterSpacing != 0)
            appendLength(ret, "letter-spacing", letterSpacing);
        if (color != null && !defaultColor.equals(color))
            appendString(ret, "color", color);
        if (strokeColor != null && !strokeColor.equals(transparentColor))
            ret.append(createTextStrokeCss(strokeColor));

        return ret.toString();
    }

    private void appendString(StringBuilder s, String propertyName, String value) {
        s.append(propertyName);
        s.append(':');
        s.append(value);
        s.append(';');
    }

    private void appendLength(StringBuilder s, String propertyName, float value) {
        s.append(propertyName);
        s.append(':');
        s.append(formatLength(value));
        s.append(';');
    }

    public String formatLength(float length) {
        //return String.format(Locale.US, "%1.1f%s", length, units); //nice but slow
        return length + units;
    }

    private String createTextStrokeCss(String color) {
        // text shadow fall back for non webkit, gets disabled in default style sheet
        // since can't use @media in inline styles
        String strokeCss = "-webkit-text-stroke: %color% 1px ;" +
                "text-shadow:" +
                "-1px -1px 0 %color%, " +
                "1px -1px 0 %color%," +
                "-1px 1px 0 %color%, " +
                "1px 1px 0 %color%;";

        return strokeCss.replaceAll("%color%", color);
    }

}
