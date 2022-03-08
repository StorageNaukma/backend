package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupsServlet extends HttpServlet {

    protected void corsSetup(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost");
        resp.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST, PATCH, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setHeader("Access-Control-Max-Age", "86400");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr;
        try {
             idStr = req.getPathInfo().substring(1);
        }
        catch (NullPointerException e) {
            doGetAll(req, resp);
            return;
        }
        int id = Integer.parseInt(idStr);

        ResultSet rs = GroupsTable.selectById(id);
        Map<String, Object> responseJsonMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSet itemsRs = ItemsTable.selectFromGroup(id);
        try {
            while (itemsRs.next()) {
                Map<String, Object> itemsMap = new HashMap<>();
                itemsMap.put("id", itemsRs.getInt("id"));
                itemsMap.put("name", itemsRs.getString("name"));
                itemsMap.put("description", itemsRs.getString("description"));
                itemsMap.put("manufacturer", itemsRs.getString("manufacturer"));
                itemsMap.put("price", itemsRs.getDouble("price"));
                itemsMap.put("quantity", itemsRs.getDouble("quantity"));
                itemsMap.put("label", itemsRs.getString("label"));
                itemsMap.put("groupId", itemsRs.getInt("id"));
                list.add(itemsMap);
            }
            assert rs != null;
            responseJsonMap.put("id", rs.getInt("id"));
            responseJsonMap.put("name", rs.getString("name"));
            responseJsonMap.put("description", rs.getString("description"));
            responseJsonMap.put("items", list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String responseJson = new ObjectMapper().writeValueAsString(responseJsonMap);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }

    private void doGetAll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResultSet groupsRs = GroupsTable.selectAll();
        List<Map<String, Object>> responseList = new ArrayList<>();
        try {
            while (groupsRs.next()) {
                Map<String, Object> map = new HashMap<>();
                List<Map<String, Object>> items = new ArrayList<>();
                ResultSet itemsRs = ItemsTable.selectFromGroup(groupsRs.getInt("id"));
                while (itemsRs.next()) {
                    Map<String, Object> itemsMap = new HashMap<>();
                    itemsMap.put("id", itemsRs.getInt("id"));
                    itemsMap.put("name", itemsRs.getString("name"));
                    itemsMap.put("description", itemsRs.getString("description"));
                    itemsMap.put("manufacturer", itemsRs.getString("manufacturer"));
                    itemsMap.put("price", itemsRs.getDouble("price"));
                    itemsMap.put("quantity", itemsRs.getDouble("quantity"));
                    itemsMap.put("label", itemsRs.getString("label"));
                    itemsMap.put("groupId", itemsRs.getInt("id"));
                    items.add(itemsMap);
                }
                map.put("id", groupsRs.getInt("id"));
                map.put("name", groupsRs.getString("name"));
                map.put("description", groupsRs.getString("description"));
                map.put("items", items);
                responseList.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String responseJson = null;
        try {
            responseJson = new ObjectMapper().writeValueAsString(responseList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HashMap<String, Object> jsonMap = new ObjectMapper().readValue(jsonData, HashMap.class);
        String name = (String) jsonMap.get("name");
        log(name);
        String description = (String) jsonMap.get("description");
        int id = GroupsTable.insert(name, description);
        Map<String, Object> responseJsonMap = new HashMap<>();
        responseJsonMap.put("name", name);
        responseJsonMap.put("description", description);
        responseJsonMap.put("id", id);
        String responseJson = new ObjectMapper().writeValueAsString(responseJsonMap);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // for reading
        String idStr = req.getPathInfo().substring(1);
        int id = Integer.parseInt(idStr);
        ResultSet rs = GroupsTable.selectById(id);
        // for update
        String jsonData = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        HashMap<String, Object> jsonMap = new ObjectMapper().readValue(jsonData, HashMap.class);
        String name = (String) jsonMap.get("name");
        String description = (String) jsonMap.get("description");
        try {
            if (name == null) name = rs.getString("name");
            if (description == null) description = rs.getString("description");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        GroupsTable.update(id, name, description);

        Map<String, Object> responseJsonMap = new HashMap<>();
        responseJsonMap.put("name", name);
        responseJsonMap.put("description", description);
        responseJsonMap.put("id", id);
        String responseJson = new ObjectMapper().writeValueAsString(responseJsonMap);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().println(responseJson);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getPathInfo().substring(1);
        int id = Integer.parseInt(idStr);
        ResultSet rs = GroupsTable.selectById(id);
        if (rs == null) {
            resp.setStatus(404);
            return;
        }
        resp.setStatus(204);
        GroupsTable.delete(id);
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
