package controllers;

import db.DBConnection;
import domain.BloodPressure;
import domain.LogIn;
import domain.User;
import other.BCrypt;

import java.sql.*;

public final class UserController {

    public static String logIn(LogIn logIn){
        String userId = null;

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM public.user WHERE email = '" + logIn.getUsername() + "';")
            ){
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();

                final String password = resultSet.getString("password");
                if(UserController.checkPassword(logIn.getPassword(), password)){
                    userId = resultSet.getString("id");
                }

            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }

        }catch (SQLException e) {
            System.err.println("SQL Error");
        }

        return userId;
    }

    public static User createUser(User user){
        String encPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(15));
        user.setPassword(encPassword);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO public.user(id, name, nif, password, email, phone, address, createdat) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.generateId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getNif());
            statement.setString(4, user.getPassword());
            statement.setString(5, user.getEmail());
            statement.setInt(6, user.getPhone());
            statement.setString(7, user.getAddress());
            statement.setDate(8, user.getCreatedat());
            statement.setString(9, user.getRole());
            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("SQL Error on Create User");
            System.err.println(e.getMessage());
            return null;
        }
        return user;
    }


    private static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided.");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return password_verified;
    }
}