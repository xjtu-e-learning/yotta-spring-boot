- 配置文件
    - 在 `com.xjtu.yottasearch.index.ESYotta`里，包括jdbc和es的配置。
- es 
    - 下载 
        - `https://dev-1252377804.cos.ap-beijing.myqcloud.com/elasticsearch-6.2.4-with-ik.zip`，已经集成了ik分词器,放在了d盘 `es-for-yoota` 文件夹里，其中es已经安装到了windows服务里，开机自动运行，`E:\es-for-yotta\yotta-spring-boot-yotta-es-search\yotta-spring-boot\yotta-spring-boot`文件夹里的是现在的工程文件，这个以后位置随意
    - es head
        -   可以看整体情况，网址是 https://xjtushilei.com/es-head/ ，只能在es安装的机器上打开该网址查看。外网禁止查看。
   
    - 文档参考
        - 官方文档6.2版本  ，使用了java客户端进行操作：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.2/java-rest-high.html    
        
- 编译
```
mvnw package
```  
- 运行

```
java -jar target/yotta-search-1.0.jar
```

