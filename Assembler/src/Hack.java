/*
 * Gili Mizrahi  302976840
 * Date: 19/05/2015
 * 
 * Translate from asembler to binary - writer class
 */


import java.io.BufferedWriter;
import java.io.IOException;


public class Hack {

	private BufferedWriter bw;
	private ASMcode asm;
	private Vars vars;
	private int currentVars;          //vars counter
	private int symbol;               //symbols counter

	//constructor
	public Hack(BufferedWriter bw, ASMcode as){

		this.bw = bw;
		this.asm = as;
		vars = new Vars();
		currentVars = 16;
		symbol = 0;
	}

	//write the hack file
	public void writeHack() throws IOException{

		String cmd;
		String[] tmp;
		String temp;
		
		while(asm.hasMoreCommands()){
			
			cmd = asm.nextCommand();
			cmd = clean(cmd);
			
			if(cmd.contains("(")){

				vars.addToSym(cmd, symbol);
			}
			else
				symbol++;
			
			asm.advance();
		}
		
		
		asm.resetCurr();
		

		while(asm.hasMoreCommands()){

			cmd = asm.nextCommand();
			
			cmd = clean(cmd);

			if(cmd.contains("@")){

				tmp = cmd.split("@");

				if(isInt(tmp[1]))
					IntCmd(tmp[1]);

				else
					symbolCmd(tmp[1]);
			}

			else if(cmd.contains("=")){

				tmp = cmd.split("=");
				if(cmd.contains("$"))
					bw.write("101");
				else
					bw.write("111");
				
				if(tmp[1].contains("$")){
					temp = clear(tmp[1], "$");
					comp(temp);
				}
				else
					comp(tmp[1]);
				
				if(tmp[0].contains("$")){
					temp = clear(tmp[0], "$");
					dest(temp);
				}
				
				else
					dest(tmp[0]);
				
				bw.write("000" + "\n");
			}

			else if(cmd.contains(";")){
				
				tmp = cmd.split(";");
				if(cmd.contains("$"))
					bw.write("101");
				else
					bw.write("111");
				
				if(tmp[0].contains("$")){
					temp = clear(tmp[0], "$");
					comp(temp);
				}
				else
					comp(tmp[0]);
				
				bw.write("000");
				
				if(tmp[1].contains("$")){
					temp = clear(tmp[1], "$");
					jump(temp);
				}
				
				else
					jump(tmp[1]);
			}

			asm.advance();
		}
	}

	//check if s is integer
	private boolean isInt(String s){

		try{
			Integer.parseInt(s);
			return true;
		}

		catch(NumberFormatException e){
			return false;
		}
	}

	//translate the A instruction to binary
	private void IntCmd(String s) throws IOException{

		int k;
		String binary;

		k = Integer.parseInt(s);		
		binary = Integer.toBinaryString(k);

		for(int i = binary.length(); i < 16; i++)   //Completion of 16 bits
			bw.write("0");

		bw.write(binary + "\n");
	}

	//take care all the symbols
	private void symbolCmd(String s) throws IOException{

		String memoryIndex = "";
		String check;
		String[] temp;

		if(s.equals("SP"))
			bw.write("0000000000000000\n");

		else if(s.equals("LCL"))
			bw.write("0000000000000001\n");

		else if(s.equals("ARG"))
			bw.write("0000000000000010\n");

		else if(s.equals("THIS"))
			bw.write("0000000000000011\n");

		else if(s.equals("THAT"))
			bw.write("0000000000000100\n");

		else if(s.equals("SCREEN"))
			bw.write("0100000000000000\n");

		else if(s.equals("KBD"))
			bw.write("0110000000000100\n");

		else if(s.equals("R0")||s.equals("R1")||s.equals("R2")||s.equals("R3")||s.equals("R4")||s.equals("R5")||s.equals("R6")||s.equals("R7")||s.equals("R8")||s.equals("R9")||s.equals("R10")||s.equals("R11")||s.equals("R12")||s.equals("R13")||s.equals("R14")||s.equals("R15")){

			temp = s.split("R");
			IntCmd(temp[1]);
		}

		else{                           //@variable

			check = "(" + s + ")";
			if(vars.isExistsAtSym(check)){
				
				memoryIndex = vars.MemoryIndex(check);
				IntCmd(memoryIndex);
			}
			
			else if(vars.isExists(s)){

				memoryIndex = vars.MemoryIndex(s);
				IntCmd(memoryIndex);
			}

			else{
				vars.add(s, currentVars);
				IntCmd(Integer.toString(currentVars));
				currentVars++;
			}
		}
	}


