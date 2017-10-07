package cool;
import cool.AST.*;
import java.util.*;

class ClassScope {

	// Class Name
	String name;

	// List of attributes in the class
	public HashMap<String, AST.attr> members;

	// List of methods in the class
	public HashMap<String, AST.method> methodTables;

	/**
	 * Create the method tables and members list
	 * @param  c    class_ object
	 * @param  self semantic object
	 */
	public ClassScope(AST.class_ c, Semantic self) {
		this.name = c.name;
		members = new HashMap<String, AST.attr>();
		methodTables = new HashMap<String, AST.method>();

		for (AST.feature f : c.features) {
			if (f instanceof method) {
				AST.method m = (method)f;
				if (methodTables.keySet().contains(m.name)) {
					self.reportError(self.fileName, m.lineNo, "Method '"+ m.name +"' has been redefined");
				}
				methodTables.put(m.name, m);
			}
			else {
				AST.attr a = (attr)f;
				if (members.keySet().contains(a.name)) {
					self.reportError(self.fileName, a.lineNo, "Attribute '"+ a.name +"' has been redefined");
				}
				members.put(a.name, a);
			}
		}

	}

}