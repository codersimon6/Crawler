package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

//负责对project对象进行数据操作
public class ProjectDao {
    public void save(Project project) throws SQLException {
        // 1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        // 2.拼接sql语句
        PreparedStatement statement = null;
        String sql = "insert into project_table values(?,?,?,?,?,?,?)";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, project.getName());
            statement.setString(2, project.getUrl());
            statement.setString(3, project.getDescription());
            statement.setInt(4, project.getStarCount());
            statement.setInt(5, project.getForkCount());
            statement.setInt(6, project.getOpenedIssueCount());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            statement.setString(7, simpleDateFormat.format(System.currentTimeMillis()));

            // 3.执行sql语句
            int i = statement.executeUpdate();
            if (i != 1) {
                System.out.println("数据保存数据库失败！");
            }
            System.out.println(project.getName() + "数据保存成功！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            DBUtil.close(connection, statement, null);
        }
    }

    public List<Project> selectProjectByDate(String date) {
        List<Project> projects = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        // 1.获取数据库连接
        connection = DBUtil.getConnection();
        // 2.拼sql
        String sql = "select name,url,starCount,forkCount,openedIssueCount " +
                "from project_table where date = ? order by starCount DESC";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, date);
            // 3.执行sql
            resultSet = statement.executeQuery();
            // 4.遍历resultSet
            while (resultSet.next()) {
                Project project = new Project();
                project.setName(resultSet.getString("name"));
                project.setUrl(resultSet.getString("url"));
                project.setStarCount(resultSet.getInt("starCount"));
                project.setForkCount(resultSet.getInt("forkCount"));
                project.setOpenedIssueCount(resultSet.getInt("openedIssueCount"));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, statement, resultSet);
        }
        return projects;
    }
}
