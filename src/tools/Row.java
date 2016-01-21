package tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.handler.ColumnDataHandler;

public class Row {

	/**
	 * Map<key is column name, value is column value>
	 */
	Map<String, Object> row;
	String action;
	Table tlb;
	Set<String> pkNames;

	public Row(Table table) {
		this.tlb = table;
		row = new HashMap<>();
	}

	public void put(String colName, Object colValue) {
		row.put(colName, colValue);
	}

	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * check if both row data totally same.
	 * 
	 * @param otherRow
	 * @return
	 */
	public boolean isSame(Row otherRow) {
		Iterator<String> it = row.keySet().iterator();
		while (it.hasNext()) {
			String columnName = it.next();

			Object v1 = getValue(columnName);
			Object v2 = otherRow.getValue(columnName);

			if (v1 == null) {
				if (v2 == null) {
					continue;
				}
				return false;
			}

			if (!v1.equals(v2)) {
				return false;
			}
		}
		return true;
	}

	public Object getValue(String columnName) {
		return row.get(columnName);
	}

	/**
	 * Update this row data by given row
	 * 
	 * @param otherRow
	 */
	public void updateValues(Row otherRow) {

		Iterator<String> it = row.keySet().iterator();
		while (it.hasNext()) {
			String k = it.next();

			Object v = otherRow.getValue(k);
			if (v == null) {
				continue;
			}

			row.put(k, v);
		}

	}

	public SqlParams generateSqlParams() {
		List<String> columns = this.tlb.getColumns();

		String sql = "";
		Object[] params = null;

		if (this.action == "insert") {
			sql = "INSERT INTO " + this.tlb.getTableName() + "(";
			String mark = "";
			int len = columns.size();
			params = new Object[len];

			for (int i = 0; i < len; i++) {
				String colname = columns.get(i);
				if (i == len - 1) {
					sql += colname;
					mark += "?";
				} else {
					sql += colname + ",";
					mark += "?,";
				}
				params[i] = row.get(colname);
			}

			sql += ") VALUES(" + mark + ")";

		} else if (this.action.equals("update")) {

			List<String> pkNames = tlb.getPrimaryKey();

			sql = "UPDATE " + this.tlb.getTableName() + " SET ";
			int len = columns.size();
			params = new Object[len];

			int idx = 0;
			for (String colname : columns) {
				if (pkNames.contains(colname)) {
					// skip the primary key
					continue;
				}
				sql += colname + "=?,";
				params[idx] = row.get(colname);
				idx++;
			}
			sql = sql.substring(0, sql.length() - 1);
			sql += " WHERE ";

			for (String col : pkNames) {
				sql += col + "=?,";
				params[idx] = row.get(col);
				idx++;
			}
			sql = sql.substring(0, sql.length() - 1);

		} else if (this.action.equals("skip")) {
			return null;
		}

		return new SqlParams(sql, params);
	}

	public PrimaryKey getPKValue() {
		List<String> pkNames = tlb.getPrimaryKey();
		Map<String, Object> m = new HashMap<>();
		for (String pkName : pkNames) {
			m.put(pkName, row.get(pkName));
		}

		PrimaryKey pk = new PrimaryKey(m);
		return pk;
	}

	/**
	 * Apply all handlers of this table to this row, in order to change the
	 * column value by user defined value.
	 * 
	 * @param handlers
	 */
	public void apply(Map<String, ColumnDataHandler> handlers) {
		for (String column : handlers.keySet()) {
			ColumnDataHandler handler = handlers.get(column);
			Object newColumnValue = handler.convert(this);
			row.put(column, newColumnValue);
		}
	}

	@Override
	public String toString() {
		return "Row [row=" + row + ", action=" + action + ", tlb=" + tlb
				+ ", pkNames=" + pkNames + "]";
	}

	public Row cloneData(Table tbl) {
		Row r = new Row(tbl);

		for (String col : row.keySet()) {
			r.put(col, row.get(col));
		}
		return r;
	}

}
