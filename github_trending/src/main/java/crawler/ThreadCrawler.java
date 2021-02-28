package crawler;

import dao.Project;
import dao.ProjectDao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadCrawler extends Crawler {
    public static void main(String[] args) throws IOException, SQLException {
        ThreadCrawler crawler = new ThreadCrawler();
        // 1.获取入口页面
        String html = crawler.getPage("https://github.com/akullpp/awesome-java/blob/master/README.md");
        // 2.解析入口页面，获取项目列表
        List<Project> projects = crawler.parseProjectList(html);
        // 3.遍历项目列表，利用线程池实现多线程
        // executorService提交任务：1）submit 有返回结果  2）execute 无返回结果
        // 此处使用submit是为了得知是否全部遍历结束，方便进行存到数据库操作
        ExecutorService executorService = Executors.newFixedThreadPool(10);  //固定大小10的线程池

        List<Future<?>> taskResults = new ArrayList<>();
        for (Project project : projects) {
            Future<?> taskResult = executorService.submit(new CrawlerTask(project, crawler));
            taskResults.add(taskResult);
        }

        // 等待所有任务执行结束，再进行下一步
        for (Future<?> taskResult : taskResults) {
            try {
                // 调用get会阻塞，直到该任务执行完毕，才会返回
                if (taskResult != null) taskResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        //代码到这里，说明所有任务都执行结束,结束线程池
        executorService.shutdown();

        // 4.保存到数据库
        ProjectDao projectDao = new ProjectDao();
        for (Project project : projects) {
            projectDao.save(project);
        }
    }


    static class CrawlerTask implements Runnable {
        private Project project;
        private ThreadCrawler threadCrawler;

        public CrawlerTask(Project project, ThreadCrawler crawler) {
            this.project = project;
            this.threadCrawler = crawler;
        }

        @Override
        public void run() {
            // 依赖project对象 和 crawler对象（调用方法进行抓取）
            // 调用API获取项目数据
            try {
                System.out.println("crawling " + project.getName() + ".....");
                String repoName = threadCrawler.getRepoName(project.getUrl());
                String jsonString = threadCrawler.getRepo(repoName);
                // 解析项目数据
                threadCrawler.parseRepoInfo(jsonString, project);
                System.out.println("crawling " + project.getName() + "done !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
