### @mapper默认映射规则
- 同类型且同名属性会自动映射
- mapStruct会自动进行类型转换
- 八种基本类型和他们的包装类之间的转换
- 八种基本类型（包括他们的包装类型）和string之间的自动转换
- 日期类型和string类型自动转换
- 对于日期和数字我们一般需要自己定义转换规则，比如：格式化规则，保留几位小数