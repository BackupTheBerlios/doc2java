/*
 * Created on 24-Nov-2003 at 14:48:14
 */
package de.enough.doc2java;

import java.util.ArrayList;


class Method {
	
	private JavaDocComment comment;
	private String definition;
	private String name;
	private ArrayList exceptions;
	private boolean isAbstract;
	private boolean isConstructor;
	private boolean belongsToInterface;
	private ClassModel parent;
	private boolean addToDoTags;
	private String toDoTag;
	
	public Method( String definition, boolean belongsToInterface, boolean isConstructor, ClassModel parent,
				boolean addToDoTags, String toDoTag ) 
	{
		this.definition = definition;
		this.belongsToInterface = belongsToInterface;
		this.isConstructor = isConstructor;
		this.parent = parent;
		this.addToDoTags = addToDoTags;
		this.toDoTag = toDoTag;
		this.isAbstract = ( definition.indexOf(" abstract ") != -1 );
		try {
			int endNameIndex = definition.indexOf( '(' );
			int startNameIndex = definition.substring(0, endNameIndex ).trim().lastIndexOf( ' ');
			this.name = definition.substring( startNameIndex + 1, endNameIndex ).trim();
		} catch (StringIndexOutOfBoundsException e) {
			System.err.println("unable to parse method [" + definition + "] of class [" + parent.getClassName() + "].");
			throw e;
		}
	}
	
	public void setComment( JavaDocComment comment ) {
		this.comment = comment;
	}

	public void addException( String line ) {
		if (this.exceptions == null ){ 
			this.exceptions = new ArrayList();
		}
		this.exceptions.add( line );
	}
	
	public String[] toStringArray( int numberOfTabs ) {
		String tabs = "";
		for (int i = 0; i < numberOfTabs; i++) {
			tabs += '\t';
		}
		ArrayList lines = new ArrayList();
		if (this.comment != null) {
			String[] comments = this.comment.toStringArray( numberOfTabs );
			for (int i = 0; i < comments.length; i++) {
				lines.add( comments[i]);
			}
		}
		// method definition:
		if (this.belongsToInterface || this.isAbstract) {
			lines.add( tabs + this.definition + ";");
		} else {
			lines.add( tabs + this.definition);
			// method body, at least when this method is not abstract or belongs to an interface:
			lines.add( tabs + "{" );
			boolean addToDo = this.addToDoTags;
			if ( (this.definition.indexOf( " void ") == -1) && (!this.isConstructor)) {
				// method has a return value
				if (this.name.startsWith( "get") && (this.comment.getParameters() == null)) {
					// this is a typical get-method for one field:
					String fieldName = getFieldName();
					lines.add( tabs + "\treturn this." + fieldName + ";");
					addToDo = false;
					// add the field to the class-model:
					this.parent.addGeneratedVariable( fieldName, getFieldType() );
				} else {
					String defStart = this.definition.substring( 0, this.definition.indexOf ('('));
					String returnValue = null;
					if ( (defStart.indexOf( " int ") != -1) || (defStart.indexOf( " long ") != -1) 
					|| (defStart.indexOf( " double ") != -1) || (defStart.indexOf( " float ") != -1)) {
						returnValue = "0;"; 
					} else if (defStart.indexOf( " byte ") != -1) {
						returnValue = "(byte)0;";
					} else if (defStart.indexOf( " char ") != -1) {
						returnValue = "(char)0;";
					} else if (defStart.indexOf( " boolean ") != -1) {
						returnValue = "false;";
					} else {
						returnValue = "null;";
					}
					lines.add( tabs + "\treturn " + returnValue );
				}
			} else if ( (this.name.startsWith( "set") ) && (this.comment.getParameters() != null) && (this.comment.getParameters().size() == 1) ) {
				String fieldName = getFieldName();
				String paramName = ((String) this.comment.getParameters().get(0)).trim();
				paramName = paramName.substring(0, paramName.indexOf( ' '));
				lines.add( tabs +"\tthis." + fieldName + " = " + paramName + ";");
				addToDo = false;
				// add the field to the class-model:
				this.parent.addGeneratedVariable( fieldName, getFieldType() );
			}
			if (addToDo) {
				lines.add( tabs + "\t" +this.toDoTag + " implement " +  this.name );
			}
			lines.add( tabs + "}");
		}
		return (String[]) lines.toArray( new String[ lines.size() ] );
	}
	
	/**
	 * retrieves the type of a setter- or getter-method.
	 * @return the type of this setter- or getter-method
	 */
	private String getFieldType() {
		if (this.name.startsWith( "get")) {
			int endPos = this.definition.indexOf( this.name );
			String type = this.definition.substring( 0, endPos ).trim();
			int spacePos = type.lastIndexOf( ' ');
			type = type.substring( spacePos + 1);
			return type;
		} else {
			int startPos = this.definition.indexOf( '(' );
			String type = this.definition.substring(startPos + 1).trim();
			int spacePos = type.indexOf( ' ');
			type = type.substring( 0, spacePos );
			return type;
		}
	}

	private String getFieldName() {
		String fieldName = this.name.substring( 3 );
		fieldName =  fieldName.substring( 0, 1 ).toLowerCase() + fieldName.substring( 1 );
		return fieldName;
	}

}