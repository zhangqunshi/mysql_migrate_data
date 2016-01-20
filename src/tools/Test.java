package tools;

import java.util.List;

/**
 * Test Case
 * 
 * @author zhangqunshi@126.com
 *
 */
public class Test {

	public static void test_getTables() {
		Connector src = new Connector(Run.src_db_name);
		Schema s = new Schema(src);
		List<String> t = s.getTables();
		System.out.println("==> " + t);
	}

	public static void main(String[] args) {
		test_getTables();
	}

}
