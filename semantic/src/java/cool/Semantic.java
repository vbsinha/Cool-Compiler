package cool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.Map.Entry;

import cool.AST.class_;

public class Semantic{
	private boolean errorFlag = false;
	public void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

/*
	Don't change code above this line
*/

    ClassTable classTable = new ClassTable();
    ScopeTable<AST.attr> scopeTable = new ScopeTable<AST.attr>();
    String filename;
	public Semantic(AST.program program){
		//Write Semantic analyzer code here
		
		check_cycles(program.classes);
		
		for(Error e : classTable.errors) {
			reportError(e.fname, e.line, e.err);
		}
		
		for (AST.class_ c : program.classes) {
		    filename = c.filename;
		    scopeTable.enterScope();
		    scopeTable.insert("self", new AST.attr("self", c.name, new AST.no_expr(c.lineNo), c.lineNo));
		    for (Entry<String, AST.attr> entry : classTable.classinfos.get(c.name).attrlist.entrySet())
		        scopeTable.insert(entry.getKey(), entry.getValue());
		    //scopeTable.insertAll(classTable.getAttrs(e.name));
		    for (Entry<String, AST.attr> entry : classTable.classinfos.get(c.name).attrlist.entrySet()){
		        AST.attr attr = entry.getValue();
		        if(attr.value.getClass() != AST.no_expr.class) {
			        ProcessExpr(attr.value);
			        if(classTable.isAncestor(attr.value.type, attr.typeid) == false) {
				        reportError(c.filename, attr.value.lineNo, "Declared type " + attr.typeid + " of attribute "
						        + attr.name + " is not an ancestor of the infered type " + attr.value.type);
			        }
		        }
		    }
		    
		    for (Entry<String, AST.method> entry : classTable.classinfos.get(c.name).methodlist.entrySet()) {
		        AST.method m = entry.getValue();
		        scopeTable.enterScope();
		        for(AST.formal e : m.formals) {
			        scopeTable.insert(e.name, new AST.attr(e.name, e.typeid, new AST.no_expr(e.lineNo), e.lineNo));
	            }
	            ProcessExpr(m.body);
	            if (classTable.isAncestor(m.body.type, m.typeid)) {
	                reportError(c.filename, m.body.lineNo, "Return type " + m.typeid + " of method "
						        + m.name + " is not an ancestor of the infered method body type " + m.body.type);
	            }
		        scopeTable.exitScope();
		        
		    }
		    
		    scopeTable.exitScope();
		}
		
		ClassInfo main_class = classTable.classinfos.get("Main");
		if(main_class == null)
			reportError(filename, 1, "Program does not contain class Main");
		else if(main_class.methodlist.containsKey("main") == false)
			reportError(filename, 1, "Main class does not contain main method");
	}
	
	private void ProcessExpr(AST.expression expr) {
	    if(expr.getClass() == AST.assign.class)
			ProcessNode((AST.assign)expr);
		else if(expr.getClass() == AST.static_dispatch.class)
			ProcessNode((AST.static_dispatch)expr);
		else if(expr.getClass() == AST.dispatch.class)
			ProcessNode((AST.dispatch)expr);
		else if(expr.getClass() == AST.cond.class)
			ProcessNode((AST.cond)expr);
		else if(expr.getClass() == AST.loop.class)
			ProcessNode((AST.loop)expr);
		else if(expr.getClass() == AST.block.class)
			ProcessNode((AST.block)expr);
		else if(expr.getClass() == AST.let.class)
			ProcessNode((AST.let)expr);
		else if(expr.getClass() == AST.typcase.class)
			ProcessNode((AST.typcase)expr);
		else if(expr.getClass() == AST.new_.class)
			ProcessNode((AST.new_)expr);
		else if(expr.getClass() == AST.isvoid.class)
			ProcessNode((AST.isvoid)expr);
		else if(expr.getClass() == AST.plus.class)
			//ProcessNode((AST.plus)expr);
			plus = (AST.plus)expr;
			plus.type = checkBinary(plus.e1, plus.e2, plus.lineNo, '+');
		else if(expr.getClass() == AST.sub.class)
			//ProcessNode((AST.sub)expr);
			sub = (AST.sub)expr;
			sub.type = checkBinary(sub.e1, sub.e2, sub.lineNo, '-');
		else if(expr.getClass() == AST.mul.class)
			//ProcessNode((AST.mul)expr);
			mul = (AST.mul)expr;
			mul.type = checkBinary(mul.e1, mul.e2, mul.lineNo, '-');
		else if(expr.getClass() == AST.divide.class)
			//ProcessNode((AST.divide)expr);
			divide = (AST.divide)expr;
			divide.type = checkBinary(divide.e1, divide.e2, divide.lineNo, '-');
		else if(expr.getClass() == AST.comp.class)
			ProcessNode((AST.comp)expr);
		else if(expr.getClass() == AST.lt.class)
			//ProcessNode((AST.lt)expr);
			lt = (AST.lt)expr;
			lt.type = checkBinary(lt.e1, lt.e2, lt.lineNo, '-');
		else if(expr.getClass() == AST.leq.class)
			//ProcessNode((AST.leq)expr);
			leq = (AST.leq)expr;
			leq.type = checkBinary(leq.e1, leq.e2, leq.lineNo, '-');
		else if(expr.getClass() == AST.eq.class)
			ProcessNode((AST.eq)expr);
		else if(expr.getClass() == AST.neg.class)
			ProcessNode((AST.neg)expr);
		else if(expr.getClass() == AST.object.class)
			ProcessNode((AST.object)expr);
		else if(expr.getClass() == AST.int_const.class)
			ProcessNode((AST.int_const)expr);
		else if(expr.getClass() == AST.string_const.class)
			ProcessNode((AST.string_const)expr);
		else if(expr.getClass() == AST.bool_const.class)
			ProcessNode((AST.bool_const)expr);
	}
	
