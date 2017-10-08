package cool;
import java.util.*;
import java.util.Map.Entry;

public class ClassTable{
    // A Map form name of class to ClassInfo 
	public HashMap <String, ClassInfo> classinfos = new HashMap <String, ClassInfo>();

	public ClassTable(){	
		
		// assignObj();
		
		// assignIO();
		
		// assignInt();
		
		// assignBool();
		
		// assignString();
		
	}

	private void assignObj(){
	    List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Object", 0));
		
		HashMap<String, AST.method> methods = new HashMap <String, AST.method>();
		methods.put("abort", new AST.method("abort", formals, "Object", new AST.no_expr(0), 0));
		methods.put("type_name", new AST.method("type_name", formals, "String", new AST.no_expr(0), 0));
		methods.put("copy", new AST.method("copy", formals, "Object", new AST.no_expr(0), 0));
		
		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.put("abort", "@_ZN6Object5abort");
		methodName.put("type_name", "@_ZN6Object9type_name");
		methodName.put("copy", "@_ZN6Object4copy");

		classinfos.put("Object", new ClassInfo(null, new HashMap<String, AST.attr>(), methods, methodName, 0));
	}
	
	private void assignIO(){
	    HashMap <String, AST.method> methods = new HashMap<String, AST.method>();
	    
	    List <AST.formal> osFormals = new ArrayList<AST.formal>();
		List <AST.formal> oiFormals = new ArrayList <AST.formal> ();
		
		List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "IO", 0));
		
		osFormals.addAll(formals);
		oiFormals.addAll(formals);
		
		osFormals.add(new AST.formal("x", "String", 0));
		oiFormals.add(new AST.formal("x", "Int", 0));
		
		methods.put("out_string", new AST.method("out_string", osFormals, "IO", new AST.no_expr(0), 0));
		methods.put("out_int", new AST.method("out_int", oiFormals, "IO", new AST.no_expr(0), 0));
		methods.put("in_string", new AST.method("in_string", formals, "String", new AST.no_expr(0), 0));
		methods.put("in_int", new AST.method("in_int", formals, "Int", new AST.no_expr(0), 0));
		//methods.put("copy", new AST.method("copy", formals, "IO", new AST.no_expr(0), 0));
		
		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.putAll(classinfos.get("Object").methodName);
		methodName.put("out_string", "@_ZN2IO10out_string");
		methodName.put("in_string", "@_ZN2IO7out_int");
		methodName.put("in_string", "@_ZN2IO9in_string");
		methodName.put("in_int", "@_ZN2IO9in_int");
		//methodName.set("copy", "@_ZN2IO4copy");
		
		classinfos.put("IO", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1));
	}	
	
	private void assignInt(){
		HashMap <String, AST.method> methods = new HashMap<String, AST.method>();
		List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Int", 0));

		//methods.put("copy", new AST.method("copy", formals, "Int", new AST.no_expr(0), 0));

		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.putAll(classinfos.get("Object").methodName);
		//methodName.set("copy", "@_ZN3Int4copy");

	    classinfos.put("Int", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1));
	}
	
	private void assignBool(){
		HashMap <String, AST.method> methods = new HashMap<String, AST.method>();
		List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Bool", 0));

		//methods.put("copy", new AST.method("copy", formals, "Bool", new AST.no_expr(0), 0));

		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.putAll(classinfos.get("Object").methodName);
		//methodName.set("copy", "@_ZN4Bool4copy");

	    classinfos.put("Bool", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1));
	}
	
	private void assignString(){
	    
	    List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "String", 0));
		
		List<AST.formal> concatFormal = new ArrayList<AST.formal>();
		concatFormal.addAll(formals);
		concatFormal.add(new AST.formal("s", "String", 0));
		
		List<AST.formal> substrFormal = new ArrayList<AST.formal>();
		substrFormal.addAll(formals);
		substrFormal.add(new AST.formal("i", "Int", 0));
		substrFormal.add(new AST.formal("l", "Int", 0));
		
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

    // Insert this class into ClassTable after some error checks
	void insert(AST.class_ c) {

		for (AST.feature f : c.features) {
			if (f instanceof AST.method)
				System.out.println(((AST.method)f).name);
		}

		/* Whenever a new class is inserted,
		 * - Inherits the attributes and methods of the parent class.
		 * - Checks for multiple method or attribute definitions.
		 * - Checks for correct method overrides and any attribute overrides
		 */
		String par = c.parent;
		ClassInfo pari = classinfos.get(par);
		ClassInfo ci = null;
		if (pari != null)
		    ci = new ClassInfo(par, pari.attrMap, new HashMap<String, AST.method>(), pari.methodName, classinfos.get(par).depth + 1);
		else 
            ci = new ClassInfo(par, new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), new HashMap<String, String>(), 0);
            
		for(AST.feature f : c.features) {
			if (f instanceof AST.attr) {
				AST.attr a = (AST.attr) f;
				ci.attrMap.put(a.name, a);
				ci.attrList.add(a.name);
			}
			else if(f instanceof AST.method) {
				AST.method m = (AST.method) f;
				ci.methodMap.put(m.name, m);
				ci.methodName.put(m.name, "@_ZN"+c.name.length()+c.name+m.name.length()+m.name);
			}
		}

		classinfos.put(c.name, ci);
	}

	// Check if given ancestor class is ancestor of the given child class
	// boolean isAncestor(String child, String ancestor){
	// 	// If child is null then there is nosense for ancestor
	// 	if (child == null) return false;
	// 	// Ancestor relation is symmetric relation
	// 	if (child.equals(ancestor)) return true;
	// 	// Recursively check if ancestor is ancestor for child's parent
	// 	return isAncestor(classinfos.get(child).parent, ancestor);
	// }

	// // Find the least common ancestor of class1 and class2
	// String commonAncestor(String class1, String class2) {
	// 	// If both are equal then itself is least common ancestor
	// 	if (class1.equals(class2)) return class1;
	// 	if (classinfos.get(class1).depth < classinfos.get(class2).depth)
	// 		return commonAncestor(class2, class1);
	// 	// Recursively find least common ancestor
	// 	return commonAncestor(classinfos.get(class1).parent, class2);
	// }
}
