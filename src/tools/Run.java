package tools;

import java.util.List;

public class Run {

	public static void main(String[] args) {
		Connector src = new Connector(Config.src_db_name);
		Connector dst = new Connector(Config.dst_db_name);

		Schema s = new Schema(dst);
		List<String> tables = s.getTables();

		int idx = 1;

		for (String tableName : tables) {
			if (Config.ignore_tables.contains(tableName)) {
				continue;
			}

			Table t = new Table(src, tableName);
			List<String> cols = t.getColumns();
			System.out.println(idx + ": " + tableName + "==>" + cols);

			Table t1 = new Table(src, tableName);
			Table t2 = new Table(dst, tableName);
			t2.fetchData(t1);

			idx++;
		}

		System.out.println("END");
	}

}
