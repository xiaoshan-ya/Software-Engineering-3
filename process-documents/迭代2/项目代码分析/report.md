# Report

# 发现的bug

### 1.

![image-20230406113357453](https://typora-tes.oss-cn-shanghai.aliyuncs.com/uPic/20230406113358image-20230406113357453.png)

![image-20230508160217945](https://typora-tes.oss-cn-shanghai.aliyuncs.com/picgo/image-20230508160217945.png)

LINE：583

iPos <= iLastChar - 1，在未修改之前是iPos <= iLastChar ，会导致在用substring的时候溢出。

### 2.

![](https://s3.bmp.ovh/imgs/2023/04/18/981cf127831c2351.png)

项目没有使用到SlangLookupTable缩写词汇表，属于功能性bug。

修改：新增了SlangLookupTable类和对应方法，在ClassificationResources中进行了对应的初始化。

![](https://s3.bmp.ovh/imgs/2023/04/18/283a858bd9acfff4.png)

### 3.

冗余类

![image.png](https://typora-tes.oss-cn-shanghai.aliyuncs.com/20230420141930.png)

进行删除，对实际运行没有影响。

4.

在Corpus类Line294中：

Scale模式用了igTrinaryCorrect，模式不匹配。应该修正为igScaleCorrect。

![__NQXI8VUIBZY4ELG_GHQCQ.png](https://s2.loli.net/2023/04/20/JUWVLsR5hwKI3t9.png)

修改后如下图所示：

![image.png](https://s2.loli.net/2023/04/20/LnyDS5KJrvc2kYp.png)



# 人工和checkstyle分析对比

## 人工检查出来的问题，但checkstyle中没有

1. 魔法数字（Magical Number）

   直接出现的数字

2. 条件判断中不应该有过于复杂的逻辑

## checkstyle中有，但是人工认为没有问题的

1. return次数过多。

   由于部分逻辑问题，如果更改很容易导致原来的代码逻辑出现问题。

2. 方法过长。

   有些方法使用的逻辑，是依靠一个一个进行判断，不可避免的会非常长。

3. public 成员变量

   sentistrength在自己的项目中使用了大量的public成员变量，但是过多，导致更改复杂。其实并不影响程序逻辑和安全性。

4. 未注释的Main方法。

   main方法能够帮助我们进行调试代码中的bug，所以没有注释。

5. System.out.println

   这个输出可能会导致性能瓶颈问题，但是由于原先的jar包是在命令行中输出结果，所以有时候必须使用sout来帮助完成。

# checkstyle检查报告分析

## 1. BoosterWordsList

### Commit1 -> Commit2
#### 定量分析：
closed警告：56
new警告：0
open警告：10
#### 定性分析：
closed警告：

1. 字符串应该使用equals进行比较，而非"=="
2. 数组大括号位置错误
3. if、else、while结构必须使用大括号‘{}’
4. “；”后应有空格
5. "while"后应该有空格
6. “{”应位于方法结尾
7. “}”应位于下一行另一条件分支前

open警告：

1.Return次数最多3次

​	不修改的原因：需要重构方法。

2.当前行匹配非法表达式

​	不修改的原因：可以等后续统一修改

3.方法超过50行

​	不修改的原因：暂时没有想到好的方法修改。

## 2. ClassficationOptions
### Commit1 -> Commit2
#### 定量分析：
closed警告：28个
new警告：0个
open警告：56个
#### 定性分析：
closed警告：

1. 字符串应该使用equals进行比较，而非"=="
2. "while"后应该有空格
3. 本行字符数超过140个

open警告：

1. Return次数最多3次

不修改的原因：需要重构方法。

2. 变量应为private，并配置访问方法。

不修改的原因：需要重构项目中所有涉及次公共变量的方法；并且这个项目中涉及到直接使用公共变量的方法非常多，非常难以修改。

3. 方法超过50行

不修改的原因：暂时没有想到好的方法修改。
## 3. ClassificationResources
### Commit1 -> Commit2
#### 定量分析：
closed警告：22
new警告：7
open警告：30
#### 定性分析：
closed警告：

1. 字符串应该使用equals进行比较，而非"=="
2. 数组括号位置错误
3. if、else、while结构必须使用大括号‘{}’
4. “*”后应有空格
5. “{”应位于方法结尾
6. “}”应位于下一行另一条件分支前
7. 去除了无效导入包

new警告：

1.将某一段极长的初始化代码按方法换行分隔，checkstyle对这几行新增警告,非缺陷。

open警告：

1.Return次数最多3次
	不修改的原因：需要重构方法。

2.当前行匹配非法表达式
	不修改的原因：可以等后续统一修改

3.变量应定义为private并配置访问方法
	不修改的原因：需要重构代码

4.一行字符数超过140个
	不修改的原因：需要拆分重构代码

## 4. ClassificationStatistics
### Commit1 -> Commit2
#### 定量分析：
closed警告：199
new警告：0
open警告：6
#### 定性分析：
closed警告：

1. 数组括号位置错误
2. if、else、while、for结构必须使用大括号‘{}’
3. “*”后应有空格
4. “{”应位于方法名结尾
5. “}”应位于下一行另一条件分支前
6. 去除了无效导入包

open警告：
1.Return次数最多3次
不修改的原因：需要重构方法。
2.当前行匹配非法表达式
不修改的原因：可以等后续统一修改
3.一行字符数超过140个
不修改的原因：后续修改
4.方法名不满足驼峰规则（开头字母大写了）
不修改的原因：public方法，调用处也需要修改，等待后续统一进行
## 5. Corpus
### Commit1 -> Commit2
#### 定量分析：
closed警告：880个
new警告：0个
open警告：26个
#### 定性分析：
closed警告

1. if、for、catch  后应有空格
2. {  应位于前一行，而不是新起一行的开头
3. if、for、catch  结构体用 { } 框起来
4. 当前行有限制字符数，通过分行使得字符数符合要求
5. return次数限制：额外设定布尔值flag，将多个条件判断中的return合并为以一个return判断，如optimiseDictionaryWeightingsForCorpusMultipleTimes方法中的return
6. System.out.print报错改为System.err.print
7. 字符串比较是否相同应使用.equals()进行比较

open警告：

1. setCorpus return次数限制：return返回是在异常catch中return false，无法用统一的布尔值进行统一判断
2. paragraph隐藏属性
3. if嵌套层数限制
4. 当前类2333行，最多1500行，无法修改
## 6. CorrectSpellingsList
### commit1-commit2
#### 定量分析：
closed警告：26个
new警告：0个
open警告：4个
#### 定性分析：
closed警告:

1. {  应位于前一行，而不是新起一行的开头
2. 数组大括号位置错误
3. 'if' 后应有空格
4. 'else' 结构必须使用大括号 '{}'
5.  'while' 结构必须使用大括号 '{}'
6. '}'应该与当前多代码块的下一部分 （if/else‑if/else, do/while 或 try/catch/finally）位于同一行
7.  当前行匹配非法表达式： 'System\.out\.println'
8. Return 次数 超过允许要求

open警告：

1. Return 次数 5 次（最大允许非空虚方法/ 拉姆达： 3 次）：由于方法内部分支较多，直接采用flag等形式return并不合适，所以保留return语句。
2. 当前行匹配非法表达式： 'System\.out\.println'
## 7. EmoticonsList
### commit1-commit2
#### 定量分析：
closed警告：39个
new警告：0个
open警告：7个
#### 定性分析：
closed警告:

1. {  应位于前一行，而不是新起一行的开头
2. 数组大括号位置错误
3. 'if' 后应有空格
4. 'else' 结构必须使用大括号 '{}'
5.  'while' 结构必须使用大括号 '{}'
6. '}'应该与当前多代码块的下一部分 （if/else‑if/else, do/while 或 try/catch/finally）位于同一行
7.  当前行匹配非法表达式： 'System\.out\.println'
8. Return 次数 超过允许要求

open警告：

1. Return 次数 超过允许要求（最大允许非空虚方法/ 拉姆达： 3 次）：由于方法内部分支较多，直接采用flag等形式return并不合适，所以保留return语句。
2. 当前行匹配非法表达式： 'System\.out\.println'
3.  方法 54 行（最多： 50 行）：方法过长，但是这样可读性反而是最高的，因此没有必要缩减到50行。
## 8. EvaluativeTerms
### commit1-commit2
#### 定量分析：
closed警告：54个
new警告：0个
open警告：15个
#### 定性分析：
closed警告:

1. {  应位于前一行，而不是新起一行的开头
2. 变量应定义为 private 的，并配置访问方法
3. Return 次数 超过允许要求
4. 数组大括号位置错误
5. 'if' 后应有空格
6. 'else' 结构必须使用大括号 '{}'
7.  'while' 结构必须使用大括号 '{}'
8. '}'应该与当前多代码块的下一部分 （if/else‑if/else, do/while 或 try/catch/finally）位于同一行
9.  当前行匹配非法表达式： 'System\.out\.println'

open警告：

1. Return 次数 超过允许要求（最大允许非空虚方法/ 拉姆达： 3 次）：由于方法内部分支较多，直接采用flag等形式return并不合适，所以保留return语句。
2. 当前行匹配非法表达式： 'System\.out\.println'
3.  方法 54 行（最多： 50 行）：方法过长，但是这样可读性反而是最高的，因此没有必要缩减到50行。
4. 变量应定义为 private 的：由于在代码的其他地方直接使用了public的变量，因此，暂时变量的公开访问。
## 9. IdiomList
### commit1-commit2
**closed警告：79**

"{"应位于前一行: 21个，"}"应位于当前多代码块下一部分: 4个

if、else、while、try、catch后面应有空格: 25个，应有大括号"{}": 18个

数组大括号"[]"位置错误: 5个

字符串应当用.equals方法比较而非"=="或者"!=": 3个

**open警告：13**

1. 使用system.out.println输出错误提示信息: 7个 

   暂不修改原因：待讨论如何修改

2. return次数过多（5次，最多允许3次): 1个

   暂不修改原因：待讨论是否修改

3. 方法名不符合驼峰命名法: 1个

   暂不修改原因：遗漏

4. 成员变量应定义为private的，并配置访问方法: 4个

   暂不修改原因：待讨论如何修改

### commit2-commit3
closed警告：9

1. 使用system.out.println输出错误提示信息: 7个
2. 修改方式：改用err输出
3. return次数过多（5次，最多允许3次): 1个
4. 修改方式：设置flag标记，统一返回
5. 方法名不符合驼峰命名法: 1个

open警告：4

1. 成员变量应定义为private的，并配置访问方法: 4个
2. 暂不修改原因：经讨论，某些类中private变量过多，为保持一致统一不修改此项

## 10. IronyList
### commit1-commit2
**closed警告：28**

1. "{"应位于前一行: 8个
2. "}"应位于当前多代码块下一部分: 2个
3. if、else、while、try、catch后面应有空格: 8个，应有大括号"{}": 6个
4. 数组大括号"[]"位置错误: 3个
5. 字符串应当用.equals方法比较而非"=="或者"!=": 1个

**open警告：3**

1. 使用system.out.println输出错误提示信息: 2个
2. 暂不修改原因：待讨论如何修改
3. return次数过多（5次，最多允许3次): 1个
4. 暂不修改原因：待讨论如何修改

### commit2-commit3
**closed警告：3**

1. 使用system.out.println输出错误提示信息: 2个
2. 修改方式：改用err输出
3. return次数过多（5次，最多允许3次): 1个
4. 修改方式：设置flag，统一返回

**open警告：0**

## 11. Lemmatiser
### Commit1 -> Commit2
#### 定量分析：
closed警告：44个
new警告：0个
open警告：2个
#### 定性分析：
closed警告：

1.  '{' 应位于前一行
2. 数组大括号位置错误
3. 'if' 结构必须使用大括号 '{}'
4. 'if' 后应有空格
5. 'while' 结构必须使用大括号 '{}'
6. 'while' 后应有空格
7. 字符串应使用equals()方法进行比较，而非'!='
8.  '}'应该与当前多代码块的下一部分 （if/else-if/else, do/while 或 try/catch/finally）位于同一行
9. 'catch' 后应有空格
10. 'else' 结构必须使用大括号 '{}'

open警告：

1. 方法超过50行

不修改的原因：暂时没有想到好的方法修改。

2. Return次数最多3次

不修改的原因：需要重构方法。
### Commit2 -> Commit3
#### 定量分析：

closed警告：2个

new警告：0个

open警告：0个

#### 定性分析：
closed警告：

1. 方法超过50行
2. Return次数最多3次

修改方式：
自定义check方法
## 12. NegatingWordList
### commit1-commit2
**closed警告：24**

1. "{"应位于前一行: 9个，"}"应位于当前多代码块下一部分: 2个
2. if、else、while、try、catch后面应有空格: 7个，应有大括号"{}": 4个
3. 数组大括号"[]"位置错误: 1个
4. 字符串应当用.equals方法比较而非"=="或者"!=": 1个

**open警告：4**

1. 使用system.out.println输出错误提示信息: 3个
2. 暂不修改原因：待讨论如何修改
3. return次数过多: 1个
4. 暂不修改原因：待讨论如何修改

### commit1-commit2
**close警告：4**

1. 用system.out.println输出错误提示信息: 3个
2. 修改方式：改用err输出
3. return次数过多（5次,最多允许3次): 1个
4. 修改方式：设置flag，统一返回

**open警告：0**

## 13. Paragraph
### Commit1 -> Commit2
#### 定量分析：
closed警告：131个
new警告：0个
open警告：23个

#### 定性分析：
closed警告：

1. if、for、catch  后应有空格
2. {  应位于前一行，而不是新起一行的开头
3. if、for、catch  结构体用 { } 框起来
4. 当前行有限制字符数，通过分行使得字符数符合要求
5. 当前方法行数限制，通过抽象一些方法缩短行数，如：抽象出getPodSentiment方法
6. System.out.print报错改为System.err.print
7. 字符串比较是否相同应使用.equals()进行比较

open警告：

1. return次数限制：calculateParagraphSentimentScores方法return次数6次，限制1次
2. calculateParagraphSentimentScores方法当前有240行，最多50行，无法化简到50行以内
3. calculateParagraphSentimentScores方法if嵌套层数限制，限制最多三层，实际五层
## 14. QuestionWords
### Commit1 -> Commit2
#### 定量分析：
closed警告：24个
new警告：0个
open警告：4个
#### 定性分析：
closed警告：

1.  '{' 应位于前一行
2. 数组大括号位置错误
3. 'if' 结构必须使用大括号 '{}'
4. 'if' 后应有空格
5. 'while' 结构必须使用大括号 '{}'
6. 'while' 后应有空格
7. 字符串应使用equals()方法进行比较，而非'!='
8.  '}'应该与当前多代码块的下一部分 （if/else-if/else, do/while 或 try/catch/finally）位于同一行
9. 'catch' 后应有空格

open警告：

1. System.out.println

不修改的原因：暂时还没有修改

2. Return次数最多3次

不修改的原因：需要重构方法。
### Commit2 -> Commit3
#### 定量分析：
closed警告：4个
new警告：0个
open警告：0个
#### 定性分析：
closed警告：
1. System.out.println
修改为System.err.println
2. Return次数最多3次
自定义check方法

## 15. Sentence
### Commit1 -> Commit2
#### 定量分析：
closed警告：96个
new警告：0个
open警告：66个（多为if嵌套次数警告）
#### 定性分析：
closed警告：

1. 数组大括号位置错误
2. '=' 后应有空格
3. 字符串应该使用equals进行比较，而非"=="
4. "while"后应该有空格
5. 'else'后应该有空格
6. 方法长度超过50行
7. 本行字符数超过140个
8. 'if' 结构必须使用大括号 '{}'
9. 'else' 结构必须使用大括号 '{}'
10. 至多3层 if
11. 类型转换后应有空格

open警告：

1. 方法超过50行：要么抽象成粒度更细的方法。但是这些方法没有复用价值，因此不应该修改。
2. System.out.println：之后统一调整为log或者取消。
3. Return次数最多3次：分支过多，可以采用flag的形式。但是return可读性更强
4. 至多3层if：本身得分计算算法涉及到的分支过多，不适合重构if-else的嵌套结构。
## 16. SentimentWords
### Commit1 -> Commit2
#### 定量分析：
closed警告：70个
new警告：0个
open警告：19个（多为System.out.print警告）
#### 定性分析：
closed警告：

1. 数组大括号位置错误
2. 字符串应该使用equals进行比较，而非"=="

修改原因：字符串比较方法有问题

3. "while"后应该有空格

修改原因：代码规范

4. 'else'后应该有空格
5. 方法长度超过50行（部份修改）
6. 本行字符数超过140个
7. 'if' 结构必须使用大括号 '{}'
8. 'else' 结构必须使用大括号 '{}'

open警告：

1. 方法超过50行

不修改的原因：暂时没有想到好的方法修改。

2. System.out.println

修改为System.err.println

3. Return次数最多3次

通过定义boolean变量

4. 至多3层if

不修改的原因：暂时还没有修改
### Commit2 -> Commit3
#### 定量分析：
closed警告：19个
new警告：0个
open警告：0个
#### 定性分析：
closed警告：

1. 方法超过50行

不修改的原因：暂时没有想到好的方法修改。

2. System.out.println

不修改的原因：暂时还没有修改

3. Return次数最多3次

不修改的原因：需要重构方法。

4. 至多3层if

不修改的原因：暂时还没有修改
## 17. SentiStrength
### Commit1 -> Commit2
#### 定量分析：
closed警告：70个
new警告：0个
open警告：111个（多为System.out.print警告）
#### 定性分析：
closed警告：

1. 字符串应该使用equals进行比较，而非"=="

修改原因：字符串比较方法有问题

2. "while"后应该有空格

修改原因：代码规范

3. 未注释的Main方法
4. 'c'隐藏属性

修改原因：代码规范

5. 'for'后应该有空格
6. '+','='后前后应该有空格
7. 类型转换后应该有空格
8. 方法长度超过50行（部份修改）
9. 本行字符数超过140个

open警告：

1. Return次数最多3次

不修改的原因：需要重构方法。

2. 变量应为private，并配置访问方法。

不修改的原因：需要重构项目中所有涉及次公共变量的方法；并且这个项目中涉及到直接使用公共变量的方法非常多，非常难以修改。

3. 方法超过50行

不修改的原因：暂时没有想到好的方法修改。

4. 未注释的Main方法

不修改的原因：还需要这个方式进行调试

5. System.out.println

不修改的原因：暂时还没有修改
### Commit2 -> Commit3
#### 定量分析：
closed警告：101个
new警告：0个
open警告：10个
#### 定性分析：
closed警告：

1. System.out.println

修改原因：代码性能，规范问题。
open警告：

1. Return次数最多3次

不修改的原因：需要重构方法。

2. 变量应为private，并配置访问方法。

不修改的原因：需要重构项目中所有涉及次公共变量的方法；并且这个项目中涉及到直接使用公共变量的方法非常多，非常难以修改。

3. 方法超过50行

不修改的原因：暂时没有想到好的方法修改。

4. 未注释的Main方法

不修改的原因：还需要这个方式进行调试
## 18. Term
### commit1-commit2
**closed警告：21**

1. if、else、while、try、catch后面应有空格: 7个，
2. 单行中字符过多（指定最多140个）: 6个
3. 字符串应当用.equals方法比较而非"=="或者"!=": 7个
4. switch块未定义default: 1个

**open警告：7**

1. return次数过多: 5个

   暂不修改原因：待讨论如何修改

2. if层数过多 (4层，最多允许3层): 1个

   暂不修改原因：遗漏

3. 方法过长: 1个

   暂不修改原因：遗漏

### commit2-commit3
**closed警告：7**

1. return次数过多: 5个

   修改方式：设置flag，统一返回

2. if层数过多 (4层，最多允许3层): 1个

   修改方式：合并冗余的判断，减少层数

3. 方法过长: 1个

   修改方式：提取出一部分逻辑作为子方法

**open警告：0**

## 19. Test
### Commit1 -> Commit2
#### 定量分析：
closed警告：16个
new警告：0个
open警告：6个
#### 定性分析：
closed警告：

1. for结构必须使用"{}"
2. "{"必须位于前一行
3. 数组大括号位置错误
4. "for"后应该有空格
5. "if"后应该有空格
6. "while"后应该有空格
7. 本行字符数超过140个

open警告：

1. System.out.println

不修改的原因：暂时还没有修改
### Commit2 -> Commit3
#### 定量分析：
closed警告：6个
new警告：0个
open警告：0个
#### 定性分析：
closed警告：

1. System.out.println

修改原因：代码性能，规范问题。
open警告：
无
## 20. TextParsingOptions
### Commit1 -> Commit2
#### 定量分析：
closed警告：2个
new警告：0个
open警告：4个
#### 定性分析：
closed警告：

1. "{"必须位于前一行

open警告：

1. 变量应为private，并配置访问方法。

不修改的原因：需要重构项目中所有涉及次公共变量的方法；并且这个项目中涉及到直接使用公共变量的方法非常多，非常难以修改。
### Commit2 -> Commit3
#### 定量分析：
closed警告：0个
new警告：0个
open警告：4个
#### 定性分析：
open警告：

1. 变量应为private，并配置访问方法。

不修改的原因：需要重构项目中所有涉及次公共变量的方法；并且这个项目中涉及到直接使用公共变量的方法非常多，非常难以修改。
## 21. UnusedTermsClassificationIndex
### Commit1 -> Commit2
#### **定量分析：**
closed警告：151个
new警告：0个
open警告：0个
#### **定性分析：**
closed警告：

1. if、for、catch  后应有空格
2. {  应位于前一行，而不是新起一行的开头
3. if、for、catch  结构体用 { } 框起来
4. 当前行有限制字符数
5. System.out.print报错改为System.err.print
