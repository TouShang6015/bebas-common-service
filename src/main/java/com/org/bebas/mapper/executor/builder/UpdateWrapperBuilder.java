package com.org.bebas.mapper.executor.builder;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.org.bebas.core.model.BaseModel;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author wyj
 * @date 2022/8/19 11:20
 */
public class UpdateWrapperBuilder<Model extends BaseModel> {

    private UpdateWrapper<Model> updateWrapper;

    public UpdateWrapperBuilder(UpdateWrapper<Model> updateWrapper) {
        this.updateWrapper = updateWrapper;
    }

    public UpdateWrapperBuilder() {
    }

    public static <Model extends BaseModel> UpdateWrapperBuilder<Model> builder() {
        UpdateWrapperBuilder<Model> wrapperBuilder = new UpdateWrapperBuilder();
        wrapperBuilder.updateWrapper = new UpdateWrapper();
        return wrapperBuilder;
    }

    public static UpdateWrapperBuilder builder(UpdateWrapper updateWrapper) {
        UpdateWrapperBuilder wrapperBuilder = new UpdateWrapperBuilder();
        if (Objects.nonNull(updateWrapper)) {
            wrapperBuilder.updateWrapper = updateWrapper;
        } else {
            wrapperBuilder.updateWrapper = new UpdateWrapper();
        }
        return wrapperBuilder;
    }

    public UpdateWrapperBuilder<Model> init() {
        UpdateWrapperBuilder<Model> wrapperBuilder = new UpdateWrapperBuilder();
        wrapperBuilder.updateWrapper = new UpdateWrapper<Model>();
        return wrapperBuilder;
    }

    public UpdateWrapperBuilder<Model> extend(Consumer<UpdateWrapper<Model>> consumer) {
        consumer.accept(this.updateWrapper);
        return this;
    }

    public UpdateWrapper<Model> build() {
        return this.updateWrapper;
    }

}
