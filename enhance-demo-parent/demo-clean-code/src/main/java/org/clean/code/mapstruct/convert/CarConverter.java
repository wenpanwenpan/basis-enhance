package org.clean.code.mapstruct.convert;

import org.clean.code.mapstruct.dto.CarDTO;
import org.clean.code.mapstruct.dto.DriverDTO;
import org.clean.code.mapstruct.dto.PartDTO;
import org.clean.code.mapstruct.vo.CarVO;
import org.clean.code.mapstruct.vo.DriverVO;
import org.clean.code.mapstruct.vo.VehicleVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * car转换器 (使用抽象类或接口都可以)
 * Mapper(componentModel = "spring") 表示指定结合spring使用，指定为spring后，从生成的代码中可以看到
 * 会为该接口生成一个默认实现，并且该实现类会加上@Component注解
 *
 * @author Mr_wenpan@163.com 2022/05/02 15:42
 */
@Mapper(componentModel = "spring")
public interface CarConverter {

    /**
     * car转换器实例
     */
    CarConverter CAR_CONVERTER = Mappers.getMapper(CarConverter.class);

    /**
     * carDTO转换为carVO
     *
     * @param carDTO 源对象
     * @return org.enhance.core.demo.api.vo.CarVO
     */
    @Mappings(
            value = {
                    // 如果字段名不一样，需要在这里指定映射关系
                    @Mapping(source = "brand", target = "brandName"),
                    // 指定数字的格式化方式
                    @Mapping(source = "price", target = "price", numberFormat = "#.00"),
                    // 指定日期的格式化方式
                    @Mapping(source = "publishDate", target = "publishDate", dateFormat = "yyyy-MM-dd HH:mm:ss"),
                    // 设置color字段不映射到carVO上
                    @Mapping(source = "color", target = "color", ignore = true),
                    // 引用类型之间的转换(需要依赖于下面的driverDto2Vo方法)
                    @Mapping(source = "driverDTO", target = "driverVO")
            }
    )
    CarVO carDto2Vo(CarDTO carDTO);

    /**
     * 将driverDTO转换为driverVO
     *
     * @param driverDTO driverDTO
     * @return org.enhance.core.demo.api.vo.DriverVO
     */
    // 直接使用@Mapping注解
    @Mapping(source = "id", target = "driverId")
    @Mapping(source = "name", target = "driverName")
    DriverVO driverDto2Vo(DriverDTO driverDTO);

    /**
     * 自定义属性映射处理(只能是抽象类，不能是接口的default方法，不生效)
     * AfterMapping 表示 mapstruct在调用完自动转换的方法后，会来自动调用本方法
     * MappingTarget 表示传来的carVO对象是已经赋值过的
     *
     * @param carDTO carDTO
     * @param carVO  carVO
     */
    @AfterMapping
    default void carDto2VoAfter(CarDTO carDTO, @MappingTarget CarVO carVO) {
        List<PartDTO> partDTOList = carDTO.getPartDTOList();
        boolean hasPart = partDTOList != null && !partDTOList.isEmpty();
        carVO.setHasPart(hasPart);
    }

    /**
     * List<CarDTO> 批量转换为List<CarVO>
     *
     * @param carDTOs carDTOs
     * @return java.util.List<org.enhance.core.demo.api.vo.CarVO>
     */
    List<CarVO> carDtos2Vos(List<CarDTO> carDTOs);

    /**
     * 将carDTO中指定的属性映射到VehicleVO（假如carDTO和VehicleVO中有非常多的同名属性，我们业务场景中只想要映射carName，
     * 其余字段不需要映射，那么就可以使用 @BeanMapping(ignoreByDefault = true) 结合@Mapping(source = "carName", target = "carName")来实现）
     *
     * @param carDTO carDTO
     * @return org.enhance.core.demo.api.vo.VehicleVO
     */
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "carName", target = "name")
    @Mapping(source = "brand", target = "brandName")
    VehicleVO carDto2VehicleVo(CarDTO carDTO);

    /**
     * 使用carDTO 来 更新 vehicleVO
     *
     * @param carDTO    carDTO
     * @param vehicleVO vehicleVO
     */
    @InheritConfiguration
    // 忽略掉VehicleVO中的brandName属性不更新
    @Mapping(target = "brandName", ignore = true)
    void updateVehicleVo(CarDTO carDTO, @MappingTarget VehicleVO vehicleVO);

    /**
     * vehicleVO转换为CarDTO
     * InheritInverseConfiguration 表示反向继承，name表示继承配置的方法名称（也就是继承了carDto2VehicleVo方法上的配置
     * 注意：BeanMapping 不会被继承）
     *
     * @param vehicleVO vehicleVO
     * @return org.enhance.core.demo.api.dto.CarDTO
     */
    @BeanMapping(ignoreByDefault = true)
    @InheritInverseConfiguration(name = "carDto2VehicleVo")
    CarDTO vehicleVo2carDto(VehicleVO vehicleVO);
}
