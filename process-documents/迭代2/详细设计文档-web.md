## 文档修改历史

| **修改人员** | **日期**        | **修改原因**             | **版本号** |
| ------------ | --------------- | ------------------------ | ---------- |
| 张若皓       | 2023.3.27 16:15 | 初始化                   | 1.0        |
| 郑杰         | 2023.4.4 17:00  | 产品概述及体系结构初始化 | 1.1        |
| 郑杰         | 2023.4.10 15:15 | 结构视角+图片            | 1.2        |

## 1.引言

### 1.1编制目的

本报告详细完成对sentiment项目的详细设计，达到知道后续软件构造的目的，同时实现和测试人员和用户的沟通。本报告面向开发人员、测试人员及最终用户而编写，是了解系统的导航。

### 1.2词汇表

| **词汇名称**  | **词汇含义**                               | **备注** |
| ------------- | ------------------------------------------ | -------- |
| SentiStrength | 基于已建立的词典和算法分析文本情绪值的工具 | 即本项目 |
|               |                                            |          |

### 1.3参考资料

[1] 骆斌,丁二玉,刘钦.软件工程与计算.卷二, 软件开发的技术基础[M].北京:机械工业出版社,2013.

## 2.产品概述

### 2.1 产品概述

SentiStrength是根据社交网站上的评论开发的工具。它的核心功能是使用基于字典的算法来分析文本情绪。 具体来说，它首先根据情绪字典将先验情绪得分指定给单词，然后用几个启发式规则调整指定结果。 它可以为每个输入文本给出一个情感分数对（ρ，η），其中ρ表示文本的正分数，η表示负分数。

### 2.2 概念术语描述

**文本情绪**：以一组值(p, n)（p表示positive，n表示negative数值）度量的， 文本的情绪分数,p值越大则积极情绪越强，n值越大则负面情绪越强

**助推词/加强词（booster word）**：助推词指增强或减少后续单词情绪的单词，无论是积极的还是消极的。增强的例如“非常”，减弱的例如“有些”。

**否定词（negative word）**：否定词指反转后续词语情绪（包含前面的所有助推词情绪）的词。例如“不是”。

**情感符号(emoticon lists)**：为常见的颜文字表情赋予情绪值。如：(^-^)被赋值为+1。

**习语表（idiom list）**：为常见的习语赋予情绪值。如：how are you被赋值为+2。

**关联性（correlation）**：通过计算预测值与正确值的相关性来判断准确率，本应用使用了Pearson相关系数。

**混淆矩阵（confusion table）**：在机器学习领域，混淆矩阵（**confusion matrix**），又称为可能性表格或是错误矩阵。它是一种特定的矩阵用来呈现算法性能的可视化效果，通常是监督学习。其每一列代表预测值，每一行代表的是实际的类别。这个名字来源于它可以非常容易的表明多个类别是否有混淆（也就是一个**class**被预测成另一个**class**）。

### 2.3基本设计描述

1、能够输入一个单词或者一段话，获取其文本情绪2、能够选择语言3、能够批量导出测试结果

## 3.体系结构设计概述

### 3.1逻辑视角

在Sentiment项目中，我们选择了分层体系结构，将系统分为了2层——展示层（UI），业务逻辑层（数据处理）。展示层包含了前端UI的实现和按钮接口；业务逻辑层包含了业务逻辑处理的实现，主要是情绪值计算分析的功能实现。数据层负责数据的持久化和访问，但在本项目中无需数据的持久化和访问，而是通过临时上传文件进行分析，因此无需数据库和数据层。

分层体系结构的逻辑视角如下所示：

