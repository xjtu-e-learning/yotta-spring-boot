#需要写统计API统计
>1. getDomainInfo 新：getStatisticalInformation
>2. getTopicByDomain
>3. getTopicDetails 新：getStatisticalInformationByDomainNameAndTopicName
>4. getDomainInfoBySubject 新：getStatisticalInformationBySubjectName
>5. getTopicInfoByDomain 新：getStatisticalInformationByDomainName
>6. getFragmentCountBySource(原API在Spider下) 新:getAssembleDistributionByDomainNameAndTopicNames
>7. getWordcount(原API在Spider下) 新：getWordFrequencyBySourceNameAndDomainNameAndTopicNames
>8. getFragmentByTopicArray(原API在Spider下) 新：getAssemblesByDomainNameAndTopicNames
>9. getDomainManage(包含学科名、课程名、课程id、主题数、
一级分面、二级分面和三级分面数、碎片数、依赖数(dependence)、上下位关系数(relation))(原API在domain下) 新：getDomainDistribution
>10. queryKeyword（原API在DomainAPI）新：queryKeyword