package com.github.selfridicule;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class H2SqlDao implements SqlDao {

    @Override
    public String queryNextLinkThenDelete() {
        //获取连接
        String link = queryLinkToBeProcessFirst();
        //没有待处理的连接
        if (link != null) {
            //从数据库删除待处理的连接
            deleteLinkToBeProcess(link);
            //return link
            return link;
        } else {
            return null;
        }
    }

    @SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    public List<Map<String, String>> queryData(String tableName, List<String> columnList) throws Exception {
        StringBuffer columnValue = new StringBuffer("");
        for (String column : columnList) {
            columnValue.append(column + " ,");
        }
        if (columnValue.length() > 0) {
            columnValue = new StringBuffer(columnValue.substring(0, columnValue.length() - 1));
        }
        List<Map<String, String>> dataList = new ArrayList<>();
        try (PreparedStatement preparedStatement = H2DB.conn.prepareStatement("SELECT " + columnValue.toString() + " FROM " + tableName)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                for (String column : columnList) {
                    map.put(column, rs.getString(column));
                }
                dataList.add(map);
            }
        }
        return dataList;
    }

    @Override
    public String queryLinkToBeProcessFirst() {
        List<String> list = queryLinkToBeProcess();
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<String> queryLinkToBeProcess() {
        try {
            List<Map<String, String>> list = queryData("link_to_be_process", Arrays.asList("link"));
            return list.stream().map(map -> map.get("link")).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertLinkToBeProcess(String link) {
        try (PreparedStatement preparedStatement = H2DB.conn.prepareStatement("INSERT INTO link_to_be_process (link) VALUES(?)")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteLinkToBeProcess(String link) {
        try (PreparedStatement preparedStatement = H2DB.conn.prepareStatement("delete from link_to_be_process where link = ?")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> queryLinkAlreadyProcess() {
        try {
            List<Map<String, String>> list = queryData("link_already_process", Arrays.asList("link"));
            return list.stream().map(map -> map.get("link")).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean existLinkAlreadyProcess(String link) {
        try (PreparedStatement preparedStatement = H2DB.conn.prepareStatement("select * from link_already_process where link = ?")) {
            preparedStatement.setString(1, link);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertLinkAlreadyProcess(String link) {
        try (PreparedStatement preparedStatement = H2DB.conn.prepareStatement("INSERT INTO link_already_process (link) VALUES(?)")) {
            preparedStatement.setString(1, link);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean insertNews(String title, String content, String url) {
        try (PreparedStatement preparedStatement = H2DB.conn.prepareStatement("INSERT INTO news (title , content , url , create_time , update_time) VALUES(?, ? , ? , now(), now())")) {
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, content);
            preparedStatement.setString(3, url);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public List<News> queryNews() {
        return null;
    }

}
