package javapp;

public interface TypeHints {
	public Class getCollectionElementType(Class c, String fieldName);
	public Class getAssocArrayKeyType(Class c, String fieldName);
	public Class getAssocArrayValueType(Class c, String fieldName);
	public boolean forceToString(Class c);
}
