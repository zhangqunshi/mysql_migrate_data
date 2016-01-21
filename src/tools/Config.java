package tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Config {

	static String src_db_name;
	static String dst_db_name;

	static List<String> ignore_tables = new ArrayList<>();
	static List<String> include_tables = new ArrayList<>();

	static {
		Properties p = new Properties();
		try {
			InputStream in = Object.class.getResourceAsStream("/db.conf");
			p.load(in);
			src_db_name = p.getProperty("src_db_name");
			dst_db_name = p.getProperty("dst_db_name");

			setList(p, "ignore_tables", ignore_tables);
			setList(p, "include_tables", include_tables);

			System.out.println("src db: " + src_db_name);
			System.out.println("dst db: " + dst_db_name);
			System.out.println("ignore tables: " + ignore_tables);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void setList(Properties p, String key, List<String> list) {
		String values = p.getProperty(key);

		if (values != null && !"".equals(values.trim())) {

			String[] v = values.trim().split(",");

			for (String one : v) {
				if ("".equals(one.trim())) {
					continue;
				}
				list.add(one.trim());
			}

		}
	}

}
