package sfsecurity.util;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;
import sfsecurity.util.FileParser;

public class TestFileParser {
	@Test
	public void testBlankFile() throws IOException {
		FileParser fp = new FileParser(new FileReader("test_cases/empty.txt"));
		ArrayList<String> lines = fp.getLines();
		assertTrue(lines.isEmpty());
		fp.close();
		fp = new FileParser(new FileReader("test_cases/empty_comments.txt"));
		lines = fp.getLines();
		assertTrue(lines.isEmpty());
		fp.close();
	}
	
	@Test
	public void testBasicParsing() throws IOException {
		FileParser fp = new FileParser(new FileReader("test_cases/ip_parse.txt"));
		ArrayList<String> lines = fp.getLines();
		assertTrue(lines.size() == 2);
		assertTrue(lines.get(0).equals("192.168.1.107"));
		assertTrue(lines.get(1).equals("127.0.0.1"));
		fp.close();
	}
}
