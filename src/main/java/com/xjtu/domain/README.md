
#需要写课程API统计
>1. getDomainManage
>2. countClassNum 新：countDomains
>3. createClass
>3. queryKeyword
>4. getDomainManage(包含学科名、课程名、课程id、主题数、
一级分面、二级分面和三级分面数、碎片数、依赖数(dependence)) 新API在statistics模块下getDomainDistribution
>5. getDomainInfo（根据课程名，查询该课程下面主题，以及分面按树状组织） 新：getDomainTreeByDomainName
>6. getDomain(原DependencyAPI下，查询所有课程) 新：getDomains

> ## 访问课程信息的API
> 
> 1.   本目录下主要是实现访问课程信息的API
> 2.   目前与原版相对照,还有以下API未完成
 >>* 读取domain，得到所有领域名和各领域下主题、分面、碎片、关系的数量
 >>* 插入一门课程
 >>* 按课程转换RDF数据