	//the dest of the C instruction
	private void dest(String dest) throws IOException{

		if(dest.equals("M"))
			bw.write("001");
		
		else if(dest.equals("D"))
			bw.write("010");
		
		else if(dest.equals("MD"))
			bw.write("011");
		
		else if(dest.equals("A"))
			bw.write("100");
		
		else if(dest.equals("AM"))
			bw.write("101");
		
		else if(dest.equals("AD"))
			bw.write("110");
		
		else if(dest.equals("AMD"))
			bw.write("111");
	}


	//the comp of the C instruction
	private void comp(String comp) throws IOException{
		
		if(comp.equals("0"))
			bw.write("0101010");
		
		else if(comp.equals("1"))
			bw.write("0111111");
		
		else if(comp.equals("-1"))
			bw.write("0111010");
		
		else if(comp.equals("D"))
			bw.write("0001100");
		
		else if(comp.equals("A"))
			bw.write("0110000");
		
		else if(comp.equals("!D"))
			bw.write("0001101");
		
		else if(comp.equals("!A"))
			bw.write("0110001");
		
		else if(comp.equals("-D"))
			bw.write("0001111");
		
		else if(comp.equals("-A"))
			bw.write("0110011");
		
		else if(comp.equals("D+1") || comp.equals("1+D"))
			bw.write("0011111");
		
		else if(comp.equals("A+1") || comp.equals("1+A"))
			bw.write("0110111");
		
		else if(comp.equals("D-1"))
			bw.write("0001110");
		
		else if(comp.equals("A-1"))
			bw.write("0110010");
		
		else if(comp.equals("D+A") || comp.equals("A+D"))
			bw.write("0000010");
		
		else if(comp.equals("D-A"))
			bw.write("0010011");
		
		else if(comp.equals("A-D"))
			bw.write("0000111");
		
		else if(comp.equals("D&A") || comp.equals("A&D"))
			bw.write("0000000");
		
		else if(comp.equals("D|A") || comp.equals("A|D"))
			bw.write("0010101");
		
		else if(comp.equals("M"))
			bw.write("1110000");
		
		else if(comp.equals("!M"))
			bw.write("1110001");
		
		else if(comp.equals("-M"))
			bw.write("1110011");
		
		else if(comp.equals("M+1") || comp.equals("1+M"))
			bw.write("1110111");
		
		else if(comp.equals("M-1"))
			bw.write("1110010");
		
		else if(comp.equals("D+M") || comp.equals("M+D"))
			bw.write("1000010");
		
		else if(comp.equals("D-M"))
			bw.write("1010011");
		
		else if(comp.equals("M-D"))
			bw.write("1000111");
		
		else if(comp.equals("D&M") || comp.equals("M&D"))
			bw.write("1000000");
		
		else if(comp.equals("D|M") || comp.equals("M|D"))
			bw.write("1010101");
	}
	
	
	//the jump of the C instruction
	private void jump(String jump) throws IOException{
		
		if(jump.equals("JGT"))
			bw.write("001");
		
		else if(jump.equals("JEQ"))
			bw.write("010");
		
		else if(jump.equals("JGE"))
			bw.write("011");
		
		else if(jump.equals("JLT"))
			bw.write("100");
		
		else if(jump.equals("JNE"))
			bw.write("101");
		
		else if(jump.equals("JLE"))
			bw.write("110");
		
		
		else if(jump.equals("JMP"))
			bw.write("111");
		
		bw.write("\n");
	}
	
	
	//remove c from s
	private String clear(String s, String c){
		
		String tmp = "";
		
		for(int i = 0; i < s.length(); i++){
			if(s.charAt(i) != c.charAt(0))
				tmp += s.charAt(i);
		}
		
		return tmp;
	}
	
	
	//clean s from space, tab and comments
	private String clean(String s){
		
		String tmp = "";
		String tab = "\t";

		for(int i = 0; i < s.length(); i++){
			
			if((int)s.charAt(i) == 47)
				return tmp;
			
			if((int)s.charAt(i) != 32 && s.charAt(i) != tab.charAt(0))
				tmp += s.charAt(i);
		}
		
		return tmp;
	}
}
