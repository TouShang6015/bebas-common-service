package com.org.bebas.mapper.core;

import com.org.bebas.core.model.BaseModel;

import java.util.List;

/**
 * @author Wuhao
 * @date 2022/8/18 23:59
 */
public interface Insert<Model extends BaseModel> {

    /**
     * 批量添加数据
     *
     * @param list
     * @return
     */
    int insertBatch(List<Model> list);

}
