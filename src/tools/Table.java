package tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Table {

	Connector conn;
	String name;
	Map<String, ColumnDataHandler> handlers;

	public Table(Connector conn, String name) {
		this.conn = conn;
		this.name = name;
		handlers = new HashMap<>();
	}

	public List<String> getColumns() {
		List<String> cols = new ArrayList<String>();
		Connection dbcon = null;
		try {
			dbcon = this.conn.getConnection();
			DatabaseMetaData dbmd = dbcon.getMetaData();
			ResultSet rs = dbmd.getColumns(null, null, name, "%");

			while (rs.next()) {
				String colname = rs.getString("COLUMN_NAME");
				cols.add(colname);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
		return cols;
	}

	public List<Map<String, Object>> getData() {
		List<Map<String, Object>> data = new ArrayList<>();
		Connection dbcon = null;
		try {
			String sql = "select * from " + this.name;
			dbcon = this.conn.getConnection();
			PreparedStatement psmt = dbcon.prepareStatement(sql);
			ResultSet rs = psmt.executeQuery();

			List<String> cols = getColumns();

			while (rs.next()) {
				Map<String, Object> row = new HashMap<>();
				for (String colname : cols) {
					row.put(colname, rs.getObject(colname));
				}
				data.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}

		return data;
	}

	public void registerDataConvertor(ColumnDataHandler handler) {
		handlers.put(handler.getColumnName(), handler);
	}

	public void fetchData(Table fromTable) {
		List<String> columns = getColumns();

		String insertSql = buildInsertSql(columns);

		List<Map<String, Object>> data = fromTable.getData();

		Connection dbcon = null;
		try {

			dbcon = this.conn.getConnection();
			PreparedStatement psmt = dbcon.prepareStatement(insertSql);

			for (Map<String, Object> row : data) {
				try {
					populateValue(columns, row, psmt);

					System.out.println("==> " + psmt);
					int count = psmt.executeUpdate();
					if (count <= 0) {
						System.err.println("Fail to run sql: " + psmt);
					}
				} catch (SQLException e) {
					// ignore the Duplicate entry key 'PRIMARY' error.
					e.printStackTrace();
				}

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
	}

	private void populateValue(List<String> columns, Map<String, Object> row,
			PreparedStatement psmt) throws SQLException {
		int idx = 1;
		for (String colname : columns) {
			// handling base on dst table columns

			Object value = null;

			if (handlers.containsKey(colname)) {
				// the column value need converting
				ColumnDataHandler dc = handlers.get(colname);
				// TODO more information needed by converting
				value = dc.convert(row);

			} else if (row.containsKey(colname)) {
				// src column and dst column both exist
				value = row.get(colname);

			} else {
				System.err.println("Don't know how to give value to column "
						+ colname);
			}

			psmt.setObject(idx, value);
			idx++;
		}
	}

	private String buildInsertSql(List<String> columns) {
		String preSql = "INSERT INTO " + this.name + "(" + columns.get(0);
		String param = "?";
		for (int i = 1; i < columns.size(); i++) {
			preSql += "," + columns.get(i);
			param += ",?";
		}
		preSql += ") VALUES(" + param + ")";
		return preSql;
	}
}