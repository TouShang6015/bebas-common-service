package com.org.bebas.mapper.core;

import com.org.bebas.core.model.BaseModel;

/**
 * 扩展sql接口
 *
 * @author WuHao
 * @since 2022/5/24 16:45
 */
public interface ExtendServiceSQL<Model extends BaseModel> extends Query<Model>, Insert<Model> {

}
