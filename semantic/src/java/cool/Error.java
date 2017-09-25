package cool;

// Error class to store intermediate errors that can occur during semantic analysis
// The errors collected are passes to Semantic Class to report
public class Error {
	public String filename;
	public int lineNo;
	public String err;
	Error(String f, int l, String er) {
		filename = f;
		lineNo = l;
		err = er;
	}
}
