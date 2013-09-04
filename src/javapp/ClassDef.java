package javapp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClassDef {
	private static ArrayList defs = new ArrayList();

	public static ClassDef get(Class type) {
		ClassDef def;
		for (Iterator itd = defs.iterator(); itd.hasNext();)
			if ((def=(ClassDef)itd.next()).type == type)
				return def;
		return new ClassDef(type);
	}

	public final Class type;
	public final boolean isPrimitive;
	public final boolean isNumericPrimitive;
	public final boolean isBoxedPrimitive;
	public final boolean isBoxedNumericPrimitive;
	public final boolean isSimpleType;
	public final boolean isArray;
	public final boolean isCollection;
	public final boolean isMap;
	public final boolean isDictionary;
	public final boolean isString;
	public final boolean isByteArray;
	public final boolean isCharArray;
	public final String name;
	public Map fields = new HashMap();
	public Map fieldInvocations = new HashMap();
	
	private ClassDef(Class type) {
		this.type = type;
		name = getName(type);
		defs.add(this);
		isPrimitive =
			type.equals(Boolean.TYPE)
			|| type.equals(Byte.TYPE)
			|| type.equals(Short.TYPE)
			|| type.equals(Integer.TYPE)
			|| type.equals(Long.TYPE)
			|| type.equals(Double.TYPE)
			|| type.equals(Float.TYPE)
			|| type.equals(Character.TYPE);
		isNumericPrimitive = isPrimitive && !type.equals(Boolean.TYPE);
		isBoxedPrimitive =
			type.equals(Boolean.class)
			|| type.equals(Byte.class)
			|| type.equals(Short.class)
			|| type.equals(Integer.class)
			|| type.equals(Long.class)
			|| type.equals(Double.class)
			|| type.equals(Float.class)
			|| type.equals(Character.class);
		isBoxedNumericPrimitive = isBoxedPrimitive && !type.equals(Boolean.class);
		isArray = type.isArray();
		isCollection = Collection.class.isAssignableFrom(type);
		isMap = Map.class.isAssignableFrom(type);
		isDictionary = Dictionary.class.isAssignableFrom(type);
		isString = String.class.isAssignableFrom(type);
		isByteArray = byte[].class.isAssignableFrom(type);
		isCharArray = char[].class.isAssignableFrom(type);
		isSimpleType = isString || isPrimitive || isBoxedPrimitive || isByteArray || isCharArray;
		if (isPrimitive || isBoxedPrimitive || isArray || isCollection || isMap || isDictionary || isString)
			return;
		for (int i = 0; i < type.getMethods().length;i++) {
			Method m = type.getMethods()[i];
			if (!m.getName().startsWith("get")) {
				continue;
			}
			if (m.getName().equals("getClass")) {
				continue;
			}
			if (m.getName().length() < 4) {
				continue;
			}
			if (Character.isUpperCase(m.getName().charAt(4))) {
				continue;
			}
			if (m.getReturnType() == Void.class) {
				continue;
			}
			if (m.getParameterTypes().length != 0) {
				continue;
			}
			if (Modifier.isStatic(m.getModifiers())) {
				continue;
			}
			fields.put(toFieldName(m), get(m.getReturnType()));
			fieldInvocations.put(toFieldName(m), m.getName()+"()");
		}
		for (int i = 0; i < type.getFields().length; i++) {
			Field field = type.getFields()[i];
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			fields.put(field.getName(), get(field.getType()));
			fieldInvocations.put(field.getName(), field.getName());
		}
	}
	
	private static String getName(Class type) {
		if (type.isArray()) {
			Class c = type;
			String n = "";
			while (c.isArray()) {
				c = c.getComponentType();
				n += "[]";
			}
			return c.getName() + n;
		} else {
			return type.getName().replaceAll("\\$", ".");
		}
	}
	
	private static String lowerFirst(String word) {
		if (word.length() == 0) return word;
		if (word.length() == 1) return word.toLowerCase();
		return word.substring(0,1).toLowerCase() + word.substring(1, word.length());
	}
	private static String toFieldName(Method getter) {
		String getterName = getter.getName();
		if (getterName.startsWith("get"))
			return lowerFirst(getterName.substring(3));
		if (getterName.startsWith("is"))
			return lowerFirst(getterName.substring(2));
		throw new RuntimeException("Not a getter! "+getterName);
	}
}
