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
* XSS攻击问题尚未完全解决，但不影响个人使用

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

![](https://oss-pic.wangshaogang.com/1586693281393-7d2f15e1-d162-4049-b10b-e35bb0f7f1b6.png)



## 一些截图

前端使用BootStrap响应式布局，同时支持PC端和移动端：

首页：

![](https://oss-pic.wangshaogang.com/1586693281394-6e973c2e-34f1-4b0e-b8f0-1ddfd705c479.png)



添加、编辑日记：

![](https://oss-pic.wangshaogang.com/1586693281395-83e9579d-be08-4fa2-a954-54212cd03968.png)



筛选：

![](https://oss-pic.wangshaogang.com/1586693281396-5bba7f25-b82f-4f07-8c06-91dd3b1846a2.png)



当编辑的内容不保存直接返回，或强制刷新页面时，会弹窗提醒：

![](https://oss-pic.wangshaogang.com/1586693281396-72582960-172e-486a-ba93-5c6cfc322057.png)



日记内容支持Markdown：

![](https://oss-pic.wangshaogang.com/1586693281396-5bd85676-eb17-461e-b3bc-d5d46dd8f57a.png)

![](https://oss-pic.wangshaogang.com/1586693281397-63f82439-b688-4f39-b245-22bad6b1cf37.png)
