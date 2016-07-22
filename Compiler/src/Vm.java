/*
 * Gili Mizrahi 302976840
 * 
 * File: class Vm - translate the jack language into vm language and write it to .vm file
 * 
 * Date: 09/04/2015
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Vm {

	private final static int KEYWORD = 1;
	private final static int IDENTIFIER = 2;	
	private final static int STRINGCONSTANT = 3;
	private final static int SYMBOL = 4;
	private final static int IntCONSTANT = 3;

	private JackTokenizer tok;
	private PrintWriter p;
	private int index, ifNum, whileNum;  //index - to tokens, ifNum - count the number of 'if' in the current method, whileNum - count the number of 'while' in the current method
	private Vector ifState, whileState;
	private String className;            //the specific class name
	private String funcName;             //current function name
	private String funcType;             //current function type

	private Array staticArr;
	private Array fieldArr;
	private Array argArr;
	private Array varArr;

	
	//constructor
	public Vm(JackTokenizer tok, String path) throws IOException{

		this.tok = tok;
		p = new PrintWriter(path, "UTF-8");

		staticArr = new Array();   //class array
		fieldArr = new Array();	   //class array	
	}

	//start to write to file
	public void writeVm() throws IOException{

		className = tok.tokens[1];
		index += 3;

		classVarDec();

		subroutineDec();

		p.close();
	}

	private void classVarDec() throws IOException{


		//catch all of the ststic and field variables
		while((tok.tokens[index].equals("static") || tok.tokens[index].equals("field")) && tok.tokens_type[index] == KEYWORD){

			int tmp = index;

			if(tok.tokens[index].equals("field")){
				index++;
				fieldArr.add(tok.tokens[index], tok.tokens[index + 1]);
				index++;
			}

			else if(tok.tokens[index].equals("static")){
				index++;
				staticArr.add(tok.tokens[index], tok.tokens[index + 1]);
				index++;
			}

			index++;

			while(!tok.tokens[index].equals(";")){

				if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL){					

					if(tok.tokens[tmp].equals("field")){
						fieldArr.add(tok.tokens[tmp+1], tok.tokens[index + 1]);
						index++;
					}	

					else if(tok.tokens[tmp].equals("static")){
						staticArr.add(tok.tokens[tmp+1], tok.tokens[index + 1]);
						index++;
					}
				}

				index++;
			}
			index++;
		}
	}

	private void subroutineDec() throws IOException{

		while((tok.tokens[index].equals("constructor") || tok.tokens[index].equals("method") || tok.tokens[index].equals("function")) && (tok.tokens_type[index] == KEYWORD)){

			argArr = new Array();    //method array
			varArr = new Array();    //method array

			funcType = tok.tokens[index];
			index += 2;		

			if(tok.tokens_type[index] == IDENTIFIER){
				funcName = tok.tokens[index];
				index += 2;
			}

			parameterList();

			if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL)
				index++;

			subroutineBody();
		}
	}

	private void parameterList() throws IOException{

		if(funcType.equals("method"))
			argArr.add("this", "this");
		
		//catch all of the arguments in the current method
		while(!tok.tokens[index].equals(")")){
			

			if(((tok.tokens[index].equals("int") || tok.tokens[index].equals("char") || tok.tokens[index].equals("boolean")) && tok.tokens_type[index] == KEYWORD)){	
				argArr.add(tok.tokens[index], tok.tokens[index + 1]);
				index += 2;
			}

			else if(tok.tokens_type[index] == IDENTIFIER){
				argArr.add(tok.tokens[index], tok.tokens[index + 1]);
				index += 2;
			}

			if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL)
				index++;
		}
	}

	private void subroutineBody() throws IOException{

		ifState = new Vector();
		whileState = new Vector();
		whileNum = 0;
		ifNum = 0;

		if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL)
			index++;

		varDec();

		p.println("function " + className + "." + funcName + " " + varArr.sizeOf());

		if(funcType.equals("constructor")){
			p.println("push constant " + fieldArr.sizeOf());
			p.println("call Memory.alloc 1");
			p.println("pop pointer 0");
		}

		else if(funcType.equals("method")){
			p.println("push argument 0");
			p.println("pop pointer 0");
		}

		statements();

		if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL)
			index++;		
	}

	private void varDec() throws IOException{

		//catch all of the variables in the current method
		while(tok.tokens[index].equals("var") && tok.tokens_type[index] == KEYWORD){

			varArr.add(tok.tokens[index + 1], tok.tokens[index + 2]);
			int tmp = index + 1;        //type
			index += 3;

			while(!tok.tokens[index].equals(";")){

				if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL)
					index++;


				if(tok.tokens_type[index] == IDENTIFIER){
					varArr.add(tok.tokens[tmp], tok.tokens[index]);
					index++;
				}
			}

			index++;
		}
	}

	private void statements() throws IOException{

		while(tok.tokens[index].equals("let") || tok.tokens[index].equals("if") || tok.tokens[index].equals("while") || tok.tokens[index].equals("do") || tok.tokens[index].equals("return")){

			if(tok.tokens[index].equals("let") && tok.tokens_type[index] == KEYWORD)
				letStatement();

			else if(tok.tokens[index].equals("if") && tok.tokens_type[index] == KEYWORD)
				ifStatement();

			else if(tok.tokens[index].equals("while") && tok.tokens_type[index] == KEYWORD)
				whileStatement();

			else if(tok.tokens[index].equals("do") && tok.tokens_type[index] == KEYWORD)
				doStatement();

			else if(tok.tokens[index].equals("return") && tok.tokens_type[index] == KEYWORD)
				ReturnStatement();
		}
	}

	private void letStatement() throws IOException{

		boolean isArray = false;
		int tmp = 0;   //IDENTIFIER
		index++;

		if(tok.tokens_type[index] == IDENTIFIER){
			tmp = index;
			index++;
		}

		//if let some[] =
		if(tok.tokens[index].equals("[") && tok.tokens_type[index] == SYMBOL){
			isArray = true;
			index++;

			expression();

			if(fieldArr.isExists(tok.tokens[tmp]) && !argArr.isExists(tok.tokens[tmp]) && !varArr.isExists(tok.tokens[tmp])){
				p.println("push this " + fieldArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}

			else if(varArr.isExists(tok.tokens[tmp])){
				p.println("push local " + varArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}
			
			else if(argArr.isExists(tok.tokens[tmp])){
				p.println("push argument " + argArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}
			
			else if(staticArr.isExists(tok.tokens[tmp])){
				p.println("push static " + staticArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}

			if(tok.tokens[index].equals("]") && tok.tokens_type[index] == SYMBOL)
				index++;

		}


		if(tok.tokens[index].equals("=") && tok.tokens_type[index] == SYMBOL)
			index++;

		expression();

		//if let some[] = ...
		if(isArray){

			p.println("pop temp 0");
			p.println("pop pointer 1");
			p.println("push temp 0");
			p.println("pop that 0");

			if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL)
				index++;
		}
		
		
		//if let some = ...
		else{

			if(fieldArr.isExists(tok.tokens[tmp]) && !argArr.isExists(tok.tokens[tmp]) && !varArr.isExists(tok.tokens[tmp]))
				p.println("pop this " + fieldArr.getIndex(tok.tokens[tmp]));

			else if(varArr.isExists(tok.tokens[tmp]))
				p.println("pop local " + varArr.getIndex(tok.tokens[tmp]));
			
			else if(argArr.isExists(tok.tokens[tmp]))
				p.println("pop argument " + argArr.getIndex(tok.tokens[tmp]));
			
			else if(staticArr.isExists(tok.tokens[tmp]))
				p.println("pop static " + staticArr.getIndex(tok.tokens[tmp]));

			if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL)
				index++;
		}
	}

	private void ifStatement() throws IOException{

		int currentIf;
		ifState.add(ifNum);
		ifNum++;
		index++;

		if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL)
			index++;

		expression();

		if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL)
			index++;

		p.println("if-goto IF_TRUE" + ifState.get(ifState.size() - 1));
		p.println("goto IF_FALSE" + ifState.get(ifState.size() - 1));
		p.println("label IF_TRUE" + ifState.get(ifState.size() - 1));

		if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL)
			index++;


		statements();

		if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL)
			index++;

		currentIf = (int)ifState.remove(ifState.size() - 1);
		if(!tok.tokens[index].equals("else"))
			p.println("label IF_FALSE" + currentIf);

		if(tok.tokens[index].equals("else")){
			p.println("goto IF_END" + currentIf);
			p.println("label IF_FALSE" + currentIf);
			index++;

			if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL)
				index++;


			statements();

			if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL)
				index++;

			p.println("label IF_END" + currentIf);
		}


	}

	private void whileStatement() throws IOException{

		int currentWhile;
		whileState.add(whileNum);
		whileNum++;;
		index++;

		p.println("label WHILE_EXP" + whileState.get(whileState.size() - 1));

		if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL)
			index++;

		expression();

		if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL)
			index++;

		p.println("not");
		p.println("if-goto WHILE_END" + whileState.get(whileState.size() - 1));

		if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL)
			index++;

		statements();

		if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL)
			index++;

		currentWhile = (int)whileState.remove(whileState.size() - 1);

		p.println("goto WHILE_EXP" + currentWhile);
		p.println("label WHILE_END" + currentWhile);
	}

	private void doStatement() throws IOException{

		index++;

		subroutineCall();

		p.println("pop temp 0");

		if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL)
			index++;
	}

	private void ReturnStatement() throws IOException{

		index++;

		if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL){
			p.println("push constant 0");
			p.println("return");
			index++;
			return;
		}

		if(!tok.tokens[index].equals(";") && !tok.tokens[index].equals("this"))
			expression();

		else if(tok.tokens[index].equals("this")){
			p.println("push pointer 0");
			index++;
		}

		if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL)
			index++;

		p.println("return");
	}

	private void expression() throws IOException{

		term();

		while(tok.tokens[index].equals("+")||tok.tokens[index].equals("-")||tok.tokens[index].equals("*")||tok.tokens[index].equals("^")||tok.tokens[index].equals("/")||tok.tokens[index].equals("&amp;")||tok.tokens[index].equals("|")||tok.tokens[index].equals("&lt;")||tok.tokens[index].equals("&gt;")||tok.tokens[index].equals("=")){
			int tmp = index;
			op();
			term();

			if(tok.tokens[tmp].equals("+"))
				p.println("add");

			else if(tok.tokens[tmp].equals("-"))
				p.println("sub");

			else if(tok.tokens[tmp].equals("*"))
				p.println("call Math.multiply 2");
			
			else if(tok.tokens[tmp].equals("^"))
				p.println("call Math.power 2");

			else if(tok.tokens[tmp].equals("/"))
				p.println("call Math.divide 2");

			else if(tok.tokens[tmp].equals("&gt;"))
				p.println("gt");

			else if(tok.tokens[tmp].equals("&lt;"))
				p.println("lt");

			else if(tok.tokens[tmp].equals("&amp;"))
				p.println("and");

			else if(tok.tokens[tmp].equals("|"))
				p.println("or");

			else if(tok.tokens[tmp].equals("="))
				p.println("eq");
		}
	}

	private void term() throws IOException{

		//constant integer
		if(isInt()){

			p.println("push constant " + tok.tokens[index]);
			index++;
		}

		//constant string
		else if(tok.tokens_type[index] == STRINGCONSTANT){

			String s = tok.tokens[index];
			int len = s.length();

			p.println("push constant " + len);
			p.println("call String.new 1");

			for(int i = 0; i < len; i++){
				char c = s.charAt(i);
				p.println("push constant " + (int)c);
				p.println("call String.appendChar 2");
			}

			index++;
		}

		//keyWordConstant
		else if(tok.tokens[index].equals("true") || tok.tokens[index].equals("false") || tok.tokens[index].equals("null") || tok.tokens[index].equals("this")){

			if(tok.tokens[index].equals("true")){
				p.println("push constant 0");
				p.println("not");
			}

			else if(tok.tokens[index].equals("false") || tok.tokens[index].equals("null"))
				p.println("push constant 0");

			else
				p.println("push pointer 0");

			index++;
		}

		//varName
		else if(!tok.tokens[index+1].equals("[") && !tok.tokens[index+1].equals(".") && !tok.tokens[index+1].equals("(") && tok.tokens_type[index] == IDENTIFIER){

			if(fieldArr.isExists(tok.tokens[index]) && !argArr.isExists(tok.tokens[index]) && !varArr.isExists(tok.tokens[index]))
				p.println("push this " + fieldArr.getIndex(tok.tokens[index]));

			else if(varArr.isExists(tok.tokens[index]))
				p.println("push local " + varArr.getIndex(tok.tokens[index]));

			else if(argArr.isExists(tok.tokens[index]))
				p.println("push argument " + argArr.getIndex(tok.tokens[index]));
			
			else if(staticArr.isExists(tok.tokens[index]))
				p.println("push static " + staticArr.getIndex(tok.tokens[index]));

			index++;
		}

		//varName[]
		else if(tok.tokens[index+1].equals("[") && tok.tokens_type[index] == IDENTIFIER){

			int tmp = index;          //IDENTIFIER
			index += 2;

			expression();

			if(fieldArr.isExists(tok.tokens[tmp]) && !argArr.isExists(tok.tokens[tmp]) && !varArr.isExists(tok.tokens[tmp])){
				p.println("push this " + fieldArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}

			else if(varArr.isExists(tok.tokens[tmp])){
				p.println("push local " + varArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}
			
			else if(argArr.isExists(tok.tokens[tmp])){
				p.println("push argument " + argArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}
			
			else if(staticArr.isExists(tok.tokens[tmp])){
				p.println("push static " + staticArr.getIndex(tok.tokens[tmp]));
				p.println("add");
			}

			p.println("pop pointer 1");
			p.println("push that 0");

			index++;
		}

		//( expression )
		else if(tok.tokens[index].equals("(")){

			index++;

			expression();

			index++;

		}

		//unaryOp term
		else if((tok.tokens[index].equals("~") || tok.tokens[index].equals("-")) && (tok.tokens_type[index] == SYMBOL)){

			int tmp = index;
			index++;
			term();

			if(tok.tokens[tmp].equals("~"))
				p.println("not");

			else if(tok.tokens[tmp].equals("-"))
				p.println("neg");
		}

		//subroutineCall
		else
			subroutineCall();
	}

	private void subroutineCall() throws IOException{

		String type = null;
		int numOfParameters = 0;
		int subName = 0;
		int tmp = 0;


		//(className | varName).subroutineName(expression)
		if(tok.tokens[index+1].equals(".") && tok.tokens_type[index+1] == SYMBOL){
			tmp = index;    //className | varName
			index += 2;

			if(tok.tokens_type[index] == IDENTIFIER){
				subName = index;    //subroutineName
				index++;
			}

			if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL)
				index++;


			//if method
			if(fieldArr.isExists(tok.tokens[tmp]) || varArr.isExists(tok.tokens[tmp]) || staticArr.isExists(tok.tokens[tmp]) || argArr.isExists(tok.tokens[tmp])){

				if(fieldArr.isExists(tok.tokens[tmp]) && !argArr.isExists(tok.tokens[tmp]) && !varArr.isExists(tok.tokens[tmp])){
					p.println("push this " + fieldArr.getIndex(tok.tokens[tmp]));
					type = fieldArr.getType(tok.tokens[tmp]);
				}

				else if(varArr.isExists(tok.tokens[tmp])){
					p.println("push local " + varArr.getIndex(tok.tokens[tmp]));
					type = varArr.getType(tok.tokens[tmp]);
				}
				
				else if(argArr.isExists(tok.tokens[tmp])){
					p.println("push argument " + argArr.getIndex(tok.tokens[tmp]));
					type = argArr.getType(tok.tokens[tmp]);
				}
				
				else if(staticArr.isExists(tok.tokens[tmp])){
					p.println("push static " + staticArr.getIndex(tok.tokens[tmp]));
					type = staticArr.getType(tok.tokens[tmp]);
				}

				numOfParameters = expressionList();
				p.println("call " + type + "." + tok.tokens[subName] + " " + (numOfParameters + 1));
			}

	
			else{
				numOfParameters = expressionList();
				p.println("call " + tok.tokens[tmp] + "." + tok.tokens[subName] + " " + numOfParameters);
			}


			if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL)
				index++;
		}


		//subroutineName(expression)
		else{
			if(tok.tokens_type[index] == IDENTIFIER){

				p.println("push pointer 0");
				subName = index;     //subroutineName
				index++;
			}

			if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL)
				index++;
			
			numOfParameters = expressionList();
			p.println("call " + className + "." + tok.tokens[subName] + " " + (numOfParameters + 1));
			

			if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL)
				index++;	
		}
	}

	private int expressionList() throws IOException{
		int i = 0;

		if(!tok.tokens[index].equals(")")){
			expression();
			i++;
		}

		while(!tok.tokens[index].equals(")")){

			if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL){
				index++;

				expression();
				i++;
			}
		}
		return i;
	}

	private void op() throws IOException{
		index++;
	}

	//check if the current value is integer
	private boolean isInt(){

		try{
			Integer.parseInt(tok.tokens[index]);
			return true;
		}

		catch(NumberFormatException e){
			return false;
		}
	}
}