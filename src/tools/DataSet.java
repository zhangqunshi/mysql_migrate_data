package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSet {

	List<Row> rows;
	private Table tbl;

	public DataSet(Table table) {
		rows = new ArrayList<>();
		this.tbl = table;
	}

	public void add(Row row) {
		row.setAction("insert"); // default is insert
		rows.add(row);
	}

	/**
	 * The most complex logic: <br>
	 * 1) src (exist) --> dst (not exist): insert <br>
	 * 2) src (not exist) --> dst (exist): skip (keep data) <br>
	 * 3) src (exist) --> dst (exist): update <br>
	 * 
	 * @param oldData
	 */
	public void merge(DataSet srcData) {
		for (Row srcRow : srcData.getRows()) {

			// TODO user can define the PK columns
			Row dstRow = findByPK(srcRow.getPKValue());

			if (dstRow != null) {

				if (dstRow.isSame(srcRow)) {
					// if there is a same row in current set, then flag = skip
					dstRow.setAction("skip");

				} else {
					// if exist, but not same, then flag = update
					dstRow.updateValues(srcRow);
					dstRow.setAction("update");
				}

				dstRow.apply(this.tbl.getColumnDataHandler());

			} else {

				Row newDstRow = srcRow.cloneData(this.tbl);
				newDstRow.apply(this.tbl.getColumnDataHandler());

				// if not exist, then insert
				this.add(newDstRow);
			}

		} // end for

	}

	private Row findByPK(PrimaryKey pk) {
		for (Row row : rows) {
			if (row.getPKValue().equals(pk)) {
				// primary key should be only one
				return row;
			}
		}
		return null;
	}

	private List<Row> getRows() {
		return this.rows;
	}

	/**
	 * Merge the sql and its params for PrepareStatement execute
	 * 
	 * @return
	 */
	public Map<String, List<Object[]>> generateSqlAndParams() {

		Map<String, List<Object[]>> sqls = new HashMap<>();

		for (Row row : rows) {
			SqlParams sp = row.generateSqlParams();
			if (sp == null) {
				System.out.println("Skip==> " + row);
				continue;
			}

			List<Object[]> paramsList;

			String sql = sp.getSql();
			if (sqls.containsKey(sql)) {
				paramsList = sqls.get(sql);
			} else {
				paramsList = new ArrayList<>();
				sqls.put(sql, paramsList);
			}

			paramsList.add(sp.getParams());

		}
		return sqls;
	}

	@Override
	public String toString() {
		return "DataSet [rows=" + rows + ", tbl=" + tbl + "]";
	}

}
