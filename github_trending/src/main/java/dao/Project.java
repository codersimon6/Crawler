package dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Project {
    // 项目名，对应<a> 标签的内容
    private String name;

    // 项目主页链接，对应<a> 标签href的属性
    private String url;

    //项目的描述信息，对应li标签的内容
    private String description;

    // 以下属性是我们需要统计的数据:需要进入到页面，获取对应的数据
    private int starCount;
    private int forkCount;
    private int openedIssueCount;

}
