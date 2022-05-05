package org.clean.code.test.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.clean.code.jackson.dto.HouseDTO;
import org.clean.code.jackson.dto.PersonDTO;
import org.clean.code.jackson.dto.ResultDTO;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * jackson测试
 *
 * @author Mr_wenpan@163.com 2022/05/04 17:22
 */
public class JacksonTest {

    private final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 全局配置，序列化时只包含非空属性
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 通过spi发现Jackson的module并注册(自动注册所有module)
        objectMapper.findAndRegisterModules();
        // 手动配置javaTimeModule并注册，see JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        objectMapper.registerModule(javaTimeModule);

        // 设置格式化时美化输出
//        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // 反序列配置

        // 没有找到对应字段的时候报错，设置为false
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 同上一种写法效果一样
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 测试通用配置
     */
    @Test
    public void test04() throws JsonProcessingException {
        // 测试 @JsonProperty和@JsonIgnore
    }

    /**
     * 泛型反序列化测试
     */
    @Test
    public void test03() throws JsonProcessingException {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setAccount(new BigDecimal("100000.2345"));
        personDTO.setAddr("成都");
        personDTO.setName("wenpan");
        personDTO.setBirthday(new Date());
        personDTO.setDateTime(LocalDateTime.now());
        ResultDTO<PersonDTO> resultDTO = ResultDTO.buildResult(personDTO);

        // 带泛型的对象序列化为字符串
        String string = objectMapper.writeValueAsString(resultDTO);
        System.out.println("string = " + string);

        // 反序列化为泛型，这里通过TypeReference来指定泛型类型
        ResultDTO<PersonDTO> personDTOResultDTO = objectMapper.readValue(string, new TypeReference<ResultDTO<PersonDTO>>() {
        });

        System.out.println("personDTOResultDTO = " + personDTOResultDTO);
        System.out.println("personDTOResultDTO.getBody() = " + personDTOResultDTO.getBody());
    }

    /**
     * 反序列化测试
     */
    @Test
    public void test02() throws JsonProcessingException {
        // 如果str里有PersonDTO种没有的属性，那么objectMapper默认会报错，比如我们在str里添加一个age字段
        final String str = "{\"age\":\"100\",\"other\":\"泛型测试\",\"id\":1000,\"name\":\"wenpan\",\"account\":100000.2345,\"addr\":\"成都\",\"number\":\"123456789\",\"hourse\":{\"id\":20000,\"position\":\"四川省\",\"size\":\"300m\"},\"hobby\":[\"篮球\",\"篮球\",\"足球\"],\"birthday\":\"2022-05-04 17:40:36\",\"dateTime\":\"2022-05-04 17:40:36\"}";
        PersonDTO personDTO = objectMapper.readValue(str, PersonDTO.class);
        System.out.println(personDTO);
    }

    /**
     * 序列化测试
     */
    @Test
    public void test01() throws JsonProcessingException {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setAccount(new BigDecimal("100000.2345"));
        personDTO.setAddr("成都");
        personDTO.setName("wenpan");
        personDTO.setBirthday(new Date());
        personDTO.setDateTime(LocalDateTime.now());
        personDTO.setId(1000L);
        personDTO.setNumber("123456789");
        HouseDTO houseDTO = new HouseDTO();
        houseDTO.setId(20000);
        houseDTO.setPosition("四川省");
        houseDTO.setSize("300m");
        personDTO.setHourse(houseDTO);
        List<String> hobby = new ArrayList<>();
        hobby.add("篮球");
        hobby.add("篮球");
        hobby.add("足球");
        personDTO.setHobby(hobby);

        // 泛型
        personDTO.setOther("泛型测试");

        String str = objectMapper.writeValueAsString(personDTO);
        System.out.println(str);
    }
}
