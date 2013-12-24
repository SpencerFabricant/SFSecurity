package sfsecurity.util;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sfsecurity.util.FileParser;

public class TestFileParser {
	
	@Test
	public void testBasicParsing() throws IOException {
		FileParser fp = new FileParser(new FileReader("test_cases/ip_parse.txt"));
		ArrayList<String> lines = fp.getLines();
		if (lines.size() != 2) {
			fail();
		}
		if (!lines.get(0).equals("192.168.1.107")) fail();
		if (!lines.get(1).equals("127.0.0.1")) fail();
	}
}
