package org.basis.enhance.multisource.client;

import org.basis.enhance.helper.RedisHelper;
import org.basis.enhance.multisource.register.RedisDataSourceRegister;
import org.basis.enhance.options.AbstractOptionsRedisDb;
import org.springframework.data.redis.core.RedisTemplate;

import static org.basis.enhance.infra.constant.EnhanceRedisConstants.MultiSource.REDIS_HELPER;
import static org.basis.enhance.infra.constant.EnhanceRedisConstants.MultiSource.REDIS_TEMPLATE;

/**
 * Redis多数据源操作客户端
 *
 * @author Mr_wenpan@163.com 2021/09/06 11:10
 */
public class RedisMultisourceClient {

    /**
     * 操作指定数据源的默认db
     *
     * @param datasource 数据源名称
     */
    public RedisTemplate<String, String> opsDefaultDb(String datasource) {
        // 获取该数据源的默认db对应的redisTemplate
        RedisTemplate<String, String> redisTemplate = RedisDataSourceRegister.getRedisTemplate(datasource + REDIS_TEMPLATE);
        if (null == redisTemplate) {
            throw new RuntimeException("没有该数据源，请确认传入的redis数据源名称是否正确.");
        }
        return redisTemplate;
    }

    public RedisTemplate<String, String> opsDbZero(String datasource) {

        return commonOpsDb(datasource).opsDbZero();
    }

    public RedisTemplate<String, String> opsDbOne(String datasource) {

        return commonOpsDb(datasource).opsDbOne();
    }

    public RedisTemplate<String, String> opsDbTwo(String datasource) {

        return commonOpsDb(datasource).opsDbTwo();
    }

    public RedisTemplate<String, String> opsDbThree(String datasource) {

        return commonOpsDb(datasource).opsDbThree();
    }

    public RedisTemplate<String, String> opsDbFour(String datasource) {

        return commonOpsDb(datasource).opsDbFour();
    }

    public RedisTemplate<String, String> opsDbFive(String datasource) {

        return commonOpsDb(datasource).opsDbFive();
    }

    public RedisTemplate<String, String> opsDbSix(String datasource) {

        return commonOpsDb(datasource).opsDbSix();
    }

    public RedisTemplate<String, String> opsDbSeven(String datasource) {

        return commonOpsDb(datasource).opsDbSeven();
    }

    public RedisTemplate<String, String> opsDbEight(String datasource) {

        return commonOpsDb(datasource).opsDbEight();
    }

    public RedisTemplate<String, String> opsDbNine(String datasource) {

        return commonOpsDb(datasource).opsDbNine();
    }

    public RedisTemplate<String, String> opsDbTen(String datasource) {

        return commonOpsDb(datasource).opsDbTen();
    }

    public RedisTemplate<String, String> opsDbEleven(String datasource) {

        return commonOpsDb(datasource).opsDbEleven();
    }

    public RedisTemplate<String, String> opsDbTwelve(String datasource) {

        return commonOpsDb(datasource).opsDbTwelve();
    }

    public RedisTemplate<String, String> opsDbThirteen(String datasource) {

        return commonOpsDb(datasource).opsDbThirteen();
    }

    public RedisTemplate<String, String> opsDbFourteen(String datasource) {

        return commonOpsDb(datasource).opsDbFourteen();
    }

    public RedisTemplate<String, String> opsDbFifteen(String datasource) {
        return commonOpsDb(datasource).opsDbFifteen();
    }

    /**
     * 操作0~15号库以外的db
     */
    public RedisTemplate<String, String> opsOtherDb(String datasource, int db) {

        return commonOpsDb(datasource).opsOtherDb(db);
    }

    private AbstractOptionsRedisDb<String, String> commonOpsDb(String datasource) {
        // 获取该数据源对应的redisHelper
        RedisHelper redisHelper = RedisDataSourceRegister.getRedisHelper(datasource + REDIS_HELPER);
        if (redisHelper == null) {
            throw new RuntimeException("没有该数据源，请确认传入的redis数据源名称是否正确.");
        }
        // 获取指定db的redisTemplate
        return redisHelper.opsDb();
    }

}
