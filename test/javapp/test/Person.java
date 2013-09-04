package javapp.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Person {
	private static boolean yesno() { return Math.random() > 0.5;}
	private static int rnd(int min, int max) {
		return (int) (min + (Math.random() * (max - min)));
	}
	public static class BornDate {
		public int day = rnd(1,30), month = rnd(1,12), year = rnd(1950,1990);
	}
	public static class Foo {
		public Double num = yesno() ? null : new Double(Math.random());
	}
	public Date now = new Date();
	private String name = yesno() ? "John" : yesno() ? null : "Jack";
	public String getName() { return name; }
	public String lastName = yesno() ? "Black" : yesno() ? null : "Doe";
	public BornDate born = new BornDate();
	public int[] lucky_numbers = new int[] {1,2,3};
	public int[][] matrix = new int[][] {
		new int[] {1,2,3},
		new int[] {4,5,6},
		new int[] {7,8,9}
	};
	public List brothers = new ArrayList(){{
		add("Bill");
		add("Jason");
		add(yesno() ? null : "Bob");
	}};
	public Set foos = new HashSet() {{
		add(new Foo());
		add(null);
		add(new Foo());
	}};
	
	public HashMap mappy = new HashMap() {{
		put("abc", new BornDate());
		put("def", yesno() ? null : new BornDate());
	}};
	
	public Dictionary hashy = new Hashtable() {{
		put("abc", new BornDate());
		put("def", new BornDate());
	}};
	public int n = 0;
	public Object o = null;
}
