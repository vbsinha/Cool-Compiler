package cool;
import java.util.HashMap;

public class ClassInfo {
	public String parent = null;
	public int depth = 0;
	public HashMap <String, AST.attr> attrlist;
	public HashMap <String, AST.method> methodlist;
	
	ClassInfo(String par, HashMap<String, AST.attr> alist, HashMap<String, AST.method> mlist, int d) {
		if(par != null)
		    parent = new String(par);
		
		attrlist = new HashMap <String, AST.attr>();
		attrlist.putAll(alist);
		//attrlist = alist;
		
		methodlist = new HashMap <String, AST.method>();
		methodlist.putAll(mlist);
		//methodlist = mlist;
		
		depth = d;
	}
}
