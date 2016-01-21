package tools.handler;

import tools.Row;

/**
 * For added new column in dst table. Or sometimes you want convert the old
 * value to a new value. Or the new column value coming from a calculating
 * result.
 * 
 * @author zhangqunshi@126.com
 *
 */
public interface ColumnDataHandler {

	/**
	 * convert a column value to wanted value
	 * 
	 * @param row
	 *            a record in database, Map contains columnName and columnValue.
	 * @return
	 */
	Object convert(Row row);
}
