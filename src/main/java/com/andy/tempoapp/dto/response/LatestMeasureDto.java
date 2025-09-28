package com.andy.tempoapp.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class LatestMeasureDto{
	private Meta meta;
	private List<ResultsItem> results;

	@Data
	public static class Coordinates{
		private Object latitude;
		private Object longitude;
	}

	@Data
	public static class Datetime{
		private String utc;
		private String local;
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
	public static class ResultsItem{
		private Datetime datetime;
		private int sensorsId;
		private int locationsId;
		private Coordinates coordinates;
		private Object value;
	}
}