	/*private void ProcessNode(AST.plus plus) {
	    plus.type = checkBinary(plus.e1, plus.e2, plus.lineNo, '+');
		ProcessNode(plus.e1);
		ProcessNode(plus.e2);
		if(plus.e1.type.equals("Int") == false || plus.e2.type.equals("Int") == false) {
			reportError(filename, plus.lineNo, "non-Int arguments: " + plus.e1.type + " + " + plus.e2.type);
		}
		plus.type = "Int";
	}*/
	
	private String checkBinary(AST.expression e1, AST.expression e2, int lineNo, char operator){
	    ProcessExpr(e1);
		ProcessExpr(e2);
		if(e1.type.equals("Int") == false || e2.type.equals("Int") == false) {
			reportError(filename, lineNo, "Non-Int argument detected : " + e1.type + " " + operator + " " + e2.type);
		}
		return "Int";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void check_cycles(List <AST.class_> classes){
	    
	    /*HashMap <String, Integer> classIndex = new HashMap <String, Integer> ();
		HashMap <Integer, String> indexClass = new HashMap <Integer, String>();*/
		HashMap <String, AST.class_> astClasses = new HashMap <String, AST.class_> ();
		HashMap < String, ArrayList <String> > graph = new HashMap < String, ArrayList <String> >();
		graph.put("Object", new ArrayList <String> ());
		graph.put("IO", new ArrayList <String> ());
		
		ArrayList <String> classNames = new ArrayList <String> ();
		classNames.add("Object");
		classNames.add("IO");
		
		List <String> redef = Arrays.asList("Object", "String", "Int", "Bool", "IO");
		List <String> inherit = Arrays.asList("String", "Int", "Bool");
		
		for (AST.class_ c : classes){
		    if (classNames.contains(c.name)){
		        reportError(c.filename, c.lineNo, "Class "+c.name+" has been redefined.");
		        System.exit(1);
		    }
		    else if (redef.contains(c.name)){
		        reportError(c.filename, c.lineNo, "Class "+c.name+" can not be redefined.");
		        System.exit(1);
		    }
		    else if (inherit.contains(c.parent)){
		        reportError(c.filename, c.lineNo, "Class "+c.parent+" can not be inherited.");
		        System.exit(1);
		    }
		    else{
		        classNames.add(c.name);
		        graph.put(c.name, new ArrayList <String> ());
		        astClasses.put(c.name, c);
		    }		        
		}
		
		graph.get("Object").add("IO");
		for (AST.class_ c : classes){
		    if (classNames.contains(c.parent) == false){
		        reportError(c.filename, c.lineNo, "Class "+c.parent+" has not been defined.");
		        System.exit(1);
		    }
		    graph.get(c.parent).add(c.name);
		}
		
		ArrayList <String> visitedClasses = new ArrayList <String> ();
		Queue <String> q = new LinkedList<String>(); 
		
		boolean cycle = false;
		//q.offer("Object");
		//while(visitedClasses.getSize() != classNames.size())
		for (String s : classNames){
		    if (visitedClasses.contains(s) == false){
		        q.offer(s);
		        while(q.isEmpty() == false){
		            String c = q.poll();
		            for (String child : graph.get(c)){
		                if (visitedClasses.contains(child)){
		                    AST.class_ cClass = astClasses.get(c);
		                    reportError(cClass.filename, cClass.lineNo, "Class "+child+" is involved in a cycle.");
		                    cycle = true;
		                }
		                else {
		                    q.offer(child);
		                }
		            }
		            visitedClasses.add(c);
		        }
		    }
		}
		
		if (cycle)
		    System.exit(1);
		    
		q.clear();
		q.offer("Object");
		
		while(q.isEmpty() == false){
		    String currClass = q.poll();
		    if (currClass != "Object" && currClass != "IO"){
		        classTable.insert(astClasses.get(currClass));
		    }
		    for (String child : graph.get(currClass))
		        q.offer(child);
		}	    
	}
}


























