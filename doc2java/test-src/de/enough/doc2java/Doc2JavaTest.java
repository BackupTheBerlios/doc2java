/*
 * Created on 26-Nov-2003 at 12:31:07
 */
package de.enough.doc2java;

import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * <p></p>
 * <p></p>
 * <p>copyright enough software 2003</p>
 * <pre>
 *    history
 *       26-Nov-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Doc2JavaTest extends TestCase {

	public Doc2JavaTest(String name) {
		super(name);
	}
	
	public void testDataSource() 
	throws FileNotFoundException, IOException 
	{
		String directory = "/home/rob/j2me/Nokia/Devices/Series_60_MIDP_Concept_SDK_Beta_0_3_Nokia_edition/docs/midp/javax/microedition/rms";
		String fileName = "RecordStore.html";
		// read file:
		StringList fileLines = Doc2Java.readFile( directory + "/" + fileName ); 
		// parse file:
		ClassModel model = Doc2Java.parseClassDefinition(directory, fileName, fileLines);
		Doc2Java.parseFields(fileLines, model, false);
		Doc2Java.parseConstructors(fileLines, model);
		Doc2Java.parseMethods(fileLines, model); 
		//Doc2Java.parseJavaDoc( directory, fileName, fileLines );
		// print model:
		//printModel( model );
		// test:
		String[] lines = model.toStringArray();
		boolean failure = false;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.indexOf( "//TODO implement getName") != -1) {
				failure = true;
			} else if ( line.indexOf( "public String getName" ) != -1) {
				System.out.println("getName at line [" + i +"]:");
				System.out.println(line);
				System.out.println(lines[i+1]);
				System.out.println(lines[i+2]);
				System.out.println(lines[i+3]);
				System.out.println(lines[i+4]);
				System.out.println();
			}
		}
		if (failure) {
			fail("getName failed.");
		}
		printModel( model );
	}

	/**
	 * @param model
	 */
	private void printModel(ClassModel model) {
		String[] lines = model.toStringArray();
		for (int i = 0; i < lines.length; i++) {
			System.out.println( lines[i] );
		}
	}

}
