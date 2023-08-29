package com.org.bebas.mapper.executor.builder;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.org.bebas.core.model.BaseModel;
import com.org.bebas.mapper.utils.ExtMapperUtil;

import java.util.function.Consumer;

/**
 * @author wyj
 * @date 2022/8/19 11:20
 */
public class QueryWrapperBuilder<Model extends BaseModel> extends QueryWrapperBuilderAbstract<Model, QueryWrapperBuilder<Model>, QueryWrapper<Model>> {

    private QueryWrapper<Model> queryWrapper;

    public QueryWrapperBuilder() {
        this.queryWrapper = new QueryWrapper<>();
    }

    @Override
    public QueryWrapperBuilder<Model> model(Model model) {
        this.queryWrapper = ExtMapperUtil.modelToWrapper(this.queryWrapper, model);
        return this;
    }

    @Override
    public QueryWrapperBuilder<Model> extend(Consumer<QueryWrapper<Model>> consumer) {
        consumer.accept(this.queryWrapper);
        return this;
    }

    @Override
    public QueryWrapper<Model> build() {
        return this.queryWrapper;
    }

}
