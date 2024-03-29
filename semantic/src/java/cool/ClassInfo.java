package cool;
import java.util.HashMap;

// ClassInfo Class to maintain parent, depth, attribute list and method list for a class
// Depth in the tree is distance between Object node and this class in inheritance tree of the graph
public class ClassInfo {
	public String parent = null;
	public int depth = 0;
	public HashMap <String, AST.attr> attrlist;
	public HashMap <String, AST.method> methodlist;

	ClassInfo(String par, HashMap<String, AST.attr> alist, HashMap<String, AST.method> mlist, int d) {
		parent = par;
		attrlist = new HashMap <String, AST.attr>();
		attrlist.putAll(alist);
		methodlist = new HashMap <String, AST.method>();
		methodlist.putAll(mlist);
		depth = d;
	}
}
