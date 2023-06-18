# PMD报告

#### 1. 在变量命名方面的不同

PMD对于变量名的命名不能过长，也不能过短，对于长度有所限制，但是checkstyle对于变量名没有限制

#### 2. 对于变量类型声明

PMD对于未初始化的变量名建议声明成final类型，但是checkstyle没有限制

PMD对于局部变量建议声明成final类型，但是checkstyle没有限制

#### 3. 对于括号的检查

PMD对于多余的 "()"会检查错误，但是checkstyle不会

#### 4. 对于void函数return语句

PMD和checkstyle都限制了对于函数return语句的次数限制；

但是PMD额外规定了return语句应为函数的最后一条语句；checkstyle只是对于void函数有次数限制

#### 5. 对于默认的访问修饰符

PMD不允许没有指定public、private、protected的变量定义，所以应给变量加上访问修饰符；checkstyle同样对于此种变量定义做了限制

#### 6. 对于导入的无用包

PMD和checkstyle都不允许导入一些无用的java包

#### 7. 对于不必要的构造函数

PMD和checkstyle都建议避免使用不必要的构造函数，编辑器会为你生成构造函数

#### 8. 对于if-else语块的大括号建议

PMD和checkstyle都建议为语块添加 "{}"和"}"

#### 9. 对于数字字面量检查

PMD要求对于数字字面量添加下划线以提高可读性；checkstyle没有作限制

#### 10.对于具有特定语义函数名的返回值类型检查

PMD要求类似名为“setXXX”的函数除void外不应有其他类型的返回值；checkstyle没有限制

#### 11.对于if-else语块的条件建议

PMD建议尽量避免在if…else…时先判断!=，从语法上来说先判断==，再else更有利于理解；checkstyle没有限制

#### 12.对于类属性声明位置的要求

PMD要求类属性应在类的顶部声明，即在任何方法声明、构造函数、初始化和内部类前；checkstyle不要求