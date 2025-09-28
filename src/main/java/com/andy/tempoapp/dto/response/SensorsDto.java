package com.andy.tempoapp.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class SensorsDto {
	private Meta meta;
	private List<ResultsItem> results;

	@Data
	public static class Coordinates{
		private Object latitude;
		private Object longitude;
	}

	@Data
	public static class Coverage{
		private int percentCoverage;
		private int expectedCount;
		private String observedInterval;
		private int percentComplete;
		private String expectedInterval;
		private int observedCount;
		private DatetimeFrom datetimeFrom;
		private DatetimeTo datetimeTo;
	}

	@Data
	public static class Datetime{
		private String utc;
		private String local;
	}

	@Data
	public static class DatetimeFirst{
		private String utc;
		private String local;
	}

	@Data
	public static class DatetimeFrom{
		private String utc;
		private String local;
	}

	@Data
	public static class DatetimeLast{
		private String utc;
		private String local;
	}

	@Data
	public static class DatetimeTo{
		private String utc;
		private String local;
	}

	@Data
	public static class Latest{
		private Datetime datetime;
		private Coordinates coordinates;
		private Object value;
	}

	@Data
	public static class Meta{
		private String website;
		private int found;
		private String name;
		private int limit;
		private int page;
	}

	@Data
	public static class Parameter{
		private String displayName;
		private String name;
		private int id;
		private String units;
	}

	@Data
	public static class ResultsItem{
		private Coverage coverage;
		private Summary summary;
		private DatetimeFirst datetimeFirst;
		private Parameter parameter;
		private String name;
		private int id;
		private DatetimeLast datetimeLast;
		private Latest latest;
	}

	@Data
	public static class Summary{
		private Object q98;
		private Object sd;
		private Object q02;
		private int min;
		private Object avg;
		private Object median;
		private Object q25;
		private Object max;
		private Object q75;
	}
}