![img](https://cdn.nlark.com/yuque/0/2023/png/35573185/1681805894937-1ea48bd1-efec-439f-9f5b-0ec1ef9c0439.png)

### 3.2组合视角

3.2.1开发包图

![img](https://s2.loli.net/2023/04/20/mksoI8KCfMEYnO4.png)

### 3.3接口视角

由于该项目结构简单，可参照4.1中的模块内部接口。

### 3.4信息视角

## 4.结构视角

### 4.1业务逻辑层的分解

4.1.1web模块

4.1.1.1模块概述

web模块承担了在网页端调用接口，传递接受文件，分析情绪值（业务逻辑）并生成返回值的功能。

4.1.1.2整体结构

根据整体结构的设计，我们将web模块分为展示层，业务逻辑层。每⼀层之间为了增加灵活性，我们会添加接⼝。⽐如展示层和业务逻辑层之间，我们添加Service接⼝。为了隔离业务逻辑职责和逻辑控制职责，我们增加了Controller，这样 Controller会把业务逻辑处理委托给ServiceImpl对象。

![img](https://cdn.nlark.com/yuque/0/2023/png/35573185/1681805894939-0a9d1238-a5d5-4770-a7e4-e7fb00b6d50e.png)

web模块各个类的职责如上所示。

4.1.1.3模块内部类接口规范

**TextService相关规范**

提供的服务（供接口）

| **TextService.analyzeText** | **语法** | **Result analyzeText(String text)** |
| --------------------------- | -------- | ----------------------------------- |
|                             | 前置条件 | 用户输入文本                        |
|                             | 后置条件 | 返回分析结果给用户                  |

| **TextService.analyzeFile** | **语法** | **ResponseEntity****<InputStreamResource>****analyzeFile(HttpServletResponse response, MultipartFile file, HttpServletRequest httpServletRequest)** |
| --------------------------- | -------- | ------------------------------------------------------------ |
|                             | 前置条件 | 用户上传指定格式文件，服务器成功响应                         |
|                             | 后置条件 | 返回给用户分析结果文件                                       |

| **TextService.setOptions** | **语法** | **Result setOptions(String[] options)**  |
| -------------------------- | -------- | ---------------------------------------- |
|                            | 前置条件 | 用户选择选项                             |
|                            | 后置条件 | 程序根据用户的选择进行对应的分析选项设置 |

| **TextService.runMachineLearning** | **语法** | **ResponseEntity****<InputStreamResource>****runMachineLearning(HttpServletResponse response, MultipartFile file, HttpServletRequest httpServletRequest)** |
| ---------------------------------- | -------- | ------------------------------------------------------------ |
|                                    | 前置条件 | 用户上传指定格式文件，服务器成功响应                         |
|                                    | 后置条件 | 返回给用户运行状态，程序对用户上传的文本进行机器学习         |

需要的服务（需接口）

无需数据库层

**TextController相关规范**

提供的服务（供接口）

| **TextController.analyzeText** | **语法** | **Result****<String>****analyzeText(@RequestBody String text)** |
| ------------------------------ | -------- | ------------------------------------------------------------ |
|                                | 前置条件 | 用户输入文本                                                 |
|                                | 后置条件 | 返回分析结果给用户                                           |

| **TextController.analyzeFile** | **语法** | **ResponseEntity****<InputStreamResource>****analyzeFile(HttpServletResponse response, @RequestParam MultipartFile file, HttpServletRequest httpServletRequest)** |
| ------------------------------ | -------- | ------------------------------------------------------------ |
|                                | 前置条件 | 用户上传指定格式文件，服务器成功响应                         |
|                                | 后置条件 | 返回给用户分析结果文件                                       |

| **TextController.setOptions** | **语法** | **Result****<String>****setOptions(@RequestParam("options") String[] options)** |
| ----------------------------- | -------- | ------------------------------------------------------------ |
|                               | 前置条件 | 用户选择选项                                                 |
|                               | 后置条件 | 程序根据用户的选择进行对应的分析选项设置                     |

| **TextController.runMachineLearning** | **语法** | **ResponseEntity****<InputStreamResource>****runMachineLearning(HttpServletResponse response, @RequestParam MultipartFile file, HttpServletRequest httpServletRequest)** |
| ------------------------------------- | -------- | ------------------------------------------------------------ |
|                                       | 前置条件 | 用户上传指定格式文件，服务器成功响应                         |
|                                       | 后置条件 | 返回给用户运行状态，程序对用户上传的文本进行机器学习         |

需要的服务（需接口）

| **服务名**                     | **服务**                       |
| ------------------------------ | ------------------------------ |
| TextService.analyzeText        | 分析文本情绪                   |
| TextService.analyzeFile        | 分析用户上传的文件             |
| TextService.setOptions         | 设置分析选项                   |
| TextService.runMachineLearning | 根据用户上传的文件进行机器学习 |

4.1.1.4业务逻辑层的动态模型

![img](https://cdn.nlark.com/yuque/0/2023/png/35573185/1681805894448-9acb0e06-3f08-4281-91d3-4e23549e2729.png)

## 5.依赖视角

![img](https://s2.loli.net/2023/04/20/mksoI8KCfMEYnO4.png)
![img](https://s2.loli.net/2023/04/20/WLsqIrbXa1Vnc7D.png)
