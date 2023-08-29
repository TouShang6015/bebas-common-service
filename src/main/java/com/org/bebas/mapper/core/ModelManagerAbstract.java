package com.org.bebas.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.bebas.core.model.BaseModel;
import com.org.bebas.mapper.exception.BebasMapperException;
import com.org.bebas.mapper.utils.ReflectUtil;

/**
 * @author Wuhao
 * @date 2022/7/17 15:56
 */
public abstract class ModelManagerAbstract<Mapper extends BaseMapper<Model>, Model extends BaseModel> extends ServiceImpl<Mapper, Model> implements ModelManager<Model> {

    /**
     * 获得当前类中泛型类型字节码class
     *
     * @param index
     * @return
     */
    @Override
    public <T> Class<T> getGenericsClass(Integer index) {
        return ReflectUtil.getGenericsClass(this.getClass(), index);
    }

    /**
     * 获得当前类中Model字节码class
     *
     * @return
     */
    @Override
    public Class<Model> getModelClass() {
        return this.getGenericsClass(1);
    }

    /**
     * 实例化当前类中泛型的第[index]个对象
     *
     * @param index
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public <T> T modelInstance(Integer index) throws InstantiationException, IllegalAccessException {
        return (T) this.getGenericsClass(index).newInstance();
    }

    /**
     * 获取model对象
     *
     * @return
     */
    @Override
    public Model modelInstance() {
        try {
            return this.modelInstance(1);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new BebasMapperException("Method: modelInstance() error ! 实例化model失败");
    }
}
