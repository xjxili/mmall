# mmall
项目初始化</br>
执行init.sql文件完成数据库和数据的初始化</br>
包含用户模块 / 品类模块 / 商品模块 /购物车模块 </br>

环境：win7 64位 / jdk1.8.0_162 /tomcat9 </br>
开发工具: Idea 2017 / FTPServer </br>
项目管理：github / Maven3 </br>
技术栈：SSM/Guava/Jackson/Joda-Time/注解 </br>
用户模块/分类模块/商品模块/购物车模块/收货地址/订单模块 </br>
1.用户模块： </br>
解决部分越权问题 </br>
对密码进行MD5盐值加密、guava缓存Token验证是否有权限修改密码 </br>
返回JSON采用高复用服务响应对象的设计思想和封装 </br>
2.分类模块： </br>
递归算法算出所有子节点,封装成适用于前台layUI的VO对象 </br>
复杂对象排重——使用Set集合并重写对象equls和hashcode方法 </br>
3.商品模块: </br>
POJO、VO抽象模型 </br>
高效分页及动态排序——PageHelper插件 </br>
FTP服务对接——上传到指定的图片服务器 </br>
4.购物车模块 </br>
商品总价计算复用封装   </br>
高复用的逻辑方法封装思想 ——一条pojo模型 封装成CartProductVo 进一步封装整个购物车CartVo </br>
解决商业运算丢失精度的坑 ——使用BigDicimal String构造器初始化进行封装进行基本运算 </br>
