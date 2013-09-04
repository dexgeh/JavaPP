package javapp;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class WorkQueue {
	private Queue classQueue = new LinkedList();
	private HashSet classOutputted = new HashSet();
	public void enqueue(Class c) {
		if (ClassDef.get(c).isSimpleType)
			return;
		if (classOutputted.contains(c))
			return;
		classQueue.add(c);
	}
	public Class poll() {
		Class c = (Class) classQueue.poll();
		if (c != null)
			if (!classOutputted.contains(c))
				classOutputted.add(c);
			else
				return poll();
		return c;
	}
	private Queue collectionQueue = new LinkedList();
	private HashSet collectionOutputted = new HashSet();
	public void enqueueCollection(Class c) {
		if (collectionOutputted.contains(c))
			return;
		collectionQueue.add(c);
		enqueue(c);
	}
	public Class pollCollection() {
		Class c = (Class) collectionQueue.poll();
		if (c != null)
			if (!collectionOutputted.contains(c))
				collectionOutputted.add(c);
			else
				return pollCollection();
		return c;
	}
	private Queue mapdictQueue = new LinkedList();
	private HashSet mapdictOutputted = new HashSet();
	private boolean mapdictOutputtedContains(Class[] pair) {
		Class key = pair[0];
		Class value = pair[1];
		Class mapdict = pair[2];
		for (Iterator ito = mapdictOutputted.iterator(); ito.hasNext();) {
			Class[] p = (Class[])ito.next();
			if (p[0].equals(key) && p[1].equals(value) && p[2].equals(mapdict)) {
				return true;
			}
		}
		return false;
	}
	public void enqueueMapdict(Class key, Class value, Class mapdict) {
		if (ClassDef.get(mapdict).isMap) {
			mapdict = Map.class;
		} else {
			mapdict = Dictionary.class;
		}
		if (mapdictOutputtedContains(new Class[] {key, value, mapdict}))
			return;
		mapdictQueue.add(new Class[] {key, value, mapdict});
		enqueue(key);
		enqueue(value);
	}
	public Class[] pollMapdict() {
		Class[] pair = (Class[]) mapdictQueue.poll();
		if (pair != null)
			if (!mapdictOutputtedContains(pair))
				mapdictOutputted.add(pair);
			else
				return pollMapdict();
		return pair;
	}
}
