/*
 * Gili Mizrahi  302976840
 * Date: 19/05/2015
 * 
 * Translate from asembler to binary - main
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;



public class Assembler {

	/**
	 * usage: Assembler <file> 
	 * @throws IOException 
	 * @throws MyException 
	 */
	public static void main(String[] args) throws IOException, Exception {
		File myfile = new File(args[0]);
		FileWriter fw;
		BufferedWriter bw;
		if(args[0].substring(args[0].length()-4).matches("\\.asm")){
			fw=new FileWriter(new File(args[0].substring(0, args[0].length()-4)+".hack"));
			bw = new BufferedWriter(fw);
			TranslateFile(myfile,bw);
		}
		else{
			throw new Exception("not an asm file: " + args[0]);
		}
		bw.close();
		fw.close();		
	}
		


/**
* handles A SINGLE vm file
*/
    private static void TranslateFile(File filename,BufferedWriter bw) throws IOException, Exception{
		   ASMcode ac = new ASMcode(filename.getPath());
		   
		   Hack hack = new Hack(bw, ac);
		   hack.writeHack();
	}
	
	}
	

class ASMcode{
		private String [] arr;
		private int  curr_index;
		
		
		public ASMcode(String filename) throws IOException, Exception{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line,str="";
			while ((line = br.readLine()) != null) {
				curr_index=line.indexOf("//");
				if(curr_index!=-1){
					line=line.substring(0, curr_index);
				}			
				if(!line.matches("^\\s*$")){			
					str=str.concat(line + "\n");
				}
			}			
			arr = str.split("\n");	
			br.close();
			curr_index=0;		
		}
		
		
		public boolean hasMoreCommands(){
			return (arr.length>(curr_index));
		}
		
		public void advance(){
			curr_index++;
		}
		
		public String nextCommand(){
			return arr[curr_index];
		}	
		
		public void resetCurr(){
			curr_index = 0;
		}
}

