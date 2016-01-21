package tools.handler;

import tools.Row;

/**
 * Save null value into given column
 * 
 * @author zhangqunshi@126.com
 *
 */
public class NullValue implements ColumnDataHandler {

	@Override
	public Object convert(Row row) {
		return null;
	}

}
