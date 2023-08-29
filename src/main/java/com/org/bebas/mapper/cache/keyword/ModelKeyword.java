package com.org.bebas.mapper.cache.keyword;

import java.io.Serializable;

/**
 * @author wyj
 * @date 2022/11/14 17:11
 */
public interface ModelKeyword {

    /**
     * 获取model的redis Key
     *
     * @return
     */
    String modelKey();

    /**
     * 获取mode 的 主键 redis key
     *
     * @param id
     * @return
     */
    String modelKeywordKey(Serializable id);

}
