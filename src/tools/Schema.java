package tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Schema {

	Connector conn;
	String name;

	public Schema(Connector conn, String name) {
		this.conn = conn;
		this.name = name;
	}

	public List<String> getTables() {
		List<String> tables = new ArrayList<String>();
		Connection dbcon = null;
		try {
			dbcon = this.conn.getConnection();
			DatabaseMetaData dbmd = dbcon.getMetaData();
			ResultSet rs = dbmd.getTables(null, this.name, "%", null);

			while (rs.next()) {
				String colname = rs.getString("TABLE_NAME");
				tables.add(colname);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
		return tables;
	}

}
