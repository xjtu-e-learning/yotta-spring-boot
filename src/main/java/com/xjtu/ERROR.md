### 问题记录

##### 1. spring boot 命令行启动的一些坑
这个[博客](https://blog.csdn.net/qq_33547169/article/details/78460538)总结很到位，
为什么命令行使用```java -jar yotta.jar --spring.profiles.active=test```，无法修改参数?
这是由于在springboot项目启动的主函数中，代码如下
```$xslt
SpringApplication.run(Application.class);
```
如上这样写,就没有传递参数args,所以无论如何也没办法修改环境,下面的这个是正确的
```$xslt
SpringApplication.run(Application.class, args);
```

##### 2. Parameter value element [10] did not match expected type [java.lang.Long (n/a)]
这是由于查询数据库biginteger(20)字段，返回的是BigInteger类型，需要转换成Long


##### 2. error:关于无穷递归
//不添加以下注解会导致FacetContainAssemble的无限递归
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "facetId")
//@JsonIgnoreProperties(value = "children")
//@JsonBackReference

