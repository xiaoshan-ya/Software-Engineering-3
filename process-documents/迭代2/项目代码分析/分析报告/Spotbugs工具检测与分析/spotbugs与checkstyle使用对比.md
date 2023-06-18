spotbugs与checkstyle使用对比：

前言：spotbugs是findbugs的继任，2020年后findbugs不再在idea插件中维护，改用spotbugs。

一、spotbugs一些警告类型：

- Bad practice 坏的实践：常见代码错误，用于静态代码检查时进行缺陷模式匹配
- Correctness 可能导致错误的代码，如空指针引用等
- 国际化相关问题：如错误的字符串转换
- 可能受到的恶意攻击，如访问权限修饰符的定义等
- 多线程的正确性：如多线程编程时常见的同步，线程调度问题。
- 运行时性能问题：如由变量定义，方法调用导致的代码低效问题。



二、spotbugs发现而checksytle未能发现的

发现：

1.ClassificationResources中有一个变量sgSlangLookupTable未被初始化使用，考虑到功能的完整性，应属于功能bug，在初始化代码中添加sgSlangLookupTable的初始化。

2.对于nullPointer的条件分支判断缺少



spotbugs主要提示可能导致错误的功能性bug，而checkstyle更多的考虑的是格式问题和代码的优化（如：空格、大括号、方法行数和return数量的控制。

三、对比

checkstyle:

![](https://s3.bmp.ovh/imgs/2023/04/18/651d30003216b612.png)

spotbugs:

![](https://s3.bmp.ovh/imgs/2023/04/18/996e531ecf9722ae.png)

checkstyle优点：

1）粒度小

2）使用群体多，有自定义检查模块

3）主要支持代码风格的检查

4）可导出文档

checkstyle缺点：

1）未对代码问题做出分类，使得修改效率偏低

2）对bug检查的作用几乎没有

spotbugs优点：

1）全方位覆盖

2）对bug查找作用好

3）对代码问题进行了分类

4）可导出文档

spotbugs缺点：

1）用户相对较少，没有很多检查模板可供使用

相同点：都是对代码的静态分析

不同点：checkstyle注重代码风格，spotbugs注重bug检查



综上：结合checkstyle和spotbugs的优缺点，可以在使用checkstyle进行代码风格检查保证规范的同时，使用spotbugs寻找代码中可能存在但通过了静态分析的bug。