package tools;

import java.util.List;

import tools.handler.CurrentDate;
import tools.handler.IntegerValue;
import tools.handler.NullValue;

public class Run {

	public static void main(String[] args) {
		Connector src = new Connector(Config.src_db_name);
		Connector dst = new Connector(Config.dst_db_name);

		Schema srcSchema = new Schema(src);
		Schema dstSchema = new Schema(dst);
		List<String> srcTables = srcSchema.getTables();
		List<String> dstTables = dstSchema.getTables();

		int idx = 1;

		for (String tableName : dstTables) {
			if (!Config.include_tables.isEmpty()
					&& !Config.include_tables.contains(tableName)) {
				continue;
			}

			if (Config.ignore_tables.contains(tableName)) {
				continue;
			}

			if (!srcTables.contains(tableName)) {
				continue;
			}

			System.out.println(idx + ": " + tableName);

			Table t1 = new Table(src, tableName);
			Table t2 = new Table(dst, tableName);

			if (tableName.equals("t1")) {
				t2.addColumnHandler("err_msg", new NullValue());
			}

			if (tableName.equals("t2")) {
				t2.addColumnHandler("lastsynctime", new CurrentDate());
			}

			if (tableName.equals("t3")) {
				t2.clearData();
			}

			if (tableName.equals("t4")) {
				// update t4 set c1=1,c2=2;
				t2.addColumnHandler("c1", new IntegerValue(1));
				t2.addColumnHandler("c2", new IntegerValue(2));
			}

			t2.fetchData(t1);

			idx++;
		}

		System.out.println("END");
	}

}
