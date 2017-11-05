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

class Quadre{
    int ticket;
    String op;
    String r1;
    String r2;
    String r3;}
class Cond{
    ArrayList<Integer> tr;
    ArrayList<Integer> fl;

    Cond(){
        tr=new ArrayList<Integer>();
        fl=new ArrayList<Integer>();}}
class Temp{
    String name;
    String type;
    int reg;

    Temp(){
        name="a";
        type="a";
        reg=0;
    }}
class Ir{
    ArrayList<Quadre> q;
    ArrayList<Cond> cond;
    ArrayList<Integer> next;
    ArrayList<Temp> temp;
    int ticket;
    int dollar;

    Ir(){
        ticket=1;
        dollar=1;
        temp=new ArrayList<Temp>();
        q=new ArrayList<Quadre>();
        cond=new ArrayList<Cond>();
        next=new ArrayList<Integer>();}
    Temp newtemp(String type){
        Temp a=new Temp();
        a.name="$"+Integer.toString(dollar);
        a.type=type;
        dollar++;
        return a;}
    void nextquad(String a,String b,String c,String d){
        Quadre qu=new Quadre();
        qu.ticket=ticket;
        qu.op=a;
        qu.r1=b;
        qu.r2=c;
        qu.r3=d;
        q.add(qu);
        ticket++;}
    void backpatch(String a){
        //System.out.println("True:"+cond.get(cond.size()-1).tr.toString());
        //System.out.println("False:"+cond.get(cond.size()-1).fl.toString());
        if(a.equals("is_t")){
            ArrayList<Integer> t=cond.get(cond.size()-1).tr;
            for(int i=0;i<t.size();i++){
                Quadre temp=q.get(t.get(i)-1);
                temp.r3=Integer.toString(next.get(next.size()-1));
                q.set(t.get(i)-1,temp);
            }
        }
        else if(a.equals("is_f")){
            ArrayList<Integer> t=cond.get(cond.size()-1).fl;
            for(int i=0;i<t.size();i++){
                Quadre temp=q.get(t.get(i)-1);
                temp.r3=Integer.toString(next.get(next.size()-1));
                q.set(t.get(i)-1,temp);
            }
        }
        else if(a.equals("and")){
            ArrayList<Integer> t=cond.get(cond.size()-1).tr;
            //System.out.println("and:"+t.toString());
            if(t.size()==1){
                Quadre temp=q.get(t.get(t.size()-1)-1);
                temp.r3=Integer.toString(next.get(next.size()-1));
                q.set(t.get(t.size()-1)-1,temp);
            }
            else{
                for(int i=0;i<t.size();i++){
                    Quadre temp=q.get(t.get(i)-1);
                    temp.r3=Integer.toString(next.get(next.size()-1));
                    q.set(t.get(i)-1,temp);
                }
            }
            cond.get(cond.size()-1).tr.clear();
        }
        else if(a.equals("or")){
            ArrayList<Integer> t=cond.get(cond.size()-1).fl;
            //System.out.println("or:"+t.toString());
            if(t.size()==1){
                Quadre temp=q.get(t.get(t.size()-1)-1);
                temp.r3=Integer.toString(next.get(next.size()-1));
                q.set(t.get(t.size()-1)-1,temp);
            }
            else{
                for(int i=0;i<t.size();i++){
                    Quadre temp=q.get(t.get(i)-1);
                    temp.r3=Integer.toString(next.get(next.size()-1));
                    q.set(t.get(i)-1,temp);
                }
            }
            cond.get(cond.size()-1).fl.clear();
        }
        else if(a.equals("is_f2")){
            ArrayList<Integer> t=cond.get(cond.size()-1).tr;
            //System.out.println("f2:"+t.toString());
            for(int i=0;i<t.size();i++){
                Quadre temp=q.get(t.get(i)-1);
                temp.r3=Integer.toString(next.get(next.size()-1)-1);
                q.set(t.get(i)-1,temp);
            }
        }
        else if(a.equals("is_t2")){
            ArrayList<Integer> t=cond.get(cond.size()-1).fl;
            //System.out.println("t2:"+t.toString());
            for(int i=0;i<t.size();i++){
                Quadre temp=q.get(t.get(i)-1);
                temp.r3=Integer.toString(next.get(next.size()-1)-1);
                q.set(t.get(i)-1,temp);
            }
        }}
    int nextquad(){return ticket;}
    void printIr(){
        System.out.println("Now Printing IR code");
        for(int i=0;i<q.size();i++){
            System.out.println(q.get(i).ticket+","+q.get(i).op+","+q.get(i).r1+","+q.get(i).r2+","+q.get(i).r3);
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("Now Printing temporaris and values"+"->Total:"+temp.size());
        for(int i=0;i<temp.size();i++){
            System.out.println(temp.get(i).name+" "+temp.get(i).type);
        }}}