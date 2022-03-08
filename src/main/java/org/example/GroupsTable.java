package org.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GroupsTable {
    private static final String TABLE_NAME = "groups";

    public static void create() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT)";
        try {
            Statement statement = DB.connection.createStatement();
            statement.execute(sqlQuery);
            System.out.println("Table " + TABLE_NAME + " created\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer insert(String name, String description) {
        String sqlQuery = "INSERT INTO " + TABLE_NAME + "(name, description) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Integer id = resultSet.getInt(1);

                System.out.println("Inserted " + id + " " + name + '\n');
                return id;
            } else {
                System.err.println("Can't insert");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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

    public static String getName(int id) {
        ResultSet rs = selectById(id);
        try {
            assert rs != null;
            return rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer update(int id, String name, String description) {
        String sqlQuery = "UPDATE " + TABLE_NAME + " SET name = ?, description = ? WHERE id = ?";

        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
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

    public static void delete(int id) {
        String sqlQuery = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try {
            PreparedStatement preparedStatement = DB.connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Some title has been deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
