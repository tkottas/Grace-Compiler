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

class Action extends DepthFirstAdapter {
    Semantic s;
    int depth=1;
    int ticket;
    ArrayList<String> cfunc;
    ArrayList<String> bp; //basepointer for fast lookup for 
    ArrayList<Integer> a;
    ArrayDeque<Eval> eval;
    Ir ir;
    Assembly asb;
/*-----------------------------------------------Visitor FUNCTIONS------------------------------------------------*/
    @Override
    public void caseAProgram(AProgram node){
        s=new Semantic();
        s.enter();
        cfunc=new ArrayList<String>();
        a=new ArrayList<Integer>();
        eval=new ArrayDeque<Eval>();
        ir=new Ir();
        s.stdin();//initialize the built in factions
        AFuncDef n=(AFuncDef)node.getFuncDef();
        AHeader h=(AHeader)n.getHeader();
        ir.nextquad("call","-","-",h.getId().getText());
        asb=new Assembly(h.getId().getText());
        asb.cr_builtin(s);
        inAProgram(node);
        if(node.getFuncDef() != null)
        {
            node.getFuncDef().apply(this);
        }
        outAProgram(node);}
    @Override
    public void inAProgram(AProgram node){
        AFuncDef element=(AFuncDef)node.getFuncDef();
        AHeader h=(AHeader)element.getHeader();
        String a=h.getRetType().toString().substring(0,(h.getRetType().toString().length()-1));
        eval=new ArrayDeque<Eval>();
        if(a.equals("int")){
            System.out.println("Main fuction should be type nothing no type Integer");
            System.exit(1);
        }
        else if(a.equals("char")){
            System.out.println("Main fuction should be type nothing no type character");
            System.exit(1);
        }
        if(h.getFparDef().size()!=0){
            System.out.println("Main fuction should have no parameters");
            System.exit(1);
        }}
    @Override
    public void caseAFuncDef(AFuncDef node){
        inAFuncDef(node);
        if(node.getHeader() != null)
        {
            node.getHeader().apply(this);
            s.parameters(cfunc.get(cfunc.size()-1));
        }
        {
            List<PLocalDef> copy = new ArrayList<PLocalDef>(node.getLocalDef());
            for(PLocalDef e : copy)
            {
                s.locals(cfunc.get(cfunc.size()-1));
                e.apply(this);
            }
        }
        AHeader h=(AHeader)node.getHeader();
        ir.nextquad("unit",h.getId().getText(),"-","-");
        if(node.getBlock() != null)
        {
            node.getBlock().apply(this);
        }
        ir.nextquad("endu",h.getId().getText(),"-","-");
        asb.cr_asb(s,ir,cfunc,a);
        outAFuncDef(node);}
    @Override
    public void outAProgram(AProgram node){
        asb.cr_data_seg();
        ir.printIr();}
    @Override
    public void inAFuncDef(AFuncDef node){
        AHeader h=(AHeader)node.getHeader();
        /*----------------------------------------------------------------------------*/
        String id=h.getId().getText();
        String r_type=h.getRetType().toString();
        cfunc.add(h.getId().getText());
        a.add(0);
        Func temp=new Func(1);
        temp.set_id(id);
        temp.set_type(r_type);
        AFparDef element;
        AFparType element2; 
        for (int i = 0; i < h.getFparDef().size(); i++)
        {
            element = (AFparDef)h.getFparDef().get(i);
            element2 = (AFparType)element.getFparType();
            String type = element2.getDataType().toString();
            for(int j = 0; j < element.getId().size(); j++)
            {
                String name=element.getId().get(j).getText();
                if(id.equals(name)){
                    System.out.println("Same name function and parameter");
                    System.exit(1);
                }
                if(element.getRef()==null)
                {
                    if(element2.getMbracket()==null)
                    {
                        if(element2.getMtype().size()==0)
                        {
                            Var var=new Var();
                            var.set_id(name);
                            var.set_type(type);
                            var.set_reg(0);
                            var.set_is_ty();
                            temp.check_var(var);
                            temp.set_v(var);
                            temp.set_r1(0);
                        }
                        else
                        {
                            System.out.println("Array parameter not passed by value");
                            System.exit(1);
                        }
                    }
                    else
                    {
                        System.out.println("Array parameter not passed by value");
                        System.exit(1);
                    }
                }
                else
                {
                   if(element2.getMbracket()==null)
                    {
                        if(element2.getMtype().size()==0)
                        {
                            Var var=new Var();
                            var.set_id(name);
                            var.set_type(type);
                            var.set_reg(0);
                            var.set_is_ty();
                            temp.check_var(var);
                            temp.set_v(var);
                            temp.set_r1(1);
                        }
                        else
                        {
                            Array var=new Array();
                            var.set_id(name);
                            var.set_type(type);
                            var.set_is_ty();
                            for(int l=0; l < element2.getMtype().size(); l++)
                            {
                                String number=element2.getMtype().get(l).toString();
                                var.set_ind(number);
                            }
                            var.set_reg(0);
                            temp.check_array(var);
                            temp.set_ar(var);
                            temp.set_r2(1);
                        }
                    }
                    else
                    {
                        Array var=new Array();
                        var.set_ind("0");
                        var.set_id(name);
                        var.set_type(type);
                        var.set_reg(0);
                        var.set_is_ty();
                        if(element2.getMtype().size()!=0)
                        {
                            for(int l=0; l < element2.getMtype().size(); l++)
                            {
                                String number=element2.getMtype().get(l).toString();
                                var.set_ind(number);
                            }
                        }
                        temp.check_array(var);
                        temp.set_ar(var);
                        temp.set_r2(1);
                    }
                }
            }
        }
        if(cfunc.size()>1){
            s.check_funDef(temp,depth,cfunc.get(cfunc.size()-2));
        }
        else{
            s.check_funDef(temp,depth,cfunc.get(cfunc.size()-1));    
        }
        s.insert4(temp,s.symbol_table.size()-1);
        s.enter();}
    @Override
    public void outAFuncDef(AFuncDef node){
        //System.out.println(s.symbol_table.size());
        //for(int i=0;i<s.symbol_table.size();i++){
        //    System.out.println(s.symbol_table.get(i).toString());
        //}
        s.check_no_ret(cfunc.get(cfunc.size()-1),a.get(a.size()-1));
        cfunc.remove(cfunc.size()-1);
        a.remove(a.size()-1);
        s.check_if_decl();
        s.exit();}
    @Override
    public void inAFuncDecl(AFuncDecl node){
        AHeader h=(AHeader)node.getHeader();
        /*----------------------------------------------------------------------------*/
        String id=h.getId().getText();
        String r_type=h.getRetType().toString();
        Func temp=new Func(0);
        temp.set_id(id);
        temp.set_type(r_type);
        AFparDef element;
        AFparType element2; 
        for (int i = 0; i < h.getFparDef().size(); i++)
        {
            element = (AFparDef)h.getFparDef().get(i);
            element2 = (AFparType)element.getFparType();
            String type = element2.getDataType().toString();
            for(int j = 0; j < element.getId().size(); j++)
            {
                String name=element.getId().get(j).getText();
                if(id.equals(name)){
                    System.out.println("Same name function and parameter");
                    System.exit(1);
                }
                if(element.getRef()==null)
                {
                    if(element2.getMbracket()==null)
                    {
                        if(element2.getMtype().size()==0)
                        {
                            Var var=new Var();
                            var.set_id(name);
                            var.set_type(type);
                            var.set_reg(0);
                            var.set_is_ty();
                            temp.check_var(var);
                            temp.set_v(var);
                            temp.set_r1(0);
                        }
                        else
                        {
                            System.out.println("Array parameter not passed by value");
                            System.exit(1);
                        }
                    }
                    else
                    {
                        System.out.println("Array parameter not passed by value");
                        System.exit(1);
                    }
                }
                else
                {
                   if(element2.getMbracket()==null)
                    {
                        if(element2.getMtype().size()==0)
                        {
                            Var var=new Var();
                            var.set_id(name);
                            var.set_type(type);
                            var.set_is_ty();
                            var.set_reg(0);
                            temp.check_var(var);
                            temp.set_v(var);
                            temp.set_r1(1);
                        }
                        else
                        {
                            Array var=new Array();
                            var.set_id(name);
                            var.set_type(type);
                            var.set_is_ty();
                            for(int l=0; l < element2.getMtype().size(); l++)
                            {
                                String number=element2.getMtype().get(l).toString();
                                var.set_ind(number);
                            }
                            var.set_reg(0);
                            temp.check_array(var);
                            temp.set_ar(var);
                            temp.set_r2(1);
                        }
                    }
                    else
                    {
                        Array var=new Array();
                        var.set_ind("0");
                        var.set_id(name);
                        var.set_type(type);
                        var.set_reg(0);
                        var.set_is_ty();
                        if(element2.getMtype().size()!=0)
                        {
                            for(int l=0; l < element2.getMtype().size(); l++)
                            {
                                String number=element2.getMtype().get(l).toString();
                                var.set_ind(number);
                            }
                        }
                        temp.check_array(var);
                        temp.set_ar(var);
                        temp.set_r2(1);
                    }
                }
            }
        }
        if(s.check_funDec(temp,depth,cfunc.get(cfunc.size()-1))==1)
        {
          temp.set_decl(1);
        }
        s.insert4(temp,s.symbol_table.size()-1);}
    @Override
    public void inAVarDef(AVarDef node){
        AType temp=(AType)node.getType();
        String type=temp.getDataType().toString();
        for(int i=0;i<node.getId().size();i++)
        {
            String id=node.getId().get(i).getText();
            if(temp.getMtype().size()==0)
            {
                Var v=new Var();
                v.set_id(id);
                v.set_type(type);
                v.set_reg(0);
                v.set_is_ty();
                s.check_var(v,depth,cfunc.get(cfunc.size()-1));
                s.insert2(v,s.symbol_table.size()-1);
            }
            else
            {
                Array v=new Array();
                AType a=(AType)node.getType();
                v.set_id(id);
                v.set_type(type);
                v.set_reg(0);
                v.set_is_ty();
                //System.out.println(a.getMtype().size());
                for(int l=0; l < a.getMtype().size(); l++)
                {
                    String number=a.getMtype().get(l).toString();
                    v.set_ind(number);
                }
                s.check_array(v,depth,cfunc.get(cfunc.size()-1));
                s.insert3(v,s.symbol_table.size()-1);
            }
        }}
    @Override
    public void inAIdentifierLValue(AIdentifierLValue node){
        //System.out.println("1:Lvalue ID stuck size "+eval.size()+":");
        Eval v=eval.poll();
        v.name=node.getId().getText();
        String a=null;
        int i=cfunc.size()-1;
        v.s_t=node.getId().getText();
        while(i>=0){
            a=s.take_type(node.getId().getText(),cfunc.get(i));
            if(a!=null){
                i=-1;
            }
            i--;
        }
        if(a==null){
            System.out.println("No Variable with name "+node.getId().getText());
            System.exit(1);
        }
        v.type=a.substring(0,a.length()-1);
        eval.push(v);
        //System.out.println("2:LValue ID stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inAAssignmentStmt(AAssignmentStmt node){
        //System.out.println("3:Assign stuck size "+eval.size()+":");
        Eval v=new Eval();
        eval.push(v);
        //System.out.println("4:Assign stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inANumberExpr(ANumberExpr node){
        //System.out.println("5:Number stuck size "+eval.size()+":");
        Eval v=new Eval();
        v.name="int_const";
        v.type="int";
        v.s_t=node.getIntConst().getText();
        Eval v1=eval.poll();
        if(v1!=null){
            v.is_p=v1.is_p;
            eval.push(v1);
        }
        eval.push(v);
        //System.out.println("6:Number stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inACharExpr(ACharExpr node){
        //System.out.println("7:Char stuck size "+eval.size()+":");
        Eval v=new Eval();
        v.name="char";
        v.type="char";
        v.s_t=node.getCharConst().getText();
        char[] opi=v.s_t.toCharArray();
        opi[0]='"';
        opi[v.s_t.length()-1]='"';
        String opi2=new String(opi);
        //System.out.println("mactomas"+opi2.trim()+"mactomas");
        asb.data_seg.add(opi2);
        Eval v1=eval.poll();
        if(v1!=null){
            v.is_p=v1.is_p;
            eval.push(v1);
        }
        eval.push(v);
        //System.out.println("8:Char stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inAAssignExpr(AAssignExpr node){
        //System.out.println("9:InAAssignExpr expr stuck size "+eval.size()+":");
        Eval v=new Eval();
        eval.push(v);
        //System.out.println("10:InAAssignExpr stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inAStringLiteralLValue(AStringLiteralLValue node){
        //System.out.println("11:String stuck size "+eval.size()+":");
        Eval v=eval.poll();
        v.name="string";
        v.type="char";
        //System.out.println(node.getStringLiteral().getText());
        v.s_t=node.getStringLiteral().getText();
        String opi=v.s_t;
        //System.out.println("mactomas"+opi.trim()+"mactomas");
        asb.data_seg.add(opi);
        v.ind.add(Integer.toString(node.getStringLiteral().getText().length()));
        eval.push(v);
        //System.out.println("12:String stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inAFuncCall(AFuncCall node){
        //System.out.println("13:Func Call stuck size "+eval.size()+":");
        //System.out.println(node.getId().getText());
        Eval v=new Eval();
        v.is_p=1;
        eval.push(v);
        //System.out.println("14:Func Call stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inANumberExpr55(ANumberExpr55 node){
        //System.out.println("17:Signed Number stuck size "+eval.size()+":");
        Eval v=new Eval();
        v.name="int_const";
        //System.out.println(node.getIntConst().getText());
        v.type="int";
        v.s_t=node.getIntConst().getText();
        eval.push(v);
        //System.out.println("18:Signed Number stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inACallingExpr(ACallingExpr node){
        //System.out.println("13:Calling stuck size "+eval.size()+":");
        Eval v=new Eval();
        v.name="func";
        AFuncCall f=(AFuncCall)node.getFuncCall();
        v.s_t=f.getId().getText();
        v.is_p=2;
        eval.push(v);
        //System.out.println("14:Calling stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void inAIdentifierExpr55(AIdentifierExpr55 node){
        //System.out.println("19:Signed Id stuck size "+eval.size()+":");
        Eval v=new Eval();
        v.name=node.getId().getText();
        //System.out.println(node.getId().getText());
        String a=null;
        v.s_t=node.getId().getText();
        int i=cfunc.size()-1;
        while(i>=0){
            a=s.take_type(node.getId().getText(),cfunc.get(i));
            if(a!=null){
                i=-1;
            }
            i--;
        }
        if(a==null){
            System.out.println("No Variable with name "+node.getId().getText());
            System.exit(1);
        }
        v.type=a.substring(0,a.length()-1);
        i=cfunc.size()-1;
        while(i>=0 && v.name.equals("string")==false && v.name.equals("int_const")==false && v.name.equals("char")==false){
            String answer=s.check_exist(v,cfunc.get(cfunc.size()-1));
            if(answer.equals("yes")){
                i=-1;
            }
            i--;
        }
        eval.push(v);
        //System.out.println("20:Signed Id stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outAAssignExpr(AAssignExpr node){
        //System.out.println("9:OutAAssignExpr expr stuck size "+eval.size()+":");
        Eval v=eval.poll();
        Eval v1=eval.poll();
        //System.out.print(v.name+" array size:"+v.ind.size());
        //for(int i=0;i<v.ind.size();i++){
        //    System.out.print(" "+v.ind.get(i));
        //}
        //System.out.println();
        if(v1!=null){
            //System.out.println("HERE "+v1.is_p);
            if(v1.is_p==1){
                int i=cfunc.size()-1;
                v.is_p=1;
                while(i>=0 && v.name.equals("string")==false && v.name.equals("int_const")==false && v.name.equals("char")==false){
                    String answer1=s.check_exist2(v,cfunc.get(i));
                    //System.out.println("HERE "+v.name);
                    if(answer1.equals("no")){
                        String answer=s.check_exist(v,cfunc.get(i));
                        if(answer.equals("yes")){
                            if(v.ind.size()>1){
                                Array ar=s.check_exist3(v,cfunc.get(i));
                                for(int bo=0;bo<v.ind.size();bo++){
                                    for(int bo2=bo+1;bo2<ar.ind.size();bo2++){
                                        Temp w=ir.newtemp("int");
                                        if(bo2==bo+1){
                                            ir.nextquad("*",v.ind.get(bo),ar.ind.get(bo2),w.name);
                                        }
                                        else{
                                            ir.nextquad("*",ir.temp.get(ir.temp.size()-1).name,ar.ind.get(bo2),w.name);
                                        }
                                        ir.temp.add(w);
                                    }
                                    Temp y=ir.newtemp("int");
                                    if(bo==0){
                                        ir.nextquad("+","base",ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                    else if(bo==v.ind.size()-1){
                                        ir.nextquad("+",v.temp.name,v.ind.get(bo),y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                        Temp x=ir.newtemp(v.type);
                                        ir.nextquad("array",v.name,v.temp.name,x.name);
                                        v.temp.name=x.name;
                                        ir.temp.add(x);
                                    }
                                    else{
                                        ir.nextquad("+",v.temp.name,ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                }
                            }
                            else if(v.ind.size()==1){
                                Temp x=ir.newtemp(v.type);
                                if(v.temp.name.equals("a")){
                                    ir.nextquad("array",v.name,v.ind.get(0),x.name);
                                }
                                else{
                                    ir.nextquad("array",v.name,v.temp.name,x.name);
                                }
                                v.temp.name=x.name;
                                ir.temp.add(x);
                            }
                            //System.out.println("HERE1 "+v.name);
                            i=-1;
                        }
                    }
                    else{
                            if(v.ind.size()>1){
                                Array ar=s.check_exist3(v,cfunc.get(i));
                                for(int bo=0;bo<v.ind.size();bo++){
                                    for(int bo2=bo+1;bo2<ar.ind.size();bo2++){
                                        Temp w=ir.newtemp("int");
                                        if(bo2==bo+1){
                                            ir.nextquad("*",v.ind.get(bo),ar.ind.get(bo2),w.name);
                                        }
                                        else{
                                            ir.nextquad("*",ir.temp.get(ir.temp.size()-1).name,ar.ind.get(bo2),w.name);
                                        }
                                        ir.temp.add(w);
                                    }
                                    Temp y=ir.newtemp("int");
                                    if(bo==0){
                                        ir.nextquad("+","base",ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                    else if(bo==v.ind.size()-1){
                                        ir.nextquad("+",v.temp.name,v.ind.get(bo),y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                        Temp x=ir.newtemp(v.type);
                                        ir.nextquad("array",v.name,v.temp.name,x.name);
                                        v.temp.name=x.name;
                                        ir.temp.add(x);
                                    }
                                    else{
                                        ir.nextquad("+",v.temp.name,ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                }
                            }
                            else if(v.ind.size()==1){
                                Temp x=ir.newtemp(v.type);
                                if(v.temp.name.equals("a")){
                                    ir.nextquad("array",v.name,v.ind.get(0),x.name);
                                }
                                else{
                                    ir.nextquad("array",v.name,v.temp.name,x.name);
                                }
                                v.temp.name=x.name;
                                ir.temp.add(x);
                            }
                        i=-1;
                    }
                    i--;
                }
            }
            else{
                int i=cfunc.size()-1;
                while(i>=0 && v.name.equals("string")==false && v.name.equals("int_const")==false && v.name.equals("char")==false){
                    String answer=s.check_exist(v,cfunc.get(i));
                    //System.out.println("HERE2 "+v.name);
                    //System.out.println(answer);
                    if(answer.equals("yes")){
                            if(v.ind.size()>1){
                                Array ar=s.check_exist3(v,cfunc.get(i));
                                for(int bo=0;bo<v.ind.size();bo++){
                                    for(int bo2=bo+1;bo2<ar.ind.size();bo2++){
                                        Temp w=ir.newtemp("int");
                                        if(bo2==bo+1){
                                            ir.nextquad("*",v.ind.get(bo),ar.ind.get(bo2),w.name);
                                        }
                                        else{
                                            ir.nextquad("*",ir.temp.get(ir.temp.size()-1).name,ar.ind.get(bo2),w.name);
                                        }
                                        ir.temp.add(w);
                                    }
                                    Temp y=ir.newtemp("int");
                                    if(bo==0){
                                        ir.nextquad("+","base",ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                    else if(bo==v.ind.size()-1){
                                        ir.nextquad("+",v.temp.name,v.ind.get(bo),y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                        Temp x=ir.newtemp(v.type);
                                        ir.nextquad("array",v.name,v.temp.name,x.name);
                                        v.temp.name=x.name;
                                        ir.temp.add(x);
                                    }
                                    else{
                                        ir.nextquad("+",v.temp.name,ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                }
                            }
                            else if(v.ind.size()==1){
                                Temp x=ir.newtemp(v.type);
                                if(v.temp.name.equals("a")){
                                    ir.nextquad("array",v.name,v.ind.get(0),x.name);
                                }
                                else{
                                    ir.nextquad("array",v.name,v.temp.name,x.name);
                                }
                                v.temp.name=x.name;
                                ir.temp.add(x);
                            }
                        i=-1;
                    }
                    i--;
                }
            }
            if(v.name.equals("string") && v.ind.size()>1){
                Temp x=ir.newtemp("char");
                ir.nextquad("array",v.name,v.ind.get(1),x.name);
                v.temp.name=x.name;
                ir.temp.add(x);
            }
            eval.push(v1);
            eval.push(v);
        }
        else{
            int i=cfunc.size()-1;
            while(i>=0 && v.name.equals("string")==false && v.name.equals("int_const")==false && v.name.equals("char")==false){
                String answer=s.check_exist(v,cfunc.get(i));
                if(answer.equals("yes")){
                            if(v.ind.size()>1){
                                Array ar=s.check_exist3(v,cfunc.get(i));
                                for(int bo=0;bo<v.ind.size();bo++){
                                    for(int bo2=bo+1;bo2<ar.ind.size();bo2++){
                                        Temp w=ir.newtemp(v.type);
                                        if(bo2==bo+1){
                                            ir.nextquad("*",v.ind.get(bo),ar.ind.get(bo2),w.name);
                                        }
                                        else{
                                            ir.nextquad("*",ir.temp.get(ir.temp.size()-1).name,ar.ind.get(bo2),w.name);
                                        }
                                        ir.temp.add(w);
                                    }
                                    Temp y=ir.newtemp(v.type);
                                    if(bo==0){
                                        ir.nextquad("+","base",ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                    else if(bo==v.ind.size()-1){
                                        ir.nextquad("+",v.temp.name,v.ind.get(bo),y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                        Temp x=ir.newtemp(v.type);
                                        ir.nextquad("array",v.name,v.temp.name,x.name);
                                        v.temp.name=x.name;
                                        ir.temp.add(x);
                                    }
                                    else{
                                        ir.nextquad("+",v.temp.name,ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                }
                            }
                            else if(v.ind.size()==1){
                                Temp x=ir.newtemp(v.type);
                                if(v.temp.name.equals("a")){
                                    ir.nextquad("array",v.name,v.ind.get(0),x.name);
                                }
                                else{
                                    ir.nextquad("array",v.name,v.temp.name,x.name);
                                }
                                v.temp.name=x.name;
                                ir.temp.add(x);
                            }
                    //System.out.println("HERE3 "+v.name);
                    i=-1;
                }
                i--;
            }
            if(v.name.equals("string") && v.ind.size()>1){
                Temp x=ir.newtemp("char");
                ir.nextquad("array",v.name,v.ind.get(1),x.name);
                v.temp.name=x.name;
                ir.temp.add(x);
            }
            eval.push(v);
        }
        //System.out.println("10:OutAAssignExpr stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
/*-----------------------------------------------POP ROUTINES(ASSIGNENTS-FUNCALLS)--------------------------------*/
    @Override
    public void caseASigned(ASigned node){
        inASigned(node);
        if(node.getExpr55() != null)
        {
            node.getExpr55().apply(this);
        }
        if(node.getSign() != null)
        {
            node.getSign().apply(this);
        }
        outASigned(node);}
    @Override
    public void inAMinusSign(AMinusSign node){
        Eval v=eval.poll();
        //System.out.println("Uminus:"+v.temp.name+" "+v.name);
        Temp w=ir.newtemp(v.type);
        if(v.temp.name.equals("a")){
            ir.nextquad("uminus",v.s_t,"-",w.name);
        }
        else{
            ir.nextquad("uminus",v.temp.name,"-",w.name);
        }
        ir.temp.add(w);
        v.temp.name=w.name;
        eval.push(v);}
    @Override
    public void outAAssignmentStmt(AAssignmentStmt node){
        //System.out.println("21:Assignment stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name+" "+v.ind.size());
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println("<-"+v1.name+" "+v1.ind.size());
        //System.out.println(v1.type);
        int i=cfunc.size()-1;
        if(v1.name.equals("string")==false && v1.name.equals("int_const")==false && v1.name.equals("char")==false){
            i=cfunc.size()-1;
            while(i>=0){
                String answer=s.check_exist(v1,cfunc.get(i));
                if(answer.equals("yes")){
                            if(v1.ind.size()>1){
                                Array ar=s.check_exist3(v1,cfunc.get(i));
                                for(int bo=0;bo<v1.ind.size();bo++){
                                    for(int bo2=bo+1;bo2<ar.ind.size();bo2++){
                                        Temp w=ir.newtemp(v1.type);
                                        if(bo2==bo+1){
                                            ir.nextquad("*",v1.ind.get(bo),ar.ind.get(bo2),w.name);
                                        }
                                        else{
                                            ir.nextquad("*",ir.temp.get(ir.temp.size()-1).name,ar.ind.get(bo2),w.name);
                                        }
                                        ir.temp.add(w);
                                    }
                                    //System.out.println("INDEXES"+bo);
                                    Temp y=ir.newtemp(v1.type);
                                    if(bo==0){
                                        ir.nextquad("+","base",ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v1.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                    else if(bo==v1.ind.size()-1){
                                        ir.nextquad("+",v1.temp.name,v1.ind.get(bo),y.name);
                                        v1.temp.name=y.name;
                                        ir.temp.add(y);
                                        Temp x=ir.newtemp(v1.type);
                                        ir.nextquad("array",v1.name,v1.temp.name,x.name);
                                        v1.temp.name=x.name;
                                        ir.temp.add(x);
                                    }
                                    else{
                                        ir.nextquad("+",v1.temp.name,ir.temp.get(ir.temp.size()-1).name,y.name);
                                        v1.temp.name=y.name;
                                        ir.temp.add(y);
                                    }
                                }
                            }
                            else if(v1.ind.size()==1){
                                Temp x=ir.newtemp(v1.type);
                                if(v1.temp.name.equals("a")){
                                    ir.nextquad("array",v1.name,v1.ind.get(0),x.name);
                                }
                                else{
                                    ir.nextquad("array",v1.name,v1.temp.name,x.name);
                                }
                                v1.temp.name=x.name;
                                ir.temp.add(x);
                            }
                    i=-1;
                }
                i--;
            }
        }
        if(v1.name.equals("string")){
            if(v1.ind.size()<2){
                System.out.println("Passing Array String");
                System.exit(1);
            }
        }
        if(v1.name.equals("string") && v1.ind.size()>1){
            Temp x=ir.newtemp("char");
            ir.nextquad("array",v1.name,v1.ind.get(1),x.name);
            v1.temp.name=x.name;
            ir.temp.add(x);
        }
        if(v.name.equals("string")==false && v.name.equals("int_const")==false && v.name.equals("char")==false){
            while(i>=0){
                String answer=s.check_exist(v,cfunc.get(i));
                if(answer.equals("yes")){
                    i=-1;
                }
                i--;
            }
        }
        if(v.name.equals("string")){
            if(v.ind.size()<2){
                System.out.println("Passing Array String");
                System.exit(1);
            }
        }
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different assign type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad(":=","["+v.temp.name+"]","-",v1.s_t);
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad(":=","["+v.temp.name+"]","-",v1.temp.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad(":=",v.s_t,"-","["+v1.temp.name+"]");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad(":=",v.temp.name,"-","["+v1.temp.name+"]");
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad(":=","["+v.temp.name+"]","-","["+v1.temp.name+"]");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad(":=",v.s_t,"-",v1.s_t);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad(":=",v.temp.name,"-",v1.s_t);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad(":=",v.s_t,"-",v1.temp.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad(":=",v.temp.name,"-",v1.temp.name);
        }
        //System.out.println("22:Assignemt stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outAFuncCall(AFuncCall node){
        //System.out.println("23:Func Call stuck size "+eval.size()+":");
        String id=node.getId().getText();
        ArrayList<Eval> params=new ArrayList<Eval>();
        AMexpr2 e=(AMexpr2)node.getMexpr2();
        if(node.getMexpr2()!=null){
            for(int i=0;i<e.getExpr().size();i++){
                params.add(eval.poll());
                //System.out.println(params.get(i).ind.size());
            }
        }
        Eval temp=eval.poll();
        Eval p=eval.poll();
        if(p!=null){
            if(p.is_p==2){
                p.type=s.check_call(id,params,ir);
                //System.out.println(p.type);
                p.temp.name=ir.temp.get(ir.temp.size()-1).name;
                eval.push(p);
            }
            //System.out.println(id);
            //System.out.println(p.is_p);
        }
        else{
            s.check_call(id,params,ir);
        }
        ir.nextquad("call","-","-",id);
        //System.out.println();
        //System.out.println("24:Func Call stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
/*-----------------------------------------------CONDITIONS-------------------------------------------------------*/
    @Override
    public void caseAOrCond(AOrCond node){
        inAOrCond(node);
        if(node.getLcond() != null)
        {
            node.getLcond().apply(this);
        }
        ir.next.add(ir.nextquad());
        ir.backpatch("or");
        ir.next.remove(ir.next.size()-1);
        if(node.getRcond() != null)
        {
            node.getRcond().apply(this);
        }
        outAOrCond(node);
        ir.next.add(ir.nextquad()+1);
        ir.backpatch("is_f2");
        ir.next.remove(ir.next.size()-1);}
    @Override
    public void caseAAndCond(AAndCond node){
        inAAndCond(node);
        if(node.getLcond() != null)
        {
            node.getLcond().apply(this);
        }
        ir.next.add(ir.nextquad());
        ir.backpatch("and");
        ir.next.remove(ir.next.size()-1);
        if(node.getRcond() != null)
        {
            node.getRcond().apply(this);
        }
        ir.next.add(ir.nextquad()+1);
        ir.backpatch("is_t2");
        ir.next.remove(ir.next.size()-1);
        outAAndCond(node);}
    @Override
    public void caseANotCond(ANotCond node){
        inANotCond(node);
        if(node.getCond() != null)
        {
            node.getCond().apply(this);
        }
        outANotCond(node);
        ir.next.add(ir.nextquad());
        ir.backpatch("is_f2");
        ir.next.remove(ir.next.size()-1);
        ir.next.add(ir.nextquad()+1);
        ir.backpatch("is_t2");
        ir.next.remove(ir.next.size()-1);}
    @Override
    public void outAOrCond(AOrCond node){
        //System.out.println("25:Or stuck size "+ir.next+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different or conditional type between "+v.name+" "+v.type+" and "+v1.name+" "+v1.type);
            System.exit(1);
        }
        v.type="int";
        eval.push(v);
        //System.out.println("26:Or stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------")*/;}
    @Override
    public void outAAndCond(AAndCond node){
        //System.out.println("27:And stuck size "+ir.next+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different and conditional type between "+v.name+" "+v.type+" and "+v1.name+" "+v1.type);
            System.exit(1);
        }
        v.type="int";
        eval.push(v);
        //System.out.println("28:And stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outANotCond(ANotCond node){
        //System.out.println("29:Not stuck size "+ir.next+":");
        Eval v=eval.poll();
        //System.out.println(v.type);
        if(v.type.trim().equals("int")==false){
            System.out.println("Different  not conditional type between "+v.name+" "+v.type+" and "+" int");
            System.exit(1);
        }
        Cond temp=new Cond();
        temp.tr=(ArrayList<Integer>)ir.cond.get(ir.cond.size()-1).tr.clone();
        ir.cond.get(ir.cond.size()-1).tr.clear();
        ir.cond.get(ir.cond.size()-1).tr=(ArrayList<Integer>)ir.cond.get(ir.cond.size()-1).fl.clone();
        ir.cond.get(ir.cond.size()-1).fl.clear();
        ir.cond.get(ir.cond.size()-1).fl=(ArrayList<Integer>)temp.tr.clone();
        v.type="int";
        eval.push(v);
        //System.out.println("30:Not stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
/*-----------------------------------------------COMPARISONS------------------------------------------------------*/
    @Override
    public void outAEqualCond(AEqualCond node){
        //System.out.println("31:Equal stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.print(v.s_t);
        //System.out.println(" "+v.type);
        Eval v1=eval.poll();
        //System.out.print(v1.s_t);
        //System.out.println(" "+v1.type);
        if(v.type.trim().equals(v1.type.trim())==false || ( (v.name.equals("string") && v.ind.size()==1) || (v1.name.equals("string")) && v1.ind.size()==1) ) {
            System.out.println("Different equal conditional type between "+v.name+" "+v.type+" and "+v1.name+" "+v1.type);
            System.exit(1);
        }
        ir.cond.get(ir.cond.size()-1).tr.add(ir.nextquad());
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("=",v1.s_t,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("=",v1.temp.name,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("=","["+v1.temp.name+"]",v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("=","["+v1.temp.name+"]",v.temp.name,"*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("=","["+v1.temp.name+"]","["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("=",v1.s_t,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("=",v1.s_t,v.temp.name,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("=",v1.temp.name,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("=",v1.temp.name,v.temp.name,"*");
        }
        ir.cond.get(ir.cond.size()-1).fl.add(ir.nextquad());
        ir.nextquad("jump","-","-","*");
        v.is_p=v1.is_p;
        v.type="int";
        eval.push(v);
        //System.out.println("32:Equal stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outANotEqualCond(ANotEqualCond node){
        //System.out.println("33:Not Equal stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false || ( (v.name.equals("string") && v.ind.size()==1) || (v1.name.equals("string")) && v1.ind.size()==1) ){
            System.out.println("Different not equal conditional type between "+v.name+" "+v.type+" and "+v1.name+" "+v1.type);
            System.exit(1);
        }
        ir.cond.get(ir.cond.size()-1).tr.add(ir.nextquad());
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("#",v1.s_t,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("#",v1.temp.name,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("#","["+v1.temp.name+"]",v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("#","["+v1.temp.name+"]",v.temp.name,"*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("#","["+v1.temp.name+"]","["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("#",v1.s_t,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("#",v1.s_t,v.temp.name,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("#",v1.temp.name,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("#",v1.temp.name,v.temp.name,"*");
        }
        ir.cond.get(ir.cond.size()-1).fl.add(ir.nextquad());
        ir.nextquad("jump","-","-","*");
        v.is_p=v1.is_p;
        v.type="int";
        eval.push(v);
        //System.out.println("34:Not Equal stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outALessCond(ALessCond node){
        //System.out.println("35:Less stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false || ( (v.name.equals("string") && v.ind.size()==1) || (v1.name.equals("string")) && v1.ind.size()==1) ){
            System.out.println("Different less conditional type between "+v.name+" "+v.type+" and "+v1.name+" "+v1.type);
            System.exit(1);
        }
        ir.cond.get(ir.cond.size()-1).tr.add(ir.nextquad());
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("<",v1.s_t,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("<",v1.temp.name,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("<","["+v1.temp.name+"]",v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("<","["+v1.temp.name+"]",v.temp.name,"*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("<","["+v1.temp.name+"]","["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("<",v1.s_t,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("<",v1.s_t,v.temp.name,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("<",v1.temp.name,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("<",v1.temp.name,v.temp.name,"*");
        }
        ir.cond.get(ir.cond.size()-1).fl.add(ir.nextquad());
        ir.nextquad("jump","-","-","*");
        v.is_p=v1.is_p;
        v.type="int";
        eval.push(v);
        //System.out.println("36:Less stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outAGreaterCond(AGreaterCond node){
        //System.out.println("37:Greater stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false || ( (v.name.equals("string") && v.ind.size()==1) || (v1.name.equals("string")) && v1.ind.size()==1) ){
            System.out.println("Different greater conditional type between "+v.name+" "+v.type+" and "+v1.name+" "+v1.type);
            System.exit(1);
        }
        ir.cond.get(ir.cond.size()-1).tr.add(ir.nextquad());
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad(">",v1.s_t,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad(">",v1.temp.name,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad(">","["+v1.temp.name+"]",v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad(">","["+v1.temp.name+"]",v.temp.name,"*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad(">","["+v1.temp.name+"]","["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad(">",v1.s_t,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad(">",v1.s_t,v.temp.name,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad(">",v1.temp.name,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad(">",v1.temp.name,v.temp.name,"*");
        }
        ir.cond.get(ir.cond.size()-1).fl.add(ir.nextquad());
        ir.nextquad("jump","-","-","*");
        v.is_p=v1.is_p;
        v.type="int";
        eval.push(v);
        //System.out.println("38:Greater stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outALessequalCond(ALessequalCond node){
        //System.out.println("39:LE stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false || ( (v.name.equals("string") && v.ind.size()==1) || (v1.name.equals("string")) && v1.ind.size()==1) ){
            System.out.println("Different less equal conditional type between "+v.name+" "+v.type+" and "+v1.name+" "+v1.type);
            System.exit(1);
        }
        ir.cond.get(ir.cond.size()-1).tr.add(ir.nextquad());
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("<=",v1.s_t,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("<=",v1.temp.name,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("<=","["+v1.temp.name+"]",v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("<=","["+v1.temp.name+"]",v.temp.name,"*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("<=","["+v1.temp.name+"]","["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("<=",v1.s_t,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("<=",v1.s_t,v.temp.name,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("<=",v1.temp.name,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("<=",v1.temp.name,v.temp.name,"*");
        }
        ir.cond.get(ir.cond.size()-1).fl.add(ir.nextquad());
        ir.nextquad("jump","-","-","*");
        v.is_p=v1.is_p;
        v.type="int";
        eval.push(v);
        //System.out.println("40:LE stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outAGreaterequalCond(AGreaterequalCond node){
        //System.out.println("41:GE stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false || ( (v.name.equals("string") && v.ind.size()==1) || (v1.name.equals("string")) && v1.ind.size()==1) ){
            System.out.println("Different greater or equal conditional type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        ir.cond.get(ir.cond.size()-1).tr.add(ir.nextquad());
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad(">=",v1.s_t,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad(">=",v1.temp.name,"["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad(">=","["+v1.temp.name+"]",v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad(">=","["+v1.temp.name+"]",v.temp.name,"*");
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad(">=","["+v1.temp.name+"]","["+v.temp.name+"]","*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad(">=",v1.s_t,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad(">=",v1.s_t,v.temp.name,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad(">=",v1.temp.name,v.s_t,"*");
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad(">=",v1.temp.name,v.temp.name,"*");
        }
        ir.cond.get(ir.cond.size()-1).fl.add(ir.nextquad());
        ir.nextquad("jump","-","-","*");
        v.is_p=v1.is_p;
        v.type="int";
        eval.push(v);
        //System.out.println("42:GE stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
/*-----------------------------------------------CALCULATIONS-----------------------------------------------------*/
    @Override
    public void outAPlusExpr(APlusExpr node){
        //System.out.println("43:+ stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different plus type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        Temp w=ir.newtemp(v.type);
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("+","["+v.temp.name+"]",v1.s_t,w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("+","["+v.temp.name+"]",v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("+",v.s_t,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("+",v.temp.name,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("+","["+v.temp.name+"]","["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("+",v.s_t,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("+",v.temp.name,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("+",v.s_t,v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("+",v.temp.name,v1.temp.name,w.name);
        }
        ir.temp.add(w);
        v.temp.name=w.name;
        v.is_p=v1.is_p;
        v.ind.clear();
        eval.push(v);
        //System.out.println("44:+ stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outAMinusExpr(AMinusExpr node){
        //System.out.println("45:- stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different minus type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        Temp w=ir.newtemp(v.type);
        //System.out.println("MINUS"v.temp.name+" "+v1.temp.name);
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("-","["+v.temp.name+"]",v1.s_t,w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("-","["+v.temp.name+"]",v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("-",v.s_t,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("-",v.temp.name,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("-","["+v.temp.name+"]","["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("-",v.s_t,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("-",v.temp.name,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("-",v.s_t,v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("-",v.temp.name,v1.temp.name,w.name);
        }
        v.temp.name=w.name;
        ir.temp.add(w);
        v.is_p=v1.is_p;
        v.ind.clear();
        eval.push(v);
        //System.out.println("46:- stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outAMulExpr(AMulExpr node){
        //System.out.println("47:Mul stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        v.is_p=v1.is_p;
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different mul type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        Temp w=ir.newtemp(v.type);
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("*","["+v.temp.name+"]",v1.s_t,w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("*","["+v.temp.name+"]",v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("*",v.s_t,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("*",v.temp.name,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("*","["+v.temp.name+"]","["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("*",v.s_t,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("*",v.temp.name,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("*",v.s_t,v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("*",v.temp.name,v1.temp.name,w.name);
        }
        v.temp.name=w.name;
        ir.temp.add(w);
        v.ind.clear();
        eval.push(v);
        /*System.out.println("48:Mul stuck size "+eval.size()+":");*/
        /*System.out.println("--------------------------------");*/}
    @Override
    public void outADivExpr(ADivExpr node){
        //System.out.println("49:Div stuck size "+eval.size()+":");
        Eval v=eval.poll();
        ///System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        v.is_p=v1.is_p;
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different div type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        Temp w=ir.newtemp(v.type);
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("/","["+v.temp.name+"]",v1.s_t,w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("/","["+v.temp.name+"]",v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("/",v.s_t,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("/",v.temp.name,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("/","["+v.temp.name+"]","["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("/",v.s_t,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("/",v.temp.name,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("/",v.s_t,v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("/",v.temp.name,v1.temp.name,w.name);
        }
        v.temp.name=w.name;
        ir.temp.add(w);
        v.ind.clear();
        eval.push(v);
        //System.out.println("--------------------------------");
        /*System.out.println("50:Div stuck size "+eval.size()+":");*/}
    @Override
    public void outAModExpr(AModExpr node){
        //System.out.println("51:Mod stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        v.is_p=v1.is_p;
        if(v.type.trim().equals(v1.type.trim())==false){
            System.out.println("Different mod type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        Temp w=ir.newtemp(v.type);
        if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")){
            ir.nextquad("%","["+v.temp.name+"]",v1.s_t,w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()==0 && v1.temp.name.equals("a")==false){
            ir.nextquad("%","["+v.temp.name+"]",v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")){
            ir.nextquad("%",v.s_t,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()!=0 && v.temp.name.equals("a")==false){
            ir.nextquad("%",v.temp.name,"["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()!=0 && v1.ind.size()!=0){
            ir.nextquad("%","["+v.temp.name+"]","["+v1.temp.name+"]",w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")){
            ir.nextquad("%",v.s_t,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")){
            ir.nextquad("%",v.temp.name,v1.s_t,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a") && v1.temp.name.equals("a")==false){
            ir.nextquad("%",v.s_t,v1.temp.name,w.name);
        }
        else if(v.ind.size()==0 && v1.ind.size()==0 && v.temp.name.equals("a")==false && v1.temp.name.equals("a")==false){
            ir.nextquad("%",v.temp.name,v1.temp.name,w.name);
        }
        v.temp.name=w.name;
        ir.temp.add(w);
        v.ind.clear();
        eval.push(v);
        //System.out.println("52:Mod stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}
/*-----------------------------------------------RETURN-----------------------------------------------------------*/
    @Override
    public void outAReturnStmt(AReturnStmt node){
        a.set(a.size()-1,1);
        if(node.getExpr()!=null){
            //System.out.println("53:stuck size "+eval.size()+":");
            Eval v=eval.poll();
            Eval v1=eval.poll();
            //System.out.println(v.type);
            s.check_ret_expr(cfunc.get(cfunc.size()-1),v.type);
            if(v1!=null){
                if(v1.is_p==2){
                    v1.type=v.type;
                }
                if(v.type.trim().equals("nothing")){
                    System.exit(1);
                }
                ir.nextquad("RET",v1.s_t,"-","-");
                eval.push(v1);
            }
            else{
                if(v.ind.size()==0){
                    if(v.temp.name.equals("a")){
                        ir.nextquad(":=",v.s_t,"-","$$");
                    }
                    else{
                        ir.nextquad(":=",v.temp.name,"-","$$");
                    }
                    ir.nextquad("RET","-","-","-");
                }
                else{
                    if(v.name.equals("string") && v.ind.size()==1){
                        System.out.println("Returning string as value");
                        System.exit(1);
                    }
                    ir.nextquad(":=","["+v.temp.name+"]","-","$$");
                    ir.nextquad("RET","-","-","-");
                }
            }
            //System.out.println("54:stuck size "+eval.size()+":");
            //System.out.println("--------------------------------");
        }
        else{
            //System.out.println("55:stuck size "+eval.size()+":");
            Eval v=eval.poll();
            Eval v1=eval.poll();
            s.check_ret_no_expr(cfunc.get(cfunc.size()-1),"nothing");
            if(v1!=null){
                if(v1.is_p==2){
                    v1.type=v.type;
                }
                eval.push(v1);
            }
            ir.nextquad("RET","-","-","-");
            //System.out.println("56:stuck size "+eval.size()+":");
            //System.out.println("--------------------------------");
        }}
/*-----------------------------------------------STATEMENTS-------------------------------------------------------*/
    @Override
    public void caseAWhileStmt(AWhileStmt node){
        inAWhileStmt(node);
        Cond c = new Cond();
        ir.cond.add(c);
        ir.next.add(ir.nextquad());
        if(node.getCond() != null)
        {
            node.getCond().apply(this);
        }
        ir.next.add(ir.nextquad());
        //System.out.println("57:While stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println("58:While stuck size "+eval.size()+":");
        //System.out.println("--------------------------------");
        ir.backpatch("is_t");
        ir.next.remove(ir.next.size()-1);
        if(node.getStmt() != null)
        {
            node.getStmt().apply(this);
        }
        ir.nextquad("jump","-","-",ir.next.get(ir.next.size()-1).toString());
        ir.next.remove(ir.next.size()-1);
        ir.next.add(ir.nextquad());
        ir.backpatch("is_f");
        ir.next.remove(ir.next.size()-1);
        ir.cond.remove(ir.cond.size()-1);
        outAWhileStmt(node);}
    @Override
    public void caseANoElseStmt(ANoElseStmt node){
        Cond c = new Cond();
        ir.cond.add(c);
        inANoElseStmt(node);
        if(node.getCond() != null)
        {
            node.getCond().apply(this);
        }
        ir.next.add(ir.nextquad());
        //System.out.println("59:NoElse stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println("60:NoElse stuck size "+eval.size()+":");
        ir.backpatch("is_t");
        ir.next.remove(ir.next.size()-1);
        if(node.getThenStmt() != null)
        {
            node.getThenStmt().apply(this);
        }
        ir.next.add(ir.nextquad());
        ir.backpatch("is_f");
        ir.next.remove(ir.next.size()-1);
        ir.cond.remove(ir.cond.size()-1);
        outANoElseStmt(node);}
    @Override
    public void caseAWithElseStmt(AWithElseStmt node){
        Cond c = new Cond();
        ir.cond.add(c);
        inAWithElseStmt(node);
        if(node.getCond() != null)
        {
            node.getCond().apply(this);
        }
        //System.out.println("61:WithElse stuck size "+eval.size()+":");
        Eval v=eval.poll();
        ir.next.add(ir.nextquad());
        //System.out.println("62:WithElse stuck size "+eval.size()+":");
        ir.backpatch("is_t");
        ir.next.remove(ir.next.size()-1);
        if(node.getThenStmt() != null)
        {
            node.getThenStmt().apply(this);
        }
        ir.next.add(ir.nextquad());
        ir.backpatch("is_f");
        ir.next.remove(ir.next.size()-1);
        ir.cond.remove(ir.cond.size()-1);
        if(node.getElseStmt() != null)
        {
            node.getElseStmt().apply(this);
        }
        outAWithElseStmt(node);}
/*-----------------------------------------------LVALUES ARRAYS---------------------------------------------------*/
    @Override
    public void outAArrayLValue(AArrayLValue node){
        //System.out.println("62:Array ArrayLvalue stuck size "+eval.size()+":");
        Eval v=eval.poll();
        //System.out.println(v.name);
        //System.out.println(v.type);
        Eval v1=eval.poll();
        //System.out.println(v1.name);
        //System.out.println(v1.type);
        if(v.type.equals("int")==false){
            System.out.println("Different assign type between "+v.type+" and "+v1.type);
            System.exit(1);
        }
        int i=v.ind.size()-1;
        if(v.ind.size()==0 && v.name.equals("int_const")){
            v1.ind.add(v.s_t);
        }
        else if(v.ind.size()==0 && v.name.equals("int_const")==false){
            v1.ind.add(v.name);
        }
        else{
            while(i>=0){
                if(i>0){
                    v1.ind.add(v.ind.get(i));
                }
                else if(i==0 && v.name.equals("int_cosnt")==false){
                    v1.ind.add(v.s_t);
                }
                else{
                    v1.ind.add(v.name);
                }
                i--;
            }
        }
        v1.temp.name=v.temp.name;
        //System.out.print(v1.name);
        //for(i=0;i<v1.ind.size();i++){
        //    System.out.print(" "+v1.ind.get(i));
        //}
        //System.out.println();
        if(v1.ind.size()>=3 && v1.name.equals("string")){
            System.out.println("String literal with >=2 Dimensions LValue ");
            System.exit(1);
        }
        eval.push(v1);
        //System.out.println("63:Array LValue stuck size "+eval.size()+":");
        /*System.out.println("--------------------------------");*/}}