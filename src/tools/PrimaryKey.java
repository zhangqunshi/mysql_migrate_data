package tools;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PrimaryKey {

	Map<String, Object> pks;

	public PrimaryKey(Map<String, Object> m) {
		this.pks = m;
	}

	public boolean equals(PrimaryKey other) {
		Iterator<String> it = pks.keySet().iterator();
		while (it.hasNext()) {
			String columnName = it.next();

			Object v2 = other.getValue(columnName);
			if (v2 == null) {
				return false;
			}

			Object v1 = getValue(columnName);
			if (!v1.equals(v2)) {
				return false;
			}
		}
		return true;
	}

	private Object getValue(String columnName) {
		return pks.get(columnName);
	}

	public Set<String> getColumns() {
		return this.pks.keySet();
	}

}
