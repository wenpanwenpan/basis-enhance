## 异常功能增强
### 一、说明
- 主要是通过spring提供的`@ControllerAdvice`注解来实现
- 在程序中抛出异常后进行捕获，然后以友好的方式返回给前端
- 支持国际化多语言，支持 `异常描述 + 参数` 格式化
- 支持多语言缓存，多语言消息描述可以从本地多语言消息文件中获取，也可以自己实现优先从缓存中获取，如果获取不到再从本地多语言文件中获取。
- 提供了常见的异常捕获，其他自定义异常需要捕获然后返回给前端可自定义即可

### 二、使用演示
#### 1、下载项目到本地并打包到maven仓库
由于没有发布到maven仓库，所以需要自己下载项目到本地，然后 `mvn clean install` 打包到本地maven仓库

执行命令：`mvn clean install`

#### 2、项目中引入依赖

```xml
<dependency>
    <groupId>org.basis.enhance</groupId>
    <artifactId>enhance-boot-exception</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>5.3.2</version>
</dependency>
```

#### 3、使用演示

**详细可参考** : `demo-enhance-exception`工程测试代码

```java
@RestController("EnhanceExceptionTestController.v1")
@RequestMapping("/v1/test-enhance-exception")
public class EnhanceExceptionTestController {

    /**
     * 默认数据源切换db测试
     */
    @GetMapping("/test-01")
    public void test01() {
        try {
            // 手动制造异常
            final int i = 1 / 0;
        } catch (Throwable throwable) {
            // 抛出自定义异常，由异常拦截器拦截并格式化异常信息输出，统一异常格式返回
            throw new CommonException("出错啦，你知道吗？{0}", "wenpan");
        }
    }

}
```
客户端返回如下
```json
{
    "failed":true,
    "code":"出错啦，你知道吗？{0}",
    "message":"出错啦，你知道吗？wenpan",
    "type":"warn",
    "exception":"出错啦，你知道吗？{0}"
}
```


#### 三、其他

暂无.

