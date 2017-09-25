package cool;
import java.util.*;
import java.util.Map.Entry;

public class ClassTable{
	public HashMap <String, ClassInfo> classinfos = new HashMap <String, ClassInfo>();

	public List<Error> errors = new ArrayList<Error>();

	public ClassTable(){

		HashMap<String, AST.method> objmethods = new HashMap <String, AST.method>();
		objmethods.put("abort", new AST.method("abort", new ArrayList<AST.formal>(), "Object", new AST.no_expr(0), 0));
		objmethods.put("type_name", new AST.method("type_name", new ArrayList<AST.formal>(), "String", new AST.no_expr(0), 0));

		classinfos.put("Object", new ClassInfo(null, new HashMap<String, AST.attr>(), objmethods, 0));

		HashMap <String, AST.method> iomethods = new HashMap<String, AST.method>();

		iomethods.put("out_string", new AST.method("out_string", Arrays.asList(new AST.formal("out_string", "String", 0))
												   , "IO", new AST.no_expr(0), 0));
		iomethods.put("out_int", new AST.method("out_int", Arrays.asList(new AST.formal("out_int", "Int", 0))
												   , "IO", new AST.no_expr(0), 0));
		iomethods.put("in_string", new AST.method("in_string", new ArrayList<AST.formal>()
												   , "String", new AST.no_expr(0), 0));
		iomethods.put("in_int", new AST.method("in_int", new ArrayList<AST.formal>()
												   , "Int", new AST.no_expr(0), 0));
		classinfos.put("IO", new ClassInfo("Object", new HashMap<String, AST.attr>(), iomethods, 1));
		classinfos.get("IO").methodlist.putAll(objmethods);		// IO inherits from Object
		//height.put("IO", 1);

		classinfos.put("Int", new ClassInfo("Object", new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), 1));
		//height.put("Int", 1);
		classinfos.get("Int").methodlist.putAll(objmethods);	// Int inherits from Object

		classinfos.put("Bool", new ClassInfo("Object", new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), 1));
		//height.put("Bool", 1);
		classinfos.get("Bool").methodlist.putAll(objmethods);	// Bool inherits from Object

		HashMap <String, AST.method> stringmethods = new HashMap<String, AST.method>();
		//List<AST.formal> concat_formal = new ArrayList<AST.formal>();
		//concat_formal.add(new AST.formal("s", "String", 0));
		//List<AST.formal> substr_formal = new ArrayList<AST.formal>();
		//substr_formal.add(new AST.formal("i", "Int", 0));
		//substr_formal.add(new AST.formal("l", "Int", 0));

		stringmethods.put("length", new AST.method("length", new ArrayList<AST.formal>(), "Int", new AST.no_expr(0), 0));
		stringmethods.put("concat", new AST.method("concat", Arrays.asList(new AST.formal("s", "String", 0))
										, "String", new AST.no_expr(0), 0));
		stringmethods.put("substr", new AST.method("substr", Arrays.asList(new AST.formal("i", "Int", 0), new AST.formal("l", "Int", 0))
										, "String", new AST.no_expr(0), 0));

		classinfos.put("String", new ClassInfo("Object", new HashMap<String, AST.attr>(), stringmethods, 1));
		//height.put("String", 1);
		classinfos.get("String").methodlist.putAll(objmethods);		// String Inherits from Object
	}

	void insert(AST.class_ c) {
		String parent = c.parent;
		ClassInfo currClass = new ClassInfo(c.parent, classinfos.get(c.parent).attrlist,
			classinfos.get(c.parent).methodlist, classinfos.get(c.parent).depth + 1);	// adding the parents attribute list and method list

		List<String> currClassAttrList = new ArrayList<>();
		List<String> currClassMethodList = new ArrayList<>();

		for(AST.feature feat : c.features) {
			if(feat instanceof AST.attr) {
				AST.attr attrfeat = (AST.attr) feat;
				if(currClassAttrList.contains(attrfeat.name)) {
					errors.add(new Error(c.filename, attrfeat.lineNo,
							   "Attribute " + attrfeat.name + " is defined multiple times in class " + c.name));
				}
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
				if(currClassMethodList.contains(methodfeat.name))
					errors.add(new Error(c.filename, methodfeat.lineNo,
							   "Method " + methodfeat.name + " is defined multiple times in class " + c.name));
				else {
					boolean foundErr = false;
					List<String> parameters = new ArrayList<>();
					for (AST.formal form : methodfeat.formals){
						if(parameters.contains(form.name)){
							errors.add(new Error(c.filename, methodfeat.lineNo, form.name +
												" has been repeated more than once as parameter in " + methodfeat.name));
							foundErr = true;
						} else parameters.add(form.name);
					}
					if (currClass.methodlist.containsKey(methodfeat.name)) {
						AST.method parentMethod = currClass.methodlist.get(methodfeat.name);

						if(methodfeat.formals.size() != parentMethod.formals.size()) {
							errors.add(new Error(c.filename, methodfeat.lineNo, "Different number of parameters in redefined method "
												+ methodfeat.name));
							foundErr = true;
						}
						else {
							if(methodfeat.typeid.equals(parentMethod.typeid) == false) {
								errors.add(new Error(c.filename, methodfeat.lineNo, "Return type" + methodfeat.typeid + " is differnt in redefined method " + methodfeat.name +  " from original return type " + parentMethod.typeid));
								foundErr = true;
							}
							for(int i = 0; i < methodfeat.formals.size(); ++i) {
								if(methodfeat.formals.get(i).typeid.equals(parentMethod.formals.get(i).typeid) == false) {
									errors.add(new Error(c.filename, methodfeat.lineNo, "Parameter type " + methodfeat.formals.get(i).typeid +" is differnt in redefined method " + methodfeat.name +  " from original return type " + parentMethod.formals.get(i).typeid));
									foundErr = true;
								}
							}
						}
					}
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
