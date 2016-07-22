/*
 * Gili Mizrahi 302976840
 * 
 * File: class JackCompiler - main
 * 
 * Date: 09/04/2015
 */

import java.io.File;
import java.io.IOException;

public class JackCompiler {

	/**
	 * usage: JackAnalyzer <file> or JackAnalyzer <dir>
	 * @throws IOException 
	 * @throws MyException 
	 */
	public static void main(String[] args) throws IOException, Exception {

		File tokenFile = new File(args[0]);
		JackTokenizer token;
		
		//if args[0] is a jack file
		if(tokenFile.isFile() && tokenFile.getName().matches("(\\w)*.jack")){
			token = new JackTokenizer(tokenFile.getPath());
			Vm vm = new Vm(token, tokenFile.getParent() + "\\" +tokenFile.getName().substring(0, tokenFile.getName().length()-5) + ".vm");
			vm.writeVm();
		}
		
		//if args[0] is a directory
		else if(tokenFile.isDirectory()){
			File[] list = tokenFile.listFiles(); 
			
			for(int i = 0; i < list.length; i++){
				
				if(list[i].getName().matches("(\\w)*.jack")){
					token = new JackTokenizer(tokenFile.getPath() + "\\" + list[i].getName());
					Vm vm = new Vm(token, tokenFile + "\\" + list[i].getName().substring(0, list[i].getName().length()-5) + ".vm");
					vm.writeVm();
				}
			}
		}
		
			else
				throw new Exception("This file is NOT a jack file: " + tokenFile);
	}
}
