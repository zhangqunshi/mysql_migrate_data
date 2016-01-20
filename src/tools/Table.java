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

	public Table(Connector conn, String name) {
		this.conn = conn;
		this.name = name;
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

	public void fetchData(Table fromTable) throws SQLException {
		List<String> columns = getColumns();

		String preSql = "INSERT INTO " + this.name + "(" + columns.get(0);
		for (int i = 1; i < columns.size(); i++) {
			preSql += "," + columns.get(i);
		}
		preSql += ") VALUES(";

		List<Map<String, Object>> data = fromTable.getData();

		Connection dbcon = null;
		try {
			for (Map<String, Object> row : data) {

				StringBuilder sb = new StringBuilder(preSql);
				for (String colname : columns) {
					if (row.containsKey(colname)) {
						Object value = row.get(colname);

						sb.append(value).append(",");
					} else {
						// TODO use column plugin to get data
					}
				}
				String insertSql = sb.substring(0, sb.length() - 1) + ")";

				System.out.println("==> " + insertSql);

				dbcon = this.conn.getConnection();
				PreparedStatement psmt = dbcon.prepareStatement(insertSql);
				int count = psmt.executeUpdate();
				if (count <= 0) {
					System.err.println("Fail to run sql: " + insertSql);
				}

			}
		} finally {
			this.conn.close(dbcon);
		}
	}
}