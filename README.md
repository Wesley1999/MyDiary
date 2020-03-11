# 简介

由于难以找到让我满意的日记平台，所以自己开发了一个\~

该日记平台的特征：

* 支持markdown
* 添加、删除、加载等动作均有动画效果
* 滚动条接近页面底部时自动加载更多内容
* 网页版完美适配PC端和移动端，另有Android版
* 日记使用AES加密存储到文件，只有通过密码才能读取到文件中的信息
* 日记文件存储在坚果云上，坚果云支持增量备份，可以保存日记文件在三个月内的所以历史版本，所以即使操作失误，也可以找回数据
* 后续会支持更多功能，例如记录位置、设备、天气、心情、直接插入图片等

注意：
* 为解决在Linux上运行出现的乱码问题，目前已将编码改为`GB2312`

本项目使用Apache开源协议。

# 获取代码

```java
git clone git@github.com:Wesley1999/MyDiary.git
```

后端使用SpringMVC，前端使用jQuery、BootStrap、Laydate等轻量级框架。

该项目不使用数据库，所以不需要修改配置，代码可以直接使用Tomcat运行。



# 快速预览

浏览器打开https://diary.wangshaogang.com/

可以直接点击登录按钮，登录预览账户。

也可以用自己的坚果云账号，添加WebDAV应用密码，在本网站中初始化后登录，参考：http://help.jianguoyun.com/?p=2064

另有套壳浏览器的Android版本，下载地址：https://file-wsg.oss-cn-shanghai.aliyuncs.com/MyDiary-v1.0-20200216-public.apk

如有疑问或建议，可联系我微信：wsg1827

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/576f9180-8ce6-430c-a897-8b324e69dd16.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/576f9180-8ce6-430c-a897-8b324e69dd16.png)



## 一些截图

前端使用BootStrap响应式布局，同时支持PC端和移动端：

首页：

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/80f66dc8-826f-40e9-8036-f95d91ee5e93.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/80f66dc8-826f-40e9-8036-f95d91ee5e93.png)



添加、编辑日记：

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/4353e785-9381-4412-adac-b76f10c07747.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/4353e785-9381-4412-adac-b76f10c07747.png)



筛选：

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/427a15ee-5d63-4bd7-a4bd-92761942fcd2.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/427a15ee-5d63-4bd7-a4bd-92761942fcd2.png)



当编辑的内容不保存直接返回，或强制刷新页面时，会弹窗提醒：

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/f36eff2a-e29c-4e02-9620-8a5bab6f9291.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/f36eff2a-e29c-4e02-9620-8a5bab6f9291.png)

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/0a361dbf-063a-45e4-b89b-cc4107f78183.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/0a361dbf-063a-45e4-b89b-cc4107f78183.png)



日记内容支持Markdown：

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/681746a3-bb81-4418-8302-831d3b1bfbff.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/681746a3-bb81-4418-8302-831d3b1bfbff.png)

![https://pic-wsg.oss-cn-shanghai.aliyuncs.com/388fd743-3405-49a7-bdb4-dc0e20e17469.png](https://pic-wsg.oss-cn-shanghai.aliyuncs.com/388fd743-3405-49a7-bdb4-dc0e20e17469.png)