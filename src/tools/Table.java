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

import tools.handler.ColumnDataHandler;

class Table {

	Connector conn;

	/**
	 * the name of this table
	 */
	String name;

	/**
	 * key is column name, value is Handler class
	 */
	Map<String, ColumnDataHandler> handlers;

	/**
	 * a list of column names of this table
	 */
	List<String> columnNames;
	/**
	 * a list of primary key column names
	 */
	List<String> PKCols;

	String dbname;

	public Table(Connector conn, String name) {
		this.conn = conn;
		this.name = name;
		this.dbname = conn.getDBName();
		handlers = new HashMap<>();
		PKCols = new ArrayList<>();
		columnNames = new ArrayList<String>();
	}

	/**
	 * Once query, the column name will not change for this table
	 * 
	 * @return
	 */
	public List<String> getColumns() {
		// firstly get from cache
		if (!this.columnNames.isEmpty()) {
			return this.columnNames;
		}

		Connection dbcon = null;
		try {
			dbcon = this.conn.getConnection();
			DatabaseMetaData dbmd = dbcon.getMetaData();
			ResultSet rs = dbmd.getColumns(null, this.dbname, this.name, "%");

			while (rs.next()) {
				String colname = rs.getString("COLUMN_NAME");
				columnNames.add(colname);
			}

		} catch (Exception e) {
			columnNames.clear();
			e.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
		return columnNames;
	}

	/**
	 * If this table hasn't primary key, then use all columns
	 * 
	 * @return a list of column names
	 */
	public List<String> getPrimaryKey() {
		if (!this.PKCols.isEmpty()) {
			return this.PKCols;
		}

		Connection dbcon = null;
		try {
			dbcon = this.conn.getConnection();
			DatabaseMetaData dbmd = dbcon.getMetaData();
			ResultSet rs = dbmd.getPrimaryKeys(null, this.dbname, this.name);

			while (rs.next()) {
				String colname = rs.getString("COLUMN_NAME");
				PKCols.add(colname);
			}

			if (PKCols.isEmpty()) {
				// if not found primary key in this table, then user all
				// columns;
				PKCols.addAll(getColumns());
			}

		} catch (Exception e) {
			PKCols.clear();
			e.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
		return this.PKCols;
	}

	public DataSet getData() {
		DataSet data = new DataSet(this);
		Connection dbcon = null;
		try {
			String sql = "SELECT * FROM " + this.name;
			dbcon = this.conn.getConnection();
			PreparedStatement psmt = dbcon.prepareStatement(sql);
			ResultSet rs = psmt.executeQuery();

			List<String> cols = getColumns();

			while (rs.next()) {
				Row row = new Row(this);
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

	public void addColumnHandler(String column, ColumnDataHandler handler) {
		handlers.put(column, handler);
	}

	public void fetchData(Table fromTable) {

		DataSet src_data = fromTable.getData();
		DataSet dst_data = this.getData();

		dst_data.merge(src_data);
		Map<String, List<Object[]>> sqlParams = dst_data.generateSqlAndParams();

		Connection dbcon = null;
		try {
			dbcon = this.conn.getConnection();

			// One sql mapping to a list of params
			for (String sql : sqlParams.keySet()) {

				PreparedStatement psmt = dbcon.prepareStatement(sql);

				List<Object[]> paramsList = sqlParams.get(sql);

				for (Object[] params : paramsList) {

					int idx = 1;
					for (Object value : params) {
						psmt.setObject(idx, value);
						idx++;
					}

					System.out.println("==> " + psmt);

					int count = psmt.executeUpdate();
					if (count <= 0) {
						System.err.println("Error to run sql: " + psmt);
					}
				}

				psmt.close();
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
	}

	public void clearData() {
		Connection dbcon = null;
		try {
			String sql = "TRUNCATE TABLE " + this.name;
			dbcon = this.conn.getConnection();
			PreparedStatement psmt = dbcon.prepareStatement(sql);
			int count = psmt.executeUpdate();
			System.out.println("==> " + sql + ", deleted: " + count);
			psmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
	}

	public void deleteData(String where) {
		Connection dbcon = null;
		try {
			String sql = "DELETE from " + this.name;
			if (where != null) {
				sql += " WHERE " + where;
			}
			dbcon = this.conn.getConnection();
			PreparedStatement psmt = dbcon.prepareStatement(sql);
			int count = psmt.executeUpdate();
			System.out.println("==> " + sql + ", deleted: " + count);
			psmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.conn.close(dbcon);
		}
	}

	public String getTableName() {
		return this.name;
	}

	public Map<String, ColumnDataHandler> getColumnDataHandler() {
		return this.handlers;
	}

	@Override
	public String toString() {
		return "Table [name=" + name + ", columnNames=" + columnNames
				+ ", PKCols=" + PKCols + ", dbname=" + dbname + "]";
	}
}