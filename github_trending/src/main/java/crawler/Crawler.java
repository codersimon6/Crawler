package crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dao.Project;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Crawler {
    private OkHttpClient okHttpClient = new OkHttpClient();
    private HashSet<String> urlBlack = new HashSet<>();
    private Gson gson = new GsonBuilder().create();

    {
        urlBlack.add("https://github.com/events");
        urlBlack.add("https://github.community");
        urlBlack.add("https://github.com/about");
        urlBlack.add("https://github.com/pricing");
        urlBlack.add("https://github.com/contact");
        urlBlack.add("https://github.com/security");
        urlBlack.add("https://github.com/readme");
    }


    public String getPage(String url) throws IOException {
        // 1.创建okhttp客户端对象,一个程序中创建一个就可以
        //  request，call，response则每次程序都需要创建

        // 2.创建request对象 (用Request的静态类创建)
        //  Builder类是一个辅助构造构造request对象的类，Builder中提供的url方法可以设定当前请求的url
        Request request = new Request.Builder().url(url).build();
        // 3.创建一个Call对象，负责进行一次网络访问操作
        Call call = okHttpClient.newCall(request);
        // 4.发送请求到服务器,获取到response对象
        Response response = call.execute();
        // 5.判断响应是否成功
        if (!response.isSuccessful()) {
            System.out.println("请求失败！");
            return null;
        }
        return response.body().string();
    }

    public List<Project> parseProjectList(String html) throws IOException {
        List<Project> result = new ArrayList<>();
        // 使用Jsoup 分析一下页面结构，把其中的li标签都获取到
        // 1.先创建一个Document对象,一个Document对应一个html
        Document document = Jsoup.parse(html);
        // 2.使用 getElementsByTag，拿到所有的li标签    elements相当于集合类。每个element对应一个标签
        Elements elements = document.getElementsByTag("li");
        for (Element li : elements) {
            // 某些li标签并不是项目，只有包括a标签的才是项目，继续筛选
            Elements allLink = li.getElementsByTag("a");
            if (allLink.size() == 0) continue;
            // 一个项目中只有一个a标签
            Element link = allLink.get(0);   //link -> a标签
//            System.out.println(link.text());   //a标签内容
//            System.out.println(link.attr("href")); //a链接
//            System.out.println(li.text());  //li标签内容/描述
//            System.out.println("=======================");

            String url = link.attr("href");
            //若不是合法链接，直接丢弃
            if (!url.startsWith("https://github.com")) continue;
            if (urlBlack.contains(url)) continue;

            Project project = new Project();
            project.setName(link.text());
            project.setUrl(link.attr("href"));
            project.setDescription(li.text());
            result.add(project);
        }
        return result;
    }

    // 调用github 的api获取指定仓库信息
    //https://api.github.com/repos/用户名/仓库名
    public String getRepo(String repoName) throws IOException {
        String userName = "codersimon6";
        String passWord = "xmh199844";
        //进行身份认证,把用户名密码加密后得到的字符串放到http的header中
        String credential = Credentials.basic(userName, passWord);

        String url = "https://api.github.com/repos/" + repoName;
        // 用创建的okhttp对象 发起请求
        Request request = new Request.Builder().url(url).header("Authorization", credential).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if (!response.isSuccessful()) {
            System.out.println("访问githubAPI失败！url = " + url);
            return null;
        }
        return response.body().string();
    }

    // 把项目中的url提取出其中的仓库名和作者名
    //https://github.com/codersimon6/test  => codersimon6/test
    public String getRepoName(String url) {
        int lastOne = url.lastIndexOf("/");
        int lastTwo = url.lastIndexOf("/", lastOne - 1);
        if (lastOne == -1 || lastTwo == -1) {
            System.out.println("当前URL不合法！不是一个标准的仓库URL！" + url);
            return null;
        }
        return url.substring(lastTwo + 1, url.length());
    }

    /**
     * 获取仓库的相关信息
     *
     * @param jsonString 表示github API获取到的结果
     * @param project    解析出的Star数，fork数，opened_issue数 保存到这里
     *                   使用Gson库解析
     */
    public void parseRepoInfo(String jsonString, Project project) {
        // 借助Gson中提供的TypeToken，获取到HashMap所对应的一个类对象
        // 然后才能传给gson.fromJson （这种写法是gson要求的）
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        // hashmap中的key都是api返回的key
        HashMap<String, Object> hashMap = gson.fromJson(jsonString, type);
        Double startCount = (Double) hashMap.get("stargazers_count");
        project.setStarCount(startCount.intValue());
        Double forkCount = (Double) hashMap.get("forks_count");
        project.setForkCount(forkCount.intValue());
        Double openedIssueCount = (Double) hashMap.get("open_issues_count");
        project.setOpenedIssueCount(openedIssueCount.intValue());

    }
}
