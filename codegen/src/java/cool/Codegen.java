package cool;

import java.io.PrintWriter;
import java.util.*;

public class Codegen{
    
    ClassInfoTable classTable = new ClassInfoTable();
    String filename;
    String mainReturnType = "i32";
    String globalStrings = "";
    int varCount = 0;
    int strCount = 0;
    int loopCount = 0;
    int ifCount = 0;
    
	public Codegen(AST.program program, PrintWriter out){
		//Write Code generator code here
        out.println("; I am a comment in LLVM-IR. Feel free to remove me.");
        printHeaders(out);
        printClasses(program.classes, out);
        
        printMainFunc(out);
        out.println(globalStrings);
	}

	private void printMainFunc(PrintWriter out) {
		out.println("define i32 @main() {\n"
			+"entry:\n"
			+"\t%0 = alloca %class.Main, align 4\n"
			+"\tcall "+mainReturnType+" @_ZN4Main4main(%class.Main* %0)\n"
			+"\tret i32 0\n"
			+"}");
	}
	
	private void printClasses(List <AST.class_> classes, PrintWriter out){
	    
	    // A hash Map that maps form class names to AST.class_
		HashMap <String, AST.class_> astClasses = new HashMap <String, AST.class_> ();
		// Store Graph as adjacency list
		HashMap < String, ArrayList <String> > graph = new HashMap < String, ArrayList <String> >();
		// Add Object and IO to the graph
		graph.put("Object", new ArrayList<String>());
		graph.put("IO", new ArrayList<String>());

        // Populate astClasses maps and strat creating Adjcacency list
		for (AST.class_ c : classes){
			//System.out.println(c.name);		
			graph.put(c.name, new ArrayList <String> ());
			astClasses.put(c.name, c);
		}

		// Add edge from Object to IO in the graph
		// IO is inherited from Object
		// String, Int, Bool are not added as no function inherits from them
		graph.get("Object").add("IO");
		// Check if parents of the classes exits and add edges from parent to child
		for (AST.class_ c : classes){
			if (c.parent != null) graph.get(c.parent).add(c.name);
		}

		// We perform a BFS on the graph and print the LLVM-IR for each class
		Queue <String> q = new LinkedList<String>(); // Queue reuired for BFS

		q.offer("Object"); // Add root Class
		while(q.isEmpty() == false){
			String c = q.poll();
			// classTable.insert(astClasses.get(c));
			if (!c.equals("Object") && !c.equals("IO")) 
				classTable.insert(astClasses.get(c));
			printClass(c, out);
			for (String child : graph.get(c)){
				q.offer(child);
			}
		}

		q.clear(); // Clear the queue so that the next BFS can be performed
		
		q.offer("Object"); // Add root Class
		while(q.isEmpty() == false){
			String c = q.poll();
			printClassMethods(c, out);
			for (String child : graph.get(c)){
				q.offer(child);
			}
		}

		q.clear();
	}

	// Print the LLVM-IR for each class declaration
	void printClass(String c, PrintWriter out){
		// if (c.equals("Object")) {
			
		// 	return;
		// }
	    ClassInfo ci = classTable.classinfos.get(c);
	    String attrsStr = "";
	    for (String a : ci.attrList) {
	    	if (a.equals("_Par")) {
	    		attrsStr += "%class."+ci.parent+", ";
	    		continue;
	    	}
	    	AST.attr attr = ci.attrMap.get(a);
	    	attrsStr += parseType(attr.typeid)+", ";
	    }
	    if (attrsStr.length() >= 2) attrsStr = attrsStr.substring(0, attrsStr.length()-2);
	    out.println("%class."+c+" = type { " + attrsStr + " }");
	}

	String parseType(String t){
		if (t.equals("Int") || t.equals("Bool"))
    		return "i32";
    	else if (t.equals("String"))
    		return "[1024 x i8]*";
    	else {
    		return "%class."+t+"*";
    	}
	}

	String reverseParseType(String t){
		if (t.equals("i32")) {
			System.out.println("I will never come here");
			return "Int";
		} else if (t.equals("[1024 x i8]*")) {
			return "String";
		} else {
			return t.substring(7, t.length()-1);
		}
	}

