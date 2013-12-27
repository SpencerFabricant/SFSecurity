package sfsecurity.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class FileParser extends BufferedReader {
	
	public static ArrayList<String> readFile(String filename) throws IOException {
		FileParser fp = null;
		ArrayList<String> ret = new ArrayList<String>();
		try {
			fp = new FileParser(new BufferedReader(new FileReader(filename)));
			ret = fp.getLines();
		} catch (IOException e) {
			throw e;
		} finally {
			if (fp != null)
				fp.close();
		}
		return ret;
	}
	public FileParser(Reader reader) {
		super(reader);
	}
	
	/**
	 * Gets the next line that is not either blank or commented out.
	 * Cuts out anything commented out.
	 * Comments begin with "#"
	 */
	public String readLine() throws IOException {
		String s = super.readLine();
		while (s != null &&
			   (s.trim().equals("")
			    || s.trim().startsWith("#")) ) {
			s = super.readLine();
		}
		if (s == null) return null;
		
		if (s.contains("#")) {
			int i = s.indexOf("#");
			s = s.substring(0, i);
		}
		s = s.trim();
		return s;
	}
	
	/**
	 * Gets all lines and closes the file
	 * @throws IOException
	 */
	public ArrayList<String> getLines() throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		String s = this.readLine();
		while(s != null) {
			lines.add(s);
			s = this.readLine();
		}
		return lines;
	}
	
	/**
	 * closes the file without demanding a try/catch.
	 */
	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
