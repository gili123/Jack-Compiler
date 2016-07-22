/*
 * Gili Mizrahi 302976840
 * 
 * File: class Array - structure to save the program's variables 
 * 
 * Date: 09/04/2015
 */

import java.util.*;


public class Array {

	private Vector type;       //variable type
	private Vector name;       //variable name

	
	//constructor - new type and name vectors
	public Array(){
		
		type = new Vector();
		name = new Vector();
	}
	
	//add new type and name
	public void add(String t, String n){
		
		type.add(t);
		name.add(n);
	}
	
	//return the index of the correct variable by name
	public int getIndex(String n){
		
		int i;
		i = name.indexOf(n);
		
		return i;
	}
	
	//return the type of the correct variable by name
	public String getType(String n){
		
		String t;
		int i;
		i = name.indexOf(n);
		t = type.get(i).toString();
		
		return t;
	}
	
	//return the size of the vectors
	public int sizeOf(){
		
		int size;
		size = name.size();
		
		return size;
	}
	
	/*
	 * check if the nameVector contain the specific variable name
	 * if the variable exists return true, else return false
	 */
	public boolean isExists(String n){
		
		int i = name.indexOf(n);
		
		if(i == -1)
			return false;
		
		else
			return true;
	}
	
	//return the variable name of the specific index
	public String getName(int i){
		
		String n;
		
		n = name.get(i).toString();
		
		return n;
	}
}