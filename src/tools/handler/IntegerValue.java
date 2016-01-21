package tools.handler;

import tools.Row;

public class IntegerValue implements ColumnDataHandler {

	int i;

	public IntegerValue(int i) {
		this.i = i;
	}

	@Override
	public Object convert(Row row) {
		return i;
	}

}
