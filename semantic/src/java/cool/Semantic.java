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

	public void reportErrors(List<Error> errors) {
		for(Error e : errors) {
			if (e.filename == null) e.filename = filename;
			reportError(e.filename, e.lineNo, e.err);
		}
	}

	public Semantic(AST.program program){

		// Check if any cycle is present in inheritence graph of the program
		check_cycles(program.classes);
		// Report the errors found while checking for cycles
		reportErrors(classTable.errors);

		for (AST.class_ c : program.classes) {
			filename = c.filename;
			scopeTable.enterScope();
			scopeTable.insert("self", new AST.attr("self", c.name, new AST.no_expr(c.lineNo), c.lineNo));
			for (Entry<String, AST.attr> entry : classTable.classinfos.get(c.name).attrlist.entrySet())
				scopeTable.insert(entry.getKey(), entry.getValue());
		   	List<Error> errors = new ArrayList<>();
			c.handle(errors, scopeTable, classTable);
			reportErrors(errors);
			scopeTable.exitScope();
		}

		ClassInfo main_class = classTable.classinfos.get("Main");
		if(main_class == null)
			reportError(filename, 1, "Program does not contain class Main");
		else if(main_class.methodlist.containsKey("main") == false)
			reportError(filename, 1, "Main class does not contain main method");
	}

	private void check_cycles(List <AST.class_> classes){
		HashMap <String, AST.class_> astClasses = new HashMap <String, AST.class_> ();
		// Store Graph as adjacency list
		HashMap < String, ArrayList <String> > graph = new HashMap < String, ArrayList <String> >();
		// Add Object and IO to the graph
		graph.put("Object", new ArrayList<String>());
		graph.put("IO", new ArrayList<String>());

		// List of seen classNames
		ArrayList <String> classNames = new ArrayList<>();
		classNames.add("Object");
		classNames.add("IO");

		// These classes cannot be redefined
		List <String> redef = Arrays.asList("Object", "String", "Int", "Bool", "IO");
		// These classes cannot be inherited from
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
