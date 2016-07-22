//Gili Mizrahi 302976840
//Syntax Analysis - write the xml file
//Date: 28/03/2015

import java.io.IOException;
import java.io.PrintWriter;

public class Xml {

	private final static int KEYWORD = 1;
	private final static int IDENTIFIER = 2;	
	private final static int STRINGCONSTANT = 3;
	private final static int SYMBOL = 4;

	private JackTokenizer tok;
	private PrintWriter p;
	private int index;

	//constructor
	public Xml(JackTokenizer tok, String path) throws IOException{

		this.tok = tok;
		p = new PrintWriter(path, "UTF-8");
	}

	//begin to write the xml file
	public void writeXml() throws IOException{

		p.println("<class>");

		if(tok.tokens[index].equals("class") && tok.tokens_type[index] == KEYWORD){
			p.println("<keyword> " + tok.tokens[index] + " </keyword>");
			index++;
		}

		if(tok.tokens_type[index] == IDENTIFIER){
			p.println("<identifier> " + tok.tokens[index] + " </identifier>");
			index++;
		}

		if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		classVarDec();

		subroutineDec();

		if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}
		
		p.println("</class>");

		p.close();
	}

	private void classVarDec() throws IOException{


		while((tok.tokens[index].equals("static") || tok.tokens[index].equals("field")) && tok.tokens_type[index] == KEYWORD){

			p.println("<classVarDec>");

			p.println("<keyword> " + tok.tokens[index] + " </keyword>");
			index++;

			if(((tok.tokens[index].equals("int") || tok.tokens[index].equals("char") || tok.tokens[index].equals("boolean")) && tok.tokens_type[index] == KEYWORD)){
				p.println("<type>");
				p.println("<keyword> " + tok.tokens[index] + " </keyword>");
				p.println("</type>");
				index++;
			}

			else if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<type>");
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				p.println("</type>");
				index++;
			}

			if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				index++;
			}

			while(!tok.tokens[index].equals(";")){

				if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL){
					p.println("<symbol> " + tok.tokens[index] + " </symbol>");
					index++;
				}

				if(tok.tokens_type[index] == IDENTIFIER){
					p.println("<identifier> " + tok.tokens[index] + " </identifier>");
					index++;
				}
			}

			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			p.println("</classVarDec>");
			index++;

		}
	}

	private void subroutineDec() throws IOException{

		while((tok.tokens[index].equals("constructor") || tok.tokens[index].equals("method") || tok.tokens[index].equals("function")) && (tok.tokens_type[index] == KEYWORD)){

			p.println("<subroutineDec>");
			p.println("<keyword> " + tok.tokens[index] + " </keyword>");
			index++;

			if(((tok.tokens[index].equals("int") || tok.tokens[index].equals("char") || tok.tokens[index].equals("boolean")) && tok.tokens_type[index] == KEYWORD)){
				p.println("<type>");
				p.println("<keyword> " + tok.tokens[index] + " </keyword>");
				p.println("</type>");
				index++;
			}

			else if(tok.tokens[index].equals("void") && tok.tokens_type[index] == KEYWORD){
				p.println("<keyword> " + tok.tokens[index] + " </keyword>");
				index++;
			}

			else if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<type>");
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				p.println("</type>");
				index++;
			}

			if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<subroutineName>");
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				p.println("</subroutineName>");
				index++;
			}

			if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}

			parameterList();

			if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}

			subroutineBody();
			
			p.println("</subroutineDec>");
		}
	}

	private void parameterList() throws IOException{

		p.println("<parameterList>");

		while(!tok.tokens[index].equals(")")){

			if(((tok.tokens[index].equals("int") || tok.tokens[index].equals("char") || tok.tokens[index].equals("boolean")) && tok.tokens_type[index] == KEYWORD)){
				
				p.println("<type>");
				p.println("<keyword> " + tok.tokens[index] + " </keyword>");
				p.println("</type>");
				index++;
			}

			else if(tok.tokens_type[index] == IDENTIFIER){
				
				p.println("<type>");
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				p.println("</type>");
				index++;
			}

			if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				index++;
			}

			if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}
		}

		p.println("</parameterList>");
	}

	private void subroutineBody() throws IOException{

		p.println("<subroutineBody>");

		if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		varDec();

		statements();

		if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}
		
		p.println("</subroutineBody>");
	}

	private void varDec() throws IOException{

		while(tok.tokens[index].equals("var") && tok.tokens_type[index] == KEYWORD){

			p.println("<varDec>");
			p.println("<keyword> " + tok.tokens[index] + " </keyword>");
			index++;


			if(((tok.tokens[index].equals("int") || tok.tokens[index].equals("char") || tok.tokens[index].equals("boolean")) && tok.tokens_type[index] == KEYWORD)){
				p.println("<type>");
				p.println("<keyword> " + tok.tokens[index] + " </keyword>");
				p.println("</type>");
				index++;
			}

			else if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<type>");
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				p.println("</type>");
				index++;
			}

			if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				index++;
			}

			while(!tok.tokens[index].equals(";")){

				if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL){
					p.println("<symbol> " + tok.tokens[index] + " </symbol>");
					index++;
				}

				if(tok.tokens_type[index] == IDENTIFIER){
					p.println("<identifier> " + tok.tokens[index] + " </identifier>");
					index++;
				}
			}
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			p.println("</varDec>");
			index++;
		}
	}

	private void statements() throws IOException{

		p.println("<statements>");

		while(tok.tokens[index].equals("let") || tok.tokens[index].equals("if") || tok.tokens[index].equals("while") || tok.tokens[index].equals("do") || tok.tokens[index].equals("return")){

			if(tok.tokens[index].equals("let") && tok.tokens_type[index] == KEYWORD){
				p.println("<statement>");
				letStatement();
				p.println("</statement>");
			}

			else if(tok.tokens[index].equals("if") && tok.tokens_type[index] == KEYWORD){
				p.println("<statement>");
				ifStatement();
				p.println("</statement>");
			}

			else if(tok.tokens[index].equals("while") && tok.tokens_type[index] == KEYWORD){
				p.println("<statement>");
				whileStatement();
				p.println("</statement>");
			}

			else if(tok.tokens[index].equals("do") && tok.tokens_type[index] == KEYWORD){
				p.println("<statement>");
				doStatement();
				p.println("</statement>");
			}

			else if(tok.tokens[index].equals("return") && tok.tokens_type[index] == KEYWORD){
				p.println("<statement>");
				ReturnStatement();
				p.println("</statement>");
			}
		}

		p.println("</statements>");
	}

	private void letStatement() throws IOException{

		p.println("<letStatement>");

		p.println("<keyword> " + tok.tokens[index] + " </keyword>");
		index++;

		if(tok.tokens_type[index] == IDENTIFIER){
			p.println("<identifier> " + tok.tokens[index] + " </identifier>");
			index++;
		}

		if(tok.tokens[index].equals("[") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;

			expression();

			if(tok.tokens[index].equals("]") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}
		}

		if(tok.tokens[index].equals("=") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		expression();

		if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		p.println("</letStatement>");
	}

	private void ifStatement() throws IOException{

		p.println("<ifStatement>");

		p.println("<keyword> " + tok.tokens[index] + " </keyword>");
		index++;

		if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		expression();

		if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		statements();

		if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		if(tok.tokens[index].equals("else")){

			p.println("<keyword> " + tok.tokens[index] + " </keyword>");
			index++;

			if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}

			statements();

			if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}
		}

		p.println("</ifStatement>");
	}

	private void whileStatement() throws IOException{

		p.println("<whileStatement>");

		p.println("<keyword> " + tok.tokens[index] + " </keyword>");
		index++;

		if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		expression();

		if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		if(tok.tokens[index].equals("{") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		statements();

		if(tok.tokens[index].equals("}") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		p.println("</whileStatement>");
	}

	private void doStatement() throws IOException{

		p.println("<doStatement>");

		p.println("<keyword> " + tok.tokens[index] + " </keyword>");
		index++;

		subroutineCall();

		if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}

		p.println("</doStatement>");
	}

	private void ReturnStatement() throws IOException{

		p.println("<returnStatement>");

		p.println("<keyword> " + tok.tokens[index] + " </keyword>");
		index++;

		if(!tok.tokens[index].equals(";"))
			expression();

		if(tok.tokens[index].equals(";") && tok.tokens_type[index] == SYMBOL){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
		}
		
		p.println("</returnStatement>");
	}

	private void expression() throws IOException{

		p.println("<expression>");

		term();

		while(tok.tokens[index].equals("+")||tok.tokens[index].equals("-")||tok.tokens[index].equals("*")||tok.tokens[index].equals("/")||tok.tokens[index].equals("&amp;")||tok.tokens[index].equals("|")||tok.tokens[index].equals("&lt;")||tok.tokens[index].equals("&gt;")||tok.tokens[index].equals("=")){
			op();
			term();
		}

		p.println("</expression>");
	}

	private void term() throws IOException{
		p.println("<term>");

		if(isInt()){
			p.println("<integerConstant> " + tok.tokens[index] + " </integerConstant>");
			index++;
		}

		else if(tok.tokens_type[index] == STRINGCONSTANT){
			p.println("<stringConstant > "+ tok.tokens[index] + " </stringConstant>");
			index++;
		}

		else if(tok.tokens[index].equals("true") || tok.tokens[index].equals("false") || tok.tokens[index].equals("null") || tok.tokens[index].equals("this")){
			p.println("<keywordConstant>");
			p.println("<keyword > " + tok.tokens[index] + " </keyword>");
			p.println("</keywordConstant>");
			index++;
		}


		else if(!tok.tokens[index+1].equals("[") && !tok.tokens[index+1].equals(".") && !tok.tokens[index+1].equals("(") && tok.tokens_type[index] == IDENTIFIER){
			p.println("<identifier> " + tok.tokens[index] + " </identifier>");
			index++;
		}

		else if(tok.tokens[index+1].equals("[") && tok.tokens_type[index] == IDENTIFIER){
			p.println("<identifier> " + tok.tokens[index] + " </identifier>");
			index++;

			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;

			expression();

			if(tok.tokens[index].equals("]")){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}
		}


		else if(tok.tokens[index].equals("(")){
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;

			expression();

			if(tok.tokens[index].equals(")")){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}
		}

		else if((tok.tokens[index].equals("~") || tok.tokens[index].equals("-")) && (tok.tokens_type[index] == SYMBOL)){
			p.println("<unaryOp>");
			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;
			p.println("</unaryOp>");
			
			term();
		}

		else
			subroutineCall();

		p.println("</term>");
	}

	private void subroutineCall() throws IOException{

		p.println("<subroutineCall>");

		if(tok.tokens[index+1].equals(".") && tok.tokens_type[index+1] == SYMBOL){
			p.println("<identifier> " + tok.tokens[index] + " </identifier>");
			index++;

			p.println("<symbol> " + tok.tokens[index] + " </symbol>");
			index++;

			if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<subroutineName>");
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				p.println("</subroutineName>");
				index++;
			}

			if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}

			expressionList();

			if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}
		}

		else{
			if(tok.tokens_type[index] == IDENTIFIER){
				p.println("<subroutineName>");
				p.println("<identifier> " + tok.tokens[index] + " </identifier>");
				p.println("</subroutineName>");
				index++;
			}

			if(tok.tokens[index].equals("(") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}

			expressionList();

			if(tok.tokens[index].equals(")") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;
			}
		}

		p.println("</subroutineCall>");
	}

	private void expressionList() throws IOException{

		p.println("<expressionList>");

		if(!tok.tokens[index].equals(")")){
			
			expression();
		}

		while(!tok.tokens[index].equals(")")){

			if(tok.tokens[index].equals(",") && tok.tokens_type[index] == SYMBOL){
				p.println("<symbol> " + tok.tokens[index] + " </symbol>");
				index++;

				expression();
			}
		}

		p.println("</expressionList>");
	}

	private void op() throws IOException{

		p.println("<op>");

		p.println("<symbol> " + tok.tokens[index] + " </symbol>");
		index++;

		p.println("</op>");
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
