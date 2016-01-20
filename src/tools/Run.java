package tools;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Run {

	static String srcdb_url;
	static String dstdb_url;

	static {
		Properties p = new Properties();
		try {
			InputStream in = Object.class.getResourceAsStream("/db.conf");
			p.load(in);
			srcdb_url = p.getProperty("srcdb_url");
			dstdb_url = p.getProperty("dstdb_url");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Connector c = new Connector(srcdb_url);

		// Table t = new Table(c, "desktop");
		// List cols = t.getColumns();
		// System.out.println("==>" + cols);
		// List data = t.getData();
		// System.out.println("==>" + data);

		Schema s = new Schema(c, "test1");
		List tables = s.getTables();
		System.out.println("==>" + tables);
	}

}
