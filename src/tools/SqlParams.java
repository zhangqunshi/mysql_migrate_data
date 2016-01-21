package tools;

public class SqlParams {

	String sql;
	Object[] params;

	public SqlParams(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return this.sql;
	}

	public Object[] getParams() {
		return this.params;
	}

}
