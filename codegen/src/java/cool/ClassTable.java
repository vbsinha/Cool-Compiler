package cool;
import java.util.*;
import java.util.Map.Entry;

public class ClassTable{
    // A Map form name of class to ClassInfo 
	public HashMap <String, ClassInfo> classinfos;

	public ClassTable(){
		classinfos = new HashMap <String, ClassInfo>();
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
