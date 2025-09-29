package com.andy.tempoapp.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class SingleLocationDto {
	private Meta meta;
	private List<ResultsItem> results;

	@Data
	public static class Attribution{
		private String name;
		private Object url;
	}

	@Data
	public static class Coordinates{
		private Object latitude;
		private Object longitude;
	}

	@Data
	public static class Country{
		private String code;
		private String name;
		private int id;
	}

	@Data
	public static class DatetimeFirst{
		private String utc;
		private String local;
	}

	@Data
	public static class DatetimeLast{
		private String utc;
		private String local;
	}

	@Data
	public static class InstrumentsItem{
		private String name;
		private int id;
	}

	@Data
	public static class LicensesItem{
		private String name;
		private Attribution attribution;
		private Object dateTo;
		private int id;
		private String dateFrom;
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
	public static class Owner{
		private String name;
		private int id;
	}

	@Data
	public static class Parameter{
		private String displayName;
		private String name;
		private int id;
		private String units;
	}

	@Data
	public static class Provider{
		private String name;
		private int id;
	}

	@Data
	public static class ResultsItem{
		private Owner owner;
		private Country country;
		private Object distance;
		private String timezone;
		private String locality;
		private Coordinates coordinates;
		private DatetimeLast datetimeLast;
		private boolean isMonitor;
		private List<LicensesItem> licenses;
		private List<InstrumentsItem> instruments;
		private List<SensorsItem> sensors;
		private Provider provider;
		private DatetimeFirst datetimeFirst;
		private String name;
		private List<Object> bounds;
		private int id;
		private boolean isMobile;
	}

	@Data
	public static class SensorsItem{
		private Parameter parameter;
		private String name;
		private int id;
	}
}