package cool;
import java.util.*;
import java.util.Map.Entry;

public class ClassTable{
    // A Map form name of class to ClassInfo 
	public HashMap <String, ClassInfo> classinfos = new HashMap <String, ClassInfo>();

    // A list for errors
	public List<Error> errors = new ArrayList<Error>();
	
	private void assignIO(){
	    HashMap <String, AST.method> methods = new HashMap<String, AST.method>();
	    
	    List <AST.formal> osFormals = new ArrayList<AST.formal>();
		List <AST.formal> oiFormals = new ArrayList <AST.formal> ();
		
		List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Object", 0));
		
		osFormals.addAll(formals);
		oiFormals.addAll(formals);
		
		osFormals.add(new AST.formal("out_string", "String", 0));
		oiFormals.add(new AST.formal("out_int", "Int", 0));

		
		methods.put("out_string", new AST.method("out_string", osFormals, "IO", new AST.no_expr(0), 0));
		methods.put("out_int", new AST.method("out_int", oiFormals, "IO", new AST.no_expr(0), 0));
		methods.put("in_string", new AST.method("in_string", formals, "String", new AST.no_expr(0), 0));
		methods.put("in_int", new AST.method("in_int", formals, "Int", new AST.no_expr(0), 0));
		
		/*HashMap <String, Integer> io_moffset = new HashMap <String, Integer>();
		io_moffset.putAll(obj_moffset);
		io_moffset.put("out_string", 3);
		io_moffset.put("out_int", 4);
		io_moffset.put("in_string", 5);
		io_moffset.put("in_int", 6);
		
		ArrayList <AST.method> io_mlist = new ArrayList <AST.method>();
		io_mlist.addAll(obj_mlist);
		io_mlist.add(new AST.method("out_string", os_formals, "IO", new AST.no_expr(0), 0));
		io_mlist.add(new AST.method("out_int", oi_formals, "IO", new AST.no_expr(0), 0));
		io_mlist.add(new AST.method("in_string", io_formal, "String", new AST.no_expr(0), 0));
		io_mlist.add(new AST.method("in_int", io_formal, "Int", new AST.no_expr(0), 0));*/
		
		// redefine copy
		//io_mlist.set(2, new AST.method("copy", io_formal, "IO", new AST.no_expr(0), 0));
		
		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.putAll(classinfos.get("Object").methodName);
		methodName.put("out_string", "@_ZN2IO10out_string");
		methodName.put("in_string", "@_ZN2IO7out_int");
		methodName.put("in_string", "@_ZN2IO9in_string");
		methodName.put("in_int", "@_ZN2IO9in_int");
		
		// change copy irname
		//io_irname.put("copy", "@_ZN2IO4copy");
		
		
		classinfos.put("IO", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1));
		//classes.get("IO").mlist.putAll(ol);		// IO inherits from Object
		//height.put("IO", 1);
		//classes.get("IO").attrList.add(new AST.attr("__Base", "Object" + ".Base", new AST.no_expr(0), 0));
		//classes.get("IO").attrOffset.put("__Base", 0);
	}
	
	private void assignObj(){
	    List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Object", 0));
		
		HashMap<String, AST.method> methods = new HashMap <String, AST.method>();
		methods.put("abort", new AST.method("abort", formals, "Object", new AST.no_expr(0), 0));
		methods.put("type_name", new AST.method("type_name", formals, "String", new AST.no_expr(0), 0));
		
		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.put("abort", "@_ZN6Object5abort");
		methodName.put("type_name", "@_ZN6Object9type_name");
		//irname.put("copy", "@_ZN6Object4copy");

		classinfos.put("Object", new ClassInfo(null, new HashMap<String, AST.attr>(), ol, methodName, 0));
	}
	
	private void assignInt(){
	    classinfos.put("Int", new ClassInfo("Object", new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), classinfos.get("Object").methodName, 1));
		//height.put("Int", 1);
		//classes.get("Int").mlist.putAll(ol);	// Int inherits from Object
		//classinfos.get("Int").methodList.get(2).typeid = "Int";	// redefine copy
		//classes.get("Int").IRname.put("copy", "@_ZN3Int4copy");
	}
	
	private void assignBool(){
	    classinfos.put("Bool", new ClassInfo("Object", new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), classinfos.get("Object").methodName, 1));
	}
	
	private void assignString(){
	    
	    List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Object", 0));
		
		List<AST.formal> concatFormal = new ArrayList<AST.formal>();
		concatFormal.addAll(formals);
		concatFormal.add(new AST.formal("that", "String", 0));
		
		List<AST.formal> substrFormal = new ArrayList<AST.formal>();
		substrFormal.addAll(formals);
		substrFormal.add(new AST.formal("index", "Int", 0));
		substrFormal.add(new AST.formal("len", "Int", 0));
		
	    HashMap <String, AST.method> methods = new HashMap<String, AST.method>();
	    methods.put("length", new AST.method("length", formals, "Int", new AST.no_expr(0), 0));
		methods.put("concat", new AST.method("concat", concatFormal, "String", new AST.no_expr(0), 0));
		methods.put("substr", new AST.method("substr", substrFormal, "String", new AST.no_expr(0), 0));
		
		HashMap <String, String> methodName = new HashMap <String, String> ();
		methodName.putAll(classinfos.get("Object").methodName);
		methodName.put("length", "@_ZN6String6length");
		methodName.put("concat", "@_ZN6String6concat");
		methodName.put("substr", "@_ZN6String6substr");
		
		classinfos.put("String", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1));
		
	}

	public ClassTable(){	
		
		assignObj();
		
		assignIO();
		
		assignInt();
		
		assignBool();
		
		assignString();
		
	}

    // Insert this class into ClassTable after some error checks
	void insert(AST.class_ c) {
		/* Whenever a new class is inserted,
		 * - Inherits the attributes and methods of the parent class.
		 * - Checks for multiple method or attribute definitions.
		 * - Checks for correct method overrides and any attribute overrides
		 */
		String par = c.parent;
		classinfos tc = new ClassInfo(par, classinfos.get(par).attrList, classinfos.get(par).methodList, classinfos.get(par).methodName, classinfos.get(par).depth + 1);	// adding the parents attribute list and method list

		//HashMap <String, 
		HashMap <String, AST.attr> tc_alist = new HashMap<String, AST.attr>();
		HashMap <String, AST.method> tc_mlist = new HashMap <String, AST.method>();
		
		tc.attrList.add(new AST.attr("_Par", "class." + par, new AST.no_expr(0), 0));
		//tc.attrOffset.put("__Base", 0);
	
		/* Checks for the following errors with respect to the inherited class:
		 * - redefinition of an inherited attribute (Note: the class retains the inherited attribute and discards the attribute defined within the class)
		 * - wrong redefinition of an inherited method (Note : the class retains the inherited method and discards the method defined within the class)
		 */
		/* adding attrs of parent */
		//for(Entry<String, AST.attr> entry : tc_alist.entrySet()) {
		//	tc.alist.put(entry.getKey(), entry.getValue());
		//}
		
		/* attrs of the parent are accessed via the base attr */
		
		
		/* Checks for the following errors within a class:
		 * - multiple attribute definitions
		 * - multiple method definitions
		 */
		/* adding attrs and methods of class */
		
		//int attr_ptr = 1;
		
		for(AST.feature e : c.features) {
			if(e.getClass() == AST.attr.class) {
				AST.attr ae = (AST.attr) e;
				
				tc_alist.put(ae.name, ae);
				tc.attrList.add(ae);
				//tc.attrOffset.put(ae.name, attr_ptr);
				//attr_ptr++;
			}
			else if(e.getClass() == AST.method.class) {
				AST.method me = (AST.method) e;
				tc_mlist.put(me.name, me);
			}
		}
		
		// change the copy method name
		tc.IRname.put("copy", "@_ZN" + tc.name.length() + tc.name + "4copy");
		
		// tc_mlist contains methods in current class
		
		int method_ptr = tc.methodList.size();
		for(Entry<String, AST.method> entry : tc_mlist.entrySet()) {
			String me_name = entry.getKey();
			if(tc.mlist.containsKey(entry.getKey())) {		// overloaded method
				tc.methodList.set(tc.methodOffset.get(me_name), entry.getValue());
				tc.IRname.put(me_name, "@_ZN" + tc.name.length() + tc.name + me_name.length() + me_name);
			} else {
				tc.methodList.add(entry.getValue());
				tc.methodOffset.put(entry.getKey(), method_ptr);
				tc.IRname.put(me_name, "@_ZN" + tc.name.length() + tc.name + me_name.length() + me_name);
				method_ptr++;
			}
		}
		height.put(c.name, height.get(c.parent) + 1);
		classes.put(c.name, tc);
	}

	// Check if given ancestor class is ancestor of the given child class
	boolean isAncestor(String child, String ancestor){
		// If child is null then there is nosense for ancestor
		if (child == null) return false;
		// Ancestor relation is symmetric relation
		if (child.equals(ancestor)) return true;
		// Recursively check if ancestor is ancestor for child's parent
		return isAncestor(classinfos.get(child).parent, ancestor);
	}

	// Find the least common ancestor of class1 and class2
	String commonAncestor(String class1, String class2) {
		// If both are equal then itself is least common ancestor
		if (class1.equals(class2)) return class1;
		if (classinfos.get(class1).depth < classinfos.get(class2).depth)
			return commonAncestor(class2, class1);
		// Recursively find least common ancestor
		return commonAncestor(classinfos.get(class1).parent, class2);
	}
}
