package tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Config {

	static String src_db_name;
	static String dst_db_name;

	static List<String> ignore_tables = new ArrayList<>();

	static {
		Properties p = new Properties();
		try {
			InputStream in = Object.class.getResourceAsStream("/db.conf");
			p.load(in);
			src_db_name = p.getProperty("src_db_name");
			dst_db_name = p.getProperty("dst_db_name");

			String ignoreTables = p.getProperty("ignore_tables");

			if (ignoreTables != null && !"".equals(ignoreTables.trim())) {

				String[] ignores = ignoreTables.trim().split(",");

				for (String tableName : ignores) {
					if ("".equals(tableName.trim())) {
						continue;
					}
					ignore_tables.add(tableName.trim());
				}

			}

			System.out.println("src db: " + src_db_name);
			System.out.println("dst db: " + dst_db_name);
			System.out.println("ignore tables: " + ignore_tables);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}