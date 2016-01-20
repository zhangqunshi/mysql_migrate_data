package tools;

import java.util.List;

public class Run {

	public static void main(String[] args) {
		Connector src = new Connector(Config.src_db_name);
		Connector dst = new Connector(Config.dst_db_name);

		Schema srcdb = new Schema(src);
		Schema dstdb = new Schema(dst);
		List<String> srcTables = srcdb.getTables();
		List<String> dstTables = dstdb.getTables();

		int idx = 1;

		for (String tableName : dstTables) {
			if (Config.ignore_tables.contains(tableName)) {
				continue;
			}

			if (!srcTables.contains(tableName)) {
				continue;
			}

			System.out.println(idx + ": " + tableName);

			Table t1 = new Table(src, tableName);
			Table t2 = new Table(dst, tableName);
			t2.fetchData(t1);

			idx++;
		}

		System.out.println("END");
	}

}
