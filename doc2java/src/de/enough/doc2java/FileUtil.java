/*
 * Created on 24-Nov-2003 at 14:05:22
 */
package de.enough.doc2java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * <p>Reads and writes text files.</p>
 * <p></p>
 * <p>copyright enough software 2003</p>
 * <pre>
 *    history
 *       24-Nov-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class FileUtil {

	/**
	 * Reads a textfile.
	 * 
	 * @param fileName (String) path and name of the file
	 * @return all lines of the file
	 * @throws FileNotFoundException when the specified file does not exist
	 * @throws IOException when the file could not be read
	 */
	public static String[] readTextFile( String fileName )
	throws FileNotFoundException, IOException 
	{
		return readTextFile( new File( fileName ));
	}
    

	/**
	 * Reads a textfile.
	 * 
	 * @param file (File) the text file
	 * @return all lines of the file
	 * @throws FileNotFoundException when the specified file does not exist
	 * @throws IOException when the file could not be read
	 */
	public static String[] readTextFile( File file )
	throws FileNotFoundException, IOException 
	{
		ArrayList list = new ArrayList();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		while ((line = in.readLine()) != null) {
			list.add(line);
		}
		in.close();
		return (String[]) list.toArray( new String[ list.size() ] );
	}
    
    /**
     * Writes a text file.
     * 
     * @param fileName (String) the name of the file
     * @param lines (String[]) all lines which should be written to the specified file
     * @throws IOException when the file could not be written
     */
	public static void writeTextFile( String fileName, String[] lines)
	throws  IOException
	{
		writeTextFile( new File( fileName), lines );
	}

	/**
	 * Writes a text file.
	 * 
	 * @param file (File) the file
	 * @param lines (String[]) all lines which should be written to the specified file
	 * @throws IOException when the file could not be written
	 */
	public static void writeTextFile( File file, String[] lines)
	throws  IOException
	{
		// create necessary directories:
		file.getParentFile().mkdirs();
		// write file:
		PrintWriter out = new PrintWriter(
				new FileWriter(file));
		for (int i = 0; i < lines.length; i++) {
			out.println(lines[i]);
		}
		out.flush();
		out.close();
	}


}
