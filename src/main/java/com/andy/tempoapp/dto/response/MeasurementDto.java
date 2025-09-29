package com.andy.tempoapp.dto.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeasurementDto {
	private Meta meta;
	private List<ResultsItem> results;

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Meta {
		private String website;
		private String found;   // may be ">10" or "48" or a number serialized as string
		private String name;
		private Integer limit;
		private Integer page;

		@JsonIgnore
		public Integer getFoundAsInt() {
			if (found == null) return null;
			try { return Integer.parseInt(found.replace(">", "").trim()); }
			catch (NumberFormatException e) { return null; }
		}
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResultsItem {
		private Summary summary;
		private Coverage coverage;
		private Period period;
		private FlagInfo flagInfo;
		private Parameter parameter;
		private Coordinates coordinates; // nullable
		private Double value;            // nullable
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Coordinates {
		private Double latitude;
		private Double longitude;
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class FlagInfo {
		private boolean hasFlags;
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Parameter {
		private String displayName; // nullable
		private String name;
		private Integer id;
		private String units;
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Period {
		private String interval;   // e.g., "01:00:00" or "1month"
		private String label;      // e.g., "1hour" or "1 month"
		private Timestamp datetimeFrom;
		private Timestamp datetimeTo;
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Timestamp {
		private String utc;
		private String local;
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Coverage {
		private Integer percentCoverage;
		private Integer expectedCount;
		private String observedInterval;  // "01:00:00", "24:00:00", "720:00:00"
		private Integer percentComplete;
		private String expectedInterval;
		private Integer observedCount;
		private Timestamp datetimeFrom;
		private Timestamp datetimeTo;
	}

	@Data @JsonIgnoreProperties(ignoreUnknown = true)
	public static class Summary {
		private Double min;
		private Double q02;
		private Double q25;
		private Double median;
		private Double q75;
		private Double q98;
		private Double max;
		private Double avg;
		private Double sd;
	}
}
