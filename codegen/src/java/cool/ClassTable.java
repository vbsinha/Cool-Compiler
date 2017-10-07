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
		
		/*classes.put("Int", new IRClassPlus("Int", "Object", new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), new HashMap <String, Integer>(), obj_moffset, new ArrayList <AST.attr>(), obj_mlist, irname));
		height.put("Int", 1);
		classes.get("Int").mlist.putAll(ol);	// Int inherits from Object
		classes.get("Int").methodList.get(2).typeid = "Int";	// redefine copy
		classes.get("Int").IRname.put("copy", "@_ZN3Int4copy");
		
		classes.put("Bool", new IRClassPlus("Bool", "Object", new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), new HashMap <String, Integer>(), obj_moffset, new ArrayList <AST.attr>(), obj_mlist, irname));
		height.put("Bool", 1);
		classes.get("Bool").mlist.putAll(ol);	// Bool inherits from Object
		classes.get("Int").methodList.get(2).typeid = "Bool";
		classes.get("Bool").IRname.put("copy", "@_ZN4Bool4copy");
		
		HashMap <String, AST.method> sl = new HashMap<String, AST.method>();
		List<AST.formal> str_formal = new ArrayList<AST.formal>();
		str_formal.add(new AST.formal("this", "String", 0));
		List<AST.formal> concat_formal = new ArrayList<AST.formal>();
		concat_formal.addAll(str_formal);
		concat_formal.add(new AST.formal("that", "String", 0));
		List<AST.formal> substr_formal = new ArrayList<AST.formal>();
		substr_formal.addAll(str_formal);
		substr_formal.add(new AST.formal("index", "Int", 0));
		substr_formal.add(new AST.formal("len", "Int", 0));
		
		sl.put("length", new AST.method("length", str_formal, "Int", new AST.no_expr(0), 0));
		sl.put("concat", new AST.method("concat", concat_formal, "String", new AST.no_expr(0), 0));
		sl.put("substr", new AST.method("substr", substr_formal, "String", new AST.no_expr(0), 0));
		
		HashMap <String, Integer> str_moffset = new HashMap <String, Integer>();
		str_moffset.putAll(obj_moffset);
		str_moffset.put("length", 3);
		str_moffset.put("concat", 4);
		str_moffset.put("substr", 5);
		
		ArrayList <AST.method> str_mlist = new ArrayList <AST.method>();
		str_mlist.addAll(obj_mlist);
		str_mlist.add(new AST.method("length", str_formal, "Int", new AST.no_expr(0), 0));
		str_mlist.add(new AST.method("concat", concat_formal, "String", new AST.no_expr(0), 0));
		str_mlist.add(new AST.method("substr", substr_formal, "String", new AST.no_expr(0), 0));
		str_mlist.set(2, new AST.method("copy", str_formal, "String", new AST.no_expr(0), 0));
		str_mlist.get(2).typeid = "String";		// redefine copy
		
		HashMap <String, String> str_irname = new HashMap <String, String> ();
		str_irname.putAll(irname);
		str_irname.put("length", "@_ZN6String6length");
		str_irname.put("concat", "@_ZN6String6concat");
		str_irname.put("substr", "@_ZN6String6substr");
		
		// change copy
		//str_irname.put("copy", "@_ZN6String4copy");
				
		
		
		classes.put("String", new IRClassPlus("String", "Object", new HashMap<String, AST.attr>(), sl, new HashMap<String, Integer>(), str_moffset, new ArrayList <AST.attr>(), str_mlist, str_irname));
		height.put("String", 1);
		classes.get("String").mlist.putAll(ol);		// String Inherits from Object
		classes.get("String").IRname.put("copy", "@_ZN5String4copy");*/
	}

    // Insert this class into ClassTable after some error checks
	void insert(AST.class_ c) {
		String parent = c.parent;
		ClassInfo currClass = new ClassInfo(c.parent, classinfos.get(c.parent).attrlist,
			classinfos.get(c.parent).methodlist, classinfos.get(c.parent).depth + 1);

		List<String> currClassAttrList = new ArrayList<>();
		List<String> currClassMethodList = new ArrayList<>();

		for(AST.feature feat : c.features) {
			if(feat instanceof AST.attr) {
				AST.attr attrfeat = (AST.attr) feat;
				// Check if an attribute is defined multiple times
				if(currClassAttrList.contains(attrfeat.name)) {
					errors.add(new Error(c.filename, attrfeat.lineNo,
							   "Attribute " + attrfeat.name + " is defined multiple times in class " + c.name));
				}
				// Check if the attribute was defined in parent's class
				else if (currClass.attrlist.containsKey(attrfeat.name)) {
					errors.add(new Error(c.filename, attrfeat.lineNo, "Attribute " + attrfeat.name
					 + " of class " + c.name + " is already an attribute of its parent class"));
				} else {
					currClass.attrlist.put(attrfeat.name, attrfeat);
					currClassAttrList.add(attrfeat.name);
				}
			}
			else if(feat instanceof AST.method) {
				AST.method methodfeat = (AST.method) feat;
				// Check if a method is defined multiple times
				if(currClassMethodList.contains(methodfeat.name))
					errors.add(new Error(c.filename, methodfeat.lineNo,
							   "Method " + methodfeat.name + " is defined multiple times in class " + c.name));
				else {
					boolean foundErr = false;
					List<String> parameters = new ArrayList<>();
					// Check if any parameter is repeated twice in argument list
					for (AST.formal form : methodfeat.formals){
						if(parameters.contains(form.name)){
							errors.add(new Error(c.filename, methodfeat.lineNo, form.name +
												" has been repeated more than once as parameter in " + methodfeat.name));
							foundErr = true;
						} else parameters.add(form.name);
					}
					// In case of inheritance
					if (currClass.methodlist.containsKey(methodfeat.name)) {
						AST.method parentMethod = currClass.methodlist.get(methodfeat.name);
                        // Have different number of parameter?
						if(methodfeat.formals.size() != parentMethod.formals.size()) {
							errors.add(new Error(c.filename, methodfeat.lineNo, "Different number of parameters in redefined method "
												+ methodfeat.name));
							foundErr = true;
						}
						else {
						    // Have different return types?
							if(methodfeat.typeid.equals(parentMethod.typeid) == false) {
								errors.add(new Error(c.filename, methodfeat.lineNo, "Return type" + methodfeat.typeid + " is differnt in redefined method " + methodfeat.name +  " from original return type " + parentMethod.typeid));
								foundErr = true;
							}
							// Have different parameter types?
							for(int i = 0; i < methodfeat.formals.size(); ++i) {
								if(methodfeat.formals.get(i).typeid.equals(parentMethod.formals.get(i).typeid) == false) {
									errors.add(new Error(c.filename, methodfeat.lineNo, "Parameter type " + methodfeat.formals.get(i).typeid +" is differnt in redefined method " + methodfeat.name +  " from original return type " + parentMethod.formals.get(i).typeid));
									foundErr = true;
								}
							}
						}
					}
					// If no error
					if (foundErr == false) {
						currClass.methodlist.put(methodfeat.name, methodfeat);
						currClassMethodList.add(methodfeat.name);
					}
				}
			}
		}

		classinfos.put(c.name, currClass);
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
