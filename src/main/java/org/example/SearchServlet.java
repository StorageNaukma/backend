package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SearchServlet extends HttpServlet {
    protected void corsSetup(HttpServletResponse resp) {
        resp.addHeader("Access-Control-Allow-Origin", "http://localhost");
        resp.addHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, PATCH");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        corsSetup(resp);
        String query = req.getParameter("request");
        Map<String, List<Map<String, Object>>> responseJsonMap = new HashMap<>();
        Map<Integer, List<Map<String, Object>>> items = new HashMap<>();
        ResultSet rs = ItemsTable.searchByName(query);
        try {
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("name", rs.getString("name"));
                item.put("description", rs.getString("description"));
                item.put("manufacturer", rs.getString("manufacturer"));
                item.put("price", rs.getDouble("price"));
                item.put("quantity", rs.getDouble("quantity"));
                item.put("label", rs.getString("label"));
                item.put("groupId", rs.getInt("groupId"));
                List<Map<String, Object>> list = items.get(item.get("groupId"));
                if (list == null) list = new ArrayList<>();
                list.add(item);
                items.put((Integer)item.get("groupId"), list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Map.Entry<Integer, List<Map<String, Object>>> entry : items.entrySet()) {
            responseJsonMap.put(GroupsTable.getName(entry.getKey()), entry.getValue());
        }
        String responseJson = new ObjectMapper().writeValueAsString(responseJsonMap);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT,PATCH");
        resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    }
}
