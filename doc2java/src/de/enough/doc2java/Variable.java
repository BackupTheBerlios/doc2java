/*
 * Created on 24-Nov-2003 at 14:49:40
 */
package de.enough.doc2java;


class Variable {
	private JavaDocComment comment;
	private String definition;
	private String value;
	
	public Variable( String definition ) {
		this.definition = definition;
	}
	
	public void setComment( JavaDocComment comment ) {
		this.comment = comment;
	}
	
	public void setValue( String value ) {
		if (this.definition.indexOf("String") != -1 && !(value.startsWith("\"")) ) {
			value = '"' + value + '"';
		}
		this.value = value;
	}
	
	public String[] toStringArray( int numberOfTabs ) {
		String tabs = "";
		for (int i = 0; i < numberOfTabs; i++) {
			tabs += '\t';
		}
		String variable = tabs + this.definition;
		if ( this.value == null ) {
			variable += ';';
		} else {
			variable += " = " + this.value + ';';
		}
		String[] lines = null;
		if (this.comment != null) {
			String[] comments = this.comment.toStringArray( numberOfTabs );
			lines = new String[ comments.length + 1 ];
			System.arraycopy(comments, 0,  lines, 0, comments.length );
			lines[ comments.length ] = variable;
		} else {
			lines = new String[] { variable };
		}
		return lines;
	}
}