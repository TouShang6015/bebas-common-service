package com.org.bebas.mapper.executor.builder;

import com.org.bebas.core.model.BaseModel;

import java.util.function.Consumer;

/**
 * @author wyj
 * @date 2022/8/20 16:41
 */
public abstract class QueryWrapperBuilderAbstract<Model extends BaseModel, Builder, Wrapper> {

    protected Model model;

    protected abstract Builder model(Model model);

    protected abstract Builder extend(Consumer<Wrapper> consumer);

    protected abstract Wrapper build();

}
