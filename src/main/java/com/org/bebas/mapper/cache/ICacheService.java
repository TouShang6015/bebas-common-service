package com.org.bebas.mapper.cache;

import com.org.bebas.core.model.BaseModel;
import com.org.bebas.mapper.cache.keyword.ModelKeyword;

import java.io.Serializable;

/**
 * @author WuHao
 * @since 2022/7/17 2:09
 */
public interface ICacheService<Model extends BaseModel> {

    /**
     * 获取缓存 通过id
     *
     * @param id
     */
    Model cacheGetById(ModelKeyword modelKeyword,Serializable id);

    /**
     * 新增缓存 通过id
     *
     * @param model
     */
    void cacheAddById(ModelKeyword modelKeyword,Model model);

    /**
     * 删除缓存通过id
     *
     * @param id
     */
    void cacheDeleteById(ModelKeyword modelKeyword,Serializable id);

    /**
     * 获取业务表主键缓存过期时间 （毫秒）
     *
     * @return
     */
    long getRandomRedisExpireTime();

}
