//Gili Mizrahi  302976840
//Date: 05/05/2015
//Stack Arithmetic

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;



public class VMtranslator {

	/**
	 * usage: VMtranslator <file> or VMtranslator <dir>
	 * @throws IOException 
	 * @throws MyException 
	 */
	public static void main(String[] args) throws IOException, Exception {
		File myfile = new File(args[0]);
		FileWriter fw;
		BufferedWriter bw;
		if(myfile.isDirectory()){
			fw=new FileWriter(new File(args[0]+".asm"));
			bw = new BufferedWriter(fw);		
			TranslateDir(myfile,bw);
		}
		else{
			if(args[0].substring(args[0].length()-3).matches("\\.vm")){
				fw=new FileWriter(new File(args[0].substring(0, args[0].length()-3)+".asm"));
				bw = new BufferedWriter(fw);
				TranslateFile(myfile,bw);
			}
			else{
				throw new Exception("not a vm file: " + args[0]);
			}
		}
		bw.close();
		fw.close();		
	}
		


/**
* handles A SINGLE vm file
*/
    private static void TranslateFile(File filename,BufferedWriter bw) throws IOException, Exception{
		   VMcode vmc = new VMcode(filename.getPath());			
			Asm asm = new Asm(vmc, bw);
			asm.writeAsm();
	}
	
/**
* handles a directory of vm files
*/
	private static void TranslateDir(File folder,BufferedWriter bw) throws IOException, Exception{
		
		File[] listOfFiles = folder.listFiles(); 
		String filename,ext,arg;
		for(int i=0;i<listOfFiles.length;i++){
			filename=listOfFiles[i].getName();
			ext = filename.substring(filename.length()-3);			
			if (ext.matches(".vm$")){			    			
				TranslateFile(listOfFiles[i],bw);			
			}
		}
	}
	
	
}

class VMcode{
		private String [] arr;
		private int  curr_index;
		private String filePath;
		public VMcode(String filename) throws IOException, Exception{
			
			this.filePath = filename;
			
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
		
		public String getFilename(){
			
			String[] tmp;
			String[] filename;
			
			tmp = this.filePath.split("\\\\");
			filename = tmp[tmp.length-1].split("vm");
			
			return filename[0];
		}
	}