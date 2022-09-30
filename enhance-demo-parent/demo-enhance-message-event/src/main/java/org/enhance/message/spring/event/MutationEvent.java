package org.enhance.message.spring.event;

import lombok.Data;
import lombok.Getter;
import org.enhance.message.spring.entity.MutationType;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * 通用泛型事件
 *
 * @author wenpanfeng 2022/8/2 17:46
 */
@Data
public class MutationEvent<T> implements ResolvableTypeProvider {

    @Getter
    private T source;

    /**
     * Created / Updated / Deleted
     */
    @Getter
    private MutationType type;

    public MutationEvent(T data, MutationType type) {
        source = data;
        this.type = type;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(),
                ResolvableType.forInstance(source));
    }
}