/*
 * Created on 24-Nov-2003 at 14:38:43
 */
package de.enough.doc2java;

import java.util.ArrayList;

/**
 * <p>provides a simple replace mechanism for Strings.</p>
 * <p></p>
 * <p>copyright enough software 2003</p>
 * <pre>
 *    history
 *       24-Nov-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class TextUtil {

	public static final String replace(String input, String search, String replacement) {
		if (input == null || search == null || replacement == null) {
			throw new IllegalArgumentException( "given input parameters must not be null.");
		}
		// start position for searching:
		int startPos = 0;
		// gefundene Position des zu ersetzenden Strings toReplace
		int pos;
		// soviel muss zwischen jedem gefundenen Ergebnis zur gef. Position hinzuaddiert werden:
		int add = replacement.length() - search.length();
		// soviel muss insgesamt zu jeder gefundener Pos. hinzuaddiert werden:
		int totalAdd = 0;
		int replaceLength = search.length();
		StringBuffer replace = new StringBuffer(input);
		while ((pos = input.indexOf(search, startPos)) != -1) {
			replace.replace(pos + totalAdd, pos + totalAdd + replaceLength, replacement);
			totalAdd += add;
			startPos = pos + 1;
		}
		return replace.toString();
	}

	public static final String[] toStringArray(String input, String delimiter) {
		ArrayList lines = new ArrayList();
		int delimiterLength = delimiter.length();
		int cutPos = 0;
		while ((cutPos = input.indexOf(delimiter)) != -1) {
			lines.add( input.substring(0, cutPos).trim() );
			input = input.substring(cutPos + delimiterLength );
		}
		lines.add( input.trim() );
		return (String[]) lines.toArray( new String[ lines.size() ] );
   }

}
