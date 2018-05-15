#需要写碎片API统计
>1. getTreeByTopicForFragment(前端：分面树构建;输入：课程名、主题名；应该在topicAPI下)
>2. countClassNum
>3. createClass
>3. queryKeyword
>4. getDomainTermFacet2Fragment(原SpiderAPI，获取二级分面下的碎片) 新：getAssemblesInSecondLayerFacet
>5. getDomainTermFacet1Fragment(原SpiderAPI，获取一级分面下的碎片) 新：getAssemblesInFirstLayerFacet
>5. getDomainTermFacet3Fragment(原SpiderAPI，获取一级分面下的碎片) 新：getAssemblesInThirdLayerFacet
>5. getDomainTermFragment(原SpiderAPI，获取主题下的碎片) 新：getAssemblesInTopic
>6. createFragment(原SpiderAPI，添加碎片到碎片暂存表（Temporary Tables）中)  新：insertTemporaryAssemble
>6. addFacetFragment(原SpiderAPI，从暂存表中添加碎片到碎片表中，删除暂存表中的碎片) 新：insertAssemble
>7. getFragment(原SpiderAPI，根据用户名从暂存表中查询碎片) 新：getTemporaryAssemblesByUserName
>8. deleteFragment(原SpiderAPI，根据碎片Id，从碎片表中删除碎片) 新：deleteAssemble
>9. updateFragment(原SpiderAPI，根据碎片Id，更新暂存表中的碎片内容) 新：updateTemporaryAssemble
>10. deleteUnaddFragment(原SpiderAPI，根据碎片Id，从碎片暂存表中删除碎片) 新：deleteTemporaryAssemble
>11. getFragmentByID(原SpiderAPI，根据碎片Id，从碎片暂存表中查询碎片) 新：getTemporaryAssembleById
>12. getAssembleFragmentByID(原SpiderAPI，根据碎片id从碎片表中查询碎片) 新：getAssembleById



#碎片表assemble设计
>1. assemble_id  碎片id
>2. assemble_content 碎片内容(保留html格式)
>3. assemble_text 碎片文本(仅保留文本)
>4. assemble_scratch_time 爬取时间
>5. facet_id 分面id