# 亿级高并发电商项目

该项目采用前后端分离的架构是一款常见的电商网站。前端开发主要采用vue.js，后端开发使用Dubbo进行分布式调用。该项目包含电商项目的大部分功能，包含商家端和用户端。 商家端有权限管理、商品管理、广告管理等功能。用户端有用户注册和登录、搜索商品、添加购物车、商品下单、支付、秒杀商品等功能。

# 使用技术栈

服务器操作系统：CentOS7
JAVA版本：JDK11
数据库：Mysql+Navicat
分布式调用：Dubbo+zookeeper
后端框架：SpringBoot+Spring MVC+Mybatis-Plus
权限控制：Spring Security 分布式鉴权：JWT
分布式文件存储：FastDFS Nosql：Redis
搜索引擎：ElasticSearch+Kibana
容器化技术：Docker
短信平台：阿里短信平台 支付平台：支付宝 工具类 其他技术：龙目岛

# 模块介绍

1. `shopping_admin_service、shopping_brand_service、shopping_file_service、shopping_category_service`这些都属于后台 api模块`shopping_manager_api`
2. `shopping_category_service`属于广告api模块，广告api模块可以根据压力模型拆分为高频高并发场景`shopping_category_customer_api`
3. `shopping_user_service、shopping_message`属于前台用户API`shopping_user_customer_api`
4. `shopping_search_service`属于搜索 API`shopping_search_customer_api`
5. `shopping_cart_service`属于购物车 API`shopping_cart_customer_api`
6. `shopping_order_service、shopping_pay_service`属于订单API`shopping_order_customer_api`
7. `shopping_seckill_service`属于秒杀api`shopping_seckill_customer_api`

# 安装服务

### 安装码头工人

```
yum install -y docker
```



### 安装MySQL

```
docker pull mysql:5.7
docker run -id --name=mysql -p 3306:3306 MYSQL_ROOT_PASSWORD=root mysql:5.7
```



### 安装动物园管理员

```
docker pull zookeeper:3.5.9
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.5.9
```



### 安装Dubbo-admin

```
docker pull docker.io/apache/dubbo-admin
docker run -d --name dubbo-admin -p 9600:8080 -e admin.registry.address=zookeeper://ip地址:2181 
-e admin.config-center=zookeeper://ip地址:2181 -e admin.metadata-report.address=zookeeper://ip地址:2181 --restart=always docker.io/apache/dubbo-admin
```



### 安装瑞迪斯

```
docker pull redis:6.2.6
docker run -id --name=redis -p 6379:6379 redis:6.2.6
```



### 安装fastdfs

该镜像自带nginx并且已经配置好了nginx，通过即可访问文件`http://ip地址:8888/文件路径`

```
docker pull delron/fastdfs
docker run -dti --network=host --name tracker --privileged=true -v /var/fdfs/tracker:/var/fdfs -v /etc/localtime:/etc/localtime delron/fastdfs tracker
docker run -dti --network=host --name storage --privileged=true -e TRACKER_SERVER=ip地址:22122 -v /var/fdfs/storage:/var/fdfs -v /etc/localtime:/etc/localtime delron/fastdfs storage
```



### 安装rabbitmq

内部集成了erlang语言

```
# 拉取镜像
docker pull rabbitmq:3.9
# 运行容器
docker run -d --hostname=myrabbit --name=rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9
# 进入容器
docker exec -it rabbitmq /bin/bash
# 开启管控台服务
rabbitmq-plugins enable rabbitmq_management
```



浏览器输入访问rabbitmq，用户名和密码都是guest`http://ip地址:15672`

### 安装ElasticSearch以及分词器

```
docker pull elasticsearch:7.17.0
docker network create elastic
# 创建ES容器
docker run --restart=always -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms512m -Xmx512m" --name='elasticsearch' --net elastic --cpuset-cpus="1" -m 1G -d elasticsearch:7.17.0
# 进入容器
docker exec -it elasticsearch /bin/bash
# ik分词器
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.17.0/elasticsearch-analysis-ik-7.17.0.zip
# 拼音分词器
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v7.17.0/elasticsearch-analysis-pinyin-7.17.0.zip
# 重启服务
docker restart elastcisearch
docker restart kibana
```



### 安装kibana

```
docker pull kibana:7.17.0
docker run -d --name kibana --link elasticsearch:elasticsearch --net elastic -p 5601:5601 kibana:7.17.0
```

# 项目截图

<img width="1279" alt="2eaf3a9aac1470d7f6207b1c9a5a98a" src="https://github.com/ErYueWink/shopping/assets/133645961/de60d987-7b5e-4c0a-a0ed-ae5374a13428">

<img width="1280" alt="57a9a8fca111b4020e6a69937370e0c" src="https://github.com/ErYueWink/shopping/assets/133645961/716e8448-4d04-4c69-84be-954b8149dd3f">

![e1db2289d33b945b6d58fb7a5a4bbcb](https://github.com/ErYueWink/shopping/assets/133645961/48f973b4-9da8-4120-8f1c-17643830f9d7)
![7e4300401a8beb6d1cee6eb5f3a2e13](https://github.com/ErYueWink/shopping/assets/133645961/95347a12-fcff-4466-9ec5-e8fac3fd09e6)
![347c2adddb48980abe475a2b854d289](https://github.com/ErYueWink/shopping/assets/133645961/2e2bbd85-b828-41c7-a4bb-cb71c8e57586)

