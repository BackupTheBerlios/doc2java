/*
 * Created on 19-Nov-2003 at 17:40:32
 */
package de.enough.doc2java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * <p>Parses javadoc files and creates java sources based on the found descriptions.</p>
 * <p>This is the main controller and accepts several command
 * line arguments, which will be printed out when getting the "--help" command. 
 * </p>
 * <p>copyright enough software 2003</p>
 * <pre>
 *    history
 *       19-Nov-2003 (rob) creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public final class Doc2Java extends Object {
	
	private static final HashMap IGNORE_LIST = new HashMap();
	private static final HashMap INVALID_DOC_FILES = new HashMap();
	static {
		IGNORE_LIST.put( "<DL>", Boolean.TRUE );
		IGNORE_LIST.put( "</DL>", Boolean.TRUE );
		IGNORE_LIST.put( "</DD>", Boolean.TRUE );
		IGNORE_LIST.put( "<DD><DL>", Boolean.TRUE );
		INVALID_DOC_FILES.put( "index.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "index-all.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "allclasses-frame.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "allclasses-noframe.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "deprecated-list.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "help-doc.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "overview-frame.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "overview-noframe.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "overview-summary.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "overview-tree.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "packages.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "package-use.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "package-frame.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "package-noframe.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "package-tree.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "package-summary.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "serialized-form.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "copyright-notice.html", Boolean.TRUE );
		INVALID_DOC_FILES.put( "constant-values.html",  Boolean.TRUE );

	}
	
	static int numberOfFiles;
	static int numberOfErrors;
	static boolean addToDoTags = true;
	static String toDoTag = "//TODO";
	static boolean isVerbose;

	/**
	 * Checks the command line arguments and controls the program flow.
	 * @param args the command line arguments
	 * @throws IOException when a java-doc file could not be read.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			printHelp();
			System.exit( 0 );
		}
		try {
			long startTime = System.currentTimeMillis();
			Arguments arguments = new Arguments( args, new String[]{ "-R", "-notodo", "-h", "-?", "-v" }, new String[]{ "--recursive", "--notodo", "--help", "--verbose" },
				new String[]{"-o", "-todotag"}, new String[]{"--output", "--todotag" } );
			if (arguments.hasFlag("-h", "--help") || arguments.hasFlag("-?", "-?")) {
				printHelp();
				System.exit( 0 );
			}
			boolean scanSubDirectories = arguments.hasFlag( "-R", "--recursive" );
			String outputDirectory = arguments.getParameter( "-o", "--output");
			if (outputDirectory == null) {
				outputDirectory = new File(".").getAbsolutePath();
			}
			if (arguments.hasFlag( "-notodo", "--notodo")) {
				Doc2Java.addToDoTags = false;
			}
			isVerbose = arguments.hasFlag( "-v", "--verbose" );
			String tag = arguments.getParameter("-todotag", "--todotag"); 
			if ( tag != null) {
				Doc2Java.toDoTag = tag; 
			}
			String[] files = arguments.getArguments();
			if (files.length == 0) {
				System.err.println("no files have been defined.");
				printHelp();
				System.exit( 1 );
			}
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i];
				File file = new File( fileName );
				if (file.isDirectory()) {
					// parse all files from this directory:
					processDirectory( file, scanSubDirectories, outputDirectory );
				} else if (file.exists()) {
					processFile( file.getParent(), file.getName(), outputDirectory);
				} else {
					System.err.println("unable to process file [" + fileName + "] - file not found.");
				}
			}
			long timeNeeded = ( System.currentTimeMillis() - startTime );
			System.out.print("processed [" + Doc2Java.numberOfFiles + "] files in [" );
			if (timeNeeded < 10 * 1000 ) {
				System.out.println( timeNeeded+ "] ms.");
			} else {
				System.out.println( timeNeeded/1000 + "] seconds.");
			}
			if (numberOfErrors > 0) {
				System.out.println("encountered [" + Doc2Java.numberOfErrors + "] errors.");
			}
		} catch (IllegalArgumentException e) {
			System.err.println( e.toString() );
			printHelp();
		}
	}	


	private static void processDirectory(File directory, boolean scanSubDirectories, String outputDirectory) 
	throws IOException 
	{
		String dirName = directory.getName();
		if (isVerbose) {
			System.out.println("processing directory " + dirName );
		}
		if ( (dirName.indexOf("class-use") != -1) || (dirName.indexOf("doc-files") != -1 ) ) {
			System.out.println("skipping directory [" + directory.getAbsolutePath() + "].");
			return;
		}
		File[] includedFiles = directory.listFiles();
		for (int i = 0; i < includedFiles.length; i++) {
			File file = includedFiles[i];
			if (file.isDirectory()) {
				if (scanSubDirectories) {
					processDirectory(file, scanSubDirectories, outputDirectory); 
				}
			} else if (file.getName().indexOf( ".html") != -1) {
				if (INVALID_DOC_FILES.get( file.getName() ) == null) {
					// okay, this seems to be a valid javadoc-file.
					try {
						processFile( file.getParent(), file.getName(), outputDirectory);
					} catch (IllegalStateException e) {
						System.err.println("unable to parse [" + file.getAbsolutePath() + "]:\n" + e.toString() );
						e.printStackTrace();
						Doc2Java.numberOfErrors++;
					}
				}
			}
		}
	}


	/**
	 * Prints a short help text on screen.
	 */
	private static void printHelp() {
		System.out.println();
		System.out.println("Doc2Java parses JavaDoc-files and generates Java-source code from it.");
		System.out.println("usage: java -jar doc2java.jar [options] file");
		System.out.println("  Options:");
		System.out.println("    -R/--recursive: scan subdirectories.");
		System.out.println("    -o/--output:     the directory where to write the java files, defaults to the current directory.");
		System.out.println("    --notodo:        write no todo tags into the files.");
		System.out.println("    --todotag:       use the given to-do-tag, defaults to //TODO. Example: [--todotag \"//TODO rob\"].");
		System.out.println("    -v/--verbose:    print additional information.");
		System.out.println("  Parameters:");
		System.out.println("     file:               javadoc-file(s) or directory containing javadoc-files.");
		System.out.println("  Example:");
		System.out.println( "    java -jar doc2java.jar -R --output /home/me/java /home/me/doc");
		System.out.println();
	}


	protected static final void processFile(String directory, String fileName, String outputDirectory )
	throws IOException 
	{
		String filePath = directory + "/" + fileName;
		System.out.println("processing javadoc file [" + fileName+ "]...");
		// read file:
		StringList fileLines = readFile( filePath ); 
		// parse file:
		ClassModel model = parseJavaDoc( directory, fileName, fileLines );
		// write java-file:
		writeJavaFile( model, outputDirectory );
		if (isVerbose) {
			System.out.println("successfully processed javadoc file [" + fileName + "].");
		}
		// processing was successful:
		Doc2Java.numberOfFiles++;
	}
	



	
	protected static StringList readFile(String filePath) 
	throws FileNotFoundException, IOException 
	{
		String[] lines  = FileUtil.readTextFile(  filePath );
		return new StringList( lines );
	}


	private static final void writeJavaFile(ClassModel model, String outputDirectory ) 
	throws IOException 
	{
		String[] lines = model.toStringArray();
		String packageName = model.getPackageName();
		if (packageName != null) {
			outputDirectory += "/" + TextUtil.replace(packageName, ".", "/");
		}
		File file = new File( outputDirectory + "/" + model.getClassName() + ".java" );
		if (isVerbose) {
			System.out.println("writing Java code to " + file.getAbsolutePath() );
		}
		FileUtil.writeTextFile(file, lines);
	}

	protected static final ClassModel parseJavaDoc(String directory, String fileName, StringList lines) {
		if (isVerbose) {
			System.out.println("parsing class definition");
		}
		ClassModel model = parseClassDefinition( directory, fileName, lines );
		if (isVerbose) {
			System.out.println("parsing fields starting at line " + (lines.getCurrentIndex()) );
		}
		parseFields( lines, model, false );
		if (isVerbose) {
			System.out.println("parsing constructors starting at line " + (lines.getCurrentIndex()) );
		}
		parseConstructors( lines, model );
		// TODO rob: parseInnerClasses( lines, model );
		if (isVerbose) {
			System.out.println("parsing methods starting at line " + (lines.getCurrentIndex()) );
		}
		parseMethods( lines, model );
		return model;
	}
	
	

	protected static final ClassModel parseClassDefinition(String directory, String fileName, StringList lines) {
		boolean startOfDataFound = false;
		String line;
		while ( (line = lines.getNext() ) != null) {
			if (line.indexOf("= START OF CLASS DATA =") != -1) {
				lines.setStartIndex( lines.getCurrentIndex() + 1);
				startOfDataFound = true;
				break;
			}
		}
		if (!startOfDataFound) {
			throw new IllegalStateException("unable to find the start of the documentation defined by [START OF CLASS DATA].");
		}
		// found start:
		String className = fileName.substring( 0, fileName.indexOf('.') );
		// try to find package declaration:
		String packageName = null;
		while ( (line = lines.getNext() ) != null) {
			if ( (line.startsWith("<Font") )  || (line.startsWith("<FONT")) ){
				packageName = removeTags( lines.getNext().trim() );
				lines.setStartIndex( lines.getCurrentIndex() + 1);
				break;
			}
		}
		// try to find class or interface definition:
		String classDefinition = null;
		while ( (line = lines.getNext() ) != null) {
			if (line.startsWith ("<DT>") &&  ((line.indexOf( " class ") != -1) || (line.indexOf(" interface ") != -1)) ) {
				classDefinition = removeTags( line );
				//System.out.println("found class-definition [" + classDefinition + "] in line [" + line +"].");
				lines.setStartIndex(  lines.getCurrentIndex()  + 1);
				break;
			}
		}
		if (classDefinition == null) {
			System.out.println("please report this error with the given file to robert@enough.de:");
			throw new IllegalStateException("unable to parse class or interface definition from file [" + fileName + "].");
		}
		boolean isInterface = false;
		isInterface = (classDefinition.indexOf( " interface ") != -1);
		ClassModel model = new ClassModel( className, classDefinition, packageName, isInterface );
		// parse javadoc:
		JavaDocComment comment = new JavaDocComment();
		boolean firstLineSet = false;
		while ( (line = lines.getNext() ) != null) {
			if (line.indexOf("= INNER CLASS SUMMARY =") != -1) {
				// documentation is finished
				lines.setStartIndex(  lines.getCurrentIndex()  + 1);
				break;
			}
			line = line.trim();
			if ( ( !"<P>".equals(line) ) &&  (IGNORE_LIST.get( line) == null ) ) {
				if (line.startsWith("<DT>")) { 
					// this could be a since, see or author tag:
					int sincePos = line.indexOf( "Since:");
					if (sincePos != -1) {
						String since = removeTags( line.substring( sincePos + 6).trim() );
						comment.setSince( since );
					}
					// TODO rob: check for author and see tags
				} else {
					comment.addExplanation(line);
					if (!firstLineSet) {
						line = removeTags( line );
						int dotPos = line.indexOf('.');
						if (dotPos != -1) {
							comment.setIntroduction( line.substring(0, dotPos + 1));
						}
						firstLineSet = true;
					}
				}
			}
		}
		model.setClassComments(comment);
		return model;
	}

	protected static final void parseFields(StringList lines, ClassModel model, boolean debug) {
		// search for start of the field details:
		String line;
		boolean inFieldDetails = false;
		while ( (line = lines.getNext()) != null)  {
			if (!inFieldDetails) {
				if (line.indexOf("= FIELD DETAIL =") != -1) {
					if (isVerbose) {
						System.out.println("Fields start at line " + lines.getCurrentIndex() );
					}
					inFieldDetails = true;
				} else if (debug) {
					System.out.println("parseFields: skipping [" + line + "].");
				}
			} else {
				if ("<PRE>".equals(line) ) {
					if (isVerbose) {
						System.out.println("Field starts at line " + lines.getCurrentIndex() + ": " + line);
					}
					// a new field has been found.
					String definition = removeTags( lines.getNext() ).trim();
					Variable variable = new Variable( definition);
					boolean valueSet = false;
					//System.out.println("found variable [" + definition + "].");
					JavaDocComment comment = new JavaDocComment();
					variable.setComment( comment );
					while ( (line = lines.getNext() ) != null  ) {
						if ( ("<HR>".equals(line))  || ( line.indexOf("= CONSTRUCTOR DETAIL =") != -1) || (line.indexOf("= METHOD DETAIL =") != -1) || (line.indexOf("= END OF CLASS DATA =") != -1)) {
							if (isVerbose) {
								System.out.println("Field ends at line " + lines.getCurrentIndex() + ": " + line );
							}
							break;
						}
						 if ( IGNORE_LIST.get( line ) == null ) {
						 	int sincePos = line.indexOf("Since:");
						 	int seeAlsoPos = line.indexOf( "See also:");
						 	if (sincePos != -1) {
						 		String since = removeTags( line.substring( sincePos + 7));
								comment.setSince( since );
								// maybe the start of the line is a comment:
								int dtPos = line.indexOf("<DT>");
								if ( (dtPos != -1) && (dtPos < sincePos ) ) {
									comment.addExplanation( line.substring(0, dtPos ));								
								}
						 	} else if (seeAlsoPos != -1) {
								String seeAlso = removeTags( line.substring( seeAlsoPos + 9));
								comment.addSeeAlso( seeAlso );
								// maybe the start of the line is a comment:
								int dtPos = line.indexOf("<DT>");
								if ( (dtPos != -1) && (dtPos < seeAlsoPos ) ) {
									comment.addExplanation( line.substring(0, dtPos ));								
								}
						 	} else {
						 		// normal comment
								if (line.startsWith("<DD>")) {
									line = line.substring( 4 );
								}
								int ddPos = line.indexOf( "<DD>");
								if (ddPos != -1) {
									line = line.substring(0, ddPos );
								}
								line = line.trim();
								//System.out.println("adding comment-line [" + line + "].");
								comment.addExplanation(line);
								// check for value:
								// TODO check extracted value against the type of this field!
								if (!valueSet) {
									int valuePos = line.indexOf ("Value");
									if (valuePos == -1) {
										valuePos = line.indexOf( "value");
										if (valuePos == line.indexOf("values")) {
											valuePos = -1;
										}
									}
									if (valuePos != -1) {
										if (line.indexOf("value of ") != -1) {
											valuePos += 4;
										}  else if (line.indexOf("value is ") != -1) {
											valuePos += 4;
										} else if (line.indexOf("value =") != -1) {
											valuePos += 2;
										}
 
										String fieldName = definition.substring( definition.lastIndexOf( ' ') + 1).trim();
										if (line.indexOf( " of " + fieldName + " is ") != -1) {
											valuePos += fieldName.length() + 4;
										}
										String value = line.substring(valuePos + 5).trim();
										int stopPos = value.indexOf(' ');
										if (stopPos == -1) {
											stopPos = value.indexOf('.');
										}
										if (stopPos != -1) {
											value = value.substring(0, stopPos);
										}
										value = removeTags( value ).trim();
										//System.out.println("found value [" + value + "].");
										variable.setValue(value); 
										if (isVerbose) {
											System.out.println("found field value: " + value );
										}
										valueSet = true;
									} 
								} // when no value has been set so far
						 	}
						 }
					}
					if (isVerbose) {
						System.out.println("found variable " + variable);
					}
					model.addVariable(variable);
				} // end of field start
				if (line.indexOf( "= CONSTRUCTOR DETAIL =") != -1 || (line.indexOf("= METHOD DETAIL =") != -1) || (line.indexOf("= END OF CLASS DATA =") != -1)) {
					if (isVerbose) {
						System.out.println("end of fields encountered at line " + lines.getCurrentIndex());
					}
					lines.setStartIndex( lines.getCurrentIndex() + 1);
					break;
				} 
			} // within field details
		} // outer while loop
	}


	protected static final void parseConstructors( StringList lines, ClassModel model) {
		parseMethods( lines, model, "= METHOD DETAIL =", true);
	}

	protected static final void parseMethods(StringList lines, ClassModel model) {
		parseMethods( lines, model, "= END OF CLASS DATA =", false);
	}

	protected static final void parseMethods(StringList lines, ClassModel model, String endTag, boolean isConstructor ) {
		String line;
		int numberOfMethods = 0;
		boolean exit = false;
		while ( (line = lines.getNext()) != null)  {
			if ("<PRE>".equals(line) ) {
				// a new method has been found.
				if (isVerbose) {
					System.out.println("found method at line " + lines.getCurrentIndex() );
				}
				String definition = TextUtil.replace( lines.getNext(), "&nbsp;", " " ).trim();
				while ( definition.indexOf("</PRE>") == -1) {
					definition += " " + TextUtil.replace( lines.getNext(), "&nbsp;", " " ).trim();
				}
				definition = removeTags( definition ).trim();
				//System.out.println("found variable [" + definition + "].");
				Method method = new Method( definition, model.isInterface(), isConstructor, model, addToDoTags, toDoTag );
				JavaDocComment comment = new JavaDocComment();
				method.setComment( comment );
				while ( (line = lines.getNext() ) != null  ) {
					if ( "<HR>".equals(line)) { 
						break;
					}
					if ( line.indexOf(endTag) != -1) {
						exit = true;
						break;
					}
					if ( IGNORE_LIST.get( line ) == null ) {
						if (line.startsWith( "<DT>")) {
							StringBuffer tagBuffer = new StringBuffer();
							tagBuffer.append( line.substring( 4) )
								     	  .append( ' ');
							while ( (line = lines.getNext()) != null) {
								if ( ("<HR>".equals(line))  || ( line.indexOf(endTag) != -1)) {
									break;
								}
								if ( IGNORE_LIST.get( line ) == null ) {
									tagBuffer.append( line.trim() )
											 	  .append( ' ');
								}
							}
							String[] tagArray = TextUtil.toStringArray( tagBuffer.toString(), "<DT>");
							for (int i = 0; i < tagArray.length; i++) {
								String tagLine = tagArray[i]; //removeTags( tagArray[i] );
								int startIndex;
								if ( (startIndex = tagLine.indexOf( "Returns:")  ) != -1) {
									String returnText = removeTags( tagLine.substring( startIndex + 8 ) );
									comment.setReturnText( returnText );
								} else if ((startIndex = tagLine.indexOf( "Since:")  ) != -1 ) {
									String sinceText  = removeTags( tagLine.substring( startIndex + 6 ) );
									comment.setSince( sinceText );
								} else {
									// either parameters, see also,  or throws declaration:
									boolean isParams = (tagLine.indexOf( "Parameters:") != -1);
									boolean isThrows =  (tagLine.indexOf( "Throws:") != -1);
									String[] subLines = TextUtil.toStringArray( tagLine, "<DD>" );
									for (int j = 1; j < subLines.length; j++) {
										String subLine = removeTags( subLines[j] ).trim();
										if (isParams) {
											comment.addParameter(subLine);
										} else if (isThrows) {
											comment.addException(subLine);
										} else {
											comment.addSeeAlso( subLine );
										}
									}
									//System.out.println( i +"=[" + tagLine + "].");
								}
							}
						} else {
							// normal comment
							if (line.startsWith("<DD>")) {
								line = line.substring( 4 );
							}
							int ddPos = line.indexOf( "<DD>");
							if (ddPos != -1) {
								line = line.substring(0, ddPos );
							}
							line = line.trim();
							//System.out.println("adding comment-line [" + line + "].");
							comment.addExplanation(line);
						}
					 }
					if ( exit || ("<HR>".equals(line))  || ( line.indexOf(endTag) != -1)) {
						break;
					}
				} // loop within variable definition
				if (isVerbose) {
					System.out.println("found method: " + method);
				}
				model.addMethod(method);
				numberOfMethods++;
				if (exit || line.indexOf( endTag ) != -1) {
					lines.setStartIndex( lines.getCurrentIndex() + 1);
					break;
				}
			} // if variable definition has been found
			if (exit || line.indexOf( endTag ) != -1) {
				lines.setStartIndex( lines.getCurrentIndex() + 1);
				break;
			}
		} // while there are more lines
		/*
		if (isConstructor) {
			System.out.println("found [" + numberOfMethods + "] constructors." );
		} else {
			System.out.println("found [" + numberOfMethods + "] methods." );
		}
		*/
	}


	/**
	 * removes all tag definitions found from the given String.
	 * @param string - the String which should cleaned from all tag definitions.
	 * @return the cleaned string
	 */
	private static final String removeTags(String string) {
		StringBuffer clean = new StringBuffer( string.length() );
		char[] chars = string.toCharArray();
		boolean append = true;
		char lastAddedChar = ' ';
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (append) {
				if ( c == '<') {
					append = false;
				} else {
					clean.append( c );
					lastAddedChar = c;
				}
			} else {
				if ( c == '>' ) {
					append = true;
					if (lastAddedChar != ' ') {
						if (i != ( chars.length - 1) ) {
							char nextChar = chars[ i + 1];
							if ( Character.isLetterOrDigit(nextChar)) {
								clean.append( ' ');
								lastAddedChar = ' ';
							}
						}
					}
				}
			}
		}
		return clean.toString();
	}




}
