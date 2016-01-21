package tools.handler;

import java.util.Date;

import tools.Row;

public class CurrentDate implements ColumnDataHandler {

	@Override
	public Object convert(Row row) {
		return new Date();
	}

}
