/*
 * Created on 24-Nov-2003 at 14:46:14
 */
package de.enough.doc2java;

import java.util.ArrayList;


class JavaDocComment {
	private String introduction;
	private ArrayList explanations;
	private ArrayList parameters;
	private String returnText;
	private ArrayList exceptions;
	private ArrayList seeAlso;
	private String since;
	
	public JavaDocComment() {
		// create intial empty comment
	}
	
	public void setIntroduction( String introduction ) {
		this.introduction = introduction;
	}
	
	public void addExplanation( String line ) {
		if (this.explanations == null ) {
			this.explanations = new ArrayList();
		}
		this.explanations.add( line );
	}
	
	public void addParameter( String line ) {
		if (this.parameters == null) {
			this.parameters = new ArrayList();
		}
		this.parameters.add( line );
	}
	
	public void setReturnText( String returnText ) {
		this.returnText = returnText;
	}
	
	public void addException( String line ) {
		if (this.exceptions == null ){ 
			this.exceptions = new ArrayList();
		}
		this.exceptions.add( line );
	}
	
	public void addSeeAlso( String line ) {
		if (this.seeAlso == null ){ 
			this.seeAlso = new ArrayList();
		}
		this.seeAlso.add( line );
	}
	
	public void setSince( String since ) {
		this.since = since;
	}
	
	public String[] toStringArray( int numberOfTabs ) {
		ArrayList lines = new ArrayList();
		String tabs = "";
		for (int i = 0; i < numberOfTabs; i++) {
			tabs += '\t';
		}
		String commentStart = tabs + " * ";
		lines.add( tabs + "/**");
		if (this.introduction != null) {
			lines.add( commentStart + this.introduction );
			lines.add( commentStart );
		}
		if (this.explanations != null) {
			for (int i = 0; i < this.explanations.size(); i++) {
				lines.add( commentStart + this.explanations.get(i) );
			}
			lines.add( commentStart );
		}
		if (this.parameters != null) {
			for (int i = 0; i < this.parameters.size(); i++) {
				lines.add( commentStart + "@param " + this.parameters.get(i) );
			}
		}
		if (this.returnText != null) {
			lines.add( commentStart + "@return " + this.returnText );
		}
		if (this.exceptions != null) {
			for (int i = 0; i < this.exceptions.size(); i++) {
				lines.add( commentStart +"@throws " + this.exceptions.get(i) );
			}
		}
		if (this.seeAlso != null) {
			for (int i = 0; i < this.seeAlso.size(); i++) {
				lines.add( commentStart +"@see " + this.seeAlso.get(i) );
			}
		}
		if (this.since != null) {
			lines.add( commentStart + "@since " + this.since );
		}
		lines.add( tabs + " */");
		return (String[]) lines.toArray( new String[ lines.size() ]);
	}

	public ArrayList getParameters() {
		return this.parameters;
	}
}