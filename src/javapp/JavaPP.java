package javapp;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JavaPP {
	
	public static String ppsource(
			String packageName,
			String className,
			Class[] classes,
			boolean useStringBuilder,
			TypeHints hints) {
		Outputter out = new Outputter();
		out.println("package "+packageName+";");
		out.printlni("public class "+className+" {"); {
			out.printlni("private static void indent(###outputter### out, int indentFactor) {"); {
				out.printlni("for (int i = 0; i < indentFactor; i++) {"); {
					out.println("out.append('\\t');");
				}
				out.printlno("}");	
			}
			out.printlno("}");
			WorkQueue workQueue = new WorkQueue();
			for (int i = 0; i < classes.length; i++) {
				workQueue.enqueue(classes[i]);
			}
			Class c;
			while ((c = workQueue.poll()) != null) {
				printComplexType(out, ClassDef.get(c), workQueue, hints);
			}
			while ((c = workQueue.pollCollection()) != null) {
				printCollection(out, ClassDef.get(c), workQueue, hints);
			}
			Class[] infos;
			while ((infos = workQueue.pollMapdict()) != null) {
				printAssociativeArray(
					out,
					ClassDef.get(infos[0]),
					ClassDef.get(infos[1]),
					ClassDef.get(infos[2]),
					workQueue,
					hints);
			}
		}
		out.printlno("}");
		return out
			.toString()
			.replaceAll(
				"###outputter###",
				useStringBuilder
					? "StringBuilder"
					: "StringBuffer"
		);
	}
	
	private static void printAssociativeArray(Outputter out, ClassDef keyType, ClassDef valueType, ClassDef mapdictType, WorkQueue workQueue, TypeHints hints) {
		out.printlni("public static void pp("+mapdictType.name+" assocArr, "+keyType.name+" nullKeyRef, "+valueType.name+" nullValueRef, ###outputter### out, int indentFactor) {"); {
			out.println("out.append(\"# "+mapdictType.name+" ("+keyType.name+", "+valueType.name+") {\\n\");");
			if (mapdictType.isMap) {
				out.printlni("for (java.util.Iterator it = assocArr.entrySet().iterator(); it.hasNext(); ) {"); {
					out.println("java.util.Map.Entry entry = (java.util.Map.Entry) it.next();");
					out.println(keyType.name+" key = ("+keyType.name+") entry.getKey();");
					out.println(valueType.name+" value = ("+valueType.name+") entry.getValue();");
				}
			} else {
				out.printlni("for (java.util.Enumeration en = assocArr.keys(); en.hasMoreElements(); ) {"); {
					out.println(keyType.name+" key = ("+keyType.name+") en.nextElement();");
					out.println(valueType.name+" value = ("+valueType.name+") assocArr.get(key);");
				}
			}
			{
				{
					out.println("indent(out, indentFactor+1);");
					if (keyType.isSimpleType || hints.forceToString(keyType.type)) {
						printSimpleType(out, keyType, "key");
					} else {
						out.printlni("if (key != null) {");
						out.println("pp(key, out, indentFactor + 1);");
						out.printlno("} else {out.append(\"null\");}");
					}
					out.println("out.append(\": \");");
					if (valueType.isSimpleType || hints.forceToString(valueType.type)) {
						printSimpleType(out, valueType, "value");
						out.println("out.append('\\n');");
					} else {
						out.printlni("if (value != null) {");
						out.println("pp(value, out, indentFactor + 1);");
						out.printlno("} else {out.append(\"null\\n\");}");
					}
				}
			}
			out.printlno("}");
			out.println("indent(out, indentFactor);");
			out.println("out.append(\"}\\n\");");
		}
		out.printlno("}");
	}
	
	private static void printCollection(Outputter out, ClassDef cd, WorkQueue workQueue, TypeHints hints) {
		out.printlni("public static void pp(java.util.Collection coll, "+cd.name+" nullref, ###outputter### out, int indentFactor) {"); {
			out.println("out.append(\"# collection of "+cd.name+" [\\n\");");
			out.printlni("for (java.util.Iterator it = coll.iterator(); it.hasNext(); ) {"); {
				out.println(cd.name +" el = ("+cd.name+") it.next();");
				out.println("indent(out, indentFactor+1);");
				if (cd.isSimpleType || hints.forceToString(cd.type)) {
					printSimpleType(out, cd, "el");
					out.println("out.append('\\n');");
				} else {
					out.printlni("if (el != null) {");
					out.println("pp(el, out, indentFactor + 1);");
					out.printlno("} else {out.append(\"null\\n\");}");
					workQueue.enqueue(cd.type);
				}
			}
			out.printlno("}");
			out.println("indent(out, indentFactor);");
			out.println("out.append(\"]\\n\");");
		}
		out.printlno("}");
	}
	
	private static void printComplexType(Outputter out, ClassDef cd, WorkQueue workQueue, TypeHints hints) {
		out.printlni("public static void pp("+cd.name+" "+(cd.isArray ? "arr" : "obj")+", ###outputter### out, int indentFactor) {"); {
			if (cd.isArray) {
				out.println("out.append(\"# array "+cd.name+" [\\n\");");
				out.printlni("for (int i = 0; i < arr.length; i++) {"); {
					ClassDef elcd = ClassDef.get(cd.type.getComponentType());
					out.println("indent(out, indentFactor+1);");
					if (elcd.isSimpleType || hints.forceToString(elcd.type)) {
						printSimpleType(out, elcd, "arr[i]");
						out.println("out.append('\\n');");
					} else {
						out.printlni("if (arr[i] != null) {");
						out.println("pp(arr[i], out, indentFactor + 1);");
						out.printlno("} else {out.append(\"null\\n\");}");
						workQueue.enqueue(elcd.type);
					}
				}
				out.printlno("}");
				out.println("indent(out, indentFactor);");
				out.println("out.append(\"]\\n\");");
			} else {
				out.println("out.append(\"# class "+cd.name+" {\\n\");"); {
					for (Iterator itf = cd.fields.entrySet().iterator(); itf.hasNext();) {
						Map.Entry e = (Entry) itf.next();
						String fieldName = (String) e.getKey();
						ClassDef fcd = (ClassDef) e.getValue();
						out.println("indent(out, indentFactor+1);");
						out.println("out.append(\""+fieldName+": \");");
						if (fcd.isSimpleType || hints.forceToString(fcd.type)) {
							printSimpleType(out, fcd, "obj."+cd.fieldInvocations.get(fieldName));
							out.println("out.append('\\n');");
						} else if (fcd.isCollection) {
							Class elemType = hints.getCollectionElementType(cd.type, fieldName);
							if (elemType == null) {
								throw new RuntimeException("No collection definition for "+cd.name+" field "+fieldName);
							}
							workQueue.enqueueCollection(elemType);
							out.printlni("if (obj."+cd.fieldInvocations.get(fieldName)+" != null) {");
							out.println("pp(obj."+cd.fieldInvocations.get(fieldName)+", ("+ClassDef.get(elemType).name+")null, out, indentFactor+1);");
							out.printlno("} else {out.append(\"null\\n\");}");
						} else if (fcd.isMap || fcd.isDictionary) {
							Class keyType = hints.getAssocArrayKeyType(cd.type, fieldName);
							if (keyType == null) {
								throw new RuntimeException("No key definition for map or dictionary "+cd.name+" field "+fieldName);
							}
							Class valueType = hints.getAssocArrayValueType(cd.type, fieldName);
							if (valueType == null) {
								throw new RuntimeException("No value definition for map or dictionary "+cd.name+" field "+fieldName);
							}
							workQueue.enqueueMapdict(keyType, valueType, fcd.type);
							out.printlni("if (obj."+cd.fieldInvocations.get(fieldName)+" != null) {");
							out.println("pp(obj."+cd.fieldInvocations.get(fieldName)+", ("+ClassDef.get(keyType).name+") null, ("+ClassDef.get(valueType).name+") null, out, indentFactor + 1);");
							out.printlno("} else {out.append(\"null\\n\");}");
						} else {
							workQueue.enqueue(fcd.type);
							out.printlni("if (obj."+cd.fieldInvocations.get(fieldName)+" != null) {");
							out.println("pp(obj."+cd.fieldInvocations.get(fieldName)+", out, indentFactor + 1);");
							out.printlno("} else {out.append(\"null\\n\");}");
						}
					}
				}
				out.println("indent(out, indentFactor);");
				out.println("out.append(\"}\\n\");");
			}
		}
		out.printlno("}");
	}
	
	private static void printSimpleType(Outputter out, ClassDef cd, String invocation) {
//		if (cd.isNumericPrimitive) {
//			out.printlni("if ("+invocation+" != 0) {");
//		}
//		if (cd.isString) {
//			out.printlni("if ("+invocation+" != null && !"+invocation+".equals(\"\")) {");
//		}
//		if (cd.isBoxedNumericPrimitive || cd.isByteArray || cd.isCharArray) {
//			out.printlni("if ("+invocation+" != null) {");
//		}
		if (cd.isByteArray || cd.isCharArray) {
			out.println("out.append(\"" + cd.type.getComponentType().getName()+"[] of \"+" + invocation + ".length+\" " + cd.type.getComponentType().getName()+"s\");");
		} else {
			out.println("out.append(" + invocation + ");");
		}
//		if (cd.isNumericPrimitive || cd.isString || cd.isBoxedNumericPrimitive || cd.isByteArray || cd.isCharArray) {
//			out.printlno("}");
//		}
	}
}
