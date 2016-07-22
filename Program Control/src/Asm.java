//Gili Mizrahi  302976840
//Date: 10/05/2015
//Program Control

import java.io.BufferedWriter;
import java.io.IOException;


public class Asm {
	
	static int call_return;
	
	private BufferedWriter bw;
	private VMcode vm;
	private int label;            //counter to eq, gt, lt labels
	private boolean init;
	
	final int equal = 1;
	final int Greater = 2;
	final int Less = 3;

	//constructor
	public Asm(VMcode vm, BufferedWriter bw, int flag){

		this.vm = vm;
		this.bw = bw;
		this.label = 0;
		
		if(flag == 0)	
			init = true;
		
		else
			init = false;
	}


	//write the asm file
	public void writeAsm() throws IOException{

		String[] command;
		int k;
		
		if(init){
			bw.write("@256\n");
			bw.write("D=A\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
			
			call("Sys.init", "0");
		}
		
		while(vm.hasMoreCommands()){

			command = vm.nextCommand().split(" ");

			if(command[0].equals("push"))
				pushToStack(command);


			else if(command[0].equals("pop"))
				popFromStack(command);


			else if(command[0].equals("add")){

				bw.write("@SP\n");
				bw.write("M=M-1         //SP--\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("D=M           //D=M[SP]\n");
				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("D=D+M\n");
				bw.write("M=D\n");
			}

			else if(command[0].equals("sub")){

				bw.write("@SP\n");
				bw.write("M=M-1         //SP--\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("D=M           //D=M[SP]\n");
				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("D=M-D\n");
				bw.write("M=D\n");
			}

			else if(command[0].equals("neg")){

				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("M=-M\n");
			}

			else if(command[0].equals("eq"))
				Comparison(equal);

			else if(command[0].equals("gt"))
				Comparison(Greater);

			else if(command[0].equals("lt"))
				Comparison(Less);

			else if(command[0].equals("and")){

				bw.write("@SP\n");
				bw.write("M=M-1            //SP--\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("D=M              //D=M[SP]\n");
				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("D=D&M\n");
				bw.write("M=D\n");

			}

			else if(command[0].equals("or")){

				bw.write("@SP\n");
				bw.write("M=M-1            //SP--\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("D=M              //D=M[SP]\n");
				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("D=D|M\n");
				bw.write("M=D\n");
			}

			else if(command[0].equals("not")){

				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("D=M\n");
				bw.write("D=!D\n");
				bw.write("M=D\n");	
			}

			else if(command[0].equals("mult2")){

				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("A=M-1\n");
				bw.write("D=M+D          //D =M[SP-1]*2\n");
				bw.write("M=D\n");
			}

			else if(command[0].equals("call")){
				call(command[1], command[2]);
			}

			else if(command[0].equals("function")){
				
				bw.write("(" + command[1] + ")\n");
				
				k = Integer.parseInt(command[2]);
				
				for(int i = 0; i < k; i++){
					bw.write("@SP\n");
					bw.write("A=M\n");
					bw.write("M=0\n");
					bw.write("@SP\n");
					bw.write("M=M+1\n");
				}
			}
			
			else if(command[0].equals("return")){

				bw.write("@LCL\n");           //FRAME=LCL
				bw.write("D=M\n");
				bw.write("@FRAME\n");
				bw.write("M=D\n");
				    
				bw.write("@FRAME\n");        //RET=*(FRAME-5)
				bw.write("D=M\n");
				bw.write("@5\n");
				bw.write("D=D-A\n");
				bw.write("A=D\n");
				bw.write("D=M\n");
				bw.write("@RET\n");
				bw.write("M=D\n");
				
				bw.write("@SP\n");            //*ARG=pop()
				bw.write("M=M-1\n");
				bw.write("A=M\n");
				bw.write("D=M\n");
				bw.write("@ARG\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				
				bw.write("@ARG\n");           //SP=ARG+1
				bw.write("D=M+1\n");
				bw.write("@SP\n");
				bw.write("M=D\n");
				
				bw.write("@FRAME\n");         //THAT=*(FRAME-1)
				bw.write("A=M-1\n");
				bw.write("D=M\n");
				bw.write("@THAT\n");
				bw.write("M=D\n");
				
				bw.write("@2\n");             //THIS=*(FRAME-2)
				bw.write("D=A\n");
				bw.write("@FRAME\n");         
				bw.write("D=M-D\n");
				bw.write("A=D\n");
				bw.write("D=M\n");
				bw.write("@THIS\n");
				bw.write("M=D\n");
				
				bw.write("@3\n");             //ARG=*(FRAME-3)
				bw.write("D=A\n");
				bw.write("@FRAME\n");         
				bw.write("D=M-D\n");
				bw.write("A=D\n");
				bw.write("D=M\n");
				bw.write("@ARG\n");
				bw.write("M=D\n");
				
				bw.write("@4\n");             //LCL=*(FRAME-4)
				bw.write("D=A\n");
				bw.write("@FRAME\n");         
				bw.write("D=M-D\n");
				bw.write("A=D\n");
				bw.write("D=M\n");
				bw.write("@LCL\n");
				bw.write("M=D\n");
				
				bw.write("@RET\n");
				bw.write("A=M\n");
				bw.write("0;JMP\n");
			}
			
			else if(command[0].equals("goto")){

				bw.write("@" + command[1] + "\n");
				bw.write("0;JMP\n");
			}
			
			else if(command[0].equals("if-goto")){

				bw.write("@SP\n");
				bw.write("M=M-1\n");
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("D=M\n");
				bw.write("@" + command[1] + "\n");
				bw.write("D;JNE\n");
			}
			
			else if(command[0].equals("label"))
				bw.write("( " +command[1] + ")\n");

			vm.advance();
		}
	}

	private void pushToStack(String[] cmd) throws IOException{

		if(cmd[1].equals("local")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A           //D=i\n");
			bw.write("@LCL\n");
			bw.write("D=D+M         //D=M[LCL]+i\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("argument")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A           //D=i\n");
			bw.write("@ARG\n");
			bw.write("D=D+M         //D=M[ARG]+i\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("this")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A          //D=i\n");
			bw.write("@THIS\n");
			bw.write("D=D+M        //D=M[THIS]+i\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("that")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A            //D=i\n");
			bw.write("@THAT\n");
			bw.write("D=D+M         //D=M[THAT]+i\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("temp")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A          //D=i\n");	

			if (Integer.parseInt(cmd[2]) >= 8)
				System.err.println(cmd[2] + " is greater or equal to 8");

			bw.write("@5\n");
			bw.write("D=D+A         //D=M[5]+i\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("pointer")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A         //D=i\n");
			bw.write("@3\n");
			bw.write("D=D+A       //D=M[3]+i\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("static")){

			bw.write("@" + vm.getFilename() + cmd[2] + "\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("constant")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A       //D=i\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].endsWith("nothing")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A      //D=i\n");
			bw.write("@SP\n");
			bw.write("M=M+D\n");
		}

		if(!cmd[1].endsWith("nothing")){
			bw.write("@SP\n");
			bw.write("M=M+1\n");
		}
	}

	private void popFromStack(String[] cmd) throws IOException{

		if(cmd[1].equals("local")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A             //D=i\n");
			bw.write("@tmp\n");
			bw.write("M=D             //M[tmp]=i\n");
			bw.write("@LCL\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("M=M+D           //M[tmp]=M[LCL]+i\n");
			bw.write("@SP\n");
			bw.write("M=M-1           //SP--\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("argument")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A             //D=i\n");
			bw.write("@tmp\n");
			bw.write("M=D             //M[tmp]=i\n");
			bw.write("@ARG\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("M=M+D          //M[tmp]=M[LCL]+i\n");
			bw.write("@SP\n");
			bw.write("M=M-1          //SP--\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("this")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A               //D=i\n");
			bw.write("@tmp\n");
			bw.write("M=D               //M[tmp]=i\n");
			bw.write("@THIS\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("M=M+D             //M[tmp]=M[LCL]+i\n");
			bw.write("@SP\n");
			bw.write("M=M-1             //SP--\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("that")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A               //D=i\n");
			bw.write("@tmp\n");
			bw.write("M=D               //M[tmp]=i\n");
			bw.write("@THAT\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("M=M+D            //M[tmp]=M[LCL]+i\n");
			bw.write("@SP\n");
			bw.write("M=M-1            //SP--\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("temp")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A            //D=i\n");
			bw.write("@tmp\n");
			bw.write("M=D\n");

			if (Integer.parseInt(cmd[2]) >= 8)
				System.err.println(cmd[2] + " is greater or equal to 8");

			bw.write("@5\n");
			bw.write("D=A         //D=5\n");
			bw.write("@tmp\n");
			bw.write("M=M+D       //M[tmp]=5+i\n");
			bw.write("@SP\n");
			bw.write("M=M-1       //SP--\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("pointer")){

			bw.write("@" + cmd[2] + "\n");
			bw.write("D=A        //D=i\n");
			bw.write("@tmp\n");
			bw.write("M=D        //M[tmp]=i\n");
			bw.write("@3\n");
			bw.write("D=A\n");
			bw.write("@tmp\n");
			bw.write("M=M+D      //m[tmp]=3+i\n");
			bw.write("@SP\n");
			bw.write("M=M-1      //SP--\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@tmp\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].equals("static")){

			bw.write("@SP\n");
			bw.write("M=M-1         //SP--\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@"+vm.getFilename() + cmd[2] +"\n");
			bw.write("M=D\n");
		}

		else if(cmd[1].endsWith("nothing")){

			bw.write("@"+ cmd[2] +"\n");
			bw.write("D=A\n");
			bw.write("@SP\n");
			bw.write("M=M-D\n");
		}
	}

	private void Comparison(int type) throws IOException{

		bw.write("@SP\n");
		bw.write("A=M-1\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("M=M-1         //SP--\n");
		bw.write("@SP\n");
		bw.write("A=M-1\n");
		bw.write("D=M-D\n");

		bw.write("@TRUE" + label + "\n");

		if(type == 1)
			bw.write("D;JEQ\n");

		else if(type == 2)
			bw.write("D;JGT\n");

		else if(type == 3)
			bw.write("D;JLT\n");

		bw.write("@SP\n");         //if false
		bw.write("A=M-1\n");
		bw.write("M=0\n");

		bw.write("@FALSE" + label + "\n");
		bw.write("0;JMP\n");

		bw.write("(TRUE" + label + ")\n");      //if true
		bw.write("@SP\n");
		bw.write("A=M-1\n");
		bw.write("M=-1\n");

		bw.write("(FALSE" + label +")\n");

		label++;
	}
	
	private void call(String f, String n) throws IOException{
		
		int k;
		k = Integer.parseInt(n);
		
		bw.write("@return-address" + call_return + "\n");
		bw.write("D=A\n");
		bw.write("@SP\n");
		bw.write("A=M\n");
		bw.write("M=D\n");
		
		bw.write("@SP\n");         //push LCL
		bw.write("M=M+1\n");
		bw.write("@LCL\n");		
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("A=M\n");
		bw.write("M=D\n");
		
		bw.write("@SP\n");         //push ARG
		bw.write("M=M+1\n");
		bw.write("@ARG\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("A=M\n");
		bw.write("M=D\n");
		
		bw.write("@SP\n");         //push THIS
		bw.write("M=M+1\n");
		bw.write("@THIS\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("A=M\n");
		bw.write("M=D\n");
		
		bw.write("@SP\n");         //push THAT
		bw.write("M=M+1\n");
		bw.write("@THAT\n");
		bw.write("D=M\n");
		bw.write("@SP\n");
		bw.write("A=M\n");
		bw.write("M=D\n");
		
		bw.write("@SP\n");         //SP++
		bw.write("M=M+1\n");
		
		bw.write("@SP\n");         //ARG = SP-n-5
		bw.write("D=M\n");
		
		if(!f.equals("Sys.init")){
			bw.write("@" + k + "\n");
			bw.write("D=D-A\n");
		}
		
		bw.write("@5\n");
		bw.write("D=D-A\n");
		bw.write("@ARG\n");        
		bw.write("M=D\n");
		
		bw.write("@SP\n");                 //LCL = SP
		bw.write("D=M\n");
		bw.write("@LCL\n");        
		bw.write("M=D\n");
		     
		bw.write("@" + f + "\n");     //goto f
		bw.write("0;JMP\n");
		
		bw.write("(return-address" + call_return + ")\n");
		
		call_return++;
	}
}

