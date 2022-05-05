package org.clean.code.test.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.clean.code.fastjson.vo.HouseVO;
import org.clean.code.fastjson.vo.PersonVO;
import org.clean.code.fastjson.vo.ResultVO;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * fastjson测试
 *
 * @author Mr_wenpan@163.com 2022/05/04 16:26
 */
public class FastJsonTest {

    /**
     * 序列化测试
     */
    @Test
    public void test01() {
        PersonVO personVO = new PersonVO();
        personVO.setAccount(new BigDecimal("100000.2345"));
        personVO.setAddr("成都");
        personVO.setName("wenpan");
        personVO.setBirthday(new Date());
        personVO.setDateTime(LocalDateTime.now());
        personVO.setId(1000L);
        personVO.setNumber("123456789");
        HouseVO houseVO = new HouseVO();
        houseVO.setId(20000);
        houseVO.setPosition("四川省");
        houseVO.setSize("300m");
        personVO.setHourse(houseVO);
        List<String> hobby = new ArrayList<>();
        hobby.add("篮球");
        hobby.add("篮球");
        hobby.add("足球");
        personVO.setHobby(hobby);

        // 泛型
        personVO.setOther("泛型测试");

        // SerializerFeature.WriteMapNullValue 表示空值也序列化进去，SerializerFeature.PrettyFormat表示美化json
        String jsonString = JSON.toJSONString(personVO, SerializerFeature.WriteMapNullValue, SerializerFeature.PrettyFormat);
        System.out.println(jsonString);
    }

    /**
     * 测试$ref，在fastjson中list会将同样的引用，使用$ref来表示一节省空间，比如下面例子（fastjson的引用探测）
     */
    @Test
    public void test02() {
        List<PersonVO> list = new ArrayList<>();
        PersonVO personVO = new PersonVO();
        personVO.setAccount(new BigDecimal("100000.2345"));
        personVO.setAddr("成都");
        personVO.setName("wenpan");

        list.add(personVO);
        list.add(personVO);
        list.add(personVO);

        // SerializerFeature.DisableCircularReferenceDetect 表示禁用引用探测功能
        String str = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat);
        System.out.println(str);
    }

    /**
     * 测试SerializeFilter , SerializeFilter可以在序列化时做一些自定义处理
     */
    @Test
    public void test03() {
        PersonVO personVO = new PersonVO();
        personVO.setAccount(new BigDecimal("100000.2345"));
        personVO.setAddr("成都");
        personVO.setName("wenpan");
        personVO.setBirthday(new Date());
        personVO.setDateTime(LocalDateTime.now());
        personVO.setId(1000L);
        personVO.setNumber("123456789");
        NameFilter nameFilter = new NameFilter() {
            /**
             * object表示序列化的这个对象
             * name 表示对象中的属性名称
             * value表示对象中的属性值
             */
            @Override
            public String process(Object object, String name, Object value) {
                // 将属性名称全部变成大写
                return name.toUpperCase();
            }
        };

        String jsonString = JSON.toJSONString(personVO, nameFilter);
        System.out.println(jsonString);
    }


    /**
     * 反序列化测试
     */
    @Test
    public void test04() {

        final String jsonStr = "{\"account\":100000.2345,\"addr\":\"成都\",\"birthday\":\"2022-05-04 16:49:50\",\"carVOList\":null,\"currentDate\":null,\"dateTime\":\"2022-05-04 16:49:50\",\"hobby\":[\"篮球\",\"篮球\",\"足球\"],\"hourse\":{\"id\":20000,\"position\":\"四川省\",\"size\":\"300m\"},\"id\":1000,\"name\":\"wenpan\",\"number\":\"123456789\",\"other\":\"泛型测试\"}\n";
        // 反序列化
        PersonVO personVO = JSON.parseObject(jsonStr, PersonVO.class);
        // 构建返回对象，注意这里的泛型
        ResultVO<PersonVO> resultVO = ResultVO.buildResult(personVO);
        System.out.println(resultVO);
        String jsonString = JSON.toJSONString(resultVO);
        // 反序列化，这里的泛型处理(可以看到这里的反序列化不能正确的变为泛型类型，需要自己强转，比较麻烦)
        ResultVO resultVO1 = JSON.parseObject(jsonString, ResultVO.class);
        // 可以看到这里是object类型的
        Object body = resultVO1.getBody();
        System.out.println("resultVO1 = " + resultVO1);
        System.out.println("body = " + body);

        // 更加优雅的处理泛型的方式
        ResultVO<PersonVO> resultVO2 = JSON.parseObject(jsonString, new TypeReference<ResultVO<PersonVO>>() {
        });
        PersonVO personVO1 = resultVO2.getBody();
        System.out.println("resultVO2 = " + resultVO2);
        System.out.println("personVO1 = " + personVO1);
    }
}
