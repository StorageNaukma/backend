package org.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ItemsTable {
    private static final String TABLE_NAME = "Items";
    public static void create() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, manufacturer TEXT," +
                " price REAL, quantity REAL, label TEXT, groupId INTEGER)";
        try {
            Statement statement = DB.connection.createStatement();
            statement.execute(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changePrice(double price, int id) {
        String sqlQuery = "UPDATE " + TABLE_NAME + " SET price = price + ? WHERE id = ?";
        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDouble(1, price);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer insert(String name, String description, String manufacturer, double price, double quantity,
                                 String label, int groupId) {
        String sqlQuery = "INSERT INTO " + TABLE_NAME + "(name, description, manufacturer," +
                " price, quantity, label, groupId) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, manufacturer);
            preparedStatement.setDouble(4, price);
            preparedStatement.setDouble(5, quantity);
            preparedStatement.setString(6, label);
            preparedStatement.setInt(7, groupId);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ResultSet selectAll() {
        String sqlQuery = "SELECT * FROM " + TABLE_NAME;

        try {
            Statement statement = DB.connection.createStatement();
            return statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet selectFromGroup(int groupId) {
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE groupId = ?";
        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, groupId);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ResultSet selectById(int id) {
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, id);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Integer update(int id, String name, String description, String manufacturer, double price, double quantity,
                                  String label, int groupId) {

        String sqlQuery = "UPDATE " + TABLE_NAME + " SET name = ?, description = ?," +
                " manufacturer = ?, price = ?, quantity = ?, label = ?, groupId = ? WHERE id = ?";


        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, manufacturer);
            preparedStatement.setDouble(4, price);
            preparedStatement.setDouble(5, quantity);
            preparedStatement.setString(6, label);
            preparedStatement.setInt(7, groupId);
            preparedStatement.setInt(8, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static void delete(int id) {
        String sqlQuery = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet searchByName(String query) {
        String sqlQuery = "SELECT * FROM " + TABLE_NAME + " WHERE lower(name) LIKE ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DB.connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, query + "%");
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void truncate() {
        String sqlQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;

        try {
            Statement statement = DB.connection.createStatement();
            statement.execute(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
