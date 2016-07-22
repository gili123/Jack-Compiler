/*
 * Gili Mizrahi  302976840
 * Date: 19/05/2015
 * 
 * Translate from asembler to binary - data structure
 */


import java.util.*;

public class Vars {

	private Vector var;
	private Vector index;
	private Vector sym;
	private Vector symIndex;

	public Vars(){
		var = new Vector();
		index = new Vector();
		sym = new Vector();
		symIndex = new Vector();
	}

	public void addToSym(String s, int i){

		var.add(s);
		index.add(Integer.toString(i));
	}

	public void add(String v, int i){

		var.add(v);
		index.add(Integer.toString(i));
	}

	public boolean isExistsAtSym(String s){

		if(var.contains(s))
			return true;

		else 
			return false;
	}

	public boolean isExists(String v){

		if(var.contains(v))
			return true;

		else 
			return false;
	}
	
	public String MemoryIndexSym(String s){

		String memoryIndex = "";
		int vectorIndex = var.indexOf(s);

		memoryIndex = (String)index.get(vectorIndex);

		return memoryIndex;
	}

	public String MemoryIndex(String v){

		String memoryIndex = "";
		int vectorIndex = var.indexOf(v);

		memoryIndex = (String)index.get(vectorIndex);

		return memoryIndex;
	}
}
