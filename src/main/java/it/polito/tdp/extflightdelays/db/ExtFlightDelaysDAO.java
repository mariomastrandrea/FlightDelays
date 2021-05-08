package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Rotta;

public class ExtFlightDelaysDAO 
{
	public void loadAllAirports(Map<Integer, Airport> airportsIdMap) 
	{
		String sql = "SELECT * FROM airports";

		try 
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet result = statement.executeQuery();

			while (result.next()) 
			{
				int id = result.getInt("ID");
				if(!airportsIdMap.containsKey(id))
				{
					Airport airport = new Airport(id, result.getString("IATA_CODE"), result.getString("AIRPORT"),
										result.getString("CITY"), result.getString("STATE"), 
										result.getString("COUNTRY"), result.getDouble("LATITUDE"), 
										result.getDouble("LONGITUDE"), result.getDouble("TIMEZONE_OFFSET"));
							
					airportsIdMap.put(id, airport);
				}	
			}
			
			result.close();
			statement.close();
			connection.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			System.out.println("Dao error in loadAllAirports()");
			throw new RuntimeException("Dao error in loadAllAirports()", sqle);
		}
	}
	
	public Set<Airport> getVertici(Map<Integer,Airport> airportsIdMap, int  minAirlines)
	{
		String sql = String.format("%s %s %s %s %s",
				"SELECT  a.id AS id",
				"FROM airports a, flights f",
				"WHERE a.id = f.ORIGIN_AIRPORT_ID OR a.id = f.DESTINATION_AIRPORT_ID",
				"GROUP BY a.id",
				"HAVING COUNT(DISTINCT(f.AIRLINE_ID)) > ?");
		
		Set<Airport> result = new HashSet<>();
		
		try 
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, minAirlines);
			ResultSet queryResult = statement.executeQuery();

			while (queryResult.next()) 
			{
				Airport a = airportsIdMap.get(queryResult.getInt("id"));
				result.add(a);
			}

			queryResult.close();
			statement.close();
			connection.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			System.out.println("Dao error in getVertici()");
			throw new RuntimeException("Dao error in getVertici()", sqle);
		}
		return result;
	}
	
	public Set<Rotta> getRotte(Map<Integer,Airport> airportsIdMap)
	{
		String sql = String.format("%s %s %s",
				"SELECT ORIGIN_AIRPORT_ID AS a1, DESTINATION_AIRPORT_ID AS a2, COUNT(*) AS numFlights",
				"FROM Flights",
				"GROUP BY ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID");
		
		Set<Rotta> result = new HashSet<>();
		
		try 
		{
			Connection connection = ConnectDB.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet queryResult = statement.executeQuery();

			while (queryResult.next()) 
			{
				int id1 = queryResult.getInt("a1");
				Airport a1 = airportsIdMap.get(id1);
				
				int id2 = queryResult.getInt("a2");
				Airport a2 = airportsIdMap.get(id2);
				
				int numFlights = queryResult.getInt("numFlights");
				
				if(a1 == null || a2 == null)
					throw new RuntimeException("DB non corretto: aeroporto non presente");
				
				Rotta r = new Rotta(a1, a2, numFlights);
				
				result.add(r);
			}

			queryResult.close();
			statement.close();
			connection.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
			System.out.println("Dao error in getRotte()");
			throw new RuntimeException("Dao error in getRotte()", sqle);
		}
		
		return result;
	}
}
