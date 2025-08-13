package com.org.bebas.mapper.core;

import com.baomidou.mybatisplus.extension.service.IService;
import com.org.bebas.core.model.BaseModel;

/**
 * @author WuHao
 * @since 2022/7/17 15:49
 */
public interface ModelManager<Model extends BaseModel> extends IService<Model> {

    /**
     * 获得当前类中泛型类型字节码class
     *
     * @param index
     * @return
     */
    <T> Class<T> getGenericsClass(Integer index);

    /**
     * 获得当前类中Model字节码class
     *
     * @return
     */
    Class<Model> getModelClass();

    /**
     * 实例化当前类中泛型的第[index]个对象
     *
     * @param index
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    <T> T modelInstance(Integer index) throws InstantiationException, IllegalAccessException;

    /**
     * 获取model对象
     *
     * @return
     */
    Model modelInstance();

}
