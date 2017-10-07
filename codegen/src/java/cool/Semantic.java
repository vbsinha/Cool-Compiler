package cool;
import cool.AST.*;
import java.util.*;

/**
 * Semantic analyzer for COOL
 */
public class Semantic {

	private boolean errorFlag = false;

	/**
	 * Method to report the error messages with the line number
	 * and filename
	 * @param filename Name of file
	 * @param lineNo   Line number of AST Node
	 * @param error    Error Message
	 */
	public void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}

	/**
	 * Method to report general errors
	 * @param error Error message
	 */
	public void reportError(String error){
		errorFlag = true;
		System.err.println(error);
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

/*
	Don't change code above this line
*/	
	
	// HashMap to easily access the class_ nodes
	private HashMap<String, class_> classMap;

	// File name stored as this COOL compiler accepts only one file
	public String fileName;

	// Attribute and Method scopes in classes stored separately
	HashMap<String, ClassScope> classScopes;

	// Local Scope Table
	ScopeTable<AST.object> scopeTable;

	/**
	 * Assign type to bool_const literal
	 * @param node bool_const node
	 */
	void typeCheck(AST.bool_const node) {
		node.type = "Bool";
	}
	
	/**
	 * Assign type to string_const literal
	 * @param node string_const node
	 */
	void typeCheck(AST.string_const node) {
		node.type = "String";
	}
	
	/**
	 * Assign type to int_const literal
	 * @param node int_const node
	 */
	void typeCheck(AST.int_const node) {
		node.type = "Int";
	}
	
	/**
	 * Check and assign type of complement 'not'
	 * @param node comp node
	 */
	void typeCheck(AST.comp node){
		if(node.e1.type.equals("Bool")) {
			node.type="Bool";
		}
		else {
			reportError(fileName, node.lineNo, "Expression should be of type bool instead of " + node.e1.type);
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of equals '='
	 * @param node eq node
	 */
	void typeCheck(AST.eq node){

		if (node.e1.type.equals("Int") || node.e1.type.equals("String") || node.e1.type.equals("Bool")) {
			if (!(node.e1.type.equals(node.e2.type))) {
				reportError(fileName, node.lineNo, "Incompatible types " + node.e1.type + " and " + node.e2.type);
				node.type = "Object";
			}
			else {
				node.type="Bool";
			}
		}
		else {
			node.type="Bool";
		}

	}
		
		/**
	 * Check and assign type of leq '<='
	 * @param node leq node
	 */
	void typeCheck(AST.leq node){
		if (node.e1.type.equals(node.e2.type) && node.e1.type.equals("Int")) {
			node.type="Bool";
		}
		else {
			reportError(fileName, node.lineNo, "Both operands should be int type. " + node.e1.type + " and " + node.e2.type);
			node.type = "Object";
		}
	}
		
		/**
	 * Check and assign type of lt '<'
	 * @param node lt node
	 */
	void typeCheck(AST.lt node){
		if (node.e1.type.equals(node.e2.type) && node.e1.type.equals("Int")) {
			node.type="Bool";
		}
		else {
			reportError(fileName, node.lineNo, "Both operands should be int type. " + node.e1.type + " and " + node.e2.type);
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of neg '~'
	 * @param node neg node
	 */
	void typeCheck(AST.neg node){
		if (node.e1.type.equals("Int")) {
			node.type="Int";
		}
		else {
			reportError(fileName, node.lineNo, "Operand should be int type instead of " + node.e1.type);
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of divide '/'
	 * @param node divide node
	 */
	void typeCheck(AST.divide node){
		if (node.e1.type.equals("Int") && node.e2.type.equals("Int")) {
			node.type="Int";
		}
		else {
			reportError(fileName, node.lineNo, "Operands should be int type." + node.e1.type + " " + node.e2.type);
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of multiply '*'
	 * @param node mul node
	 */
	void typeCheck(AST.mul node){
		if (node.e1.type.equals("Int") && node.e2.type.equals("Int")) {
			node.type="Int";
		}
		else {
			reportError(fileName, node.lineNo, "Operands should be int type." + node.e1.type + " " + node.e2.type);
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of subtract '-'
	 * @param node sub node
	 */
	void typeCheck(AST.sub node){
		if (node.e1.type.equals("Int") && node.e2.type.equals("Int")) {
			node.type="Int";
		}
		else {
			reportError(fileName, node.lineNo, "Operands should be int type." + node.e1.type + " " + node.e2.type);
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of add '+'
	 * @param node plus node
	 */
	void typeCheck(AST.plus node){
		if (node.e1.type.equals("Int") && node.e2.type.equals("Int")) {
			node.type="Int";
		}
		else {
			reportError(fileName, node.lineNo, "Operands should be int type." + node.e1.type + " " + node.e2.type);
			node.type = "Object";
		}
	}
	
	/**
	 * Assign type of block expression '{}'
	 * @param node block node
	 */
	void typeCheck(AST.block node){

		node.type = node.l1.get(node.l1.size() - 1).type;
	
	}
	
	/**
	 * Check and assign type of loop expression 'while-loop-pool'
	 * @param node loop node
	 */
	void typeCheck(AST.loop node){
		if (node.predicate.type.equals("Bool")) {
			node.type = "Object";
		}
		else {
			reportError(fileName, node.lineNo, "Condition of loop should be bool");
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of conditional expression 'if-then-else-fi'
	 * @param node cond node
	 */
	void typeCheck(AST.cond node){
		if(node.predicate.type.equals("Bool")) {
			node.type = join(node.ifbody.type, node.elsebody.type);
		}
		else {
			reportError(fileName, node.lineNo, "Condition of if should be bool");
			node.type = "Object";
		}
	}
	
	/**
	 * Check and assign type of object ID expression
	 * @param node object node
	 */
	void typeCheck(AST.object node) {
		AST.object obj = scopeTable.lookUpGlobal(node.name);
		if (obj == null) {
			reportError(fileName, node.lineNo, "identifier '" + node.name + "' came as a complete surprise to me! Did you forget to declare it?");
			node.type = "Object";
		}
		else {
			node.type = obj.type;
		}
	}
	
	/**
	 * Check and assign type of isvoid expression 'isvoid()'
	 * @param node isvoid node
	 */
	void typeCheck(AST.isvoid node){
		node.type="Bool";
	}
	
	/**
	 * Check and assign type of new expression 'new'
	 * @param node new_ node
	 */
	void typeCheck(AST.new_ node){
		// not handling SELF_TYPE
		if (classMap.get(node.typeid) == null) {
			reportError(fileName, node.lineNo, "Cannot find symbol " + node.typeid);
			node.type = "Object";
		}
		else {
			node.type = node.typeid;
		}
		
	}
	
	/**
	 * Check and assign type of assignment expression '<-'
	 * @param node assign node
	 */
	void typeCheck(AST.assign node){
		AST.object tmp = scopeTable.lookUpGlobal(node.name);
		if (tmp != null) {

			if (conformance(node.e1.type, tmp.type))
				node.type = tmp.type;
			else {
				reportError(fileName, node.lineNo, "Expression type '" + node.e1.type + "' does not conform to type '" + tmp.type + "' of identifier");
				node.type = "Object";
			}

		}
		else {
			reportError(fileName, node.lineNo, "identifier '" + node.name + "' came as a complete surprise to me! Did you forget to declare it?");
		}

	}
	
	
	/**
	 * Check and assign type of 'let' expression
	 * @param node let node
	 */
	void typeCheck(AST.let node){
		
		if (!(node.value instanceof no_expr)) {
			if (!(conformance(node.value.type, node.typeid))) {
				reportError(fileName, node.lineNo, "Invalid type '" + node.value.type + "' assigned to '" + node.name + "'");
				node.type = "Object";
			}
			else {
				node.type = node.body.type;
			}
		}
		else {
			node.type = node.body.type;
		}
	}

	
	/**
	 * Check and assign type of dispatch expression (Eg: 'obj.method()')
	 * @param node dispatch node
	 */
	void typeCheck(AST.dispatch node){
		ClassScope callingObject;
		if (node.caller.type.equals("self")) {
			callingObject = classScopes.get(scopeTable.lookUpGlobal("self").type);
			
		}
		else {
			callingObject = classScopes.get(node.caller.type);
		}

		if (callingObject != null) {

			AST.method currentMethod = callingObject.methodTables.get(node.name);
			while (currentMethod == null) {
				callingObject = classScopes.get(classMap.get(callingObject.name).parent);
				currentMethod = callingObject.methodTables.get(node.name);
				if (callingObject.name.equals("Object") && currentMethod == null) {
					break;
				}
			}

			if (currentMethod != null) {
				if (node.actuals.size() != currentMethod.formals.size()) {
					reportError(fileName, node.lineNo, "The number of arguments do not match");
					node.type = "Object";
				}
				else {

					node.type = currentMethod.typeid;

					for (int i = 0; i < node.actuals.size(); i++) {
						AST.expression actualArgument = node.actuals.get(i);
						AST.formal formalArgument = currentMethod.formals.get(i);
						if (!conformance(actualArgument.type, formalArgument.typeid)) {
							reportError(fileName, node.lineNo, "The actual type '" + actualArgument.type + "' does not conform to formal type '" + formalArgument.typeid + "'");
							node.type = "Object";
						}
						else {
							node.type = currentMethod.typeid;
						}
					}
				}

			}
			else {
				reportError(fileName, node.lineNo, "Method '" + node.name + "' came as a complete surprise to me! Did you forget to define it?");
				node.type = "Object";
			}

		}
		else {
			reportError(fileName, node.lineNo, "Object '" +  node.caller.type + "' came as a complete surprise to me! Did you forget to declare it?");
			node.type = "Object";
		}

	}
		
		/**
	 * Check and assign type of static_dispatch expression (Eg: 'obj@T.method()')
	 * @param node static_dispatch node
	 */
	void typeCheck(AST.static_dispatch node){

		ClassScope callingObject = classScopes.get(node.caller.type);

		if (classMap.get(node.typeid) == null) {
			reportError(fileName, node.lineNo, "Type '" + node.typeid + "' came as a complete surprise to me! Did you forget to define it?");
			node.type = "Object";
		}
		else if (!conformance(node.caller.type, node.typeid)) {
				reportError(fileName, node.lineNo, "The type '" + node.caller.type + "' of caller does not conform to the type '" + node.typeid + "'");
			node.type = "Object";
		}
		else if (callingObject != null) {
			AST.method currentMethod = callingObject.methodTables.get(node.name);
			if (currentMethod != null) {

				if (node.actuals.size() != currentMethod.formals.size()) {
					reportError(fileName, node.lineNo, "The number of arguments do not match");
					node.type = "Object";
				}
				else {
					for (int i = 0; i < node.actuals.size(); i++) {
						AST.expression actualArgument = node.actuals.get(i);
						AST.formal formalArgument = currentMethod.formals.get(i);
						if (!conformance(actualArgument.type, formalArgument.typeid)) {
							reportError(fileName, node.lineNo, "The actual type '" + actualArgument.type + "' does not conform to formal type '" + formalArgument.typeid + "'");
							node.type = "Object";
						}
						else {
							node.type = currentMethod.typeid;
						}
					}
				}

			}
			else {
				reportError(fileName, node.lineNo, "Method '" + node.name + "' came as a complete surprise to me! Did you forget to define it?");
				node.type = "Object";
			}

		}
		else {
			reportError(fileName, node.lineNo, "Object '" + node.caller.type + "' came as a complete surprise to me! Did you forget to define it?");
			node.type = "Object";
		}

	}
		
	/**
	 * Check and assign type of case expression 'case-branches-esac'
	 * @param node case node
	 */
	void typeCheck(AST.typcase node){
		String resultType;
		if (node.branches.size() == 1) {
			resultType = node.branches.get(0).type;
		}
		else if (node.branches.size() >= 2) {
			resultType = join(node.branches.get(0).type, node.branches.get(1).type);
			for (int i = 2; i < node.branches.size(); i++) {
				AST.branch b = node.branches.get(i);
				resultType = join(resultType, b.type);
			}
		}
	}
	
	/**
	 * Check whether type of formal parameters of methods are valid
	 * @param node formal node
	 */
	void typeCheck(AST.formal node) {
		if (classMap.get(node.typeid) == null) {
			reportError(fileName, node.lineNo, node.typeid + " for " + node.name + " came as complete suprise to me! Did you forget to define it?");
		}

	}
	
	/**
	 * Check whether the type of branch is valid
	 * @param node branch node
	 */
	void typeCheck(AST.branch node, List<String> definedTypes) {

		if (!definedTypes.isEmpty()) {
			if (definedTypes.contains(node.type)) {
				reportError(fileName, node.lineNo, "Duplicate branch '" + node.type + "' in case expression");
			}
			else if(classMap.get(node.type) == null) {
				reportError(fileName, node.lineNo, "Type '" + node.type + "' of '" + node.name + "' came as a complete surprise to me! Did you forget to define it?");
			}
		}
	}

	/**
	 * Check whether the type of method exists and is valid
	 * @param node method node
	 */
	void typeCheck(AST.method node) {
		if (!conformance(node.body.type, node.typeid)) {
			reportError(fileName, node.lineNo, "The return type '" + node.body.type + "' of method '" + node.name + "' does not conform with the declared type " + node.typeid);
		}
	}

	/**
	 * This method inserts the default classes with the dummy methods
	 * @param classes list of class_ objects
	 */
	private void installDefaultClasses(List<class_> classes) {

		String filename = classes.get(0).filename;

		AST.expression abortType = new AST.expression();
		abortType.type = "Object";

		AST.method abortMethod = new AST.method("abort", new ArrayList<AST.formal>(), "Object", abortType, 0);

		AST.expression type_nameType = new AST.expression();
		type_nameType.type = "String";
		AST.method typeNameMethod = new AST.method("type_name", new ArrayList<AST.formal>(), "String", type_nameType, 0);

		AST.expression copyType = new AST.expression();
		copyType.type = "Object";
		AST.method copyMethod = new AST.method("copy", new ArrayList<AST.formal>(), "Object", copyType, 0);

		List<feature> objectClassFeatures = new ArrayList<feature>();
		objectClassFeatures.add(abortMethod);
		objectClassFeatures.add(typeNameMethod);
		objectClassFeatures.add(copyMethod);

		class_ ObjectClass = new class_("Object", filename, null, objectClassFeatures, 0);

		List<AST.formal> outStringFormals = new ArrayList<AST.formal>();
		outStringFormals.add(new AST.formal("x", "String", 0));
		
		List<AST.formal> outIntFormals = new ArrayList<AST.formal>();
		outIntFormals.add(new AST.formal("x", "Int", 0));

		AST.expression outStringType = new AST.expression();
		outStringType.type = "IO";
		AST.method outStringMethod = new AST.method("out_string", outStringFormals, "IO", outStringType, 0);
		
		AST.expression outIntType = new AST.expression();
		outIntType.type = "IO";
		AST.method outIntMethod = new AST.method("out_int", outIntFormals, "IO", outIntType, 0);

		AST.expression inStringType = new AST.expression();
		inStringType.type = "String";
		AST.method inStringMethod = new AST.method("in_string", new ArrayList<AST.formal>(), "String", inStringType, 0);

		AST.expression inIntType = new AST.expression();
		inIntType.type = "Int";
		AST.method inIntMethod = new AST.method("in_int", new ArrayList<AST.formal>(), "Int", inIntType, 0);

		List<feature> ioClassFeatures = new ArrayList<feature>();
		ioClassFeatures.add(outStringMethod);
		ioClassFeatures.add(outIntMethod);
		ioClassFeatures.add(inStringMethod);
		ioClassFeatures.add(inIntMethod);
		

		class_ IOClass = new class_("IO", filename, "Object", ioClassFeatures, 0);

		
		class_ IntClass = new class_("Int", filename, "Object", new ArrayList<AST.feature>(), 0);

		List<AST.formal> concatFormals = new ArrayList<AST.formal>();
		concatFormals.add(new AST.formal("s", "String", 0));
		List<AST.formal> substrFormals = new ArrayList<AST.formal>();
		substrFormals.add(new AST.formal("i", "Int", 0));
		substrFormals.add(new AST.formal("l", "Int", 0));

		AST.expression lengthType = new AST.expression();
		lengthType.type = "Int";
		AST.method lengthMethod = new AST.method("length", new ArrayList<AST.formal>(), "Int", lengthType, 0);

		AST.expression concatSubstrType = new AST.expression();
		concatSubstrType.type = "String";
		AST.method concatMethod = new AST.method("concat", concatFormals, "String", concatSubstrType, 0);
		AST.method substrMethod = new AST.method("substr", substrFormals, "String", concatSubstrType, 0);

		List<feature> stringClassFeatures = new ArrayList<feature>();
		stringClassFeatures.add(lengthMethod);
		stringClassFeatures.add(concatMethod);
		stringClassFeatures.add(substrMethod);

		class_ StringClass = new class_("String", filename, "Object", stringClassFeatures, 0);
		
		class_ BoolClass = new class_("Bool", filename, "Object", new ArrayList<AST.feature>(), 0);

		classes.add(ObjectClass);
		classes.add(IOClass);
		classes.add(IntClass);
		classes.add(StringClass);
		classes.add(BoolClass);

	}

	/**
	 * This method is used to check for cycles in the inheritance graph.
	 * This is implemented according to the algorithm given in CLRS.
	 * @param graph  Inheritance graph given in the form of adjacency lists with parent names as keys
	 * @param u      Starting node where the Depth first visit starts
	 * @param colors Store the initial, intermediate and final states which is used to check for cycles
	 * @param cycles add the node that results in a cycle, passed in as empty
	 */
	private void dfsVisit(HashMap<String, List<class_>> graph, String u, HashMap<String, Integer> colors, List<class_> cycles) {
		
		colors.put(u, 2);

		List<class_> adj = graph.get(u);
		if (adj != null) {
			for (int i = 0; i < adj.size(); i++) {
				class_ v = adj.get(i);
				if (colors.get(v.name) != 2) {
					dfsVisit(graph, v.name, colors, cycles);
				}
				else {
					cycles.add(v);
				}
				
			}
		}
		colors.put(u, 3);
	}

	/**
	 * This method does all the checks related to class definitions and inheritance
	 * and it creates the graph to be used to test for cycles
	 * @param classes list of class_ objects
	 */
	private void checkInheritance(List<AST.class_> classes) {
		HashMap<String, List<class_>> graph = new HashMap<String, List<class_>>();
		HashMap<String, Boolean> classDefnCount = new HashMap<String, Boolean>();
		boolean mainClassExists = false;
		boolean mainMethodExists = false;
		HashMap<String, Integer> colors = new HashMap<String, Integer>();
		for (class_ c : classes) {
			boolean parentExists = false;

			colors.put(c.name, 1);

			if (c.name.equals("Main")) {
				for(AST.feature f : c.features) {
					if (f instanceof AST.method) {
						if (((AST.method)f).name.equals("main")) {
							mainMethodExists = true;
							break;
						}
					}
				}
				mainClassExists = true;
			}

			if (classDefnCount.get(c.name) != null) {
				reportError(c.filename, c.lineNo, "Class " + c.name + " was previously defined");
			}
			else {
				classDefnCount.put(c.name, true);
			}

			for (class_ cObj : classes) {
				if (cObj.name.equals(c.parent) || c.name.equals("Object")) {
					parentExists = true;
					break;
				}
			}

			if(!parentExists) {
				reportError(c.filename, c.lineNo, "Class " + c.name + " inherits an undefined class " + c.parent);
			}
			else {
				if (!c.name.equals("Object")) {
					List<class_> c_adj = graph.get(c.parent);
					if (c_adj != null) {
						if (!c_adj.contains(c)) {
							c_adj.add(c);
						}
					}
					else {
						c_adj = new ArrayList<class_>();
						c_adj.add(c);
						graph.put(c.parent, c_adj);
					}
				}
			}
		}

		String basic[] = {"Int", "String", "Bool"};
		for (String p : basic) {
			if (graph.get(p) != null) {
				List<class_> c_adj = graph.get(p);
				for (class_ c : c_adj) {
					reportError(c.filename, c.lineNo, "Class " + c.name + " cannot inherit class " + p);
				}
			}
		}

		if (!errorFlag) {
			

			for (String k : graph.keySet()) {
				if (colors.get(k) == 1) {
					List<class_> cycles = new ArrayList<class_>();
					dfsVisit(graph, k, colors, cycles);
					if (!cycles.isEmpty()) {
						for (class_ n : cycles) {
							reportError(n.filename, n.lineNo, "Class " + n.name + ", or an ancestor of class " + n.name + ", is involved in an inheritance cycle");
						}
					}
				}
			}
		}

		if (!mainClassExists && !errorFlag) {
			reportError("Class Main is not defined");
		}

		if (!mainMethodExists && !errorFlag) {
			reportError("No 'main' method in class Main");
		}

	}

	/**
	 * This method checks whether the type 'name' conforms to the type 'ancestor'
	 * @param  name     The type to be checked for conformance (i.e. the child type)
	 * @param  ancestor The type to which 'name' has to conform to (i.e. the ancestor type)
	 * @return          true if the types conform, and false otherwise
	 */
	private boolean conformance(String name, String ancestor) {
			
		if (ancestor.equals("Object") || name.equals("Object")) return true;

		List<String> ancestors = getAncestors(name);


		for (String parent : ancestors) {
			if (ancestor.equals(parent)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This method returns the list of ancestors of the class name passed
	 * starting from the current class itself and moving up the tree
	 * @param  name Name of the class whose ancestors are needed
	 * @return      List of Ancestor names
	 */
	private List<String> getAncestors(String name) {
		List<String> ancestors = new ArrayList<String>();

		while (!name.equals("Object")) {
			ancestors.add(name);
			name = classMap.get(name).parent;
		}
		ancestors.add("Object");
		return ancestors;
	}

	/**
	 * This method calculates the lub() of two classes (i.e. the lowest common ancestor)
	 * This method should never return null
	 * @param  a First class
	 * @param  b Second class
	 * @return   The class name of the lowest common ancestor if present and null otherwise
	 */
	private String join(String a, String b) {
		List<String> a_parents = getAncestors(a);
		List<String> b_parents = getAncestors(b);

		for (String p : a_parents) {
			if (b_parents.contains(p)) {
				return p;
			}
		}

		return null;

	}

	/**
	 * This method is used to check the type of expression node passed and to 
	 * recursively traverse the nodes
	 * @param node expression node
	 */
	public void traverseExpression(AST.expression node) {


		if (node instanceof AST.bool_const) {
			typeCheck((AST.bool_const)node);
		}
		else if (node instanceof AST.string_const) {
			typeCheck((AST.string_const)node);
		}
		else if (node instanceof AST.int_const) {
			typeCheck((AST.int_const)node);
		}
		else if (node instanceof AST.object) {
			typeCheck((AST.object)node);
		}
		else if (node instanceof AST.comp) {
			AST.comp c = (AST.comp)node;
			traverseExpression(c.e1);
			typeCheck(c);
		}
		else if (node instanceof AST.eq) {
			AST.eq c = (AST.eq)node;
			traverseExpression(c.e1);
			traverseExpression(c.e2);
			typeCheck(c);
		}
		else if (node instanceof AST.leq) {
			AST.leq c = (AST.leq)node;
			traverseExpression(c.e1);
			traverseExpression(c.e2);
			typeCheck(c);
		}
		else if (node instanceof AST.lt) {
			AST.lt c = (AST.lt)node;
			traverseExpression(c.e1);
			traverseExpression(c.e2);
			typeCheck(c);
		}
		else if (node instanceof AST.neg) {
			AST.neg c = (AST.neg)node;
			traverseExpression(c.e1);
			typeCheck(c);
		}
		else if (node instanceof AST.divide) {
			AST.divide c = (AST.divide)node;
			traverseExpression(c.e1);
			traverseExpression(c.e2);
			typeCheck(c);
		}
		else if (node instanceof AST.mul) {
			AST.mul c = (AST.mul)node;
			traverseExpression(c.e1);
			traverseExpression(c.e2);
			typeCheck(c);
		}
		else if (node instanceof AST.sub) {
			AST.sub c = (AST.sub)node;
			traverseExpression(c.e1);
			traverseExpression(c.e2);
			typeCheck(c);
		}
		else if (node instanceof AST.plus) {
			AST.plus c = (AST.plus)node;
			traverseExpression(c.e1);
			traverseExpression(c.e2);
			typeCheck(c);
		}
		else if (node instanceof AST.isvoid) {
			AST.isvoid c = (AST.isvoid)node;
			traverseExpression(c.e1);
			typeCheck(c);
		}
		else if (node instanceof AST.new_) {
			AST.new_ c = (AST.new_)node;
			typeCheck(c);
		}
		else if (node instanceof AST.assign) {
			AST.assign c = (AST.assign)node;
			traverseExpression(c.e1);
			typeCheck(c);
		}
		else if (node instanceof AST.block) {
			AST.block c = (AST.block)node;
			for (AST.expression e : c.l1)
				traverseExpression(e);
			typeCheck(c);
		}
		else if (node instanceof AST.loop) {
			AST.loop c = (AST.loop)node;
			traverseExpression(c.predicate);
			traverseExpression(c.body);
			typeCheck(c);
		}
		else if (node instanceof AST.cond) {
			AST.cond c = (AST.cond)node;
			traverseExpression(c.predicate);
			traverseExpression(c.ifbody);
			traverseExpression(c.elsebody);
			typeCheck(c);
		}
		else if (node instanceof AST.let) {
			AST.let c = (AST.let)node;
			scopeTable.enterScope();

			AST.object tmp = new AST.object(c.name, c.lineNo);
			tmp.type = c.typeid;
			scopeTable.insert(c.name, tmp);

			traverseExpression(c.value);
			traverseExpression(c.body);
			typeCheck(c);
			scopeTable.exitScope();
		}
		else if (node instanceof AST.dispatch) {
			AST.dispatch c = (AST.dispatch)node;
			traverseExpression(c.caller);
			for (AST.expression e : c.actuals)
				traverseExpression(e);
			typeCheck(c);
		}
		else if (node instanceof AST.static_dispatch) {
			AST.static_dispatch c = (AST.static_dispatch)node;
			traverseExpression(c.caller);
			for (AST.expression e : c.actuals)
				traverseExpression(e);
			typeCheck(c);
		}
		else if (node instanceof AST.typcase) {
			AST.typcase c = (AST.typcase)node;
			traverseExpression(c.predicate);
			List<String> definedTypes = new ArrayList<String>();
			for (AST.branch b : c.branches) {
				scopeTable.enterScope();

				AST.object tmp = new AST.object(b.name, b.lineNo);
				tmp.type = b.type;
				scopeTable.insert(tmp.name, tmp);

				traverseBranch(b, definedTypes);
				scopeTable.exitScope();
			}
			typeCheck(c);
		}



	}

	/**
	 * This method traverses the expression node in the branch and then checks the type of the branch
	 * @param node         branch node
	 * @param definedTypes List of types already defined in previous branches of the current case
	 */
	public void traverseBranch(AST.branch node, List<String> definedTypes) {
		traverseExpression(node.value);
		typeCheck(node, definedTypes);
		definedTypes.add(node.type);
	}

	/**
	 * This method traverses the value expression and then checks the type conformance of the expression with the declared type
	 * @param node attr node
	 */
	public void traverseAttr(AST.attr node) {
		if (!(node.value instanceof AST.no_expr)) {
			traverseExpression(node.value);
			if (!conformance(node.value.type, node.typeid)) {
				reportError(fileName, node.lineNo, "The type '" + node.value.type + "' of value assigned to '" + node.name + "' does not conform with the declared type " + node.typeid);
			}
		}
	}

	/**
	 * This method checks the types of the formal parameters and the return type 
	 * of the method defined and also traverses the expression in the method
	 * @param node method node
	 */
	public void traverseMethod(AST.method node) {

		scopeTable.enterScope();

		for(AST.formal f : node.formals) {
			typeCheck(f);
			AST.object tmpObj = new AST.object(f.name, f.lineNo);
			tmpObj.type = f.typeid;
			scopeTable.insert(tmpObj.name, tmpObj);
		}


		traverseExpression(node.body);
		typeCheck(node);
	}

	/**
	 * This method calls the traversMethod or the traverseAttr method based on the 
	 * type of the feature passed
	 * @param node [description]
	 */
	public void traverseClass(AST.class_ node) {
		for (int i = 0; i < node.features.size() ; i++) {
			AST.feature f = node.features.get(i);
			
			if (f instanceof method) {
				traverseMethod((method)f);
			}
			else {
				traverseAttr((attr)f);
			}

		}		
	}

	/**
	 * This method checks whether the method signature of the two methods 
	 * passes match
	 * @param method1 
	 * @param method2 
	 */
	private void checkMethodSignature(AST.method method1, AST.method method2) {
		if (method1.formals.size() != method2.formals.size()) {
			reportError(fileName, method1.lineNo, "Overridden method " + method1.name + "'s number of formal parameters does not match with the original method");
		}
		else if (!(method1.typeid.equals(method2.typeid))) {
			reportError(fileName, method1.lineNo, "Overridden method " + method1.name + "'s signature does not match");
		}
		else {
			for (int i = 0; i < method1.formals.size(); i++) {
				if (!(method1.formals.get(i).typeid.equals(method2.formals.get(i).typeid))) {
					reportError(fileName, method1.lineNo, "Overridden method " + method1.name + "'s signature does not match");
				}
			}
		}
	}

	/**
	 * This constructor accepts the AST of the program and does semantic analysis 
	 * on the AST and annotates the nodes with the right types
	 * @param  program AST.program node that contains the entire program
	 */
	public Semantic(AST.program program){

		fileName = program.classes.get(0).filename;

		scopeTable = new ScopeTable<AST.object>();

		installDefaultClasses(program.classes);

		classMap = new HashMap<String, class_>();
		for (class_ c : program.classes) {
			classMap.put(c.name, c);
		}

		checkInheritance(program.classes);

		if (!errorFlag) {
			classScopes = new HashMap<String, ClassScope>();

			for (AST.class_ c : program.classes) {
				ClassScope cs = new ClassScope(c, this);
				classScopes.put(c.name, cs);
			}



			for (AST.class_ c : program.classes) {
				List<String> ancestors = getAncestors(c.name);
				ClassScope cs = classScopes.get(c.name);
				for (int i = 1; i < ancestors.size(); i++) {
					String s = ancestors.get(i);
					ClassScope parentScope = classScopes.get(s);
					for (String methodName : cs.methodTables.keySet()) {
						if (parentScope.methodTables.keySet().contains(methodName)) {
							checkMethodSignature(cs.methodTables.get(methodName), parentScope.methodTables.get(methodName));
						}
					}

					for (String attrName : cs.members.keySet()) {
						if (parentScope.members.keySet().contains(attrName)) {
							reportError(fileName, cs.members.get(attrName).lineNo, "Attribute '" + attrName + "' is an attribute of inherited class");
						}
					}

				}



			}

			for (class_ c : program.classes) {

				for (String s : classScopes.get(c.name).members.keySet()) {
					AST.attr atr = classScopes.get(c.name).members.get(s);
					AST.object tmpObj = new AST.object(atr.name, atr.lineNo);
					tmpObj.type = atr.typeid;
					scopeTable.insert(tmpObj.name, tmpObj);
				}

				AST.object selfObj = new AST.object("self", c.lineNo);
				selfObj.type = c.name;
				scopeTable.insert("self", selfObj);

				traverseClass(c);
			}
		}

	}
}
