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

		// For all classes, add self to scope, type annotate method and attributes
		for (AST.class_ c : program.classes) {
			filename = c.filename;
			scopeTable.enterScope();
			scopeTable.insert("self", new AST.attr("self", c.name, new AST.no_expr(c.lineNo), c.lineNo));
			for (Entry<String, AST.attr> entry : classTable.classinfos.get(c.name).attrlist.entrySet())
				scopeTable.insert(entry.getKey(), entry.getValue());
		   	List<Error> errors = new ArrayList<>();
			// Type annotate the features of the class
			c.handle(errors, scopeTable, classTable);
			// Report the errors found
			reportErrors(errors);
			scopeTable.exitScope();
		}

		// Check if class Main is defined
		ClassInfo main_class = classTable.classinfos.get("Main");
		if(main_class == null)
			reportError(filename, 1, "Program does not contain class Main");
		// Check if class Main has method main
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
			if (classNames.contains(c.name)) { // Case when class is redefined
				reportError(c.filename, c.lineNo, "Class "+c.name+" has been redefined.");
				System.exit(1);
			}
			else if (redef.contains(c.name)) { // Case when basic class is redefined
				reportError(c.filename, c.lineNo, "Class "+c.name+" can not be redefined.");
				System.exit(1);
			}
			else if (inherit.contains(c.parent)) { // Case when class is inherited from String, Int or Bool
				reportError(c.filename, c.lineNo, "Class "+c.parent+" can not be inherited.");
				System.exit(1);
			}
			else {
				classNames.add(c.name);
				graph.put(c.name, new ArrayList <String> ());
				astClasses.put(c.name, c);
			}
		}

		// Add edge from Object to IO in the graph
		// IO is inherited from Object
		// String, Int, Bool are not added as no function inherits from them
		graph.get("Object").add("IO");
		// Check if parents of the classes exits and add edges from parent to child
		for (AST.class_ c : classes){
			if (classNames.contains(c.parent) == false){
				reportError(c.filename, c.lineNo, "Class "+c.parent+" has not been defined.");
				System.exit(1);
			}
			graph.get(c.parent).add(c.name);
		}

		// We perform a BFS on the graph and check for inheritence cycle in the graph
		ArrayList <String> visitedClasses = new ArrayList<>(); // Class that have been visited while doing BFS
		Queue <String> q = new LinkedList<String>(); // Queue reuired for BFS

		boolean cycle = false;
		// For BFS we start from every node so as to check independent cycles that might have formed
		for (String s : classNames) {
			if (visitedClasses.contains(s) == false) {
				q.offer(s); // Add root Class
				while(q.isEmpty() == false){
					String c = q.poll();
					for (String child : graph.get(c)){
						// If atleast one child is already visited then there is a cycle
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
		// Add classes from the tree (since no cycles are found) to the classTable
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
