package tools;

import java.util.Map;

public interface DataConvertor {

	/**
	 * Which column value need to convert
	 * 
	 * @return
	 */
	String getColumnName();

	/**
	 * convert a column value to wanted value
	 * 
	 * @param row
	 *            a record in database, Map contains columnName and columnValue.
	 * @return
	 */
	Object convert(Map<String, Object> row);

}
