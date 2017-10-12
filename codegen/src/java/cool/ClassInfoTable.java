package cool;
import java.util.*;
import java.util.Map.Entry;

public class ClassInfoTable{
    // A Map form name of class to ClassInfo 
	public HashMap <String, ClassInfo> classinfos;

	public ClassInfoTable(){
		classinfos = new HashMap <String, ClassInfo>();
		assignObj();
		assignIO();
		assignBool();
		assignInt();
		assignString();
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

		classinfos.put("Object", new ClassInfo(null, new HashMap<String, AST.attr>(), methods, methodName, 0, 0));
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
		
		classinfos.put("IO", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1, 0));
	}	
	
	private void assignInt(){
		HashMap <String, AST.method> methods = new HashMap<String, AST.method>();
		List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Int", 0));

		//methods.put("copy", new AST.method("copy", formals, "Int", new AST.no_expr(0), 0));

		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.putAll(classinfos.get("Object").methodName);
		//methodName.set("copy", "@_ZN3Int4copy");

	    classinfos.put("Int", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1, 4));
	}
	
	private void assignBool(){
		HashMap <String, AST.method> methods = new HashMap<String, AST.method>();
		List<AST.formal> formals = new ArrayList<AST.formal>();
		formals.add(new AST.formal("this", "Bool", 0));

		//methods.put("copy", new AST.method("copy", formals, "Bool", new AST.no_expr(0), 0));

		HashMap <String, String> methodName = new HashMap <String, String>();
		methodName.putAll(classinfos.get("Object").methodName);
		//methodName.set("copy", "@_ZN4Bool4copy");

	    classinfos.put("Bool", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1, 4));
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
		
		classinfos.put("String", new ClassInfo("Object", new HashMap<String, AST.attr>(), methods, methodName, 1, 4));
	}

    // Insert this class into ClassTable after some error checks
	void insert(AST.class_ c) {

		/* Whenever a new class is inserted,
		 * - Inherits the attributes and methods of the parent class.
		 * - Checks for multiple method or attribute definitions.
		 * - Checks for correct method overrides and any attribute overrides
		 */
		String par = c.parent;
		ClassInfo pari = classinfos.get(par);
		ClassInfo ci = null;
		if (pari != null)
		    ci = new ClassInfo(par, pari.attrMap, new HashMap<String, AST.method>(), pari.methodName, classinfos.get(par).depth + 1, 0);
		else 
            ci = new ClassInfo(par, new HashMap<String, AST.attr>(), new HashMap<String, AST.method>(), new HashMap<String, String>(), 0, 0);
        int size = classinfos.get(par).size;
		for(AST.feature f : c.features) {
			if (f instanceof AST.attr) {
				AST.attr a = (AST.attr) f;
				ci.attrMap.put(a.name, a);
				ci.attrList.add(a.name);
				if (a.typeid == "Int" || a.typeid == "Bool") size += 4;
				else size += 8;
			}
			else if(f instanceof AST.method) {
				AST.method m = (AST.method) f;
				ci.methodMap.put(m.name, m);
				ci.methodName.put(m.name, "@_ZN"+c.name.length()+c.name+m.name.length()+m.name);
			}
		}
		ci.size = size;
		classinfos.put(c.name, ci);
	}
}
