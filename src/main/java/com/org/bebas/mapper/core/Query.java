package com.org.bebas.mapper.core;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.org.bebas.core.model.BaseModel;
import com.org.bebas.mapper.utils.ExtMapperUtil;
import com.org.bebas.mapper.utils.ModelUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wuhao
 * @date 2022/8/18 23:56
 */
public interface Query<Model extends BaseModel> {

    /**
     * 查询列表通过Model
     *
     * @param param
     * @return
     */
    List<Model> listByParam(Model param);

    /**
     * 统计count 通过 model
     *
     * @param param
     * @return
     */
    long countByParam(Model param);

    /**
     * 查询列表并且分页通过model
     *
     * @param page
     * @param param
     * @return
     */
    IPage<Model> listPageByParam(IPage<Model> page, Model param);

    /**
     * sum聚合函数统计
     *
     * @param wrapper
     * @return
     */
    Number sumByColumn(String columnName, QueryWrapper<Model> wrapper);

    BigDecimal selectFieldSum(String columnName, QueryWrapper<Model> wrapper);

    default BigDecimal sumDecimal(Func1<Model, Object> func, Model model) {
        String fieldName = LambdaUtil.getFieldName(func);
        String fieldNameColumn = ModelUtil.humpToLine(fieldName);
        return selectFieldSum(fieldNameColumn, ExtMapperUtil.modelToWrapper(model));
    }

    default Number sum(Func1<Model, Object> func, Model model) {
        String fieldName = LambdaUtil.getFieldName(func);
        String fieldNameColumn = ModelUtil.humpToLine(fieldName);
        return sumByColumn(fieldNameColumn, ExtMapperUtil.modelToWrapper(model));
    }

}
