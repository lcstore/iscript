package com.lezo.iscript.yeam.defend.dirs;

import java.io.File;

import org.junit.Test;

public class DirsUtilsTest {

	@Test
	public void testClearDirs() {
		File dir = new File("D:/lezo/iscript/trunk/iscript-yeam/iscript-yeam-client/target/classes");
		DirsUtils.clearDirs(dir);
	}
}
