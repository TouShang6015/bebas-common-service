package com.org.bebas.mapper.service;

import com.org.bebas.core.model.BaseModel;
import com.org.bebas.mapper.core.ExtendServiceSQL;
import com.org.bebas.mapper.core.ModelManager;

/**
 * IService增强接口
 *
 * @author WuHao
 * @date 2022/5/17 16:14
 */
public interface IService<Model extends BaseModel> extends ExtendServiceSQL<Model>, ModelManager<Model> {

}
