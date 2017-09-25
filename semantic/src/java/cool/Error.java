package cool;

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
