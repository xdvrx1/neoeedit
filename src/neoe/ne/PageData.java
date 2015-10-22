package neoe.ne;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neoe.ne.U.History;

/** Text data stores here. */
public class PageData {

	static Map<String, PageData> dataPool = new HashMap<String, PageData>();

	public static PageData newEmpty(String title) {
		return newEmpty(title, "empty");
	}

	public static PageData newEmpty(String title, String initStr) {
		PageData pd = dataPool.get(title);
		if (pd != null)
			return pd;
		pd = new PageData();
		pd.title = title;
		pd.lines = new ArrayList<CharSequence>();
		pd.lines.add(initStr);
		dataPool.put(title, pd);
		return pd;
	}

	public static PageData newFromFile(String fn) throws IOException {
		PageData pd = dataPool.get(fn);
		if (pd != null)
			return pd;
		pd = new PageData();
		pd.fn = fn;
		U.readFile(pd, fn);
		U.saveFileHistory(fn, 1);
		dataPool.put(fn, pd);
		return pd;
	}

	U.BasicEdit editNoRec = new U.BasicEdit(false, this);

	U.BasicEdit editRec = new U.BasicEdit(true, this);

	String encoding;

	long fileLastModified;

	private String fn;// private!
	U.History history;
	boolean isCommentChecked;

	/* element: String or StringBuilder(after edit) */
	public List<CharSequence> lines;
	public String lineSep = "\n";

	public int ref;
	U.ReadonlyLines roLines = new U.ReadonlyLines(this);
	private String title;// private!

	String workPath;

	private PageData() {
		history = new History(this);
	}

	public void close() {
		dataPool.remove(getTitle());
		System.out.println("released data " + getTitle());
	}

	public String getFn() {
		return fn;
	}

	public String getTitle() {
		if (title != null)
			return title;
		return fn;
	}

	public void setFn(String fn2) {
		String key = getTitle();
		dataPool.remove(key);
		title = null;
		fn = fn2;
		workPath = new File(fn).getParent();
		dataPool.put(fn2, this);
	}

	void setLines(List<CharSequence> newLines) {
		lines = newLines;
		history.clear();
	}

	public void setText(String s) {
		List<CharSequence> ss = U.removeTailR(U.split(s.toCharArray(), U.N));
		if (ss.size() == 0) {
			ss.add("empty");
		}
		setLines(ss);
	}
}
