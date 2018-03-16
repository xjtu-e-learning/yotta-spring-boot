#需要写分面API统计
>1. getDomainInfo
>2. getTermFacet(和6似乎功能相同) 新：getFacetsByDomainNameAndTopicName
>3. createFacet1(根据课程名、主题名、以及分面名创建一级分面) 新：insertFirstLayerFacet
>4. createFacet2(根据课程名、主题名、以及分面名创建二级分面) 新：insertSecondLayerFacet
>5. createFacet3 新：insertFacet(facetName,facetLayer,parentFacetId,topicId)
>6. getTopicFacet 新：getSecondLayerFacetGroupByFirstLayerFacet
>7. getFacet1Facet2Num 新：getSecondLayerFacetNumber
>8. getFacet2Facet3Num 新：getThirdLayerFacetNumber
