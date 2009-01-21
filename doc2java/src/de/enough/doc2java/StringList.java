/*
 * Created on 24-Nov-2003 at 14:48:58
 */
package de.enough.doc2java;


class StringList {
	private String[] lines;
	private int startIndex;
	private int currentIndex;
	
	public StringList( String[] lines ) {
		this.lines = lines;
	}
	
	/**
	 * @return
	 */
	public int getCurrentIndex() {
		return this.currentIndex;
	}

	public void setStartIndex( int startIndex ) {
		this.startIndex = startIndex;
		this.currentIndex = startIndex;
	}
	
	public void reset() {
		this.currentIndex = this.startIndex;
	}
	
	public String getNext() {
		if (this.currentIndex == this.lines.length ) {
			this.currentIndex = this.startIndex;
			return null;
		} else {
			String line = this.lines[ this.currentIndex ]; 
			this.currentIndex ++;
			return line;
		}
	}
	
	public int getStartIndex() {
		return this.startIndex;
	}
}