package javapp.test;

public class PPRun {
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 50000; i++) {
			StringBuilder out = new StringBuilder();
			PP.pp(new Person(), out, 0);
		}
		System.out.println("OK");
	}
}
