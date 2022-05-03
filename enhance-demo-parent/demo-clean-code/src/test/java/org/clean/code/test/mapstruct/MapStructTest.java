package org.clean.code.test.mapstruct;

import org.clean.code.CleanCodeApplication;
import org.clean.code.mapstruct.convert.CarConverter;
import org.clean.code.mapstruct.dto.CarDTO;
import org.clean.code.mapstruct.dto.DriverDTO;
import org.clean.code.mapstruct.dto.PartDTO;
import org.clean.code.mapstruct.vo.CarVO;
import org.clean.code.mapstruct.vo.VehicleVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * mapstruct测试
 *
 * @author Mr_wenpan@163.com 2022/05/02 16:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CleanCodeApplication.class)
public class MapStructTest {

    /**
     * 自动注入CarConverter
     */
    @Autowired
    private CarConverter carConverter;

    /**
     * 测试基础使用
     */
    @Test
    public void test01() {
        CarDTO carDTO = buildCarDTO();
        // 转换
        CarVO carVO = CarConverter.CAR_CONVERTER.carDto2Vo(carDTO);
        System.out.println(carDTO);
        System.out.println("========================================================================================");
        System.out.println(carVO);
    }

    /**
     * 测试批量转换
     */
    @Test
    public void test02() {
        // 批量转换list属性
        ArrayList<CarDTO> carDTOS = new ArrayList<>();
        CarDTO carDTO1 = CarDTO.builder().publishDate(new Date()).price(new BigDecimal("1234")).build();
        CarDTO carDTO2 = CarDTO.builder().publishDate(new Date()).price(new BigDecimal("5678")).build();
        carDTOS.add(carDTO1);
        carDTOS.add(carDTO2);
        List<CarVO> carVOS = CarConverter.CAR_CONVERTER.carDtos2Vos(carDTOS);
        System.out.println(carVOS);
    }

    /**
     * 测试@BeanMapping
     */
    @Test
    public void test03() {
        CarDTO carDTO = buildCarDTO();
        VehicleVO vehicleVO = CarConverter.CAR_CONVERTER.carDto2VehicleVo(carDTO);
        System.out.println(vehicleVO);
    }

    /**
     * 测试@InheritConfiguration
     */
    @Test
    public void test04() {
        CarDTO carDTO = buildCarDTO();
        VehicleVO vehicleVO = CarConverter.CAR_CONVERTER.carDto2VehicleVo(carDTO);
        System.out.println(vehicleVO);
        CarDTO carDTO2 = CarDTO.builder().carName("奔驰").build();
        CarConverter.CAR_CONVERTER.updateVehicleVo(carDTO2, vehicleVO);
        System.out.println(vehicleVO);
    }

    /**
     * 测试@InheritInverseConfiguration
     */
    @Test
    public void test05() {
        VehicleVO vehicleVO = new VehicleVO();
        vehicleVO.setName("大众-suv");
        vehicleVO.setPrice(new BigDecimal(2395));
        vehicleVO.setBrandName("大众");
        CarDTO carDTO = CarConverter.CAR_CONVERTER.vehicleVo2carDto(vehicleVO);
        System.out.println(vehicleVO);
        System.out.println(carDTO);
    }

    /**
     * 测试和spring结合使用
     */
    @Test
    public void test06() {
        VehicleVO vehicleVO = new VehicleVO();
        vehicleVO.setName("大众-suv");
        vehicleVO.setPrice(new BigDecimal(2395));
        vehicleVO.setBrandName("大众");
        CarDTO carDTO = carConverter.vehicleVo2carDto(vehicleVO);
        System.out.println(vehicleVO);
        System.out.println(carDTO);
    }


    /**
     * 构建一个carDTO
     */
    private CarDTO buildCarDTO() {
        CarDTO carDTO = CarDTO.builder()
                .brand("宝马")
                .carName("宝马5系")
                .color("耀夜黑")
                .number(2)
                .oilWear(1.5589)
                .price(new BigDecimal("12345678.99815673"))
                .publishDate(new Date())
                .build();

        DriverDTO driverDTO = DriverDTO.builder().age(25).id(1).name("wenpanfeng").build();

        List<PartDTO> partDTOS = new ArrayList<>();
        PartDTO partDTO1 = PartDTO.builder().partId("100001").partName("智能方向盘").build();
        PartDTO partDTO2 = PartDTO.builder().partId("100002").partName("耐用轮胎").build();
        PartDTO partDTO3 = PartDTO.builder().partId("100003").partName("福耀挡风玻璃").build();
        partDTOS.add(partDTO1);
        partDTOS.add(partDTO2);
        partDTOS.add(partDTO3);

        carDTO.setDriverDTO(driverDTO);
        carDTO.setPartDTOList(partDTOS);

        return carDTO;
    }


}
