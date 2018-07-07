# 公共配置

用来写一些公用的东西，比如全局环境变量，或者spring-boot的依赖注入，切面编程等

- **Config** 全局变量
- **CorsConfig** 浏览器端的跨域设置
- **SwaggerConfig** swagger的依赖注入。自动生成swagger.json并提供swaggerui。默认网址为：`http://ip+port+工程名字（放在tomcat的ROOT下则没有工程名字）/swagger-ui.html` ，例如`http://localhost:8080/swagger-ui.html`