	// Print the constructor of te classes and print th inbuilt functions for Object, String, IO, Int, Bool
	void printClassMethods(String c, PrintWriter out){
		if (c.equals("Object")) {
			printObjectMethods(out);
			return;
		}
		if (c.equals("String")) {
			printStringMethods(out);
			return;
		}
		if (c.equals("IO")) {
			printIOMethods(out);
			return;
		}

	    ClassInfo ci = classTable.classinfos.get(c);
	    
	    for (Map.Entry<String, AST.method> entry : ci.methodMap.entrySet()){
	    	AST.method m = entry.getValue();
	    	String formals = "%class."+c+"* %self";
	    	for (AST.formal f : m.formals) {
	    		formals += ", "+parseType(f.typeid)+" %"+f.name;
	    	}
	    	if (c.equals("Main") && entry.getKey().equals("main"))
	    		mainReturnType = parseType(m.typeid);
	        out.println("define "+parseType(m.typeid)+" "+ci.methodName.get(entry.getKey())+"( "+formals+" )" + "{");
	        out.println("entry:");
	        varCount = -1;
	        loopCount = -1;
	        ifCount = -1;
	        String ret = printExpr(c, m.body, out);
	        out.println("\tret "+ret);
	        out.println("}");
	    }
	}

	String printExpr(String cname, AST.expression expr, PrintWriter out) {
		ClassInfo ci = classTable.classinfos.get(cname);
		if (expr instanceof AST.bool_const) {
			AST.bool_const e = (AST.bool_const) expr;
			return "i32 " + (e.value ? 1 : 0);
		} else if (expr instanceof AST.string_const) {
			AST.string_const e = (AST.string_const) expr;
			String ty = "["+(e.value.length()+1)+" x i8]";
			globalStrings += "@.str"+(strCount++)+" = private unnamed_addr constant "+ty+" c\""+e.value+"\\00\", align 1\n";
			out.println("\t%"+(++varCount)+" = bitcast "+ty+"* @.str"+(strCount-1)+" to [1024 x i8]*");
			return "[1024 x i8]* %"+varCount;
		} else if (expr instanceof AST.int_const) {
			AST.int_const e = (AST.int_const) expr;
			return "i32 " + e.value;
		} else if (expr instanceof AST.object) {
			AST.object e = (AST.object) expr;
			int attri = ci.attrList.indexOf(e.name);
			if (attri == -1)
				return parseType(e.type) + " %"+e.name;
			String parseTypeName = parseType(cname);
			parseTypeName = parseTypeName.substring(0, parseTypeName.length()-1);
			out.println("\t%"+(++varCount)+" = getelementptr inbounds "+parseTypeName+", "+parseTypeName+"* %self, i32 0, i32 "+attri);
			out.println("\t%"+(++varCount)+" = load "+parseType(e.type)+", "+parseType(e.type)+"* %"+(varCount-1)+", align 4");
			return parseType(e.type)+" %"+varCount;
		} else if (expr instanceof AST.comp) {
			AST.comp e = (AST.comp) expr;
			String e1 = printExpr(cname, e.e1, out);
			out.println("\t%"+(++varCount)+" = sub nsw i32 1, " + e1.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.eq) {
			AST.eq e = (AST.eq) expr;
			String e1 = printExpr(cname, e.e1, out);
			String e2 = printExpr(cname, e.e2, out);
			out.println("\t%"+(++varCount)+" = icmp eq i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.leq) {
			AST.leq e = (AST.leq) expr;
			String e1 = printExpr(cname, e.e1, out);
			String e2 = printExpr(cname, e.e2, out);
			out.println("\t%"+(++varCount)+" = icmp sle i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.lt) {
			AST.lt e = (AST.lt) expr;
			String e1 = printExpr(cname, e.e1, out);
			String e2 = printExpr(cname, e.e2, out);
			out.println("\t%"+(++varCount)+" = icmp slt i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.neg) {
			AST.neg e = (AST.neg) expr;
			String e1 = printExpr(cname, e.e1, out);
			out.println("\t%"+(++varCount)+" = sub nsw i32 0, " + e1.substring(4));
		} else if (expr instanceof AST.divide) {
			AST.divide e = (AST.divide) expr;
			String e1 = printExpr(cname, e.e1, out);
			String e2 = printExpr(cname, e.e2, out);
			out.println("\t%"+(++varCount)+" = sdiv i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.mul) {
			AST.mul e = (AST.mul) expr;
			String e1 = printExpr(cname, e.e1, out);
			String e2 = printExpr(cname, e.e2, out);
			out.println("\t%"+(++varCount)+" = mul nsw i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.sub) {
			AST.sub e = (AST.sub) expr;
			String e1 = printExpr(cname, e.e1, out);
			String e2 = printExpr(cname, e.e2, out);
			out.println("\t%"+(++varCount)+" = sub nsw i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.plus) {
			AST.plus e = (AST.plus) expr;
			String e1 = printExpr(cname, e.e1, out);
			String e2 = printExpr(cname, e.e2, out);
			out.println("\t%"+(++varCount)+" = add nsw i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.isvoid) {
			AST.isvoid e = (AST.isvoid) expr;
			// TODO
			return "";
		} else if (expr instanceof AST.new_) {
			AST.new_ e = (AST.new_) expr;
			out.println("\t%"+(++varCount)+" = alloca "+parseType(e.typeid)+", align 4");
			return parseType(e.typeid)+" %"+varCount;
		} else if (expr instanceof AST.assign) {
			AST.assign e = (AST.assign) expr;
			out.println("\tstore "); // TODO
			return "";
		} else if (expr instanceof AST.block) {
			AST.block e = (AST.block) expr;
			String re = "";
			for (AST.expression ex : e.l1) {
				re = printExpr(cname, ex, out);
			}
			return parseType(expr.type)+" "+re.substring(4);
		} else if (expr instanceof AST.loop) {
			AST.loop e = (AST.loop) expr;
			int loopcnt = ++loopCount;
			out.println("\tbr label %loop.cond"+loopcnt);
			out.println();
			out.println("loop.cond"+loopcnt+":");
			String pred = printExpr(cname, e.predicate, out);
			out.println("\tbr i1 "+pred.substring(4)+", label %loop.body"+loopcnt+" , label %loop.end"+loopcnt);
			out.println();
			out.println("loop.body"+loopcnt+":");
			String body = printExpr(cname, e.body, out);
			out.println("\tbr label %loop.cond"+loopcnt);
			out.println();
			out.println("loop.end"+loopcnt+":");
			return body;
		} else if (expr instanceof AST.cond) {
			AST.cond e = (AST.cond) expr;
			int ifcnt = ++ifCount;
			String pred = printExpr(cname, e.predicate, out);
			out.println("\tbr i1 "+pred.substring(4)+", label %if.then"+ifcnt+", label %if.else"+ifcnt);
			out.println();
			out.println("if.then"+ifcnt+":");
			String ifbody = printExpr(cname, e.ifbody, out);
			ifbody = ifbody.split(" ")[1];
			out.println("\tbr label %if.end"+ifcnt);
			out.println();
			out.println("if.else"+ifcnt+":");
			String elsebody = printExpr(cname, e.elsebody, out);
			elsebody = elsebody.split(" ")[1];
			out.println("\tbr label %if.end"+ifcnt);
			out.println();
			out.println("if.end"+ifcnt+":");
			out.println("\t%"+(++varCount)+" = phi "+parseType(e.type)
				+" ["+ifbody+", %if.then"+ifcnt+"], ["+elsebody+", %if.else"+ifcnt+"]");
			return parseType(e.type)+" %"+varCount;
		} else if (expr instanceof AST.static_dispatch) {
			AST.static_dispatch e = (AST.static_dispatch) expr;
			String caller = printExpr(cname, e.caller, out);
			List<String> actuals = new ArrayList<>();
			for (AST.expression actual : e.actuals) {
				String a = printExpr(cname, actual, out);
				actuals.add(a);
			}
			String funcname = "@_ZN"+e.typeid.length()+e.typeid+e.name.length()+e.name;
			ClassInfo ci2 = classTable.classinfos.get(reverseParseType(caller.split(" ")[0]));
			while (!caller.split(" ")[0].equals(parseType(e.typeid))) {
				String par = parseType(ci2.parent);
				par = par.substring(0, par.length()-1);
				String ty = caller.split(" ")[0];
				ty = ty.substring(0, ty.length()-1);
				out.println("\t%"+(++varCount)+" = getelementptr inbounds "+ty+", "+ty+"* "+caller.split(" ")[1]+", i32 0, i32 0");
				caller = par+"* %"+varCount;
				ci2 = classTable.classinfos.get(ci2.parent);
			}
			String actualsStr = caller;
			for (int i=0; i<actuals.size(); i++)
				actualsStr += ", " + actuals.get(i);
			out.println("\t%"+(++varCount)+" = call "+parseType(e.type)+" "+funcname+"("+actualsStr+")");
			return parseType(e.type)+" %"+varCount;
		} else {
			System.out.println("I will never come here");
		}
		return "";
	}

	void printHeaders(PrintWriter out) {
		out.println("target datalayout = \"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"");
		out.println("target triple = \"x86_64-unknown-linux-gnu\"");
		out.println("@Abortdivby0 = private unnamed_addr constant [22 x i8] c\"Error: Division by 0\\0A\\00\", align 1\n"
			+ "@Abortdispvoid = private unnamed_addr constant [25 x i8] c\"Error: Dispatch on void\\0A\\00\", align 1\n");
		out.println("declare i32 @printf(i8*, ...)\n"
			+ "declare i32 @scanf(i8*, ...)\n"
			+ "declare i32 @strcmp(i8*, i8*)\n"
			+ "declare i8* @strcat(i8*, i8*)\n"
			+ "declare i8* @strcpy(i8*, i8*)\n"
			+ "declare i8* @strncpy(i8*, i8*, i32)\n"
			+ "declare i64 @strlen(i8*)\n"
			+ "declare i8* @malloc(i64)\n"
			+ "declare void @exit(i32)");
		out.println("@strformatstr = private unnamed_addr constant [3 x i8] c\"%s\\00\", align 1\n"
			+ "@intformatstr = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\n");
	}

	void printObjectMethods(PrintWriter out) {
		out.println("define %class.Object* @_ZN6Object5abort( %class.Object* %this ) noreturn {\n"
			+ "entry:\n"
			+ "\tcall void @exit( i32 1 )\n"
			+ "\tret %class.Object* null\n"
			+ "}\n");

		// out.println("define [1024 x i8]* @_ZN6Object9type_name( %class.Object* %this ) {\n"
		// 	+ "entry:\n"
		// 	+ "\t%0 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 0\n"
		// 	+ "\t%1 = load i32, i32* %0\n"
		// 	+ "\t%2 = getelementptr inbounds [8 x [1024 x i8]], [8 x [1024 x i8]]* @classnames, i32 0, i32 %1\n"
		// 	+ "\t%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %2 )\n"
		// 	+ "\tret [1024 x i8]* %retval\n"
		// 	+ "}\n");

		// out.println("define %class.Object* @_ZN6Object4copy( %class.Object* %this ) {\n"
		// 	+ "entry:\n"
		// 	+ "\t%call = call i8* @malloc( i64 32 )\n"
		// 	+ "\t%retval = bitcast i8* %call to %class.Object*\n"
		// 	+ "\t%0 = getelementptr inbounds %class.Object, %class.Object* %retval, i32 0, i32 0\n"
		// 	+ "\tstore i32 0, i32* %0\n"
		// 	+ "\t%1 = getelementptr inbounds %class.Object, %class.Object* %retval, i32 0, i32 1\n"
		// 	+ "\tstore i8* bitcast ( [3 x i8*]* @VTObject to i8*), i8** %1\n"
		// 	+ "\t%2 = getelementptr inbounds %class.Object, %class.Object* %retval, i32 0, i32 2\n"
		// 	+ "\t%3 = getelementptr inbounds %class.Object, %class.Object* %this, i32 0, i32 2\n"
		// 	+ "\tcall void @_ZN6Object4copyTo( %classbaseObject* %2, %classbaseObject* %3 )\n"
		// 	+ "\tret %class.Object* %retval\n"
		// 	+ "}\n");
	}

	void printStringMethods(PrintWriter out) {
		out.println("define i32 @_ZN6String6length( [1024 x i8]* %this ) {\n"
			+ "\tentry:\n"
			+ "\t%0 = bitcast [1024 x i8]* %this to i8*\n"
			+ "\t%1 = call i64 @strlen( i8* %0 )\n"
			+ "\t%retval = trunc i64 %1 to i32\n"
			+ "\tret i32 %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String6concat( [1024 x i8]* %this, [1024 x i8]* %that ) {\n"
			+ "entry:\n"
			+ "\t%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %this )\n"
			+ "\t%0 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%1 = bitcast [1024 x i8]* %that to i8*\n"
			+ "\t%2 = call i8* @strcat( i8* %0, i8* %1 )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %this ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %0 to [1024 x i8]*\n"
			+ "\t%1 = bitcast [1024 x i8]* %this to i8*\n"
			+ "\t%2 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%3 = call i8* @strcpy( i8* %2, i8* %1)\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String6substr( [1024 x i8]* %this, i32 %start, i32 %len ) {\n"
			+ "entry:\n"
			+ "\t%0 = getelementptr inbounds [1024 x i8], [1024 x i8]* %this, i32 0, i32 %start\n"
			+ "\t%1 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %1 to [1024 x i8]*\n"
			+ "\t%2 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%3 = call i8* @strncpy( i8* %2, i8* %0, i32 %len )\n"
			+ "\t%4 = getelementptr inbounds [1024 x i8], [1024 x i8]* %retval, i32 0, i32 %len\n"
			+ "\tstore i8 0, i8* %4\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	}

	void printIOMethods(PrintWriter out) {
		out.println("define %class.IO* @_ZN2IO10out_string( %class.IO* %this, [1024 x i8]* %str ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %str )\n"
			+ "\tret %class.IO* %this\n"
			+ "}\n");
	
		out.println("define %class.IO* @_ZN2IO7out_int( %class.IO* %this, i32 %int ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32 %int )\n"
			+ "\tret %class.IO* %this\n"
			+ "}\n");
	
		out.println("define [1024 x i8]* @_ZN2IO9in_string( %class.IO* %this ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %0 to [1024 x i8]*\n"
			+ "\t%1 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %retval )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	
		out.println("define i32 @_ZN2IO9in_int( %class.IO* %this ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 4 )\n"
			+ "\t%1 = bitcast i8* %0 to i32*\n"
			+ "\t%2 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32* %1 )\n"
			+ "\t%retval = load i32, i32* %1\n"
			+ "\tret i32 %retval\n"
			+ "}\n");
	}
}
