package it.polito.tdp.extflightdelays.model;

public class Airport 
{	
	private int id;
	private String iataCode;
	private String airportName;
	private String city;
	private String state;
	private String country;
	private Double latitude;
	private Double longitude;
	private Double timezoneOffset;
	
	
	public Airport(int id, String iataCode, String airportName, String city, String state, String country,
			Double latitude, Double longitude, Double timezoneOffset) 
	{
		this.id = id;
		this.iataCode = iataCode;
		this.airportName = airportName;
		this.city = city;
		this.state = state;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timezoneOffset = timezoneOffset;
	}

	public int getId() 
	{
		return this.id;
	}

	public String getIataCode() 
	{
		return this.iataCode;
	}
	
	public String getAirportName() 
	{
		return this.airportName;
	}

	public String getCity() 
	{
		return this.city;
	}

	public String getState() 
	{
		return this.state;
	}

	public String getCountry() 
	{
		return this.country;
	}

	public Double getLatitude() 
	{
		return this.latitude;
	}

	public Double getLongitude() 
	{
		return this.longitude;
	}

	public Double getTimezoneOffset() 
	{
		return this.timezoneOffset;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Airport other = (Airport) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() 
	{
		return this.airportName;
	}
	
}
