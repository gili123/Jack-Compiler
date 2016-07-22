//Gili Mizrahi 302976840
//Syntax Analysis - main
//Date: 28/03/2015

import java.io.File;
import java.io.IOException;


public class JackAnalyzer {

	public static void main(String[] args) throws IOException, Exception {

		File tokenFile = new File(args[0]);
		JackTokenizer token;
		
		//if args[0] is a jack file
		if(tokenFile.isFile() && tokenFile.getName().matches("(\\w)*.jack")){
			token = new JackTokenizer(tokenFile.getPath());
			Xml newXml = new Xml(token, tokenFile.getParent() + "\\" +tokenFile.getName().substring(0, tokenFile.getName().length()-5) + ".xml");
			newXml.writeXml();
		}
		
		//if args[0] is a directory
		else if(tokenFile.isDirectory()){
			File[] list = tokenFile.listFiles(); 
			
			for(int i = 0; i < list.length; i++){
				
				if(list[i].getName().matches("(\\w)*.jack")){
					token = new JackTokenizer(tokenFile.getPath() + "\\" + list[i].getName());
					Xml newXml = new Xml(token, tokenFile + "\\" + list[i].getName().substring(0, list[i].getName().length()-5) + ".xml");
					newXml.writeXml();
				}
			}
		}
		
			else
				throw new Exception("This file is NOT a jack file: " + tokenFile);
			
	}
}

