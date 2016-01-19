package tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}