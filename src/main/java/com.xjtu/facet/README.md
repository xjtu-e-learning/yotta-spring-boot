#需要写分面API统计
>2. getTermFacet/getDomainTermFacet1(和6似乎功能相同) 新：getFacetsInTopic
>3. createFacet1(根据课程名、主题名、以及一级分面名创建一级分面) 新：insertFirstLayerFacet
>4. createFacet2(根据课程名、主题名、以及一二级分面名创建二级分面) 新：insertSecondLayerFacet
>5. createFacet3 新：insertThirdLayerFacet
>6. getTopicFacet 新：getSecondLayerFacetGroupByFirstLayerFacet
>7. getFacet1Facet2Num 新：getSecondLayerFacetNumber
>8. getFacet2Facet3Num 新：getThirdLayerFacetNumber
>9. getTermFacet1Fragment（获取一级分面下的碎片数量） 新：getAssembleNumbersInFirstLayerFacet
>10. getTermFacet1/getDomainTermFacet2（获取一级分面下的所有分面） 新：getFacetsInFirstLayerFacet
>11. updataFacet1 新：updateFirstLayerFacet
>12. updataFacet2 新：updateSecondLayerFacet
>13. updataFacet3 新：updateThirdLayerFacet
>14. deleteFacet1 新：deleteFirstLayerFacet
>15. deleteFacet2 新：deleteSecondLayerFacet
>16. deleteFacet3 新：deleteThirdLayerFacet
