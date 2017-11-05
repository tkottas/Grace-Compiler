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

class Semantic{
    ArrayList<LinkedHashMap<String,Entry>> symbol_table;

    Semantic(){symbol_table=new ArrayList<LinkedHashMap<String,Entry>>();}
    void enter(){
        LinkedHashMap<String,Entry> temp = new LinkedHashMap<String,Entry>();
        symbol_table.add(temp);}
    void insert(LinkedHashMap<String,Entry> temp,int depth){symbol_table.set(depth,temp);}
    void insert2(Var temp,int depth){
        LinkedHashMap<String,Entry> s=symbol_table.get(depth);
        s.put(temp.id,temp);
        symbol_table.set(depth,s);}
    void insert3(Array temp,int depth){
        LinkedHashMap<String,Entry> s=symbol_table.get(depth);
        s.put(temp.id,temp);
        symbol_table.set(depth,s);}
    void insert4(Func temp,int depth){
        LinkedHashMap<String,Entry> s=symbol_table.get(depth);
        s.put(temp.id,temp);
        symbol_table.set(depth,s);}
    void exit(){symbol_table.remove(symbol_table.size()-1);}
    void stdin(){
        LinkedHashMap<String,Entry> temp=new LinkedHashMap<String,Entry>();
        for(int i=0;i<=13;i=i+1){
            if(i==0){
                Func temp2 = new Func("puti",0,"nothing",1);
                Var temp3 = new Var("n","int",0);
                temp2.set_v(temp3);
                temp2.set_r1(0);
                temp.put("puti",temp2);
            }
            if(i==1){
                Func temp2 = new Func("putc",0,"nothing",1);
                Var temp3 = new Var("c","char",0);
                temp2.set_v(temp3);
                temp2.set_r1(0);
                temp.put("putc",temp2);
            }
            else if(i==2){
                ArrayList<String> temp4 = new ArrayList<String>();
                temp4.add("0");
                Func temp2 = new Func("puts",0,"nothing",1);
                Array temp3 = new Array("s","char",0,temp4);
                temp3.ind.add("0");
                temp2.set_ar(temp3);
                temp2.set_r2(1);
                temp.put("puts",temp2);
            }
            else if(i==3){
                Func temp2 = new Func("geti",0,"int",1);
                temp.put("geti",temp2);
            }
            else if(i==4){
                Func temp2 = new Func("getc",0,"char",1);
                temp.put("getc",temp2);
            }
            else if(i==5){
                ArrayList<String> temp4 = new ArrayList<String>();
                temp4.add("0");
                Func temp2 = new Func("gets",0,"nothing",1);
                Var temp5 = new Var("n","int",0);
                Array temp3 = new Array("s","char",0,temp4);
                temp3.ind.add("0");
                temp2.set_v(temp5);
                temp2.set_r1(0);
                temp2.set_ar(temp3);
                temp2.set_r2(1);
                temp.put("gets",temp2);
            }
            else if(i==6){
                Func temp2 = new Func("abs",0,"int",1);
                Var temp3 = new Var("n","int",0);
                temp2.set_v(temp3);
                temp2.set_r1(0);
                temp.put("abs",temp2);
            }
            else if(i==7){
                Func temp2 = new Func("ord",0,"int",1);
                Var temp3 = new Var("c","char",0);
                temp2.set_v(temp3);
                temp2.set_r1(0);
                temp.put("ord",temp2);
            }
            else if(i==8){
                Func temp2 = new Func("chr",0,"char",1);
                Var temp3 = new Var("n","int",0);
                temp2.set_v(temp3);
                temp2.set_r1(0);
                temp.put("chr",temp2);
            }
            else if(i==9){
                ArrayList<String> temp4 = new ArrayList<String>();
                temp4.add("0");
                Func temp2 = new Func("strlen",0,"int",1);
                Array temp3 = new Array("s","char",0,temp4);
                temp3.ind.add("0");
                temp2.set_ar(temp3);
                temp2.set_r2(1);
                temp.put("strlen",temp2);
            }
            else if(i==10){
                ArrayList<String> temp4 = new ArrayList<String>();
                temp4.add("0");
                Func temp2 = new Func("strcmp",0,"int",1);
                Array temp5 = new Array("s1","char",0,temp4);
                Array temp3 = new Array("s2","char",0,temp4);
                temp3.ind.add("0");
               	temp5.ind.add("0");
                temp2.set_ar(temp5);
                temp2.set_r2(1);
                temp2.set_ar(temp3);
                temp2.set_r2(1);
                temp.put("strcmp",temp2);
            }
            else if(i==11){
                ArrayList<String> temp4 = new ArrayList<String>();
                temp4.add("0");
                Func temp2 = new Func("strcpy",0,"nothing",1);
                Array temp5 = new Array("trg","char",0,temp4);
                Array temp3 = new Array("src","char",0,temp4);
                temp3.ind.add("0");
                temp5.ind.add("0");
                temp2.set_ar(temp5);
                temp2.set_r2(1);
                temp2.set_ar(temp3);
                temp2.set_r2(1);
                temp.put("strcpy",temp2);
            }
            else if(i==12){
                ArrayList<String> temp4 = new ArrayList<String>();
                temp4.add("0");
                Func temp2 = new Func("strcat",0,"nothing",1);
                Array temp5 = new Array("s1","char",0,temp4);
                Array temp3 = new Array("s2","char",0,temp4);
                temp3.ind.add("0");
                temp5.ind.add("0");
                temp2.set_ar(temp5);
                temp2.set_r2(1);
                temp2.set_ar(temp3);
                temp2.set_r2(1);
                temp.put("strcat",temp2);
            }
        }
        symbol_table.set(0,temp);}
    int check_funDef(Func a,int depth,String cfunc){
        for(int i=0;i<symbol_table.size();i++){
            LinkedHashMap<String,Entry> h=symbol_table.get(i);
            if(i==symbol_table.size()-2){
                Func element=(Func)h.get(cfunc);
                for(Var v1:element.v)
                {
                    if(v1.id.equals(a.id))
                    {
                        System.out.println("Double Declared Fuction previously as parameter variable");
                        System.exit(1);
                    }
                }
                for(Array v2:element.ar)
                {
                    if(v2.id.equals(a.id))
                    {
                        System.out.println("Double Declared Fuction previously as parameter array");
                        System.exit(1);
                    }
                }
            }
            for (String key: h.keySet()){
                Entry temp=h.get(key);
                if(temp.id.equals(a.id) && temp.is_ty==0 && i==symbol_table.size()-1){
                    Func element=(Func)h.get(key);
                    if(element.declared==1){
                        System.out.println("Double Defined Function");
                        System.exit(1);
                    }
                    else{
                        if(element.pos.size()==a.pos.size()){
                            for(int j=0;j<element.pos.size();j++){
                                if(element.pos.get(j).equals(a.pos.get(j))==false){
                                    System.out.println("Double Defined Function");
                                    System.exit(1);
                                }
                            }
                            int j=0;
                            for(Var v1:element.v){
                                if(v1.type.equals(a.v.get(j))==false && element.ref1.get(j)!=a.ref1.get(j)){
                                    System.out.println("Double Defined Function");
                                    System.exit(1);
                                }
                            }
                            j=0;
                            for(Array v1:element.ar){
                                if(v1.type.equals(a.ar.get(j))==false){
                                    System.out.println("Double Defined Function");
                                    System.exit(1);
                                }
                            }
                            if(element.ret_t.equals(a.ret_t)==false){
                                System.out.println("Wrong return type");
                                System.exit(1);
                            }
                        }
                        else{
                            System.out.println("Wrong parameters for function declaration");
                            System.exit(1);
                        }
                    }
                }
                else if(temp.id.equals(a.id) && temp.is_ty==1 && i==symbol_table.size()-1){
                    System.out.println("Defined Function previously as Variable");
                    System.exit(1);
                }
                else if(temp.id.equals(a.id) && temp.is_ty==2 && i==symbol_table.size()-1){
                    System.out.println("Defined Function previously as Array");
                    System.exit(1);
                }
            }
        }
        return 0;}
    int check_funDec(Func a,int depth,String cfunc){
        for(int i=0;i<symbol_table.size();i++){
            LinkedHashMap<String,Entry> h=symbol_table.get(i);
            if(i==symbol_table.size()-2){
                Func element=(Func)h.get(cfunc);
                for(Var v1:element.v)
                {
                    if(v1.id.equals(a.id))
                    {
                        System.out.println("Double Declared Fuction previously as parameter variable");
                        System.exit(1);
                    }
                }
                for(Array v2:element.ar)
                {
                    if(v2.id.equals(a.id))
                    {
                        System.out.println("Double Declared Fuction previously as parameter array");
                        System.exit(1);
                    }
                }
            }
            for (String key: h.keySet()){
                Entry temp=h.get(key);
                if(temp.id.equals(a.id) && temp.is_ty==0 && i==symbol_table.size()-1){   
                    Func element=(Func)h.get(key);
                    if(element.declared==0){
                        System.out.println("Double Declared Function");
                        System.exit(1);
                    }
                    if(element.declared==1){
                        System.out.println("Declaration after definition");
                        System.exit(1);
                    }
                }
                else if(temp.id.equals(a.id) && temp.is_ty==1 && i==symbol_table.size()-1)
                {
                    System.out.println("Double Declared Function previously as Variable");
                    System.exit(1);
                }
                else if(temp.id.equals(a.id) && temp.is_ty==2 && i==symbol_table.size()-1)
                {
                    System.out.println("Double Declared Function previously as Array");
                    System.exit(1);
                }
                else if(temp.is_ty==0 && i==symbol_table.size()-1)
                {
                    Func element=(Func)h.get(key);
                    for(Var v1:element.v)
                    {
                        if(v1.id.equals(a.id))
                        {
                            System.out.println("Double Declared Fuction previously as parameter variable");
                            System.exit(1);
                        }
                    }
                    for(Array v2:element.ar)
                    {
                        if(v2.id.equals(a.id))
                        {
                            System.out.println("Double Declared Fuction previously as parameter array");
                            System.exit(1);
                        }
                    }
                }
            }
        }
        return 0;}
    void check_var(Var a,int depth,String cfunc){
        for(int i=0;i<symbol_table.size();i++){
            LinkedHashMap<String,Entry> h=symbol_table.get(i);
            if(i==symbol_table.size()-2){
                Func element=(Func)h.get(cfunc);
                for(Var v1:element.v)
                {
                    if(v1.id.equals(a.id))
                    {
                        System.out.println("Double Declared Variable previously as parameter variable");
                        System.exit(1);
                    }
                }
                for(Array v2:element.ar)
                {
                    if(v2.id.equals(a.id))
                    {
                        System.out.println("Double Declared Variable previously as parameter array");
                        System.exit(1);
                    }
                }
            }
            for (String key: h.keySet()){
                Entry temp=h.get(key);
                if(temp.is_ty==0 && temp.id.equals(a.id) && i==symbol_table.size()-1){
                    System.out.println("Function Defined as Variable");
                    System.exit(1);
                }
                else if(temp.id.equals(a.id) && temp.is_ty==1 && i==symbol_table.size()-1)
                {
                    System.out.println("Double Declared Variable");
                    System.exit(1);
                }
                else if(temp.id.equals(a.id) && temp.is_ty==2 && i==symbol_table.size()-1)
                {
                    System.out.println("Double Declared Variable");
                    System.exit(1);
                }
                else if(temp.is_ty==0 && temp.id.equals(a.id) && i==symbol_table.size()-1){
                    System.out.println("Function Declared as Variable");
                    System.exit(1);
                }
            }
        }}
    void check_array(Array a,int depth,String cfunc){
        for(int i=0;i<symbol_table.size();i++){
            LinkedHashMap<String,Entry> h=symbol_table.get(i);
            if(i==symbol_table.size()-2){
                Func element=(Func)h.get(cfunc);
                for(Var v1:element.v)
                {
                    if(v1.id.equals(a.id))
                    {
                        System.out.println("Double Declared Array previously as parameter variable");
                        System.exit(1);
                    }
                }
                for(Array v2:element.ar)
                {
                    if(v2.id.equals(a.id))
                    {
                        System.out.println("Double Declared Array previously as parameter array");
                        System.exit(1);
                    }
                }
            }
            for (String key: h.keySet()){
                Entry temp=h.get(key);
                if(temp.is_ty==0 && temp.id.equals(a.id) && i==symbol_table.size()-1){
                    System.out.println("Function Declared as Variable");
                    System.exit(1);
                }
                else if(temp.id.equals(a.id) && temp.is_ty==1 && i==symbol_table.size()-1)
                {
                    System.out.println("Double Declared Variable");
                    System.exit(1);
                }
                else if(temp.id.equals(a.id) && temp.is_ty==2 && i==symbol_table.size()-1)
                {
                    System.out.println("Double Declared Variable");
                    System.exit(1);
                }
            }
        }}
    void check_if_decl(){
        LinkedHashMap<String,Entry> h=symbol_table.get(symbol_table.size()-1);
        for (String key: h.keySet()){
            Entry temp=h.get(key);
            if(temp.is_ty==0){
                Func temp2=(Func)h.get(key);
                if(temp2.declared==0){
                    System.out.println("Function no defined just declared");
                    System.exit(1);
                }
            }
        }}
    String check_call(String name,ArrayList<Eval> params,Ir ir){
      int i=symbol_table.size()-1;
      if(i<0){
        i=0;
      }
      while(i>=0){
        LinkedHashMap<String,Entry> h=symbol_table.get(i);
        Entry temp=h.get(name);
        if(temp!=null){
            if(temp.is_ty!=0){
                System.out.println("Variable Used As Function");
                System.exit(1);
            }
            else if(temp.is_ty==0){
                Func element=(Func)h.get(name);
                int k=0,l=0;
                if(element.pos.size()>0){
                    if(element.pos.size()==params.size()){
                        for(int j=0;j<element.pos.size();j++){
                            if(element.pos.get(j).equals("var")){
                                if(element.v.get(k).type.trim().equals(params.get(params.size()-1-j).type.trim())==false){
                                    //System.out.println(params.get(params.size()-1-j).type);
                                    System.out.println("Calling Undefined Function2");
                                    System.exit(1); 
                                }
                                if((params.get(params.size()-1-j).name.equals("int_const") || params.get(params.size()-1-j).name.equals("char")) && element.ref1.get(k)==1){
                                	System.out.println("You cannot pass constant as reference");
                                	System.exit(1);
                                }
                                if(element.ref1.get(k)==1 && params.get(params.size()-1-j).ind.size()==0 && params.get(params.size()-1-j).temp.name.equals("a")){
                                    ir.nextquad("par",params.get(params.size()-1-j).s_t,"R","-");
                                }
                                else if(element.ref1.get(k)==0 && params.get(params.size()-1-j).ind.size()==0 && params.get(params.size()-1-j).temp.name.equals("a")){
                                    ir.nextquad("par",params.get(params.size()-1-j).s_t,"V","-");
                                }
                                else if(element.ref1.get(k)==0 && params.get(params.size()-1-j).ind.size()!=0){
                                    ir.nextquad("par","["+params.get(params.size()-1-j).temp.name+"]","V","-");
                                }
                                else if(element.ref1.get(k)==1 && params.get(params.size()-1-j).ind.size()!=0){
                                    ir.nextquad("par","["+params.get(params.size()-1-j).temp.name+"]","R","-");
                                }
                                else if(element.ref1.get(k)==1 && params.get(params.size()-1-j).ind.size()==0 && params.get(params.size()-1-j).temp.name.equals("a")==false){
                                    ir.nextquad("par",params.get(params.size()-1-j).temp.name,"R","-");
                                }
                                else if(element.ref1.get(k)==0 && params.get(params.size()-1-j).ind.size()==0 && params.get(params.size()-1-j).temp.name.equals("a")==false){
                                    ir.nextquad("par",params.get(params.size()-1-j).temp.name,"V","-");
                                }

                                k++;
                            }
                            else{
                                //System.out.println("Func:"+name+" "+element.ar.get(l).id+" "+element.ar.get(l).type.trim());
                                //System.out.println("Arg"+params.get(params.size()-1-j).type.trim());
                                if(element.ar.get(l).type.trim().equals(params.get(params.size()-1-j).type.trim())==false || (params.get(params.size()-1-j).ad-params.get(params.size()-1-j).ind.size()!=element.ar.get(l).ind.size() && params.get(params.size()-1-j).name.equals("string")==false)){
                                    System.out.println("Calling Undefined Function3");
                                    System.exit(1); 
                                }
                                else if(element.ar.get(l).type.trim().equals(params.get(params.size()-1-j).type.trim())==false || (params.get(params.size()-1-j).ind.size()>1 && params.get(params.size()-1-j).name.equals("string"))){
                                    System.out.println("Calling Undefined Function4");
                                    System.exit(1);
                                }
                                if(params.get(params.size()-1-j).ad-params.get(params.size()-1-j).ind.size()>1){
                                	//System.out.println(params.get(params.size()-1-j).ind2.size());
                                	//System.out.println(params.get(params.size()-1-j).ad-params.get(params.size()-1-j).ind.size()-1);
                                	for(int op=1;op<element.ar.get(l).ind.size();op++){
                                		//System.out.println(params.get(params.size()-1-j).ind2.get(params.get(params.size()-1-j).ad-params.get(params.size()-1-j).ind.size()-2+op));
                                		if(element.ar.get(l).ind.get(op).equals(params.get(params.size()-1-j).ind2.get(params.get(params.size()-1-j).ad-params.get(params.size()-1-j).ind.size()-2+op))==false){
                                			System.out.println("Passing array out of bounds");
                                			System.exit(1);
                                		}
                                	}
                                }
                                //System.out.println(params.get(params.size()-1-j).name+params.get(params.size()-1-j).ind.size());
                                if(params.get(params.size()-1-j).ind.size()==1 && params.get(params.size()-1-j).name.equals("string")){
                                    ir.nextquad("par",params.get(params.size()-1-j).s_t,"R","-");
                                }
                                else if(params.get(params.size()-1-j).ind.size()==0){
                                	ir.nextquad("par",params.get(params.size()-1-j).s_t,"R","-");
                                }
                                else{
                                	ir.nextquad("par","["+params.get(params.size()-1-j).temp.name+"]","R","-");
                                }
                                l++;
                            }
                        }
                        if(element.ret_t.trim().equals("int")){
                            Temp w=ir.newtemp("int");
                            ir.nextquad("par",w.name,"RET","-");
                            ir.temp.add(w);
                        }
                        else if(element.ret_t.trim().equals("char")){
                            Temp w=ir.newtemp("char");
                            ir.nextquad("par",w.name,"RET","-");
                            ir.temp.add(w);
                        }
                        return element.ret_t.trim();
                    }
                }
                else{
                    if(element.pos.size()==params.size()){
                        if(element.ret_t.trim().equals("int")){
                            Temp w=ir.newtemp("int");
                            ir.nextquad("par",w.name,"RET","-");
                            ir.temp.add(w);
                        }
                        else if(element.ret_t.trim().equals("char")){
                            Temp w=ir.newtemp("char");
                            ir.nextquad("par",w.name,"RET","-");
                            ir.temp.add(w);
                        }
                        return element.ret_t.trim();
                    }
                }
                System.out.println("Calling Undefined Function4");
                System.exit(1);
            }
        }
        i--;
      }
      System.out.println("Calling Extinct Function");
      System.exit(1);
      return null;}
    String take_type(String name,String cfunc){
      int i=symbol_table.size()-1;
      while(i>=0){
        LinkedHashMap<String,Entry> v=symbol_table.get(i);
        Entry t=v.get(name);
        if(t!=null){
            if(t.is_ty==0){
                System.out.println("Function used as variable");
                System.exit(1);
            }
            return t.type;
        }
        else if(t==null && i>0){
            LinkedHashMap<String,Entry> v3=symbol_table.get(i-1);
            Entry f=v3.get(cfunc);
            if(f!=null){
                Func element=(Func)v3.get(cfunc);
                for(Var v1:element.v){
                    if(v1.id.equals(name)){
                        return v1.type;
                    }
                }
                for(Array v1:element.ar){
                    if(v1.id.equals(name)){
                        return v1.type;
                    }
                }
             return null;
            }
        }
        i--;
      }
      return null;}
    String check_exist(Eval name,String cfunc){
      int i=symbol_table.size()-1;
      while(i>=0){
        LinkedHashMap<String,Entry> v=symbol_table.get(i);
        Entry t=v.get(name.name);
        if(t!=null){
            if(t.is_ty==0){
                System.out.println("Function used as variable");
                System.exit(1);
            }
            else if(t.is_ty==1){
                Var element=(Var)v.get(name.name);
                if(name.ind.size()!=0){
                    System.out.println("1:Not Array used as Array1");
                    System.exit(1);
                }
                else if(name.ind.size()==0){
                    return "yes";
                }
            }
            else if(t.is_ty==2){
                Array element=(Array)v.get(name.name);
                //System.out.println(name.name+" "+name.ind.size()+" "+element.ind.size()+" ");
                if(name.ind.size()!=element.ind.size()){
                    System.out.println("1:Arrays with different dimensions1");
                    System.exit(1);
                }
                else if(name.ind.size()==element.ind.size()){
                	for(int k=0;k<element.ind.size();k++){
                		name.ind2.add(element.ind.get(k));
                	}
                    return "yes";
                }
            }
            System.out.println("No such variable");
            System.exit(1);
        }
        else if(t==null && i>0){
            LinkedHashMap<String,Entry> v3=symbol_table.get(i-1);
            Entry f=v3.get(cfunc);
            if(f!=null){
                Func element=(Func)v3.get(cfunc);
                for(Var v1:element.v){
                    if(name.ind.size()!=0 && v1.id.equals(name.name)){
                        System.out.println("1:Not Array used as Array2");
                        System.exit(1);
                    }
                    else if(v1.id.equals(name.name) && name.ind.size()==0){
                        return "yes";
                    }
                }
                for(Array v1:element.ar){
                    if(name.ind.size()!=v1.ind.size() && v1.id.equals(name.name)){
                        System.out.println("1:Arrays with different dimensions2");
                        System.exit(1);
                    }
                    else if(v1.id.equals(name.name) && name.ind.size()==v1.ind.size()){
	                	for(int k=0;k<v1.ind.size()-1;k++){
	                		name.ind2.add(v1.ind.get(k));
	                	}
                        return "yes";
                    }
                }
                return "no";
            }
        }
        i--;
      }
      System.out.println("Variable Doesn't Exist with name "+name.name);
      System.exit(1);
      return null;}
    String check_exist2(Eval name,String cfunc){
      int i=symbol_table.size()-1;
      while(i>=0){
        LinkedHashMap<String,Entry> v=symbol_table.get(i);
        Entry t=v.get(name.name);
        if(t!=null){
            if(t.is_ty==2 && name.is_p==1){
                Array element=(Array)v.get(name.name);
                if(name.ind.size()<element.ind.size()){
	                for(int k=0;k<element.ind.size();k++){
	                	name.ind2.add(element.ind.get(k));
	                }
                	name.ad=element.ind.size();
                    return "yes";
                }
            }
            return "no";
        }
        else if(t==null && i>0){
            LinkedHashMap<String,Entry> v3=symbol_table.get(i-1);
            Entry f=v3.get(cfunc);
            if(f!=null){
                Func element=(Func)v3.get(cfunc);
                for(Array v1:element.ar){
                    if(v1.id.equals(name.name) && name.ind.size()<v1.ind.size()){
                    	name.ad=v1.ind.size();
	                	for(int k=0;k<v1.ind.size();k++){
	                		name.ind2.add(v1.ind.get(k));
	                	}
                        return "yes";
                    }
                }
                return "no";
            }
        }
        i--;
      }
      return "no";}
    Array check_exist3(Eval name,String cfunc){
      int i=symbol_table.size()-1;
      while(i>=0){
        LinkedHashMap<String,Entry> v=symbol_table.get(i);
        Entry t=v.get(name.name);
        if(t!=null){
            if(t.is_ty==2){
                Array element=(Array)v.get(name.name);
                //System.out.println(name.name+" "+name.ind.size()+" "+element.ind.size()+" ");
                if(name.ind.size()>element.ind.size()){
                    System.out.println("Arrays with different dimensions1");
                    System.exit(1);
                }
                else{
                	for(int k=0;k<element.ind.size();k++){
                		name.ind2.add(element.ind.get(k));
                	}
                    return element;
                }
            }
            System.out.println("No such variable");
            System.exit(1);
        }
        else if(t==null && i>0){
            LinkedHashMap<String,Entry> v3=symbol_table.get(i-1);
            Entry f=v3.get(cfunc);
            if(f!=null){
                Func element=(Func)v3.get(cfunc);
                for(Array v1:element.ar){
                    if(name.ind.size()>v1.ind.size() && v1.id.equals(name.name)){
                        System.out.println("Arrays with different dimensions2");
                        System.exit(1);
                    }
                    else{
	                	for(int k=0;k<v1.ind.size();k++){
	                		name.ind2.add(v1.ind.get(k));
	                	}
                        return v1;
                    }
                }
            }
        }
        i--;
      }
      System.out.println("Variable Doesn't Exist with name "+name.name);
      System.exit(1);
      return null;}
    void check_ret_expr(String name,String rt){
        LinkedHashMap<String,Entry> h=symbol_table.get(symbol_table.size()-2);
        Entry f=h.get(name);
        if(f.is_ty!=0){
            System.out.println("Something is wrong with symbol_table");
            System.exit(1);
        }
        else{
            Func element=(Func)h.get(name);
            //System.out.println(rt.length()-1);
            if("nothing".equals(element.ret_t.trim())){
                System.out.println("Fuction type nothing->return is prohibited");
                System.exit(1);
            }
            else if("int".equals(element.ret_t.trim())){
                    if(rt.trim().equals(element.ret_t.trim())==false){
                        System.out.println("we have return "+element.ret_t+"we got "+rt);
                        System.exit(1);
                    }
            }
            else if("char".equals(element.ret_t.trim())){
                    if(rt.trim().equals(element.ret_t.trim())==false){
                        System.out.println("we have return "+element.ret_t+"we got "+rt);
                        System.exit(1);
                    }
            }
        }}
    void check_no_ret(String name,int a){
        LinkedHashMap<String,Entry> h=symbol_table.get(symbol_table.size()-2);
        Entry f=h.get(name);
        if(f.is_ty!=0){
            System.out.println("Something is wrong with symbol_table");
            System.exit(1);
        }
        else{
            Func element=(Func)h.get(name);
            if( ( "int".equals(element.ret_t.trim()) || "char".equals(element.ret_t.trim()) ) && a==0  ){
                System.out.println("Returnable function withought return "+element.ret_t);
                System.exit(1);
            }
        }}
    void check_ret_no_expr(String name,String rt){
        LinkedHashMap<String,Entry> h=symbol_table.get(symbol_table.size()-2);
        Entry f=h.get(name);
        if(f.is_ty!=0){
            System.out.println("Something is wrong with symbol_table");
            System.exit(1);
        }
        else{
            Func element=(Func)h.get(name);
            if("nothing".equals(element.ret_t.trim())==false){
                System.out.println("Return withought expr with type "+element.ret_t);
                System.exit(1);
            }
        }}
 	int locals(String cfunc){
 		//System.out.println(cfunc);
 		int total=0;
 		LinkedHashMap<String,Entry> h=symbol_table.get(symbol_table.size()-1);
 		for (String key: h.keySet()){
 			Entry temp=h.get(key);
 			if(temp.is_ty==1){
 				Var element=(Var)h.get(key);
 				if(element.type.trim().equals("int")){
                    if(element.reg==0){
 					  element.reg=total+4;
                    }
 					total=total+4;
 				}
 				//System.out.println("Variable: "+element.id+" "+element.reg);
 			}
 			else if(temp.is_ty==2){
 				Array element=(Array)h.get(key);
 				if(element.type.trim().equals("int")){
                    if(element.end==0){
 					  element.end=total+4;
                    }
 					int c=Integer.parseInt(element.ind.get(0).trim());
 					for(int i=1;i<element.ind.size();i++){
 						//System.out.println(c);
 						c=c*Integer.parseInt(element.ind.get(i).trim());
 					}
 					total=total+c*4;
                    if(element.reg==0){
 					  element.reg=total;
                    }
 				}
 				//System.out.println("Array: "+element.id+" "+element.end+" "+element.reg);
 			}
 		}
 		for (String key: h.keySet()){
 			Entry temp=h.get(key);
 			if(temp.is_ty==1){
 				Var element=(Var)h.get(key);
 				if(element.type.trim().equals("char")){
                    if(element.reg==0){
 					  element.reg=total+1;
                    }
  					total=total+1;
 				}
 				//System.out.println("Variable: "+element.id+" "+element.reg);
 			}
 			else if(temp.is_ty==2){
 				Array element=(Array)h.get(key);
 				if(element.type.trim().equals("char")){
                    if(element.end==0){
 					  element.end=total+1;
                    }
 					for(int i=0;i<element.ind.size();i++){
 						total=total+Integer.parseInt(element.ind.get(i).trim());
 					}
                    if(element.reg==0){
 					  element.reg=total;
                    }
 				}
 				//System.out.println("Array: "+element.id+" "+element.end+" "+element.reg);
 			}
 		}
 		//System.out.println("total:"+total);
 		//System.out.println("total mod:"+(total%4));
 		total=total+(total%4);
 		return total;}
 	int fscope(String cfunc){
 		int i=symbol_table.size()-1;
 		int scope=0;
 		if(i<0){
 			i=0;
 		}
 		while(i>=0){
 			LinkedHashMap<String,Entry> h=symbol_table.get(i);
 			Entry temp=h.get(cfunc);
 			if(temp!=null){
 				scope=i;
 				return scope;
 			}
 			i--;
 		}
 		return scope;}
 	int parameters(String cfunc){
        LinkedHashMap<String,Entry> h=symbol_table.get(symbol_table.size()-2);
        Entry temp1=h.get(cfunc);
        int total=0;
        int argu=0;
        if(temp1!=null){
            Func element=(Func)h.get(cfunc);
                //System.out.println(cfunc);
                int op=0;
                int k=0;   
                for(int i=0;i<element.pos.size();i++){
                    //System.out.println("We are here2:");
                    if(element.pos.get(i).equals("var")){
                        if(element.v.get(op).type.trim().equals("int")){
                            element.v.get(op).reg=4;
                        }
                        else{
                            element.v.get(op).reg=1;
                        }
                        argu=argu+4;
                        //System.out.println("We are here2:");
                        element.v.get(op).arg=argu;   
                        op++;
                    }
                    else{
                        if(element.ar.get(k).type.trim().equals("int")){
                            element.ar.get(k).end=4;
                            int c=Integer.parseInt(element.ar.get(k).ind.get(0).trim());
                            for(int j=1;j<element.ar.get(k).ind.size();j++){
                                //System.out.println(c);
                                c=c*Integer.parseInt(element.ar.get(k).ind.get(j).trim());
                            }
                            element.ar.get(k).reg=c*4;
                        }
                        else{
                            element.ar.get(k).end=total+1;
                            for(int j=0;j<element.ar.get(k).ind.size();j++){
                                total=total+Integer.parseInt(element.ar.get(k).ind.get(j).trim());
                            }
                            element.ar.get(k).reg=total;
                        }
                        argu=argu+4;
                        element.ar.get(k).arg=argu; 
                        k++;
                    }
                }
        }
 		return total;}
 	ArrayList<Integer> get_place(String name,int current_scope,String cfunc){
 		ArrayList<Integer> total=new ArrayList<Integer>();
 		int i=symbol_table.size()-1;
 		//System.out.println(name);
 		while(i>=0){
 			LinkedHashMap<String,Entry> h=symbol_table.get(i);
 			if(i==symbol_table.size()-1){
	 			Entry temp=h.get(name);
	 			if(temp!=null){
		 			if(temp.is_ty==1){
		 				Var element=(Var)h.get(name);
		 				total.add(element.reg);
		 				total.add(element.end);
		 				total.add(i); //the scope this exist
		 				total.add(1); //is variable or argument 1 is variable 2 is argument
		 				if(element.type.trim().equals("int")){
		 					total.add(0);
		 				}
		 				else{
		 					total.add(1);
		 				}
		 				total.add(0); //if argument is by ref or by value passed by 0 is dont care 1 is by value 2 is  by ref
		 				return total;
		 			}
		 			else if(temp.is_ty==2){
		 				Array element=(Array)h.get(name);
		 				total.add(element.reg);
		 				total.add(element.end);
		 				total.add(i);
		 				total.add(1);
		 				if(element.type.trim().equals("int")){
		 					total.add(0);
		 				}
		 				else{
		 					total.add(1);
		 				}
		 				total.add(0); //if argument is by ref or by value passed by 0 is dont care 1 is by value 2 is  by ref
		 				return total;
		 			}
		 		}
	 		}
	 		else{
                Entry temp1=h.get(cfunc);
                if(temp1!=null){
                            Func element=(Func)h.get(cfunc);
                            //System.out.println(cfunc);
                            int op=0;
                            if(element.v.size()!=0){
                                for(Var v1:element.v){
                                    if(v1.id.equals(name)){
                                        total.add(v1.reg);
                                        total.add(v1.end);
                                        total.add(i+1);
                                        total.add(2);
                                        if(v1.type.trim().equals("int")){
                                            total.add(0);
                                        }
                                        else{
                                            total.add(1);
                                        }
                                        total.add(element.ref1.get(op)+1); //if argument is by ref or by value passed by 0 is dont care 1 is by value 2 is  by ref
                                        total.add(v1.arg);
                                        return total;
                                    }
                                    op++;
                                }
                            }
                            if(element.ar.size()!=0){
                                for(Array v1:element.ar){
                                    if(v1.id.equals(name)){
                                        total.add(v1.reg);
                                        total.add(v1.end);
                                        total.add(i+1);
                                        total.add(2);
                                        if(v1.type.trim().equals("int")){
                                            total.add(0);
                                        }
                                        else{
                                            total.add(1);
                                        }
                                        total.add(2); //if argument is by ref or by value passed by 0 is dont care 1 is by value and if 2 is  by ref
                                        total.add(v1.arg);
                                        return total;
                                    }
                                }
                            }
                }
    		 		for (String key: h.keySet()){
                        //System.out.println("We are here09:"+key);
    		 			Entry temp=h.get(key);
                        //System.out.println("We are here16:"+temp.id);
    		 			if(temp.is_ty==1 && temp.id.equals(name)){
    		 				Var element=(Var)h.get(key);
    		 				if(element.id.equals(name)){
    			 				total.add(element.reg);//start of index
    			 				total.add(element.end);//end of index
    			 				total.add(i); //the scope this exist
    			 				total.add(1); //1 is variable 2 is argument
    			 				if(element.type.trim().equals("int")){
    			 					total.add(0);//type 0 is int 1 is char
    			 				}
    			 				else{
    			 					total.add(1);
    			 				}
    			 				total.add(0); //if argument is by ref or by value passed by 0 is dont care 1 is by value 2 is  by ref
    			 				return total;
    			 			}
    		 			}
    		 			else if(temp.is_ty==2 && temp.id.equals(name)){
    		 				Array element=(Array)h.get(key);
    		 				if(element.id.equals(name)){
    			 				total.add(element.reg);
    			 				total.add(element.end);
    			 				total.add(i);
    			 				total.add(1);
    			 				if(element.type.trim().equals("int")){
    			 					total.add(0);
    			 				}
    			 				else{
    			 					total.add(1);
    			 				}
    			 				total.add(0); //if argument is by ref or by value passed by 0 is dont care 1 is by value 2 is  by ref
    			 				return total;
    			 			}
    		 			}
    		 			else if(temp.is_ty==0){
    		 				Func element=(Func)h.get(key);
    		 				//System.out.println(cfunc);
    		 				int op=0;
    		 				if(element.v.size()!=0){
    			                for(Var v1:element.v){
    			                	if(v1.id.equals(name)){
    					 				total.add(v1.reg);
    					 				total.add(v1.end);
    					 				total.add(i+1);
    					 				total.add(2);
    					 				if(v1.type.trim().equals("int")){
    					 					total.add(0);
    					 				}
    					 				else{
    					 					total.add(1);
    					 				}
    			 						total.add(element.ref1.get(op)+1); //if argument is by ref or by value passed by 0 is dont care 1 is by value 2 is  by ref
    					 				total.add(v1.arg);
                                        return total;
    					 			}
    					 			op++;
    			                }
    			            }
    			            if(element.ar.size()!=0){
    			                for(Array v1:element.ar){
    			                	if(v1.id.equals(name)){
    					 				total.add(v1.reg);
    					 				total.add(v1.end);
    					 				total.add(i+1);
    					 				total.add(2);
    					 				if(v1.type.trim().equals("int")){
    					 					total.add(0);
    					 				}
    					 				else{
    					 					total.add(1);
    					 				}
    			 						total.add(2); //if argument is by ref or by value passed by 0 is dont care 1 is by value and if 2 is  by ref
    					 				total.add(v1.arg);
                                        return total;
    					 			}
    			                }
    			            }
    		 			}
    		 		}
	 		}
 			i--;
 		}
 		System.out.println("Something you did wrong with the symbol table and assembly exited");
 		System.exit(1);
 		return total;}}