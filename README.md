# YujingTube
模仿 youtube与bilibili的练习用项目

演示视频链接: https://wwt.lanzouf.com/ikRQz07wp2zc

## 技术栈

------

|  模块   |                 说明                 |
| :-----: | :----------------------------------: |
|   web   |         从项目分离的前端内容         |
| Common  |            后端的公共模块            |
| Gateway |             后端路由模块             |
| Product | 后端主模块, 管理除用户功能的其它功能 |
|  User   |     用户模块, 管理用户登录注册等     |
|  Admin  |      管理员模块, 管理视频审核等      |

### 后端

|                             框架                             |         说明         |      版本      |
| :----------------------------------------------------------: | :------------------: | :------------: |
|    [Spring Boot](https://spring.io/projects/spring-boot)     |     应用开发框架     | 2.3.12.RELEASE |
|           [Spring Cloud](https://cloud.spring.io/)           |    微服务开发框架    |  Hoxton.SR12   |
| [Spring Cloud Alibaba](https://spring.io/projects/spring-cloud-alibaba/) |    微服务开发框架    | 2.2.7.RELEASE  |
|  [Dubbo](https://dubbo.incubator.apache.org/zh/index.html)   |       RPC框架        |     3.0.6      |
|          [Nacos](https://github.com/alibaba/nacos)           |  服务注册与配置中心  |     2.0.3      |
|                  [Seata](https://seata.io/)                  |    分布式事务框架    |     1.3.0      |
|              [MySQL](https://www.mysql.com/cn/)              |     数据库服务器     |      5.7       |
|    [Dynamic Datasource](https://dynamic-datasource.com/)     |      动态数据源      |     3.5.0      |
|    [Hikari](https://github.com/brettwooldridge/HikariCP)     |      JDBC连接池      |     3.4.5      |
|           [MyBatis Plus](https://mp.baomidou.com/)           |  MyBatis 增强工具包  |     3.5.0      |
|  [Elasticsearch](https://www.elastic.co/cn/elasticsearch/)   |     数据检索框架     |     7.16.2     |
|                  [Redis](https://redis.io/)                  |   key-value 数据库   |     6.2.6      |
|       [Redisson](https://github.com/redisson/redisson)       |    Redis工具框架     |     3.17.0     |
| [Spring MVC](https://github.com/spring-projects/spring-framework/tree/master/spring-webmvc) |       MVC 框架       | 5.2.15RELEASE  |
| [Spring Security](https://github.com/spring-projects/spring-security) |   Spring 安全框架    |  5.3.9RELEASE  |
| [Hibernate Validator](https://github.com/hibernate/hibernate-validator) |     参数校验组件     |  6.1.7.Final   |
|             [Lombok](https://projectlombok.org/)             | 消除冗长的 Java 代码 |    1.18.20     |
|             [jjwt](https://github.com/jwtk/jjwt)             |       JWT框架        |     0.9.1      |
|       [Fastjson](https://github.com/alibaba/fastjson)        |     JSON转换工具     |     1.2.75     |
|     [aliyun-sdk-oss](https://www.aliyun.com/product/oss)     |    云数据存储服务    |     3.10.2     |

### 前端

|                      框架                      |      说明      |  版本  |
| :--------------------------------------------: | :------------: | :----: |
|           [Vue](https://vuejs.org/)            | JavaScript框架 | 2.6.14 |
| [Element UI](https://element.eleme.cn/#/zh-CN) |   桌面组件库   | 2.15.6 |
|   [Vue Router](https://router.vuejs.org/zh/)   |  页面路由类库  | 3.5.3  |
|        [VueX](https://vuex.vuejs.org/)         |  状态管理类库  | 3.4.0  |
|                  particlesjs                   |  粒子效果类库  | 2.2.3  |
|         [moment](http://momentjs.cn/)          |  日期处理类库  | 2.29.1 |
|  [flv.js](https://github.com/bilibili/flv.js)  | flv视频编码库  | 1.6.2  |
|       [dplayer](http://dplayer.js.org/)        | 视频播放器类库 | 1.26.0 |

