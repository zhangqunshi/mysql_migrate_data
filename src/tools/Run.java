package tools;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Run {

	static String src_db_name;
	static String dst_db_name;

	static {
		Properties p = new Properties();
		try {
			InputStream in = Object.class.getResourceAsStream("/db.conf");
			p.load(in);
			src_db_name = p.getProperty("src_db_name");
			dst_db_name = p.getProperty("dst_db_name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException {
		Connector src = new Connector(src_db_name);
		Connector dst = new Connector(dst_db_name);

		// Schema s = new Schema(src);
		// List<String> tables = s.getTables();
		//
		// int idx = 1;
		//
		// for (String tableName : tables) {
		// Table t = new Table(src, tableName);
		// List<String> cols = t.getColumns();
		// System.out.println(idx + ": " + tableName + "==>" + cols);
		// // List<Map<String, Object>> data = t.getData();
		// // System.out.println("==>" + data);
		// idx++;
		// }

		Table t1 = new Table(src, "administrator");
		Table t2 = new Table(dst, "administrator");
		t2.fetchData(t1);

		System.out.println("END");
	}

}
