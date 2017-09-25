package cool;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class AST{
	public static class ASTNode {
		int lineNo;
	}
	public static String sp = "  ";

        static String escapeSpecialCharacters(String text) {
                return
                        text
                                .replaceAll("\\\\", "\\\\\\\\")
                                .replaceAll("\n", "\\\\n")
                                .replaceAll("\t", "\\\\t")
                                .replaceAll("\b", "\\\\b")
                                .replaceAll("\f", "\\\\f")
                                .replaceAll("\"", "\\\\\"")
                                .replaceAll("\r", "\\\\015")
                                .replaceAll("\033","\\\\033")
                                .replaceAll("\001","\\\\001")
                                .replaceAll("\002","\\\\002")
                                .replaceAll("\003","\\\\003")
                                .replaceAll("\004","\\\\004")
                                .replaceAll("\022","\\\\022")
                                .replaceAll("\013","\\\\013")
                                .replaceAll("\000", "\\\\000")
                                ;
        }


	public static abstract class expression extends ASTNode  {
		String type;
		public expression(){
			type = "_no_type";
		}
		String getString(String space){
			return "";
		};
		// An abstract handle function that will be inherited by all subclasses
		abstract void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable);
	}
	public static class no_expr extends expression {
		public no_expr(int l){
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_no_expr\n"+space+": "+type;
		}
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			System.err.println(lineNo);
			errors.add(new Error(null, lineNo, "This should occur never"));
		}
	}
	public static class bool_const extends expression{
		public boolean value;
		public bool_const(boolean v, int l){
			value = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_bool\n"+space+sp+(value?"1":"0")+"\n"+space+": "+type;
		}
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			type = "Bool";
		}
	}
	public static class string_const extends expression{
		public String value;
		public string_const(String v, int l){
			value = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_string\n"+space+sp+"\""+escapeSpecialCharacters(value)+"\""+"\n"+space+": "+type;
		}
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			type = "String";
		}
	}

	public static class int_const extends expression{
		public int value;
		public int_const(int v, int l){
			value = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_int\n"+space+sp+value+"\n"+space+": "+type;
		}
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			type = "Int";
		}
	}

	public static class object extends expression{
		public String name;
		public object(String v, int l){
			name = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_object\n"+space+sp+name+"\n"+space+": "+type;
		}
		// Return the type of the object
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			attr a = scopeTable.lookUpGlobal(name);
			if (a == null) {
				errors.add(new Error(null, lineNo, "Identifier " + name + " not declared"));
				type = "Object";
			} else type = a.typeid;
		}
	}
	public static class comp extends expression{
		public expression e1;
		public comp(expression v, int l){
			e1 = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_comp\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		// CHeck if comaprision is of type bool return bool
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Bool") == false) {
				errors.add(new Error(null, lineNo, "Bool complement applied on type " + e1.type));
			}
			type = "Bool";
		}
	}
	public static class eq extends expression{
		public expression e1;
		public expression e2;
		public eq(expression v1, expression v2, int l){
			e1=v1;
			e2=v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_eq\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if only String, Int or Bool are comapared. Return boool
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			e2.handle(errors, scopeTable, classTable);
			List<String> basicTypes = Arrays.asList("String", "Int", "Bool");
			if (basicTypes.contains(e1.type) || basicTypes.contains(e2.type)) {
				if (e1.type.equals(e2.type) == false)
					errors.add(new Error(null, lineNo, "Cannot compare " + e1.type + " and " + e2.type));
			}
			type = "Bool";
		}
	}


	public static class leq extends expression{
		public expression e1;
		public expression e2;
		public leq(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_leq\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if operands are int, return bool
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			e2.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e1.type));
			}
			if (e2.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e2.type));
			}
			type = "Bool";
		}
	}

	public static class lt extends expression{
		public expression e1;
		public expression e2;
		public lt(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_lt\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if operands are int, return bool
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			e2.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e1.type));
			}
			if (e2.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e2.type));
			}
			type = "Bool";
		}
	}
	public static class neg extends expression{
		public expression e1;
		public neg(expression v, int l){
			e1 = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_neg\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if operand if Int, return Int
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int argument "+e1.type));
			}
			type = "Int";
		}
	}
	public static class divide extends expression{
		public expression e1;
		public expression e2;
		public divide(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_divide\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if operands are int, return Int
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			e2.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e1.type));
			}
			if (e2.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e2.type));
			}
			type = "Int";
		}
	}
	public static class mul extends expression{
		public expression e1;
		public expression e2;
		public mul(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_mul\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if operands are int, return Int
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			e2.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e1.type));
			}
			if (e2.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e2.type));
			}
			type = "Int";
		}
	}
	public static class sub extends expression{
		public expression e1;
		public expression e2;
		public sub(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_sub\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if operands are int, return Int
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			e2.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e1.type));
			}
			if (e2.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e2.type));
			}
			type = "Int";
		}
	}
	public static class plus extends expression{
		public expression e1;
		public expression e2;
		public plus(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_plus\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		// Check if operands are int, return Int
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			e2.handle(errors, scopeTable, classTable);
			if (e1.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e1.type));
			}
			if (e2.type.equals("Int") == false) {
				errors.add(new Error(null, lineNo, "non-Int first argument "+e2.type));
			}
			type = "Int";
		}
	}
	public static class isvoid extends expression{
		public expression e1;
		public isvoid(expression v, int l){
			e1 = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_isvoid\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		// Return bool
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			type = "Bool";
		}
	}
	public static class new_ extends expression{
		public String typeid;
		public new_(String t, int l){
			typeid = t;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_new\n"+space+sp+typeid+"\n"+space+": "+type;
		}
		// Return the type of the newly created object
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			ClassInfo c = classTable.classinfos.get(typeid);
			if (c == null) {
				errors.add(new Error(null, lineNo, "Undefined class " + typeid));
				type = "Object";
			} else type = typeid;
		}
	}
	public static class assign extends expression{
		public String name;
		public expression e1;
		public assign(String n, expression v1, int l){
			name = n;
			e1 = v1;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_assign\n"+space+sp+name+"\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		//  CHeck if types conform
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			e1.handle(errors, scopeTable, classTable);
			attr a = scopeTable.lookUpGlobal(name);
			if (a == null) {
				errors.add(new Error(null, lineNo, "Undeclared varaible "+name));
			} else if (classTable.isAncestor(e1.type, a.typeid) == false) {
				errors.add(new Error(null, lineNo, a.typeid + " does not conform to " + e1.type));
			}
			type = e1.type;
		}
	}
	public static class block extends expression{
		public List<expression> l1;
		public block(List<expression> v1, int l){
			l1 = v1;
			lineNo = l;
		}
		String getString(String space){
			String str = space+"#"+lineNo+"\n"+space+"_block\n";
			for (expression e1 : l1){
				str += e1.getString(space+sp)+"\n";
			}
			str+=space+": "+type;
			return str;
		}
		// Return the last type for a block
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			for (expression e : l1)
				e.handle(errors, scopeTable, classTable);
			type = l1.get(l1.size() - 1).type;
		}
	}
	public static class loop extends expression{
		public expression predicate;
		public expression body;
		public loop(expression v1, expression v2, int l){
			predicate = v1;
			body = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_loop\n"+predicate.getString(space+sp)+"\n"+body.getString(space+sp)+"\n"+space+": "+type;
		}
		// Handle loop's body and return "Object"
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			predicate.handle(errors, scopeTable, classTable);
			if (predicate.type.equals("Bool") == false) {
				errors.add(new Error(null, lineNo, "Condition of loop is not of type Bool"));
			}
			body.handle(errors, scopeTable, classTable);
			type = "Object";
		}
	}
	public static class cond extends expression{
		public expression predicate;
		public expression ifbody;
		public expression elsebody;
		public cond(expression v1, expression v2, expression v3, int l){
			predicate = v1;
			ifbody = v2;
			elsebody = v3;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_cond\n"+predicate.getString(space+sp)+"\n"+ifbody.getString(space+sp)+"\n"+elsebody.getString(space+sp)+"\n"+space+": "+type;
		}
		// Handle if-else body and return commonAncestor
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			predicate.handle(errors, scopeTable, classTable);
			if (predicate.type.equals("Bool") == false) {
				errors.add(new Error(null, lineNo, "Condition of if is not of type Bool"));
			}
			ifbody.handle(errors, scopeTable, classTable);
			elsebody.handle(errors, scopeTable, classTable);
			type = classTable.commonAncestor(ifbody.type, elsebody.type);
		}
	}
	public static class let extends expression{
		public String name;
		public String typeid;
		public expression value;
		public expression body;
		public let(String n, String t, expression v, expression b, int l){
			name = n;
			typeid = t;
			value = v;
			body = b;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_let\n"+space+sp+name+"\n"+space+sp+typeid+"\n"+value.getString(space+sp)+"\n"+body.getString(space+sp)+"\n"+space+": "+type;
		}
		// handle let and Scope properly
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			if (value instanceof no_expr == false) {
				value.handle(errors, scopeTable, classTable);
				if (classTable.isAncestor(value.type, typeid) == false) {
					errors.add(new Error(null, lineNo, "Inferred type "+value.type+" does not conform to "+typeid));
				}
			}
			scopeTable.enterScope();
			scopeTable.insert(name, new attr(name, typeid, value, lineNo));
			body.handle(errors, scopeTable, classTable);
			type = body.type;
			scopeTable.exitScope();
		}
	}
	public static class dispatch extends expression{
		public expression caller;
		public String name;
		public List<expression> actuals;
		public dispatch(expression v1, String n, List<expression> a, int l){
			caller = v1;
			name = n;
			actuals = a;
			lineNo = l;
		}
		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_dispatch\n"+caller.getString(space+sp)+"\n"+space+sp+name+"\n"+space+sp+"(\n";
			for ( expression e1 : actuals ) {
				str += e1.getString(space+sp)+"\n";
			}
			str+=space+sp+")\n"+space+": "+type;
			return str;
		}
		// For dispatch
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			method m = null;
			type = "Object";
			caller.handle(errors, scopeTable, classTable);
			ClassInfo c = classTable.classinfos.get(caller.type);
			for (expression e : actuals) {
				e.handle(errors, scopeTable, classTable);
			}
			// Undefined class
			if (c == null) {
				errors.add(new Error(null, lineNo, "Undefined class " + caller.type));
			} else {
				if (c.methodlist.containsKey(name)) {
					m = c.methodlist.get(name);
					// wrong number of arguments
					if (actuals.size() != m.formals.size())
						errors.add(new Error(null, lineNo, m.name+" called with wrong number of arguments"));
					else {
					    // Parameter conform types?
						for (int i=0; i<actuals.size(); ++i) {
							String act = actuals.get(i).type;
							String form = m.formals.get(i).typeid;
							if (classTable.isAncestor(act, form) == false) {
								errors.add(new Error(null, lineNo, act+" does not conform to "+form));
							}
						}
					}
					type = m.typeid;
				} else {
				    // Method not found
					errors.add(new Error(null, lineNo, "dispatch to Undefined method "+name));
				}
			}
		}
	}
	public static class static_dispatch extends expression{
                public expression caller;
		public String typeid;
                public String name;
                public List<expression> actuals;
                public static_dispatch(expression v1, String t, String n, List<expression> a, int l){
                        caller = v1;
			typeid = t;
                        name = n;
                        actuals = a;
                        lineNo = l;
                }
                String getString(String space){
                        String str;
                        str = space+"#"+lineNo+"\n"+space+"_static_dispatch\n"+caller.getString(space+sp)+"\n"+space+sp+typeid+"\n"+space+sp+name+"\n"+space+sp+"(\n";
                        for ( expression e1 : actuals ) {
                                str += e1.getString(space+sp)+"\n";
                        }
                        str+=space+sp+")\n"+space+": "+type;
                        return str;
                }
        // For static dispatch
        void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			method m = null;
			type = "Object";
			caller.handle(errors, scopeTable, classTable);
			ClassInfo c = classTable.classinfos.get(typeid);
			for (expression e : actuals) {
				e.handle(errors, scopeTable, classTable);
			}
			// Undefined class
			if (c == null) {
				errors.add(new Error(null, lineNo, "Static dispatch to undefined class " + typeid));
			} else if (classTable.isAncestor(caller.type, typeid) == false) {
				errors.add(new Error(null, lineNo, caller.type+" does not conform to "+typeid));
			} else {
				if (c.methodlist.containsKey(name)) {
					m = c.methodlist.get(name);
					// wrong number of arguments
					if (actuals.size() != m.formals.size())
						errors.add(new Error(null, lineNo, m.name+" called with wrong number of arguments"));
					else {
					    // Parameter conform types?
						for (int i=0; i<actuals.size(); ++i) {
							String act = actuals.get(i).type;
							String form = m.formals.get(i).typeid;
							if (classTable.isAncestor(act, form) == false) {
								errors.add(new Error(null, lineNo, act+" does not conform to "+form));
							}
						}
					}
					type = m.typeid;
				} else {
				    // Method not found
					errors.add(new Error(null, lineNo, "Static dispatch to undefined method "+name));
				}
			}
		}
    }
	public static class typcase extends expression{
		public expression predicate;
		public List<branch> branches;
		public typcase(expression p, List<branch> b, int l){
			predicate = p;
			branches = b;
			lineNo = l;
		}
		String getString(String space){
			String str = space+"#"+lineNo+"\n"+space+"_typcase\n"+predicate.getString(space+sp)+"\n";
			for ( branch b1 : branches ) {
				str += b1.getString(space+sp)+"\n";
			}
			str += space+": "+type;
			return str;
		}
		// Handle the case
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			predicate.handle(errors, scopeTable, classTable);
			for (branch e : branches) {
				scopeTable.enterScope();
				ClassInfo c = classTable.classinfos.get(e.type);
				if (c == null) {
					errors.add(new Error(null, lineNo, "Undefined class "+e.type));
					scopeTable.insert(e.name, new attr(e.name, "Object", e.value, e.lineNo));
				} else {
					scopeTable.insert(e.name, new attr(e.name, e.type, e.value, e.lineNo));
				}
				e.value.handle(errors, scopeTable, classTable);
				scopeTable.exitScope();
			}
			// return the common type
			String t = branches.get(0).value.type;
			List<String> branchTypes = new ArrayList<>();
			for (branch br : branches) {
				if (branchTypes.contains(br.type) == false)
					branchTypes.add(br.type);
				else
					errors.add(new Error(null, lineNo, br.type+" is multiply branched in case statement"));
				t = classTable.commonAncestor(t, br.value.type);
			}
			type = t;
		}
	}
	public static class branch extends ASTNode {
		public String name;
		public String type;
		public expression value;
		public branch(String n, String t, expression v, int l){
			name = n;
			type = t;
			value = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_branch\n"+space+sp+name+"\n"+space+sp+type+"\n"+value.getString(space+sp);
		}
	}
	public static class formal extends ASTNode {
		public String name;
		public String typeid;
		public formal(String n, String t, int l){
			name = n;
			typeid = t;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_formal\n"+space+sp+name+"\n"+space+sp+typeid;
		}
	}
	public static abstract class feature extends ASTNode {
		public feature(){
		}
		String getString(String space){
			return "";
		}
		abstract void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable);
	}
	public static class method extends feature {
		public String name;
		public List<formal> formals;
		public String typeid;
		public expression body;
		public method(String n, List<formal> f, String t, expression b, int l){
			name = n;
			formals = f;
			typeid = t;
			body = b;
			lineNo = l;
		}
		String getString(String space){
			String str = space+"#"+lineNo+"\n"+space+"_method\n"+space+sp+name+"\n";
			for ( formal f : formals ) {
				str += f.getString(space+sp)+"\n";
			}
			str += space+sp+typeid+"\n"+body.getString(space+sp);
			return str;
		}
		// Scope and check if return type is valid
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			scopeTable.enterScope();
			for (formal f : formals) {
				scopeTable.insert(f.name, new attr(f.name, f.typeid, new no_expr(f.lineNo), lineNo));
			}
			body.handle(errors, scopeTable, classTable);
			if (classTable.isAncestor(body.type, typeid) == false) {
				errors.add(new Error(null, body.lineNo, "Return type " + typeid + " of method "
							+ name + " does not conform to the infered method body type " + body.type));
			}
			scopeTable.exitScope();
		}
	}
	public static class attr extends feature {
		public String name;
		public String typeid;
		public expression value;
		public attr(String n, String t, expression v, int l){
			name = n;
			typeid = t;
			value = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_attr\n"+space+sp+name+"\n"+space+sp+typeid+"\n"+value.getString(space+sp);
		}
		// CHeck for type conformance
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			if (value instanceof no_expr == false) {
				value.handle(errors, scopeTable, classTable);
				if (classTable.isAncestor(value.type, typeid) == false) {
					errors.add(new Error(null, value.lineNo, "Declared type " + typeid + " of attribute "
							+ name + " does not conform to the infered type " + value.type));
				}
			}
		}
	}
	public static class class_ extends ASTNode {
		public String name;
		public String filename;
		public String parent;
		public List<feature> features;
		public class_(String n, String f, String p, List<feature> fs, int l){
			name = n;
			filename = f;
			parent = p;
			features = fs;
			lineNo = l;
		}
		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_class\n"+space+sp+name+"\n"+space+sp+parent+"\n"+space+sp+"\""+filename+"\""+"\n"+space+sp+"(\n";
			for ( feature f : features ) {
				str += f.getString(space+sp)+"\n";
			}
			str += space+sp+")";
			return str;
		}
		// For each feature call handle
		void handle(List<Error> errors, ScopeTable<attr> scopeTable, ClassTable classTable) {
			for (feature f : features) {
				f.handle(errors, scopeTable, classTable);
			}
		}
	}
	public static class program extends ASTNode {
		public List<class_> classes;
		public program(List<class_> c, int l){
			classes = c;
			lineNo = l;
		}
		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_program";
			for ( class_ c : classes ) {
				str += "\n"+c.getString(space+sp);
			}

			return str;
		}
	}
}
