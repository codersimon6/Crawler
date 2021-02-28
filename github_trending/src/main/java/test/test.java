package test;

import dao.Project;
import dao.ProjectDao;

import java.util.List;

public class test {

//    public static void main(String[] args) throws IOException, SQLException {
//        Crawler crawler = new Crawler();
//        // 1.获取入口页面
//        String html = crawler.getPage("https://github.com/akullpp/awesome-java/blob/master/README.md");
//        // 2.解析入口页面，获取项目列表
//        List<Project> projects = crawler.parseProjectList(html);
//        //System.out.println(projects);
//        // 3.遍历项目列表，调用github API 获取项目信息
//        ProjectDao projectDao = new ProjectDao();
//        for (int i = 0; i < projects.size(); i++) {
//            // 不会因为一次失败停止
//            try {
//                Project p = projects.get(i);
//                System.out.println("crawling " + p.getName() + ".....");
//                String repoName = crawler.getRepoName(p.getUrl());
//                String jsonString = crawler.getRepo(repoName);
//                //System.out.println(jsonString);
//                // 4.解析每个仓库获取到的 JSON数据，得到需要的信息
//                crawler.parseRepoInfo(jsonString, p);
//
//                System.out.println("crawling " + p.getName() + "    done!");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (int i = 0; i < projects.size(); i++) {
//            Project p = projects.get(i);
//            // 5.把解析到的project 存到数据库中
//            projectDao.save(p);
//        }
//    }

    public static void main(String[] args) {
        ProjectDao projectDao = new ProjectDao();
        List<Project> projects = projectDao.selectProjectByDate("20210203");
        for (Project p : projects) {
            System.out.println(p);
        }
    }
}
