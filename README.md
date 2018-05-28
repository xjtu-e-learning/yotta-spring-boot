# Yotta后端
spring boot框架实现

其中application.java是工程主入口，spring-boot的主入口。

- common 公共配置
- dependency 主题认知关系模块
- spider 爬虫模块
- start  工程启动检查模块


###问题：jar包运行：Cannot determine embedded database driver class for database type NONE
Application.class 添加注解
@PropertySource({"application.properties"})
未解决