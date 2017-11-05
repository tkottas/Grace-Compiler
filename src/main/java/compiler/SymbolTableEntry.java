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

class Entry{
    String id;
    String type;
    int is_ty;//0 func,1 var,2 Array
    int reg;//0 means empty it is a faction
    int end;
    int arg;

    Entry(String a,String b,int c,int d){
        arg=0;
        id=a;
        type=b;
        is_ty=c;
        reg=d;
        end=0;
    } 

    Entry(String a,int b){
        type=a;
        is_ty=b;
    }

    Entry(int a){
        int is_ty=a;
    } }
class Func extends Entry{
    ArrayList<Var> v;
    ArrayList<Integer> ref1;//0 is not ref 1 is ref
    ArrayList<Array> ar;
    ArrayList<Integer> ref2;//0 is not ref 1 is ref
    String ret_t;
    int declared;
    ArrayList<String> pos;

    Func(int a){
        super("func",0);
        v=new ArrayList<Var>();
        ref1=new ArrayList<Integer>();
        ar=new ArrayList<Array>();
        ref2=new ArrayList<Integer>();
        pos=new ArrayList<String>();
        declared=a;
    }

    Func(String a,int b,String c,int d){
        super(a,"func",0,b);
        v=new ArrayList<Var>();
        ref1=new ArrayList<Integer>();
        ar=new ArrayList<Array>();
        ref2=new ArrayList<Integer>();
        pos=new ArrayList<String>();
        ret_t=c;
        declared=d;
    }

    void set_decl(int a){declared=a;}
    void set_id(String a){id=a;}
    void set_type(String a){ret_t=a;}
    void set_reg(int a){reg=a;}
    void set_ret_type(String a){ret_t=a;}
    void set_v(Var a){
        v.add(a);
        pos.add("var");
    }
    void set_r1(int a){ref1.add(a);}
    void set_ar(Array a){
        ar.add(a);
        pos.add("array");
    }
    void set_r2(int a){ref2.add(a);}
    int get_decl(){return declared;}

    //check for double declared parameters
    void check_var(Var a){
        for(Var var: v){
            if(var.id.equals(a.id)){
                System.out.println("Conflict types previous declaration variable");
                System.exit(1);
            }
        }
        for(Array var: ar){
            if(var.id.equals(a.id)){
                System.out.println("Conflict types previous declaration Array");
                System.exit(1);
            }
        }
    }

    //check for double declared array parameters
    void check_array(Array a){
        for(Array var: ar){
            if(var.id.equals(a.id)){
                System.out.println("Conflict types previous declaration Array");
                System.exit(1);
            }
        }
        for(Var var: v){
            if(var.id.equals(a.id)){
                System.out.println("Conflict types previous declaration variable");
                System.exit(1);
            }
        }
    }}
class Var extends Entry{

    Var(String a,String b,int c){super(a,b,1,c);}
    Var(){super(1);}
    void set_id(String a){id=a;}
    void set_type(String a){type=a;}
    void set_reg(int a){reg=a;}
    void set_is_ty(){is_ty=1;}
    int ret_reg(){return reg;}}
class Array extends Entry{
    ArrayList<String> ind;//0 means that it is an index that user will enter

    Array(String a,String b,int c,ArrayList<String> d){
        super(a,b,2,c);
        ind=new ArrayList<String>();
    }
    Array(){
        super(2);
        ind=new ArrayList<String>();
    }
    void set_id(String a){id=a;}
    void set_type(String a){type=a;}
    void set_ind(String a){ind.add(a);}
    void set_reg(int a){reg=a;}
    void set_is_ty(){is_ty=2;}
    int ret_reg(){return reg;}}
class Eval{
    String type;
    String name;
    String s_t;
    ArrayList<String> ind;
    ArrayList<String> ind2;
    int ad;
    int is_p;
    Temp temp;

    Eval(){
        ind=new ArrayList<String>();
        ind2=new ArrayList<String>();
        temp=new Temp();
        is_p=0;
        ad=0;
    }}