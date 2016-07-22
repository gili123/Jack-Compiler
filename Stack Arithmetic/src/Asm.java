//Gili Mizrahi  302976840
//Date: 05/05/2015
//Stack Arithmetic

import java.io.BufferedWriter;
import java.io.IOException;


public class Asm {

	private BufferedWriter bw;
	private VMcode vm;
	private int label;            //counter to eq, gt, lt labels

	final int equal = 1;
	final int Greater = 2;
	final int Less = 3;

	//constructor
	public Asm(VMcode vm, BufferedWriter bw){

		this.vm = vm;
		this.bw = bw;
		this.label = 0;
	}


	//write the asm file
	public void writeAsm() throws IOException{

		String[] command;

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
}
