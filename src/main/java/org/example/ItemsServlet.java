package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemsServlet extends HttpServlet {

    protected void corsSetup(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost");
        resp.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, PATCH, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "86400");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getPathInfo().substring(1);
        int id = Integer.parseInt(idStr);
        ResultSet rs = ItemsTable.selectById(id);
        Map<String, Object> responseJsonMap = new HashMap<>();
        try {
            assert rs != null;
            responseJsonMap.put("id", rs.getInt("id"));
            responseJsonMap.put("name", rs.getString("name"));
            responseJsonMap.put("description", rs.getString("description"));
            responseJsonMap.put("manufacturer", rs.getString("manufacturer"));
            responseJsonMap.put("price", rs.getDouble("price"));
            responseJsonMap.put("quantity", rs.getDouble("quantity"));
            responseJsonMap.put("label", rs.getString("label"));
            responseJsonMap.put("groupId", rs.getInt("groupId"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String responseJson = new ObjectMapper().writeValueAsString(responseJsonMap);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HashMap<String, Object> jsonMap = new ObjectMapper().readValue(jsonData, HashMap.class);
        String name = (String) jsonMap.get("name");
        String description = (String) jsonMap.get("description");
        String manufacturer = (String) jsonMap.get("manufacturer");
        double price = (double) jsonMap.get("price");
        double quantity = (double) jsonMap.get("quantity");
        String label = (String) jsonMap.get("label");
        int groupId = (int) jsonMap.get("groupId");
        if (groupId < 0) {
            resp.setStatus(400);
            return;
        }
        int id = ItemsTable.insert(name, description, manufacturer, price, quantity, label, groupId);
        Map<String, Object> responseJsonMap = new HashMap<>();
        responseJsonMap.put("name", name);
        responseJsonMap.put("description", description);
        responseJsonMap.put("manufacturer", manufacturer);
        responseJsonMap.put("price", price);
        responseJsonMap.put("quantity", quantity);
        responseJsonMap.put("label", label);
        responseJsonMap.put("groupId", groupId);
        responseJsonMap.put("id", id);
        String responseJson = new ObjectMapper().writeValueAsString(responseJsonMap);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // for reading
        String idStr = req.getPathInfo().substring(1);
        int id = Integer.parseInt(idStr);
        ResultSet rs = ItemsTable.selectById(id);
        // for update
        String jsonData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HashMap<String, Object> jsonMap = new ObjectMapper().readValue(jsonData, HashMap.class);
        Double increase = (double) jsonMap.get("increase");
        Double decrease = (double) jsonMap.get("decrease");
        if (increase != null) {
            ItemsTable.changePrice(increase, id);
            return;
        } else if(decrease != null) {
            ItemsTable.changePrice(-decrease, id);
        }
        String name = (String) jsonMap.get("name");
        String description = (String) jsonMap.get("description");
        String manufacturer = (String) jsonMap.get("manufacturer");
        Double price = (Double) jsonMap.get("price");
        Double quantity = (Double) jsonMap.get("quantity");
        String label = (String) jsonMap.get("label");
        Integer groupId = (Integer) jsonMap.get("groupId");
        try {
            if (name == null) name = rs.getString("name");
            if (description == null) description = rs.getString("description");
            if (manufacturer == null) manufacturer = rs.getString("manufacturer");
            if (price == null) price = rs.getDouble("price");
            if (quantity == null) quantity = rs.getDouble("quantity");
            if (label == null) label = rs.getString("label");
            if (groupId == null) groupId = rs.getInt("groupId");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ItemsTable.update(id, name, description, manufacturer, price, quantity, label, groupId);

        Map<String, Object> responseJsonMap = new HashMap<>();
        responseJsonMap.put("name", name);
        responseJsonMap.put("description", description);
        responseJsonMap.put("manufacturer", manufacturer);
        responseJsonMap.put("price", price);
        responseJsonMap.put("quantity", quantity);
        responseJsonMap.put("label", label);
        responseJsonMap.put("groupId", groupId);
        responseJsonMap.put("id", id);
        String responseJson = new ObjectMapper().writeValueAsString(responseJsonMap);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getPathInfo().substring(1);
        int id = Integer.parseInt(idStr);
        ResultSet rs = ItemsTable.selectById(id);
        if (rs == null) {
            resp.setStatus(404);
            return;
        }
        resp.setStatus(204);
        ItemsTable.delete(id);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT,PATCH,DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        corsSetup(resp);
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }


}
