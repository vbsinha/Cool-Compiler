package cool;
import java.util.*;

// ClassInfo Class to maintain parent, depth, attribute Map, attribute list, method Map, method Name Map and byte size for a class
// Depth in the tree is distance between Object node and this class in inheritance tree of the graph
// Size is the amount of bytes taken to store an object of this class
// Method Name Map maps the actual name of function to the name in LLVM IR 
public class ClassInfo {
	public String parent = null;
	public int depth = 0;
	public List <String> attrList;
	public HashMap <String, AST.attr> attrMap;
	public HashMap <String, AST.method> methodMap;
	public HashMap <String, String> methodName;
	public int size = 0;

	ClassInfo(String par, HashMap<String, AST.attr> alist, HashMap<String, AST.method> mlist, HashMap<String, String> mn, int d, int s) {
		parent = par;
		attrMap = new HashMap <String, AST.attr>();
		attrMap.putAll(alist);
		methodMap = new HashMap <String, AST.method>();
		methodMap.putAll(mlist);
		methodName = new HashMap <String, String>();
		methodName.putAll(mn);
		attrList = new ArrayList <String>();
		if (par != null) {
			attrList.add("_Par");
		}
		depth = d;
		size = s;
	}
}
