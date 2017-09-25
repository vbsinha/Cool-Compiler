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
			reportError(e.filename, e.lineNo, e.err);
		}

		for (AST.class_ c : program.classes) {
			filename = c.filename;
			scopeTable.enterScope();
			scopeTable.insert("self", new AST.attr("self", c.name, new AST.no_expr(c.lineNo), c.lineNo));
			for (Entry<String, AST.attr> entry : classTable.classinfos.get(c.name).attrlist.entrySet())
				scopeTable.insert(entry.getKey(), entry.getValue());
			//scopeTable.insertAll(classTable.getAttrs(e.name));
			// c.handle
		   	List<Error> errors = new ArrayList<>();
			c.handle(errors, scopeTable, classTable);
			for (Error e : errors) {
				reportError(filename, e.lineNo, e.err);
			}
			scopeTable.exitScope();
		}

		ClassInfo main_class = classTable.classinfos.get("Main");
		if(main_class == null)
			reportError(filename, 1, "Program does not contain class Main");
		else if(main_class.methodlist.containsKey("main") == false)
			reportError(filename, 1, "Main class does not contain main method");
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
