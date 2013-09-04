package javapp;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaPPCommandLine {
	
	private static class TypeHintsCommandLine implements TypeHints {
		private static Pattern collection_pattern = Pattern.compile("^(.*)=(.*)$");
		private static Pattern assocarray_pattern = Pattern.compile("^(.*)=(.*),(.*)$");
		private HashMap collTypes = new HashMap();
		private HashMap dictKeyTypes = new HashMap();
		private HashMap dictValueTypes = new HashMap();
		private Set forcetostrings = new HashSet();
		public TypeHintsCommandLine(String[] args) throws Exception {
			for (int i = 3; i < args.length; i += 2) {
				if (args[i].equals("--collection")) {
					Matcher matcher = collection_pattern.matcher(args[i+1]);
					matcher.find();
					collTypes.put(matcher.group(1), Class.forName(matcher.group(2)));
				} else if (args[i].equals("--forcetostring")) {
					forcetostrings.add(Class.forName(args[i+1]));
				} else if (args[i].equals("--assocarray")) {
					Matcher matcher = assocarray_pattern.matcher(args[i+1]);
					matcher.find();
					dictKeyTypes.put(matcher.group(1), Class.forName(matcher.group(2)));
					dictValueTypes.put(matcher.group(1), Class.forName(matcher.group(3)));
				}
			}
		}
		
		
		public Class getCollectionElementType(Class c, String fieldName) {
			return (Class) collTypes.get(c.getName() + "." + fieldName);
		}
		public boolean forceToString(Class c) {
			for (Iterator it = forcetostrings.iterator(); it.hasNext();) {
				if (((Class)it.next()).isAssignableFrom(c))
					return true;
			}
			return false;
		}
		
		public Class getAssocArrayKeyType(Class c, String fieldName) {
			return (Class) dictKeyTypes.get(c.getName() + "." + fieldName);
		}
		public Class getAssocArrayValueType(Class c, String fieldName) {
			return (Class) dictValueTypes.get(c.getName() + "." + fieldName);
		}
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help") || args[0].equals("-help")) {
			System.err.println("javapp.JavaPPCommandLine");
			System.err.println("Usage:");
			System.err.println("\tjava -cp javapp.jar javapp.JavaPPCommandLine");
			System.err.println("\t\t[targetPackageName] [targetClassName] [targetFileName] [options]");
			System.err.println("Options:");
			System.err.println("\t--class [className]\tGenerate pretty printer for this class.");
			System.err.println("\t--collection [className].[collectionFieldName]=[elementClassName]");
			System.err.println("\t--assocarray [className].[assocArrayFieldName]=[keyClassName],[valueClassName]");
			System.err.println("\t--usestringbuilder [true|false]\tGenerate source code using StringBuilder or StringBuffer (1.4 compat)");
			System.err.println("\t--forcetostring [className]\tUse toString() method instead of generating a pretty printer for the class");
			System.err.println("Example:");
			System.err.println("\tjava -cp javapp.jar javapp.JavaPPCommandLine \\\n\t\tjavapp.test PP test/javapp/test/PP.java \\\n\t\t--class javapp.test.Person \\\n\t\t--collection javapp.test.Person.brothers=String \\\n\t\t--assocarray javapp.test.Person.mappy=String,javapp.test.Person.BornDate");
			System.exit(1);
		}
		String targetPackageName = args[0];
		String targetClassName = args[1];
		String targetFileName = args[2];
		ArrayList cls = new ArrayList();
		boolean useStringBuilder = false;
		for (int i = 3; i < args.length; i += 2) {
			if (args[i].equals("--class")) {
				cls.add(Class.forName(args[i+1]));
			} else if (args[i].equals("--usestringbuilder")) {
				useStringBuilder = Boolean.parseBoolean(args[i+1]);
			}
		}
		
		Class[] classes = new Class[cls.size()];
		cls.toArray(classes);
		TypeHintsCommandLine hints = new TypeHintsCommandLine(args);
		String source = JavaPP.ppsource(
				targetPackageName, targetClassName,
				classes, useStringBuilder,
				hints);
		FileOutputStream fos = new FileOutputStream(targetFileName);
		fos.write(source.getBytes("UTF-8"));
		fos.close();
	}
}
