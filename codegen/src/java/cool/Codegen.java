package cool;

import java.io.PrintWriter;
import java.util.*;

public class Codegen{
    
    ClassTable classTable = new ClassTable();
    String filename;
    
	public Codegen(AST.program program, PrintWriter out){
		//Write Code generator code here
        out.println("; I am a comment in LLVM-IR. Feel free to remove me.");
        
        print_classes(program.classes, out);
        
        
	}
	
	private void print_classes(List <AST.class_> classes, PrintWriter out){
	    
	    // A hash Map that maps form class names to AST.class_
		HashMap <String, AST.class_> astClasses = new HashMap <String, AST.class_> ();
		// Store Graph as adjacency list
		HashMap < String, ArrayList <String> > graph = new HashMap < String, ArrayList <String> >();
		// Add Object and IO to the graph
		//graph.put("Object", new ArrayList<String>());
		//graph.put("IO", new ArrayList<String>());

        // Populate astClasses maps and strat creating Adjcacency list
		for (AST.class_ c : classes){			
			graph.put(c.name, new ArrayList <String> ());
			astClasses.put(c.name, c);
		}

		// Add edge from Object to IO in the graph
		// IO is inherited from Object
		// String, Int, Bool are not added as no function inherits from them
		// graph.get("Object").add("IO");
		// Check if parents of the classes exits and add edges from parent to child
		for (AST.class_ c : classes){
			System.out.println(c.name);
			if (c.parent != null) graph.get(c.parent).add(c.name);
		}

		// We perform a BFS on the graph and print the LLVM-IR for each class
		Queue <String> q = new LinkedList<String>(); // Queue reuired for BFS

		q.offer("Object"); // Add root Class
		while(q.isEmpty() == false){
			String c = q.poll();
			// classTable.insert(astClasses.get(c));
			if (!c.equals("Object")) classTable.insert(astClasses.get(c));
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
	    ClassInfo ci = classTable.classinfos.get(c);
	    String attrsStr = "";
	    for (String a : ci.attrList) {
	    	if (a.equals("_Par")) {
	    		attrsStr += "%class."+ci.parent+", ";
	    		continue;
	    	}
	    	AST.attr attr = ci.attrMap.get(a);
	    	if (attr.typeid.equals("Int") || attr.typeid.equals("Bool"))
	    		attrsStr += "i38, ";
	    	else {
	    		attrsStr += "%class."+attr.typeid+", ";
	    	}
	    }
	    if (attrsStr.length() >= 2) attrsStr = attrsStr.substring(0, attrsStr.length()-2);
	    out.println("%class."+c+" = type { " + attrsStr + " }");
	}

	// Print the constructor of te classes and print th inbuilt functions for Object, String, IO, Int, Bool
	void printClassMethods(String c, PrintWriter out){
	    ClassInfo ci = classTable.classinfos.get(c);
	}
}
