package controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import domain.BloodPressure;
import domain.User;
import domain.VisitsDoctor;
import other.BCrypt;

public class VisitsDoctorController {
	
    public static List<VisitsDoctor> getVisitsDoctorById(String userId){
        List<VisitsDoctor> visitsDoctorList = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.visits_doctor WHERE visits_doctor.userid = '" + userId + "';")) {

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                		visitsDoctorList.add(new VisitsDoctor(
                            userId,
                            resultSet.getString("doctor"),
                            resultSet.getDate("date").getTime(),
                            resultSet.getString("time"),
                            resultSet.getString("reason")
                    ));
                }
            } catch (SQLException e) {
                System.err.println("Error on getVisitsDoctor ResultSet");
                System.err.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error on getVisitsDoctor SQL connection");
            System.err.println(e.getMessage());
        }

        return visitsDoctorList;
    }
    
    
    public static VisitsDoctor getLastVisitsDoctorByUserId(String userId){
        VisitsDoctor visitsDoctor = null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.visits_doctor WHERE visits_doctor.userid = '" + userId + "' order by visits_doctor.date desc limit 1;")) {

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                visitsDoctor  = new VisitsDoctor(
                        userId,
                        resultSet.getString("doctor"),
                        resultSet.getDate("date").getTime(),
                        resultSet.getString("time"),                        
                        resultSet.getString("reason")
                );

            } catch (SQLException e) {
                System.err.println("Error on getLastVisitsDoctorByUserId ResultSet");
                System.err.println(e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error on getLastVisitsDoctorByUserId SQL Connection");
            System.err.println(e.getMessage());
        }

        return visitsDoctor;
    }
    
    public static VisitsDoctor insertVisit(VisitsDoctor visitsDoctor){

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO public.visits_doctor(userid, doctor, date, reason, time) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, visitsDoctor.getUserId());
            statement.setString(2, visitsDoctor.getDoctor());
            statement.setDate(3, new Date(visitsDoctor.getDate()));
            statement.setString(4, visitsDoctor.getReason());
            statement.setString(5, visitsDoctor.getTime());
            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("SQL Error on Insert new visit");
            System.err.println(e.getMessage());
            return null;
        }
        return visitsDoctor;
    }

}
