package javapp;

public class Outputter {
	private StringBuffer out = new StringBuffer();
	public String toString() {
		return out.toString();
	}
	private int indent = 0;
	public void indent() { indent++; }
	public void outdent() { indent--; }
	private boolean startOfLine = true;
	private void printIndent() {
		if (startOfLine) {
			for (int i = 0; i < indent; i++) {
				out.append('\t');
			}
			startOfLine = false;
		}
	}
	public void print(String s) {
		printIndent();
		out.append(s);
	}
	public void println(String s) {
		printIndent();
		out.append(s);
		out.append('\n');
		startOfLine = true;
	}
	public void printlni(String s) {
		printIndent();
		out.append(s);
		out.append('\n');
		startOfLine = true;
		indent();
	}
	public void printlno(String s) {
		outdent();
		printIndent();
		out.append(s);
		out.append('\n');
		startOfLine = true;
	}
}
