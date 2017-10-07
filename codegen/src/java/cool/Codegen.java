package cool;

import java.io.PrintWriter;

public class Codegen{
    
    ClassTable classTable = new ClassTable();
    String filename;
    
	public Codegen(AST.program program, PrintWriter out){
		//Write Code generator code here
        out.println("; I am a comment in LLVM-IR. Feel free to remove me.");
        
        print_classes(program.classes, out);
        
        
	}
	
	private void print_classes(List <AST.class_> classes, PrintWriter out){
		HashMap <String, AST.class_> astClasses = new HashMap <String, AST.class_> ();
		// Store Graph as adjacency list
		HashMap < String, ArrayList <String> > graph = new HashMap < String, ArrayList <String> >();
		// Add Object and IO to the graph
		graph.put("Object", new ArrayList<String>());
		graph.put("IO", new ArrayList<String>());

		// List of seen classNames
		//ArrayList <String> classNames = new ArrayList<>();
		//classNames.add("Object");
		//classNames.add("IO");

		// These classes cannot be redefined
		//List <String> redef = Arrays.asList("Object", "String", "Int", "Bool", "IO");
		// These classes cannot be inherited from
		//List <String> inherit = Arrays.asList("String", "Int", "Bool");

		for (AST.class_ c : classes){			
			graph.put(c.name, new ArrayList <String> ());
			astClasses.put(c.name, c);
		}

		// Add edge from Object to IO in the graph
		// IO is inherited from Object
		// String, Int, Bool are not added as no function inherits from them
		graph.get("Object").add("IO");
		// Check if parents of the classes exits and add edges from parent to child
		for (AST.class_ c : classes){
			graph.get(c.parent).add(c.name);
		}

		// We perform a BFS on the graph and check for inheritence cycle in the graph
		//ArrayList <String> visitedClasses = new ArrayList<>(); // Class that have been visited while doing BFS
		Queue <String> q = new LinkedList<String>(); // Queue reuired for BFS

		q.offer("Object"); // Add root Class
		while(q.isEmpty() == false){
			String c = q.poll();
			classTable.insert(astClasses.get(c));
			printClass(c);
			for (String child : graph.get(c)){
				q.offer(child);
			}
		}

		q.clear();
		
		q.offer("Object"); // Add root Class
		while(q.isEmpty() == false){
			String c = q.poll();
			printClassMethods(c);
			for (String child : graph.get(c)){
				q.offer(child);
			}
		}

		q.clear();
	}
}
void printClass(String c){
    ClassInfo ci = classTable.classinfos.get(c);
}
void printClassMethods(String c){
    ClassInfo ci = classTable.classinfos.get(c);
}
