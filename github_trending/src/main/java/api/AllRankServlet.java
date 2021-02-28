package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Project;
import dao.ProjectDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/allRank")
public class AllRankServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        // 获取querystring
        String date = req.getParameter("date");
        if (date == null || date.equals("")) {
            resp.setStatus(404);
            resp.getWriter().write("date 参数错误！");
            return;
        }
        // 从数据库中查找
        ProjectDao projectDao = new ProjectDao();
        List<Project> projects = projectDao.selectProjectByDate(date);
        // 把结果整理为json 返回给客户端
        String jsonString = gson.toJson(projects);
        resp.getWriter().write(jsonString);
        return;
    }
}
