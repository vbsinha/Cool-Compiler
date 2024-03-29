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
        printHeaders(out);
        printClasses(program.classes, out);
        printMainFunc(out);
        printStringMethods(out);
        out.println(globalStrings);
	}

	// Print main function that creates object of Main class and calls main function
	private void printMainFunc(PrintWriter out) {
		out.println("define i32 @main() {\n"
			+"entry:\n"
			+"\t%0 = alloca %class.Main, align 4\n"
			+"\tcall i32 @_ZN4Main8__cons__(%class.Main* %0)\n"
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
		for (AST.class_ c : classes) {
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
			// Add classes to classTable (object of ClassInfoTable)
			if (!c.equals("Object") && !c.equals("IO")) 
				classTable.insert(astClasses.get(c));
			// Print the code related to class
			printClass(c, out);
			// Add children to BFS queue
			for (String child : graph.get(c)){
				q.offer(child);
			}
		}

		out.println();

		q.clear(); // Clear the queue so that the next BFS can be performed
		
		q.offer("Object"); // Add root Class
		while(q.isEmpty() == false){
			String c = q.poll();
			// Print all the methods in the class
			printClassMethods(c, out);
			// Add children to BFS queue
			for (String child : graph.get(c)){
				q.offer(child);
			}
		}

		q.clear();
	}

	// Print the LLVM-IR for each class declaration
	void printClass(String c, PrintWriter out){

		if (c.equals("Object")) {
			out.println("%class.Object = type { i32, [1024 x i8] * }");
			return;
		}

	    ClassInfo ci = classTable.classinfos.get(c);
	    // Print all the attributes
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

	// Returns type equivalent in LLVM_IR
	String parseType(String t){
		if (t.equals("Int") || t.equals("Bool"))
    		return "i32";
    	else if (t.equals("String"))
    		return "[1024 x i8]*";
    	else {
    		return "%class."+t+"*";
    	}
	}

	// Return type in cool given return type in LLVM-IR
	String reverseParseType(String t){
		if (t.equals("i32")) {
			System.out.println("I will never come here reverseParseType");
			return "Int";
		} else if (t.equals("[1024 x i8]*")) {
			return "String";
		} else {
			return t.substring(7, t.length()-1);
		}
	}

	// Return type given in format "type %var"
	String reverseParseTypeValue(String t) {
		if (t.length() > 12 && t.substring(0, 12).equals("[1024 x i8]*")) {
			return "[1024 x i8]*";
		} else {
			return t.split(" ")[0];
		}
	}

	// Return var given in format "type %var"
	String reverseParseTypeValueVar(String t) {
		String[] vals = t.split(" ");
		return vals[vals.length-1];
	}

	// Print the constructor of te classes and print the inbuilt functions for Object, IO
	void printClassMethods(String c, PrintWriter out){
		// Print methods for Object
		if (c.equals("Object")) {
			printObjectMethods(out);
			return;
		}
		// Print methods for IO
		if (c.equals("IO")) {
			printIOMethods(out);
			return;
		}

	    ClassInfo ci = classTable.classinfos.get(c);

	    // print constructor for each class
	    String formals = parseType(c)+" %self";
	    out.println("define i32 @_ZN"+c.length()+c+8+"__cons__( "+formals+" ) {");
	    out.println("entry:");
	   	varCount = -1;
        loopCount = -1;
        ifCount = -1;
        List<String> blocks = new ArrayList<>();
	    for (Map.Entry<String, AST.attr> entry : ci.attrMap.entrySet()) {
	    	// Add all assignements for attributes in constructor
	    	AST.attr a = entry.getValue();
	    	if (!(a.value instanceof AST.no_expr)) {
		    	AST.assign exp = new AST.assign(a.name, a.value, 0);
		    	exp.type = a.typeid;
		    	printExpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else if (a.typeid.equals("Int")) {
		    	AST.assign exp = new AST.assign(a.name, new AST.int_const(0, 0), 0);
		    	exp.type = "Int";
		    	printExpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else if (a.typeid.equals("Bool")) {
		    	AST.assign exp = new AST.assign(a.name, new AST.bool_const(false, 0), 0);
		    	exp.type = "Bool";
		    	printExpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else if (a.typeid.equals("String")) {
		    	AST.assign exp = new AST.assign(a.name, new AST.string_const("", 0), 0);
		    	exp.type = "String";
		    	printExpr(c, null, exp, new ArrayList<>(), blocks, out);
		    } else {
		    	int attri = ci.attrList.indexOf(a.name);
		    	String ctype = parseType(c);
		    	ctype = ctype.substring(0, ctype.length()-1);
		    	out.println("\t%"+(++varCount)+" = getelementptr inbounds "+ctype+", "+ctype+"* %self, i32 0, i32 "+attri);
		    	out.println("\tstore "+parseType(a.typeid)+" null, "+parseType(a.typeid)+"* %"+varCount+", align 4");
		    }
	    }
	    String caller = parseType(c)+" %self";
	    ClassInfo ci2 = ci;
	    while (!reverseParseTypeValue(caller).equals(parseType("Object"))) {
			String par = parseType(ci2.parent);
			par = par.substring(0, par.length()-1);
			String ty = reverseParseTypeValue(caller);
			ty = ty.substring(0, ty.length()-1);
			out.println("\t%"+(++varCount)+" = getelementptr inbounds "+ty+", "+ty+"* "+reverseParseTypeValueVar(caller)+", i32 0, i32 0");
			caller = par+"* %"+varCount;
			ci2 = classTable.classinfos.get(ci2.parent);
		}
		// Print LLVM-IR to add size and type_name in the object attribue of the current class
		out.println("\t%"+(++varCount)+" = getelementptr inbounds %class.Object, %class.Object* "+reverseParseTypeValueVar(caller)+", i32 0, i32 0");
		out.println("\tstore i32 "+ci.size+", i32* %"+varCount);
		String ty = "["+(c.length()+1)+" x i8]";
		globalStrings += "@.str"+(strCount++)+" = private unnamed_addr constant "+ty+" c\""+c+"\\00\", align 1\n";
		out.println("\t%"+(++varCount)+" = bitcast "+ty+"* @.str"+(strCount-1)+" to [1024 x i8]*");
		out.println("\t%"+(++varCount)+" = getelementptr inbounds %class.Object, %class.Object* "+reverseParseTypeValueVar(caller)+", i32 0, i32 1");
		out.println("\tstore [1024 x i8]* %"+(varCount-1)+", [1024 x i8]** %"+varCount);
		out.println("\tret i32 0");
	    out.println("}\n");
	    
	    // Print all other methods
	    for (Map.Entry<String, AST.method> entry : ci.methodMap.entrySet()) {
	    	AST.method m = entry.getValue();
	    	formals = "%class."+c+"* %self";
	    	for (AST.formal f : m.formals) {
	    		formals += ", "+parseType(f.typeid)+" %"+f.name;
	    	}
	    	blocks.clear();
	    	// Set return type for main function in Main class
	    	if (c.equals("Main") && entry.getKey().equals("main"))
	    		mainReturnType = parseType(m.typeid);

	    	// start defining the function
	        out.println("define "+parseType(m.typeid)+" "+ci.methodName.get(entry.getKey())+"( "+formals+" )" + "{");
	        out.println("entry:");
	        blocks.add("entry");
	        varCount = -1;
	        loopCount = -1;
	        ifCount = -1;
	        List<String> changedFormals = new ArrayList<>();
	        // Allot all formals in the stack
			for (AST.formal f : m.formals) {
				String type = parseType(f.typeid);
				out.println("%"+f.name+".addr = alloca "+type+", align 4");
				changedFormals.add(f.name);
				out.println("\tstore "+type+" %"+f.name+", "+type+"* %"+f.name+".addr, align 4");
			}
			// Print LLVM-IR for body of the function
	        String ret = printExpr(c, m, m.body, changedFormals, blocks, out);
	        String rettype = reverseParseTypeValue(ret);
	        // Print return statement
	        if (!rettype.equals(parseType(m.typeid))) {
	        	if (rettype.equals("i32")) {
	        		out.println("\t%"+(++varCount)+" = call noalias i8* @malloc(i64 8)"); // Object size
	        		out.println("\t%"+(++varCount)+" = bitcast i8* %"+(varCount-1)+" to "+parseType(m.typeid));
	        	} else {
	        		out.println("\t%"+(++varCount)+" = bitcast "+ret+" to "+parseType(m.typeid));	        		
	        	}
	        	ret = parseType(m.typeid)+" %"+varCount;
	        }
	        out.println("\tret "+ret);
	        out.println("}\n");
	    }
	}

	// Print LLVM-IR for expression
	String printExpr(String cname, AST.method method, AST.expression expr, List<String> changedFormals, List<String> blocks, PrintWriter out) {
		ClassInfo ci = classTable.classinfos.get(cname);
		if (expr instanceof AST.bool_const) { // Bool constant
			AST.bool_const e = (AST.bool_const) expr;
			return "i32 " + (e.value ? 1 : 0);
		} else if (expr instanceof AST.string_const) { // String constant
			AST.string_const e = (AST.string_const) expr;
			String ty = "["+(e.value.length()+1)+" x i8]";
			globalStrings += "@.str"+(strCount++)+" = private unnamed_addr constant "+ty+" c\""+e.value+"\\00\", align 1\n";
			out.println("\t%"+(++varCount)+" = bitcast "+ty+"* @.str"+(strCount-1)+" to [1024 x i8]*");
			return "[1024 x i8]* %"+varCount;
		} else if (expr instanceof AST.int_const) { // Int constant
			AST.int_const e = (AST.int_const) expr;
			return "i32 " + e.value;
		} else if (expr instanceof AST.object) { // Object name, can be attribute or formal
			AST.object e = (AST.object) expr;
			int attri = ci.attrList.indexOf(e.name);
			for (AST.formal f : method.formals) {
				if (f.name.equals(e.name)) {
					attri = -1;
					break;
				}
			}
			if (attri == -1) {
				if (changedFormals.indexOf(e.name) == -1)
					return parseType(e.type) + " %"+e.name;
				else {
					String ty = parseType(e.type);
					out.println("\t%"+(++varCount)+" = load "+ty+", "+ty+"* %"+e.name+".addr, align 4");
					return ty+" %"+varCount;
				}
			}
			String parseTypeName = parseType(cname);
			parseTypeName = parseTypeName.substring(0, parseTypeName.length()-1);
			out.println("\t%"+(++varCount)+" = getelementptr inbounds "+parseTypeName+", "+parseTypeName+"* %self, i32 0, i32 "+attri);
			out.println("\t%"+(++varCount)+" = load "+parseType(e.type)+", "+parseType(e.type)+"* %"+(varCount-1)+", align 4");
			return parseType(e.type)+" %"+varCount;
		} else if (expr instanceof AST.comp) { // Complement operator
			// For each of the operators first we peint LLVM-IR for its operands
			AST.comp e = (AST.comp) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = sub nsw i32 1, " + e1.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.eq) { // Equal operator
			AST.eq e = (AST.eq) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			String e2 = printExpr(cname, method, e.e2, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = icmp eq i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.leq) { // Less or equal operator
			AST.leq e = (AST.leq) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			String e2 = printExpr(cname, method, e.e2, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = icmp sle i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.lt) { // Less than
			AST.lt e = (AST.lt) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			String e2 = printExpr(cname, method, e.e2, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = icmp slt i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.neg) { // Negation operator
			AST.neg e = (AST.neg) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = sub nsw i32 0, " + e1.substring(4));
		} else if (expr instanceof AST.divide) { // Divide operator
			AST.divide e = (AST.divide) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			String e2 = printExpr(cname, method, e.e2, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = icmp eq i32 0, "+e2.substring(4));
			ifCount++;
			// Also check for devide by zero, runtime exit
			out.println("\tbr i1 %"+varCount+", label %if.then"+ifCount+", label %if.else"+ifCount);
			out.println();
			out.println("if.then"+ifCount+":");
			blocks.add("if.then"+ifCount);
			out.println("\t%"+(++varCount)+" = bitcast [22 x i8]* @Abortdivby0 to [1024 x i8]*");
			out.println("\t%"+(++varCount)+" = call %class.IO* @_ZN2IO10out_string( %class.IO* null, [1024 x i8]* %"+(varCount-1)+")");
			out.println("\tcall void @exit(i32 1)");
			out.println("\tbr label %if.else"+ifCount);
			out.println();
			out.println("if.else"+ifCount+":");
			blocks.add("if.else"+ifCount);
			out.println("\t%"+(++varCount)+" = sdiv i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.mul) { // Multiply operator
			AST.mul e = (AST.mul) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			String e2 = printExpr(cname, method, e.e2, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = mul nsw i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.sub) { // Subtract operator
			AST.sub e = (AST.sub) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			String e2 = printExpr(cname, method, e.e2, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = sub nsw i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.plus) { // Add operator
			AST.plus e = (AST.plus) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			String e2 = printExpr(cname, method, e.e2, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = add nsw i32 " + e1.substring(4) + ", " + e2.substring(4));
			return "i32 %"+varCount;
		} else if (expr instanceof AST.isvoid) { // isvoid operator
			AST.isvoid e = (AST.isvoid) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			out.println("\t%"+(++varCount)+" = icmp eq "+e1+", null");
			return "i32 %"+varCount;
		} else if (expr instanceof AST.new_) { // New expression, calls constructor
			AST.new_ e = (AST.new_) expr;
			String type = parseType(e.typeid);
			// out.println("\t%"+(++varCount)+" = alloca "+type+", align 4");
			int size = classTable.classinfos.get(e.typeid).size;
			out.println("\t%"+(++varCount)+" = call noalias i8* @malloc(i64 "+size+")");
			out.println("\t%"+(++varCount)+" = bitcast i8* %"+(varCount-1)+" to "+type);
			out.println("\t%"+(++varCount)+" = call i32 @_ZN"+e.typeid.length()+e.typeid+8+"__cons__( "+type+" %"+(varCount-1)+" )");
			// out.println("\tstore "+type+" %"+varCount+", "+type+"* %"+(varCount-2)+", align 4");
			return type+" %"+(varCount-1);
		} else if (expr instanceof AST.assign) { // Assignment
			AST.assign e = (AST.assign) expr;
			String e1 = printExpr(cname, method, e.e1, changedFormals, blocks, out);
			int attri = ci.attrList.indexOf(e.name);
			// figure out assign to formal or attr
			if (method != null) {
				for (AST.formal f : method.formals) {
					if (f.name.equals(e.name)) {
						attri = -1;
						break;
					}
				}
			}
			String type = parseType(e.type);
			String stype = parseType(cname);
			stype = stype.substring(0, stype.length()-1);
			String e1type = reverseParseTypeValue(e1);
			// Bitcast of types not same
	        if (!e1type.equals(type)) {
	        	if (e1type.equals("i32")) {
	        		out.println("\t%"+(++varCount)+" = call noalias i8* @malloc(i64 8)"); // Object size
	        		out.println("\t%"+(++varCount)+" = bitcast i8* %"+(varCount-1)+" to "+type);
	        	} else {
	        		out.println("\t%"+(++varCount)+" = bitcast "+e1+" to "+type);	        		
	        	}
	        	e1 = type+" %"+varCount;
	        }
	        // If formal was reassigned
			if (attri == -1) {
				if (changedFormals.indexOf(e.name) == -1) {
					out.println("%"+e.name+".addr = alloca "+type+", align 4");
					changedFormals.add(e.name);
				}
				out.println("\tstore "+e1+", "+type+"* %"+e.name+".addr, align 4");
				return e1;
			} else {
				out.println("\t%"+(++varCount)+" = getelementptr inbounds "+stype+", "+stype+"* %self, i32 0, i32 "+attri);
				out.println("\tstore "+e1+", "+type+"* %"+varCount+", align 4");
				return e1;
			}
		} else if (expr instanceof AST.block) { // Block statement
			AST.block e = (AST.block) expr;
			String re = "";
			for (AST.expression ex : e.l1) {
				re = printExpr(cname, method, ex, changedFormals, blocks, out);
			}
			return re;
		} else if (expr instanceof AST.loop) { // Loops
			// Add blocks required for cond and loop block
			AST.loop e = (AST.loop) expr;
			int loopcnt = ++loopCount;
			out.println("\tbr label %loop.cond"+loopcnt);
			out.println();
			out.println("loop.cond"+loopcnt+":");
			blocks.add("loop.cond"+loopcnt);
			String pred = printExpr(cname, method, e.predicate, changedFormals, blocks, out);
			out.println("\tbr i1 "+pred.substring(4)+", label %loop.body"+loopcnt+" , label %loop.end"+loopcnt);
			out.println();
			out.println("loop.body"+loopcnt+":");
			blocks.add("loop.body"+loopcnt);
			String body = printExpr(cname, method, e.body, changedFormals, blocks, out);
			out.println("\tbr label %loop.cond"+loopcnt);
			out.println();
			out.println("loop.end"+loopcnt+":");
			blocks.add("loop.end"+loopcnt);
			return body;
		} else if (expr instanceof AST.cond) { // If condition
			// Add required blocks for cond, if.then, if.else
			AST.cond e = (AST.cond) expr;
			int ifcnt = ++ifCount;
			String pred = printExpr(cname, method, e.predicate, changedFormals, blocks, out);
			out.println("\tbr i1 "+pred.substring(4)+", label %if.then"+ifcnt+", label %if.else"+ifcnt);
			out.println();
			out.println("if.then"+ifcnt+":");
			blocks.add("if.then"+ifcnt);
			String ifbody = printExpr(cname, method, e.ifbody, changedFormals, blocks, out);
			String ifbodylabel = blocks.get(blocks.size()-1);
			ifbody = reverseParseTypeValueVar(ifbody);
			out.println("\tbr label %if.end"+ifcnt);
			out.println();
			out.println("if.else"+ifcnt+":");
			blocks.add("if.else"+ifcnt);
			String elsebody = printExpr(cname, method, e.elsebody, changedFormals, blocks, out);
			String elsebodylabel = blocks.get(blocks.size()-1);
			elsebody = reverseParseTypeValueVar(elsebody);
			out.println("\tbr label %if.end"+ifcnt);
			out.println();
			out.println("if.end"+ifcnt+":");
			blocks.add("if.end"+ifcnt);
			out.println("\t%"+(++varCount)+" = phi "+parseType(e.type)
				+" ["+ifbody+", %"+ifbodylabel+"], ["+elsebody+", %"+elsebodylabel+"]");
			return parseType(e.type)+" %"+varCount;
		} else if (expr instanceof AST.static_dispatch) { // Static dispatch
			AST.static_dispatch e = (AST.static_dispatch) expr;
			String caller = printExpr(cname, method, e.caller, changedFormals, blocks, out);
			// Print LLVM-IR for parameters
			List<String> actuals = new ArrayList<>();
			for (AST.expression actual : e.actuals) {
				String a = printExpr(cname, method, actual, changedFormals, blocks, out);
				actuals.add(a);
			}
			// Check if dispatch to void
			ifCount++;
			out.println("\t%"+(++varCount)+" = icmp eq "+caller+", null");
			out.println("\tbr i1 %"+varCount+", label %if.then"+ifCount+", label %if.else"+ifCount);
			out.println();
			out.println("if.then"+ifCount+":");
			blocks.add("if.then"+ifCount);
			out.println("\t%"+(++varCount)+" = bitcast [25 x i8]* @Abortdispvoid to [1024 x i8]*");
			out.println("\t%"+(++varCount)+" = call %class.IO* @_ZN2IO10out_string( %class.IO* null, [1024 x i8]* %"+(varCount-1)+")");
			out.println("\tcall void @exit(i32 1)");
			out.println("\tbr label %if.else"+ifCount);
			out.println();
			out.println("if.else"+ifCount+":");
			blocks.add("if.else"+ifCount);
			// Call the required function
			String funcname = "@_ZN"+e.typeid.length()+e.typeid+e.name.length()+e.name;
			ClassInfo ci2 = classTable.classinfos.get(reverseParseType(reverseParseTypeValue(caller)));
			while (!reverseParseTypeValue(caller).equals(parseType(e.typeid))) {
				String par = parseType(ci2.parent);
				par = par.substring(0, par.length()-1);
				String ty = reverseParseTypeValue(caller);
				ty = ty.substring(0, ty.length()-1);
				out.println("\t%"+(++varCount)+" = getelementptr inbounds "+ty+", "+ty+"* "+reverseParseTypeValueVar(caller)+", i32 0, i32 0");
				caller = par+"* %"+varCount;
				ci2 = classTable.classinfos.get(ci2.parent);
			}
			String actualsStr = caller;
			for (int i=0; i<actuals.size(); i++)
				actualsStr += ", " + actuals.get(i);
			out.println("\t%"+(++varCount)+" = call "+parseType(e.type)+" "+funcname+"("+actualsStr+")");
			return parseType(e.type)+" %"+varCount;
		} else { // This should never happen
			System.out.println("I will never come here");
		}
		return "";
	}

	// Print header information, declare the functions
	void printHeaders(PrintWriter out) {
		out.println("target datalayout = \"e-m:e-i64:64-f80:128-n8:16:32:64-S128\"");
		out.println("target triple = \"x86_64-unknown-linux-gnu\"");
		out.println("@Abortdivby0 = private unnamed_addr constant [22 x i8] c\"Error: Division by 0\\0A\\00\", align 1\n"
			+ "@Abortdispvoid = private unnamed_addr constant [25 x i8] c\"Error: Dispatch to void\\0A\\00\", align 1\n");
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
			+ "@instrformatstr = private unnamed_addr constant [7 x i8] c\"%[^\\0A]s\\00\", align 1\n"
			+ "@intformatstr = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\n");
	}

	// Print methods related to object
	void printObjectMethods(PrintWriter out) {
		out.println("define i32 @_ZN6Object8__cons__( %class.Object* %self ) noreturn {\n"
			+ "entry:\n"
			+"\tret i32 0\n"
			+"}\n");

		out.println("define %class.Object* @_ZN6Object5abort( %class.Object* %self ) noreturn {\n"
			+ "entry:\n"
			+ "\tcall void @exit( i32 1 )\n"
			+ "\tret %class.Object* null\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6Object9type_name( %class.Object* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = getelementptr inbounds %class.Object, %class.Object* %self, i32 0, i32 1\n"
			+ "\t%1 = load [1024 x i8]*, [1024 x i8]** %0\n"
			+ "\t%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %1 )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	}

	// Print methods related to string
	void printStringMethods(PrintWriter out) {
		out.println("define i32 @_ZN6String6length( [1024 x i8]* %self ) {\n"
			+ "\tentry:\n"
			+ "\t%0 = bitcast [1024 x i8]* %self to i8*\n"
			+ "\t%1 = call i64 @strlen( i8* %0 )\n"
			+ "\t%retval = trunc i64 %1 to i32\n"
			+ "\tret i32 %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String6concat( [1024 x i8]* %self, [1024 x i8]* %that ) {\n"
			+ "entry:\n"
			+ "\t%retval = call [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %self )\n"
			+ "\t%0 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%1 = bitcast [1024 x i8]* %that to i8*\n"
			+ "\t%2 = call i8* @strcat( i8* %0, i8* %1 )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String4copy( [1024 x i8]* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %0 to [1024 x i8]*\n"
			+ "\t%1 = bitcast [1024 x i8]* %self to i8*\n"
			+ "\t%2 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%3 = call i8* @strcpy( i8* %2, i8* %1)\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");

		out.println("define [1024 x i8]* @_ZN6String6substr( [1024 x i8]* %self, i32 %start, i32 %len ) {\n"
			+ "entry:\n"
			+ "\t%0 = getelementptr inbounds [1024 x i8], [1024 x i8]* %self, i32 0, i32 %start\n"
			+ "\t%1 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %1 to [1024 x i8]*\n"
			+ "\t%2 = bitcast [1024 x i8]* %retval to i8*\n"
			+ "\t%3 = call i8* @strncpy( i8* %2, i8* %0, i32 %len )\n"
			+ "\t%4 = getelementptr inbounds [1024 x i8], [1024 x i8]* %retval, i32 0, i32 %len\n"
			+ "\tstore i8 0, i8* %4\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	}

	// Print IO methods
	void printIOMethods(PrintWriter out) {
		out.println("define i32 @_ZN2IO8__cons__( %class.IO* %self ) noreturn {\n"
			+ "entry:\n"
			+"\tret i32 0\n"
			+"}\n");

		out.println("define %class.IO* @_ZN2IO10out_string( %class.IO* %self, [1024 x i8]* %str ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %str )\n"
			+ "\tret %class.IO* %self\n"
			+ "}\n");
	
		out.println("define %class.IO* @_ZN2IO7out_int( %class.IO* %self, i32 %int ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i32 (i8*, ...) @printf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32 %int )\n"
			+ "\tret %class.IO* %self\n"
			+ "}\n");
	
		out.println("define [1024 x i8]* @_ZN2IO9in_string( %class.IO* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 1024 )\n"
			+ "\t%retval = bitcast i8* %0 to [1024 x i8]*\n"
			+ "\t%1 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @strformatstr to i8* ), [1024 x i8]* %retval )\n"
			+ "\tret [1024 x i8]* %retval\n"
			+ "}\n");
	
		out.println("define i32 @_ZN2IO6in_int( %class.IO* %self ) {\n"
			+ "entry:\n"
			+ "\t%0 = call i8* @malloc( i64 4 )\n"
			+ "\t%1 = bitcast i8* %0 to i32*\n"
			+ "\t%2 = call i32 (i8*, ...) @scanf( i8* bitcast ( [3 x i8]* @intformatstr to i8* ), i32* %1 )\n"
			+ "\t%retval = load i32, i32* %1\n"
			+ "\tret i32 %retval\n"
			+ "}\n");
	}
}
