# tmall
# 1.项目简介
>天猫整站是一个模仿天猫的购物网站，项目包含前台的产品展示，后台的用户管理等。 
  
>本项目没有使用SSH，SSM框架，而是使用J2EE整套技术来作为解决方案。项目中借助反射等技术，采用了精妙的设计模式。   

# 2.开发流程  
### [2.1需求分析](#3需求分析)
### [2.2表结构设计](#4表结构设计)  
### [2.3原型](#5原型)
### [2.4实体类设计](#6实体类设计)  
### [2.5DAO类设计](#7DAO类设计)
### [2.6后台 - 分类管理](#8后台-分类管理)
### [2.7后台 - 其他管理](#9后台-其他管理)
### [2.8前台 - 首页](#10前台-首页)
### [2.9前台 - 无需登录](#11前台-无需登录)  
### [2.10前台 - 需要登录](#12前台-需要登录)

# 3.需求分析
>需求分析主要分为3类
#### 3.1前端展示  
>在前端页面显示数据，如首页，产品页，购物车，产品分类等。  
#### 3.2前端交互  
>通过POST,GET等http协议，与服务端进行同步或者异步数据交互。  
#### 3.3后台功能  
>对前台需要用到的数据，进行管理维护。  

# 4.表结构设计  
>表的关系如下  
![表的关系](https://github.com/gucheng86/tmall/blob/master/img/table.png)

# 5.原型设计  
>后端的分类管理  
![分类管理](https://github.com/gucheng86/tmall/blob/master/img/category.png)  
  
>前端的首页  
![首页](https://github.com/gucheng86/tmall/blob/master/img/fore.png)  
  
#6.实体类设计  
>实体类与数据库中的表相互映射。
[实体类](https://github.com/gucheng86/tmall/tree/master/src/tmall/bean)

#7.DAO类设计  
>####工具类  
>[DBUtil类](https://github.com/gucheng86/tmall/blob/master/src/tmall/util/DBUtil.java)用于连接数据库，[DateUtil类](https://github.com/gucheng86/tmall/blob/master/src/tmall/util/DateUtil.java)主要是用于java.util.Date类与java.sql.Timestamp 类的互相转换。  

>####DAO类  


[回到顶部](#readme)
