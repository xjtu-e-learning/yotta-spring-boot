#需要写主题API统计
>1. getTopicRelation(主题上下位关系，在前端：知识主题爬取)
>2. getDomainTopicAll/getDomainTerm(原接口在FacetAPI下) 新：getTopicsByDomainName
>3. getDomainTermInfo 新：
>4. updateTermName 新：updateTopicByTopicName
>4. deleteTermName 新：deleteDomainByNameAndTopicName（注：删除过程的事务一致性问题待解决）
>5. createTopic 新：insertTopicByNameAndDomainName
>6. getTreeByTopicForFragment（原api在assemble下） 新：getCompleteTopicByNameAndDomainNameWithHasFragment
>7. getTreeByDomain（原api在assemble下） 新：getFirstTopicByDomianName


error:关于无穷递归
//不添加以下注解会导致FacetContainAssemble的无限递归
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "facetId")
//@JsonIgnoreProperties(value = "children")
//@JsonBackReference
