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

class Assembly{
    BufferedWriter out;
    int cir;
    ArrayList<String> if_labels;
    String mfunc;
    ArrayList<String> data_seg;
    int pos_data;
    int total_temps;
    Assembly(String a){
        try{
            mfunc=a;
            //System.out.println("HEREEEEEEEEE  "+a);
            cir=1;
            pos_data=1;
            total_temps=0;
            File f=new File("assembly/"+a+".s");
            int flag=1;
            data_seg=new ArrayList<String>();
            if(f.createNewFile()==false){
                flag=0;
            }
            int i=0;
            while(flag==0){
                File fe=new File("assembly/"+a+Integer.toString(i)+".s");
                if(fe.createNewFile()==true){
                    flag=1;
                    f=fe;
                }
                i++;
            }
            out=new BufferedWriter(new FileWriter(f));
            out.write(".intel_syntax noprefix");
            out.newLine();
            out.write(".text");
            out.newLine();
            out.write("	.global main");
            out.newLine();
            out.write("main:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
            out.write("	call "+a+"0");
            out.newLine();
            out.write("	mov eax, 0");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
        }
        catch(IOException e){
            System.out.println("Could not write file");
            System.exit(1);
        }}
    void cr_builtin(Semantic c){
        try{
            out.write("puti0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov esi, DWORD PTR [ebp+16]");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov eax, OFFSET FLAT:printf_int");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write("	call printf");
            out.newLine();
            out.write("	add esp, 8");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("putc0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write(" mov esi, DWORD PTR [ebp+16]");
            out.newLine();
            out.write(" push esi");
            out.newLine();
            out.write("	mov eax, OFFSET FLAT:printf_char");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write("	call printf");
            out.newLine();
            out.write("	add esp, 8");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("puts0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write(" mov esi, DWORD PTR [ebp+16]");
            out.newLine();
            out.write(" push esi");
            out.newLine();
            out.write("	mov eax, OFFSET FLAT:printf_string");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write("	call printf");
            out.newLine();
            out.write("	add esp, 8");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("geti0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov eax, OFFSET FLAT:prompt_int");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write("	call printf");
            out.newLine();
            out.write("	add esp, 4");
            out.newLine();
            out.write(" lea eax, [ebp+12]");
            out.newLine();
            out.write(" push eax");
            out.newLine();
            out.write("	mov eax, OFFSET FLAT:int_fmt");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write("	call scanf");
            out.newLine();
            out.write("	add esp, 8");
            out.newLine();
            out.write(" mov eax, DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("getc0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov eax, OFFSET FLAT:prompt_char");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write("	call printf");
            out.newLine();
            out.write("	add esp, 4");
            out.newLine();
            out.write("	push DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	mov eax, OFFSET FLAT:char_fmt");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write("	call scanf");
            out.newLine();
            out.write("	add esp, 8");
            out.newLine();
            out.write(" mov eax, DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("gets0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov eax, DWORD PTR stdin");
            out.newLine();
            out.write("	push eax");
            out.newLine();
            out.write(" push DWORD PTR [ebp+16]");
            out.newLine();
            out.write(" push DWORD PTR [ebp+20]");
            out.newLine();
            out.write("	call fgets");
            out.newLine();
            out.write("	add esp, 12");
            out.newLine();
            out.write(" push DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	call strchr");
            out.newLine();
            out.write("	add esp, 8");
            out.newLine();
            out.write("	cmp eax, 0");
            out.newLine();
            out.write("	je grace_gets_no_newline");
            out.newLine();
            out.write("	mov BYTE PTR [eax], 0");
            out.newLine();
            out.write("grace_gets_no_newline:");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("abs0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov eax, DWORD PTR [ebp+16]");
            out.newLine();
            out.write("	mov edx, 0");
            out.newLine();
            out.write("	cmp eax, edx");
            out.newLine();
            out.write("	jl abs_neg");
            out.newLine();
            out.write("	jmp abs_pos");
            out.newLine();
            out.write("abs_neg:");
            out.newLine();
            out.write("	neg eax");
            out.newLine();
            out.write("	mov esi, DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	mov DWORD PTR [esi], eax");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            out.write("abs_pos:");
            out.newLine();
            out.write("	mov esi, DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	mov DWORD PTR [esi], eax");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("ord0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov ebx, DWORD PTR [ebx+16]");
            out.newLine();
            out.write("	mov esi, DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	mov DWORD PTR [esi], ebx");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.write("chr0:");
            out.newLine();
            out.write("	push ebp");
            out.newLine();
            out.write("	mov ebp, esp");
            out.newLine();
           	out.write("	push edi");
            out.newLine();
            out.write("	push ebx");
            out.newLine();
            out.write("	push esi");
            out.newLine();
            out.write("	mov ebx, DWORD PTR [ebx+16]");
            out.newLine();
            out.write("	mov esi, DWORD PTR [ebp+12]");
            out.newLine();
            out.write("	mov DWORD PTR [esi], ebx");
            out.newLine();
            out.write("	pop esi");
            out.newLine();
            out.write("	pop ebx");
            out.newLine();
            out.write("	pop edi");
            out.newLine();
            out.write("	mov esp,ebp");
            out.newLine();
            out.write("	pop ebp");
            out.newLine();
            out.write("	ret");
            out.newLine();
            //-----------END----------------
            out.flush();
        }
        catch(IOException e){
            System.out.println("Could not write file");
            System.exit(1);}}
    void cr_data_seg(){
        try{
            out.newLine();
            out.write(".data");
            out.newLine();
            for(int i=0;i<data_seg.size();i++){
                String a="	string"+Integer.toString(i+1)+": "+".asciz"+"  "+data_seg.get(i);
                out.write(a);
                out.newLine();
                out.flush();
            }
                String a="	int_fmt: .asciz  "+'"'+"%d"+'"';
                out.write(a);
                out.newLine();
                out.flush();
                a="	char_fmt: .asciz  "+'"'+"%c"+'"';
                out.write(a);
                out.newLine();
                out.flush();
                a="	string_fmt: .asciz  "+'"'+"%c"+'\\'+"n"+'"';
                out.write(a);
                out.newLine();
                a="	printf_int: .asciz  "+'"'+"%d"+'\\'+"n"+'"';
                out.write(a);
                out.newLine();
                out.flush();
                a="	printf_char: .asciz  "+'"'+"%c"+'\\'+"n"+'"';
                out.write(a);
                out.newLine();
                out.flush();
                a="	printf_string: .asciz  "+'"'+"%s"+'\\'+"n"+'"';
                out.write(a);
                out.newLine();
                a="	prompt_int: .asciz  "+'"'+"Enter a number"+'\\'+"n"+'"';
                out.write(a);
                out.newLine();
                out.flush();
                a="	prompt_char: .asciz  "+'"'+"Enter a character"+'\\'+"n"+'"';
                out.write(a);
                out.newLine();
                out.flush();
                a="	prompt_string: .asciz  "+'"'+"Enter a string"+'"';
                out.write(a);
                out.newLine();
                out.flush();
                a="	prompt_string2: .asciz  "+'"'+"with length %d characters"+'\\'+"n"+'"';
                out.write(a);
                out.newLine();
                out.flush();
        }
        catch(IOException e){
            System.out.println("Could not write file");
            System.exit(1);
        }}
    void store(String reg,int co,int current_temps,int current_scope,Semantic s,Ir ir,ArrayList<String>cfunc,ArrayList<Integer> yolo,String r1,String r2){
    	try{
			String a="	mov ";
	        int flag1=0;
	        int k=current_temps;
	        while(k<ir.temp.size()){
	            //System.out.println(ir.temp.get(k).name);
	            if(ir.temp.get(k).name.equals(r1)){
	            	if(ir.temp.get(k).type.trim().equals("int")){
		                a=a+"DWORD PTR [ebp-"+Integer.toString(ir.temp.get(k).reg)+"]";
		                flag1=1;
		            }
		            else if(ir.temp.get(k).type.trim().equals("char")){
		                a=a+"BYTE PTR [ebp-"+Integer.toString(ir.temp.get(k).reg)+"]"+", al";
		                flag1=3;
		            }
	            }
	            char[] temp1=r1.toCharArray();
	            if(temp1[0]=='['){
	                char[] cleaner=r1.toCharArray();
	                cleaner[0]=' ';
	                cleaner[r1.length()-1]=' ';
	                String element=new String(cleaner);
	                element=element.trim();
	                if(ir.temp.get(k).name.equals(element) && ir.temp.get(k).type.trim().equals("int")){
	                	load("edi",co,current_temps,current_scope,s,ir,cfunc,yolo,element,r1);
	                    out.write(a+"DWORD PTR [edi]"+", "+reg);
	                    out.newLine();
	                    flag1=3;
	                }
	               	else if(ir.temp.get(k).name.equals(element) && ir.temp.get(k).type.trim().equals("char")){
		               	String storing=" ";
	               		storing="al";
		                load("edi",co,current_temps,current_scope,s,ir,cfunc,yolo,element,r1);
		                out.write(a+"BYTE PTR [edi]"+", "+storing);
		                out.newLine();
	                	flag1=3;
	                }
	            }
	            k++;
	        }
	        if(flag1==0){
	            char[] cleaner=r1.toCharArray();
	            if(cleaner[0]>='0' && cleaner[0]<='9'){
	            	System.out.println("This should never happen->Assembly error");
	            	System.exit(1);
	                a=a+r1;
	            }
	            else if(r1.equals("$$")){
			        out.write("	mov esi, DWORD PTR [ebp+"+Integer.toString(12)+"]");
			        out.newLine();
			        a=a+"DWORD PTR [esi]";
	            }
	            else{
		            //System.out.println("We are here1:"+cfunc.get(cfunc.size()-1)+" "+ir.q.get(i).r1);
		            ArrayList<Integer> element=s.get_place(r1,current_scope,cfunc.get(cfunc.size()-1));
		            //System.out.println("We are here1:"+element.get(2)+" "+current_scope);
		            if(element.get(4)==0){
			       		if(element.get(2)==current_scope+1){
			                if(element.get(3)==1){
			                    //variable
			                   	a=a+"DWORD PTR [ebp-"+Integer.toString(element.get(0))+"]";
			                }
			               	else{
			                    //argument
			                    int temp_total=12+element.get(6);
			                    if(element.get(5)==1){
			                    //by value
			                        a=a+"DWORD PTR [ebp+"+Integer.toString(temp_total)+"]";
			                    }
			                    else if(element.get(5)==2){
			                        //by ref
			                        out.write("	mov esi, DWORD PTR [ebp+"+Integer.toString(temp_total)+"]");
			                        out.newLine();
			                        a=a+"DWORD PTR [esi]";
			                    }
			               	}
			            }
			        	else{
				            for(int kalos=0;kalos<current_scope-element.get(2)+1;kalos++){
				               	//getting the correct AR
				                if(kalos==0){
				                    out.write("	mov esi, DWORD PTR [ebp+8]");
				                    out.newLine();
				                }
				                else{
				                    out.write("	mov esi, DWORD PTR [esi+8]");
				                    out.newLine();
				                }
				            }
				            if(element.get(3)==1){
				                //variable
				                a=a+"DWORD PTR [esi-"+Integer.toString(element.get(0))+"]";
				            }
				            else{
				                //argument
				                int temp_total=12+element.get(6);
				                if(element.get(5)==1){
				                    //by value
				                    a=a+"DWORD PTR [esi+"+Integer.toString(temp_total)+"]";
				                }
				                else if(element.get(5)==2){
				                    //by ref
				                    out.write("	mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                    a=a+"DWORD PTR [esi]";
				                }
			            	}
			        	}
			    	}
			        else{
			            if(element.get(2)==current_scope+1){
			                if(element.get(3)==1){
			                    //variable
			               		String storing=" ";
			               		storing="al";
			                    a=a+"BYTE PTR [ebp-"+Integer.toString(element.get(0))+"], "+storing;
			                    out.write(a);
			                    out.newLine();
			                	flag1=3;
			                }
			                else{
				                //argument
				                int temp_total=12+element.get(6);
				                if(element.get(5)==1){
				                    //by value
		               				String storing=" ";
				               		storing="al";
				                    a=a+"BYTE PTR [ebp+"+Integer.toString(temp_total)+"], "+storing;
				                    out.write(a);
				                    out.newLine();
				                	flag1=3;
				                }
				                else if(element.get(5)==2){
				                    //by ref
		               				String storing=" ";
			               			storing="al";
				                    out.write("	mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                    a=a+"BYTE PTR [esi], "+storing;
				                    out.write(a);
				                    out.newLine();
				                	flag1=3;
				                }
			                }
			            }
			            else{
			               	for(int kalos=0;kalos<current_scope-element.get(2)+1;kalos++){
			                        //getting the correct AR
			                        if(kalos==0){
			                            out.write("	mov esi, DWORD PTR [ebp+8]");
			                            out.newLine();
			                        }
			                        else{
			                            out.write("	mov esi, DWORD PTR [esi+8]");
			                            out.newLine();
			                        }
			                }
			                if(element.get(3)==1){
			                        //variable
		               				String storing="al";
				                    a=a+"BYTE PTR [esi-"+Integer.toString(ir.temp.get(k).reg)+"], "+storing;
				                    out.write(a);
				                    out.newLine();
				                	flag1=3;
			                }
			                else{
			                    //argument
			                    int temp_total=12+element.get(6);
			                    if(element.get(5)==1){
			                       	//by value
				               		String storing="al";
				                    a=a+"BYTE PTR [esi+"+Integer.toString(temp_total)+"], "+storing;
				                    out.write(a);
				                    out.newLine();
				                	flag1=3;
			                    }
			                    else if(element.get(5)==2){
			                        //by ref
				               		String storing="al";
				                    out.write("	mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                    a=a+"BYTE PTR [esi], "+storing;
				                    out.write(a);
				                    out.newLine();
				                	flag1=3;
			                    }
			               	}
			            }
			        }
		        }
		   	}
			if(flag1!=3){
				out.write(a+", "+reg);
			    out.newLine();
			}
		}
	   	catch(IOException e){
	            System.out.println("Could not write file");
	            System.exit(1);
	    }}
    void load(String reg,int co,int current_temps,int current_scope,Semantic s,Ir ir,ArrayList<String>cfunc,ArrayList<Integer> yolo,String r1,String r2){
    	try{
			String a="	mov "+reg;
	        int flag1=0;
	        int k=current_temps;
	        while(k<ir.temp.size()){
	            //System.out.println(r1);
	            if(ir.temp.get(k).name.equals(r1)){
	            	if(ir.temp.get(k).type.trim().equals("int")){
		                a=a+", DWORD PTR [ebp-"+Integer.toString(ir.temp.get(k).reg)+"]";
		                flag1=1;
		            }
		            else if(ir.temp.get(k).type.trim().equals("char")){
		                a="	mov eax, DWORD PTR [ebp-"+Integer.toString(ir.temp.get(k).reg)+"]";
		                flag1=1;
		            }
	            }
	            char[] temp1=r1.toCharArray();
	            if(temp1[0]=='['){
	                char[] cleaner=r1.toCharArray();
	                cleaner[0]=' ';
	                cleaner[r1.length()-1]=' ';
	                String element=new String(cleaner);
	                element=element.trim();
	                if(ir.temp.get(k).name.equals(element) && ir.temp.get(k).type.trim().equals("int")){
	                	load("edi",co,current_temps,current_scope,s,ir,cfunc,yolo,element,r1);
	                    a=a+", DWORD PTR [edi]";
	                    out.write(a);
	                    out.newLine();
	                    flag1=3;
	                }
	               	else if(ir.temp.get(k).name.equals(element) && ir.temp.get(k).type.trim().equals("char")){
                        load("edi",co,current_temps,current_scope,s,ir,cfunc,yolo,element,r1);
                        a="movzx eax, BYTE PTR [edi]";
                        out.write(a);
                        out.newLine();
                        flag1=3;
	                }
	            }
	            k++;
	        }
	        if(r1.contains("'")){
	           	char[] cleaner=r1.toCharArray();
	            cleaner[0]=' ';
	            cleaner[r1.length()-1]=' ';
	            String element=new String(cleaner);
	            element=element.trim();
	            a="	mov "+reg+", OFFSET FLAT:string"+pos_data;
	            out.write(a);
	            out.newLine();
	            out.write("	movzx eax, "+"BYTE PTR ["+reg+"]");
	            out.newLine();
	            flag1=3;
	            pos_data++;
	        }
	        if(flag1==0){
	            char[] cleaner=r1.toCharArray();
	            if(cleaner[0]>='0' && cleaner[0]<='9'){
	                a=a+", "+r1;
	            }
	            else{
		            //System.out.println("We are here1:"+cfunc.get(cfunc.size()-1)+" "+ir.q.get(i).r1);
		            ArrayList<Integer> element=s.get_place(r1,current_scope,cfunc.get(cfunc.size()-1));
		            //System.out.println("We are here1:"+element.get(2)+" "+current_scope);
		            if(element.get(4)==0){
			       		if(element.get(2)==current_scope+1){
			                if(element.get(3)==1){
			                    //variable
			                   	a=a+", DWORD PTR [ebp-"+Integer.toString(element.get(0))+"]";
			                }
			               	else{
			                    //argument
			                    int temp_total=12+element.get(6);
			                    if(element.get(5)==1){
			                    //by value
			                        a=a+", DWORD PTR [ebp+"+Integer.toString(temp_total)+"]";
			                    }
			                    else if(element.get(5)==2){
			                        //by ref
			                        out.write("	mov esi, DWORD PTR [ebp+"+Integer.toString(temp_total)+"]");
			                        out.newLine();
			                        a=a+", DWORD PTR [esi]";
			                    }
			               	}
			            }
			        	else{
				            for(int kalos=0;kalos<current_scope-element.get(2)+1;kalos++){
				               	//getting the correct AR
				                if(kalos==0){
				                    out.write("	mov esi, DWORD PTR [ebp+8]");
				                    out.newLine();
				                }
				                else{
				                    out.write("	mov esi, DWORD PTR [esi+8]");
				                    out.newLine();
				                }
				            }
				            if(element.get(3)==1){
				                //variable
				                a=a+", DWORD PTR [esi-"+Integer.toString(element.get(0))+"]";
				            }
				            else{
				                //argument
				                int temp_total=12+element.get(6);
				                if(element.get(5)==1){
				                    //by value
				                    a=a+", DWORD PTR [esi+"+Integer.toString(temp_total)+"]";
				                }
				                else if(element.get(5)==2){
				                    //by ref
				                    out.write("	mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                    a=a+", DWORD PTR [esi]";
				                }
			            	}
			        	}
			    	}
			        else{
			            if(element.get(2)==current_scope+1){
			                if(element.get(3)==1){
			                    //variable
				                out.write("	movzx eax, BYTE PTR [ebp-"+Integer.toString(element.get(0))+"]");
				                out.newLine();
				                flag1=3;
			                }
			                else{
				                //argument
				                int temp_total=12+element.get(6);
				                if(element.get(5)==1){
				                    //by value
					                out.write("	movzx eax, BYTE PTR [ebp+"+Integer.toString(temp_total)+"]");
					                out.newLine();
						            flag1=3;
				                }
				                else if(element.get(5)==2){
				                    //by ref
				                    out.write(" mov esi, DWORD PTR [ebp+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                    out.write("movzx eax, BYTE PTR [esi]");
				                    out.newLine();
				                    flag1=3;
				                }
			                }
			            }
			            else{
			               	for(int kalos=0;kalos<current_scope-element.get(2)+1;kalos++){
			                        //getting the correct AR
			                        if(kalos==0){
			                            out.write("	mov esi, DWORD PTR [ebp+8]");
			                            out.newLine();
			                        }
			                        else{
			                            out.write("	mov esi, DWORD PTR [esi+8]");
			                            out.newLine();
			                        }
			                }
			                if(element.get(3)==1){
			                        //variable
				                    out.write("	mov "+reg+", BYTE PTR [esi-"+Integer.toString(element.get(0))+"]");
				                    out.newLine();
				                    flag1=3;
			                }
			                else{
			                    //argument
			                    int temp_total=12+element.get(6);
			                    if(element.get(5)==1){
			                       	//by value
					                out.write("	mov "+reg+", BYTE PTR [esi+"+Integer.toString(temp_total)+"]");
					                out.newLine();
					                flag1=3;
			                    }
			                    else if(element.get(5)==2){
			                        //by ref
				                    out.write(" mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                    out.write("movzx eax, BYTE PTR [esi]");
				                    out.newLine();
				                    flag1=3;
			                    }
			               	}
			            }
			        }
		        }
		   	}
			if(flag1!=3){
				out.write(a);
			    out.newLine();
			}
		}
	   	catch(IOException e){
	            System.out.println("Could not write file");
	            System.exit(1);
	    }}
    void load_address(String reg,int co,int current_temps,int current_scope,Semantic s,Ir ir,ArrayList<String>cfunc,ArrayList<Integer> yolo,String r1,String r2){
    	try{
			String a="	lea "+reg;
	       	int flag1=0;
	        int k=current_temps;
	        while(k<ir.temp.size()){
	            //System.out.println("I am inner load address loop"+r1);
	            if(ir.temp.get(k).name.equals(r1)){
	            	if(ir.temp.get(k).type.trim().equals("int")){
		                a=a+", [ebp-"+Integer.toString(ir.temp.get(k).reg)+"]";
		                flag1=1;
		            }
		            else if(ir.temp.get(k).type.trim().equals("char")){
		                a=a+", [ebp-"+Integer.toString(ir.temp.get(k).reg)+"]";
		                flag1=1;
		            }
	            }
	            char[] temp1=r1.toCharArray();
	           	if(temp1[0]=='['){
					//System.out.println(ir.temp.get(k).name);
	             	char[] cleaner=r1.toCharArray();
	                cleaner[0]=' ';
	                cleaner[r1.length()-1]=' ';
	                String element=new String(cleaner);
	                element=element.trim();
	                if(ir.temp.get(k).name.equals(element) && ir.temp.get(k).type.trim().equals("int")){
						load(reg,co,current_temps,current_scope,s,ir,cfunc,yolo,element,r2);
	                    flag1=3;
	                }
	                else if(ir.temp.get(k).name.equals(element) && ir.temp.get(k).type.trim().equals("char")){
						load(reg,co,current_temps,current_scope,s,ir,cfunc,yolo,element,r2);
	                    flag1=3;
	                }
	         	}
	            k++;
	        }
	        char[] temp55=r1.toCharArray();
	       	if(r1.equals("string") || temp55[0]=='"'){
	            char[] cleaner=r1.toCharArray();
	            cleaner[0]=' ';
	            cleaner[r1.length()-1]=' ';
	            String element=new String(cleaner);
	            element=element.trim();
	            out.write("	mov "+reg+", OFFSET FLAT:string"+pos_data);
	            out.newLine();
                out.write(" lea eax"+", ["+reg+"]");
                out.newLine();
	            flag1=3;
				pos_data++;
	        }
	        if(r1.contains("'")){
	            char[] cleaner=r1.toCharArray();
	            cleaner[0]=' ';
	            cleaner[r1.length()-1]=' ';
	            String element=new String(cleaner);
	            element=element.trim();
	            out.write("	mov "+reg+", OFFSET FLAT:string"+pos_data);
	            out.newLine();
	            flag1=3;
				pos_data++;
	        }
	        if(flag1==0){
	            char[] cleaner=r1.toCharArray();
	            if(cleaner[0]>='0' && cleaner[0]<='9'){
	            	System.out.println("This should never happen->Assembly");
	            	System.exit(1);
	                a=a+", "+r1;
	            }
	            else{
	                //System.out.println("We are here1:"+cfunc.get(cfunc.size()-1)+" "+ir.q.get(i).r1);
	                ArrayList<Integer> element=s.get_place(r1,current_scope,cfunc.get(cfunc.size()-1));
	                //System.out.println("We are here1:"+element.get(2)+" "+current_scope);
	                if(element.get(4)==0){
		                if(element.get(2)==current_scope+1){
		                    if(element.get(3)==1){
		                    	//variable
		                        a=a+", [ebp-"+Integer.toString(element.get(0))+"]";
		                    }
		                    else{
		                        //argument
		                        int temp_total=12+element.get(6);
		                        if(element.get(5)==1){
		                            //by value
		                            a=a+", [ebp+"+Integer.toString(temp_total)+"]";
		                        }
		                        else if(element.get(5)==2){
		                            //by ref
		                            out.write("	mov esi, DWORD PTR [ebp+"+Integer.toString(temp_total)+"]");
		                            out.newLine();
		                            a=a+", [esi]";
		                        }
		                    }
		                }
		                else{
		                    for(int kalos=0;kalos<current_scope-element.get(2)+1;kalos++){
		                     	//getting the correct AR
		                       if(kalos==0){
		                            out.write("	mov esi, DWORD PTR [ebp+8]");
		                            out.newLine();
		                       	}
		                        else{
		                            out.write("	mov esi, DWORD PTR [esi+8]");
		                            out.newLine();
		                        }
		                    }
		                    if(element.get(3)==1){
		                        //variable
		                        a=a+", [esi-"+Integer.toString(element.get(0))+"]";
		                    }
		                    else{
		                        //argument
		                        int temp_total=12+element.get(6);
		                        if(element.get(5)==1){
		                            //by value
		                            a=a+", [esi+"+Integer.toString(temp_total)+"]";
		                        }
		                        else if(element.get(5)==2){
		                            //by ref
		                            out.write("	mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
		                            out.newLine();
		                           	a=a+", [esi]";
		                        }
		                    }
		                }
		            }
		            else{
		                if(element.get(2)==current_scope+1){
		                    if(element.get(3)==1){
		                        //variable
			                    out.write(a+", [ebp-"+Integer.toString(element.get(0))+"]");
			                   	out.newLine();
			                    flag1=3;
		                    }
		                    else{
		                        //argument
		                        int temp_total=12+element.get(6);
		                        if(element.get(5)==1){
		                            //by value
				                    out.write(a+", [ebp+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                    flag1=3;
		                        }
		                        else if(element.get(5)==2){
		                            //by ref
                                    out.write(" mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
                                    out.newLine();
                                    a=a+", [esi]";
		                            flag1=3;
		                       	}
		                    }
		            	}
		                else{
		                    for(int kalos=0;kalos<current_scope-element.get(2)+1;kalos++){
		                        //getting the correct AR
		                        if(kalos==0){
		                            out.write("	mov esi, DWORD PTR [ebp+8]");
		                            out.newLine();
		                        }
		                        else{
		                            out.write("	mov esi, DWORD PTR [esi+8]");
		                            out.newLine();
		                        }
		                  	}
		                    if(element.get(3)==1){
		                        //variable
			                    out.write(a+", [esi-"+Integer.toString(element.get(0))+"]");
			                    out.newLine();
			                    flag1=3;
		                    }
		                    else{
		                       	//argument
		                        int temp_total=12+element.get(6);
		                        if(element.get(5)==1){
		                           	//by value
				                    out.write(a+", [esi+"+Integer.toString(temp_total)+"]");
				                    out.newLine();
				                   	flag1=3;
		                        }
		                        else if(element.get(5)==2){
		                            //by ref
                                    out.write(" mov esi, DWORD PTR [esi+"+Integer.toString(temp_total)+"]");
                                    out.newLine();
                                    a=a+", [esi]";
		                            flag1=3;
		                        }
		                    }
		               	}
		            }
	            }
	       	}
	        if(flag1!=3){
	            out.write(a);
	            out.newLine();
	        }
	    }
        catch(IOException e){
            System.out.println("Could not write file");
            System.exit(1);}}
    void cr_asb(Semantic s,Ir ir,ArrayList<String>cfunc,ArrayList<Integer> yolo){
        try{
            int argu=0;
            int total_bytes=0;
            int current_temps=0;
            int total_temps_bytes=0;
            int current_char_temps=0;
            int current_scope=0;
            String returns=null;
            int jal;
            int co=0;
            //ir.printIr();
            for(int i=cir;i<ir.q.size();i++){
                //System.out.println("# "+Integer.toString(i)+":"+ir.q.get(i).op.trim()+","+ir.q.get(i).r1.trim()+","+ir.q.get(i).r2.trim()+","+ir.q.get(i).r3.trim());
                //System.out.println(total_temps);
                if(ir.q.get(i).op.trim().equals("par")==false){
	                out.write("# "+Integer.toString(i)+":"+ir.q.get(i).op.trim()+","+ir.q.get(i).r1.trim()+","+ir.q.get(i).r2.trim()+","+ir.q.get(i).r3.trim());
	                out.newLine();
	                out.write("instruction_label"+ir.q.get(i).ticket+":");
	                out.newLine();}
                if(ir.q.get(i).op.equals("+")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	if(ir.q.get(i).r1.trim().equals("base")==false){
                		load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	}
                	else{
	                	out.write("	mov edx, 0");
	                	out.newLine();
                	}
                	out.write("	add eax, edx");
                	out.newLine();
					store("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals("-")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	out.write("	sub eax, edx");
                	out.newLine();
					store("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals("%")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	out.write("	cdq");
                	out.newLine();
                	load("ecx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	out.write("	idiv ecx");
                	out.newLine();
					store("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals("*")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	load("ecx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	out.write("	imul ecx");
                	out.newLine();
					store("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals("uminus")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r1);
                	out.write("	neg eax");
                	out.newLine();
					store("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals("/")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	out.write("	cdq");
                	out.newLine();
                	load("ecx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	out.write("	idiv ecx");
                	out.newLine();
					store("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals(":=")){
                	load("ecx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r1);
					store("ecx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals("=")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	out.write("	cmp eax, edx");
                	out.newLine();
	                out.write("	jz instruction_label"+ir.q.get(i).r3);
	                out.newLine();}
                else if(ir.q.get(i).op.equals("#")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	out.write("	cmp eax, edx");
                	out.newLine();
	                out.write("	jnz instruction_label"+ir.q.get(i).r3);
	                out.newLine();}
                else if(ir.q.get(i).op.equals(">")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r1);
                	load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r2);
                	out.write("	cmp eax, edx");
                	out.newLine();
	                out.write("	jg instruction_label"+ir.q.get(i).r3);
	                out.newLine();}
                else if(ir.q.get(i).op.equals("<")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r1);
                	load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r2);
                	out.write("	cmp eax, edx");
                	out.newLine();
	                out.write("	jl instruction_label"+ir.q.get(i).r3);
	                out.newLine();}
                else if(ir.q.get(i).op.equals("<=")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r1);
                	load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r2);
                	out.write("	cmp eax, edx");
                	out.newLine();
	                out.write("	jle instruction_label"+ir.q.get(i).r3);
	                out.newLine();}
                else if(ir.q.get(i).op.equals(">=")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r1);
                	load("edx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r2);
                	out.write("	cmp eax, edx");
                	out.newLine();
	                out.write("	jge instruction_label"+ir.q.get(i).r3);
	                out.newLine();}
                else if(ir.q.get(i).op.equals("par")){
                	if(co==0){
	                	for(int here=i;here<ir.q.size();here++){
	                		if(ir.q.get(here).op.equals("par")){
	                			co=here;
	                		}
	                		else{
	                			here=ir.q.size()+2;
	                		}
	                	}
	                }
	                if(ir.q.get(co).r2.trim().equals("RET")==false){
		                out.write("# "+Integer.toString(co)+":"+ir.q.get(co).op.trim()+","+ir.q.get(co).r1.trim()+","+ir.q.get(co).r2.trim()+","+ir.q.get(co).r3.trim());
		                out.newLine();
		                out.write("instruction_label"+ir.q.get(co).ticket+":");
		                out.newLine();
		            }
                	if(ir.q.get(co).r2.equals("V")){
	                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(co).r1,ir.q.get(co).r1);
	                	out.write("	push eax");
	                	out.newLine();
	                	argu=argu+4;
                	}
                	else if(ir.q.get(co).r2.equals("R")){
						//System.out.println("# "+Integer.toString(i+1)+":"+ir.q.get(i+1).op.trim()+","+ir.q.get(i).r1.trim()+","+ir.q.get(i).r2.trim()+","+ir.q.get(i).r3.trim());
	                	load_address("esi",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(co).r1,ir.q.get(co).r1);
	                	out.write("	push esi");
	                	out.newLine();
	                	argu=argu+4;
                	}
                	co--;}
                else if(ir.q.get(i).op.equals("array")){
                	load("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r2,ir.q.get(i).r1);
                	int k=current_temps;
					while(k<ir.temp.size()){
						//System.out.println("i am array loop");
			            if(ir.temp.get(k).name.equals(ir.q.get(i).r3) && ir.temp.get(k).type.trim().equals("int")){
							//System.out.println("i am inner array loop");
			               	out.write("	mov ecx, 4");
			                out.newLine();
			                k=ir.temp.size()+5;
			            }
			            else if(ir.temp.get(k).name.equals(ir.q.get(i).r3) && ir.temp.get(k).type.trim().equals("char")){
							//System.out.println("i am inner array loop");
			                out.write("	mov ecx, 1");
			                out.newLine();
			                k=ir.temp.size()+5;
			            }
						k++;
					}
                	out.write("	imul ecx");
                	out.newLine();
                	load_address("ecx",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r1,ir.q.get(i).r2);
                	out.write("	add eax, ecx");
                	out.newLine();
					store("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i).r3,ir.q.get(i).r1);}
                else if(ir.q.get(i).op.equals("call")){
                    int end=s.fscope(ir.q.get(i).r3);
                    //System.out.println(end);
                    //System.exit(1);
                    //System.out.println(current_scope);
                    //System.out.println("--------------");
                    co=0;
                    if(ir.q.get(i).r3.equals("strlen") || ir.q.get(i).r3.equals("strcpy") || ir.q.get(i).r3.equals("strcmp") || ir.q.get(i).r3.equals("strcat") && end!=0){
		                out.write("	call "+ir.q.get(i).r3);
	                    out.newLine();
	                    out.write("	add esp, "+Integer.toString(argu));
	                    out.newLine();
	                    if(ir.q.get(i).r3.equals("strlen") || ir.q.get(i).r3.equals("strcmp")){
							store("eax",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i-1).r1,ir.q.get(i-1).r1);
	                    }
	                }
	                else{
	                	if(ir.q.get(i-1).r2.equals("RET")){
			                out.write("# "+Integer.toString(i-1)+":"+ir.q.get(i-1).op.trim()+","+ir.q.get(i-1).r1.trim()+","+ir.q.get(i-1).r2.trim()+","+ir.q.get(i-1).r3.trim());
			                out.newLine();
		                	load_address("esi",pos_data,current_temps,current_scope,s,ir,cfunc,yolo,ir.q.get(i-1).r1,ir.q.get(i-1).r1);
		                	out.write("	push esi");
		                	out.newLine();
		                	argu=argu+8;
	                	}
	                    if(ir.q.get(i-1).r2.equals("RET")==false){
	                    	out.write("	sub esp, 4");
	                    	out.newLine();
	                    	argu=argu+8;
	                    }
	                    if(current_scope<end){
	                        out.write("	push ebp");
	                        out.newLine();
	                    }
	                    else if(current_scope>=end){
	                        if(end==current_scope){
	                            out.write("	mov esi, DWORD PTR [ebp+8]");
	                            out.newLine();
		                        out.write("	push esi");
		                        out.newLine();
	                        }
	                        else{
	                            for(int op=0;op<current_scope-end;op++){
	                                if(op==0){
	                                    out.write("	mov esi,DWORD PTR [ebp+8]");
	                                    out.newLine();
	                                }
	                                else{
	                                    out.write("	mov esi,DWORD PTR [esi+8]");
	                                    out.newLine();
	                                }
		                            out.write("	push esi");
		                            out.newLine();
	                            }
	                        }
	                    }
		                out.write("	call "+ir.q.get(i).r3+Integer.toString(end));
	                    out.newLine();
	                    out.write("	add esp, "+Integer.toString(argu));
	                    out.newLine();
	                }
                    argu=0;}
                else if(ir.q.get(i).op.equals("RET")){
                        out.write("	pop esi");
                        out.newLine();
                        out.write("	pop ebx");
                        out.newLine();
                        out.write("	pop edi");
                        out.newLine();
                        if(s.symbol_table.size()>=1){
                            out.write("	add esp, "+Integer.toString(total_bytes));
                            out.newLine();
                        }
                        out.write(" mov esp, ebp");
                        out.newLine();
                        out.write("	pop ebp");
                        out.newLine();
                        out.write("	ret");
                        out.newLine();}
                else if(ir.q.get(i).op.equals("unit")){
                    //System.out.println(s.symbol_table.size());
                    current_scope=s.fscope(ir.q.get(i).r1);
                    if(s.symbol_table.size()==1){
                        out.write(mfunc+":");
                        out.newLine();
                    }
                    else{
                        out.write(ir.q.get(i).r1+Integer.toString(s.fscope(ir.q.get(i).r1))+":");
                        out.newLine();
                    }
                    out.write("	push ebp");
                    out.newLine();
                    out.write(" mov ebp, esp");
                    out.newLine();
                    if(cfunc.size()>0){
                        total_bytes=s.locals(cfunc.get(cfunc.size()-1));
                    }
                    total_temps_bytes=total_bytes+4;
                    current_temps=total_temps;
                    int k=total_temps;
                    int op=total_temps;
                    //System.out.println(total_bytes);
                    while(k<ir.temp.size()){
                        //System.out.println(ir.temp.get(k).name);
                        if(ir.temp.get(k).type.trim().equals("int")){
                            ir.temp.get(k).reg=total_bytes+4;
                            total_bytes=total_bytes+4;
                            //System.out.println(ir.temp.get(k).reg);
                        }
                        k++;
                    }
                    while(op<ir.temp.size()){
                        if(ir.temp.get(op).type.trim().equals("char")){
                            total_bytes=total_bytes+1;
                            ir.temp.get(op).reg=total_bytes+1;
                            current_char_temps=current_char_temps+1;
                            //System.out.println(ir.temp.get(op).reg);
                        }
                        op++;
                    }
                    current_char_temps=total_bytes-current_char_temps;
                    total_bytes=total_bytes+((total_bytes-total_temps_bytes)%4);
                    total_temps=total_temps+(ir.temp.size()-total_temps);
                    //System.out.println(total_bytes);
                    //System.out.println(total_bytes-current_char_temps);
                    out.write("	sub esp, "+Integer.toString(total_bytes));
                    out.newLine();
                    out.write("	push edi");
                    out.newLine();
                    out.write("	push ebx");
                    out.newLine();
                    out.write("	push esi");
                    out.newLine();}
                else if(ir.q.get(i).op.equals("endu")){
                    out.write("	pop esi");
                   	out.newLine();
                    out.write("	pop ebx");
                    out.newLine();
                    out.write("	pop edi");
                    out.newLine();
                    if(s.symbol_table.size()>=1){
                        out.write("	add esp, "+Integer.toString(total_bytes));
                        out.newLine();
                    }
                    out.write(" mov esp, ebp");
                    out.newLine();
                    out.write("	pop ebp");
                    out.newLine();
                    out.write("	ret");
                    out.newLine();}
                else if(ir.q.get(i).op.equals("jump")){
                    //System.out.println(ir.q.get(i).r3);
                    //System.out.println(ir.q.get(i-1).op.equals(">=")==false);
	                out.write("	jmp instruction_label"+ir.q.get(i).r3);
	                out.newLine();}
                else{
                    System.out.println(ir.q.get(i).op);
                    System.exit(1);}
                //-------------------ENDOFASSEMBLY---------------
                //System.out.println("-------------------------------");
                out.newLine();
            }
            out.flush();
        }
        catch(IOException e){
            System.out.println("Could not write file");
            System.exit(1);}
        cir=cir+(ir.q.size()-cir);
    }}