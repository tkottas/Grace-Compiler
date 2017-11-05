package compiler;

import compiler.lexer.Lexer;
import compiler.lexer.LexerException;
import compiler.parser.Parser;
import compiler.parser.ParserException;
import java.util.*;
import compiler.node.*;
import compiler.node.Start;
import compiler.node.Token;
import java.io.PushbackReader;
import compiler.analysis.DepthFirstAdapter;
import java.lang.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        for(String s: args){
	        Start tree = null;
	        File file=new File(s);
	        try{
		        PushbackReader reader = new PushbackReader(new BufferedReader(new FileReader(file)), 1024);
		        try {
		            Parser p = new Parser(new Lexer(reader));
		            tree = p.parse();
		        } catch (LexerException e) {
		            System.err.printf("Lexing error: %s\n", e.getMessage());
		        } catch (IOException e) {
		            System.err.printf("I/O error: %s\n", e.getMessage());
		            e.printStackTrace();
		        } catch (ParserException e) {
		            System.err.printf("Parsing error: %s\n", e.getMessage());
		        }
		    }
		    catch (FileNotFoundException e)  
		    {
				System.err.printf("I/O error: %s\n", e.getMessage());
		        e.printStackTrace();
		        // insert code to run when exception occurs
		    }

	        tree.apply(new Action());
    	}
        System.out.println("Compilation Finished");
    }}