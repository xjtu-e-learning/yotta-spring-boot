#智慧教育项目

>表一：(MappingDomian)与网院联合，需要设计一个课程对应表,对应课程信息;

>表二：(Recommendation)表id，推荐主题列表，（暂定由冒号和逗号隔开）、课程id、用户id
>* recommendation_id：主键，Long
>* domain_id：课程id，Long
>* topic_names：主题Map，String(主题id1:0,主题id2:0,主题id3:0,主题id4:0)
>* user_id：用户id，Long
>* recommendation_type：推荐方式，Integer

