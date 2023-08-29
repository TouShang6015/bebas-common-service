package com.org.bebas.mapper.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.org.bebas.constants.RedisConstant;
import com.org.bebas.core.function.OpenRunnable;
import com.org.bebas.core.model.BaseModel;
import com.org.bebas.enums.ConditionEnum;
import com.org.bebas.mapper.exception.BebasMapperException;
import com.org.bebas.utils.StringUtils;
import com.org.bebas.utils.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mapper增强工具类
 *
 * @author WuHao
 * @date 2022/5/13 19:24
 */
@Slf4j
public class ExtMapperUtil {

    public static final String SECTION_SUFFIX = "_";

    /**
     * 根据Model获取redis key
     *
     * @param c model class
     * @return
     */
    public static String getCacheTableAllListKey(Class<? extends BaseModel> c) {
        return StrBuilder.create()
                .append(RedisConstant.NameSpace.MODULE_DATA)
                .append(RedisConstant.NameSpace.TABLE)
                .append(ModelUtil.getTableName(c)).append(":")
                .append(RedisConstant.Keyword.ALL_LIST)
                .toString();
    }

    /**
     * 根据Model获取redis ID key
     *
     * @param c model class
     * @return
     */
    public static String getCacheTableByIdKey(Class<? extends BaseModel> c, Serializable id) {
        return StrBuilder.create()
                .append(RedisConstant.NameSpace.MODULE_DATA)
                .append(RedisConstant.NameSpace.TABLE)
                .append(ModelUtil.getTableName(c)).append(":")
                .append(RedisConstant.Keyword.ID)
                .append("@ID").append(id)
                .toString();
    }

    /**
     * 根据查询条件获得完整的sql语句
     *
     * @param wrapper
     * @param <T>
     * @return
     */
    public static <T extends BaseModel> String getWrapperQueryWhereSql(Wrapper<T> wrapper) {
        String statementSql = wrapper.getTargetSql();       // 带占位符的sql
        if (StrUtil.isEmpty(statementSql)) return null;
        Map paramMap = ((LambdaQueryWrapper<T>) wrapper).getParamNameValuePairs();
        if (MapUtil.isEmpty(paramMap)) return null;
        List<Object> params = CollUtil.newArrayList();
        paramMap.keySet().forEach(key -> params.add(paramMap.get(key)));
        return StringUtils.replace(statementSql, "\\?", params);
    }

    /**
     * 根据model的modelName，
     * 置空modelName的值，
     * 返回原始modelName中的值
     *
     * @param model
     * @param modelName
     * @param <M>
     * @param <R>
     * @return
     */
    public static <M extends BaseModel, R> R modelNullGetValue(M model, String modelName) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(modelName, model.getClass());
            Method readMethod = descriptor.getReadMethod();
            R modelValue = (R) readMethod.invoke(model);
            Method writeMethod = descriptor.getWriteMethod();
            writeMethod.invoke(model, (Object) null);
            return modelValue;
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new BebasMapperException("ExtMapperUtil modelNullGetValue error !");
        }
    }

    /**
     * 置空model中modelName的值
     *
     * @param model
     * @param modelName
     * @param <M>
     * @param <R>
     */
    public static <M extends BaseModel, R> void modelSetNull(M model, String modelName) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(modelName, model.getClass());
            Method writeMethod = descriptor.getWriteMethod();
            writeMethod.invoke(model, (Object) null);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new BebasMapperException("ExtMapperUtil modelSetNull error !");
        }
    }

    public static <M extends BaseModel> QueryWrapper<M> modelToWrapper(QueryWrapper<M> queryWrapper, M m) {
        if (Objects.isNull(queryWrapper)) {
            queryWrapper = new QueryWrapper<>();
        }
        /* 将空字符串置空 */
        M model = BeanUtil.emptyToNull(m);
        if (ObjectUtil.isNull(model))
            return queryWrapper.setEntity(model);
        // 条件封装
        conditionSelect(queryWrapper, model);
        // 自定义排序封装
        sortCondition(queryWrapper, model);
        // 设置model
        queryWrapper.setEntity(model);
        return queryWrapper;
    }

    /**
     * 将model转换为Wrapper 条件
     *
     * @param m
     * @param <M>
     * @return
     */
    public static <M extends BaseModel> QueryWrapper<M> modelToWrapper(M m) {
        QueryWrapper<M> queryWrapper = new QueryWrapper<>();
        return modelToWrapper(queryWrapper, m);
    }

    /**
     * 条件封装
     *
     * @param wrapper
     * @param model
     * @param <M>
     */
    public static <M extends BaseModel> void conditionSelect(QueryWrapper<M> wrapper, M model) {
        /* 获取自定义查询条件 */
        Map<String, String> queryCondition = model.getQueryCondition();
        if (ObjectUtil.isNotNull(queryCondition)) {
            /* 根据key遍历，满足条件的字段会被置空（默认是EQ比较） */
            queryCondition.keySet().forEach(modelName -> {
                String condition = queryCondition.get(modelName);
                String columnName = ModelUtil.humpToLine(modelName);
                if (condition.equals(ConditionEnum.IN.name())
                        || condition.equals(ConditionEnum.OR.name())
                        || ObjectUtil.isNotNull(BeanUtil.valueByPropertyName(model, modelName))) {
                    // 封装查询条件
                    handleCondition(condition, wrapper, columnName, model, modelName);
                }
            });
            // 清空le lt ge gt的属性
            queryCondition.keySet().stream().filter(item ->
                    queryCondition.get(item).equalsIgnoreCase(ConditionEnum.GT.name()) ||
                            queryCondition.get(item).equalsIgnoreCase(ConditionEnum.GE.name()) ||
                            queryCondition.get(item).equalsIgnoreCase(ConditionEnum.LT.name()) ||
                            queryCondition.get(item).equalsIgnoreCase(ConditionEnum.LE.name())
            ).map(originalColumnName -> {
                String tempString = originalColumnName;
                if (SECTION_SUFFIX.equals(tempString.substring(tempString.length() - 1))) {
                    tempString = tempString.substring(0, tempString.length() - 1);
                }
                return tempString;
            }).distinct().collect(Collectors.toList()).forEach(modelName -> {
                modelSetNull(model, modelName);
            });
        }
    }

    /**
     * 排序条件封装
     *
     * @param wrapper
     * @param model
     * @param <M>
     */
    public static <M extends BaseModel> void sortCondition(QueryWrapper<M> wrapper, M model) {
        /* 获取自定义排序参数 */
        Map<String, Boolean> sortConditionMap = model.getSortCondition();
        if (ObjectUtil.isNotNull(sortConditionMap)) {
            Set<String> sortConditionSet = sortConditionMap.keySet();
            sortConditionSet.forEach(sortCondition -> {
                String columnName = ModelUtil.humpToLine(sortCondition);
                Boolean f = sortConditionMap.get(sortCondition);
                if (Objects.nonNull(f)) {
                    if (f) {      // ture asc 正序
                        wrapper.orderByAsc(columnName);
                    } else {     // false desc 倒序
                        wrapper.orderByDesc(columnName);
                    }
                }
            });
        }
    }

    private static <M extends BaseModel> void handleCondition(String condition, QueryWrapper<M> queryWrapper, String columnName, M model, String modelName) {
        Function<String, String> filterSuffix = originalColumnName -> {
            String tempString = originalColumnName;
            if (SECTION_SUFFIX.equals(tempString.substring(tempString.length() - 1))) {
                tempString = tempString.substring(0, tempString.length() - 1);
            }
            return tempString;
        };
        if (condition.equalsIgnoreCase(ConditionEnum.EQ.name())) {
            Object _thisValue = ExtMapperUtil.modelNullGetValue(model, modelName);
            queryWrapper.eq(Objects.nonNull(_thisValue), columnName, _thisValue);
        } else if (condition.equalsIgnoreCase(ConditionEnum.NE.name())) {
            Object _thisValue = ExtMapperUtil.modelNullGetValue(model, modelName);
            queryWrapper.ne(Objects.nonNull(_thisValue), columnName, _thisValue);
        } else if (condition.equalsIgnoreCase(ConditionEnum.OR.name())) {
            Object _thisValue = ExtMapperUtil.modelNullGetValue(model, modelName);
            OpenRunnable.run(_thisValue, Objects::nonNull, v -> {
                Map<String, String> orParamMap = BeanUtil.convertObjectToMap(v);
                queryWrapper.or(t -> {
                    for (String mName : orParamMap.keySet()) {
                        String cName = ModelUtil.humpToLine(mName);
                        handleCondition(mName, t, cName, model, mName);
                    }
                });
            });
        } else if (condition.equalsIgnoreCase(ConditionEnum.LIKE.name())) {
            Object _thisValue = ExtMapperUtil.modelNullGetValue(model, modelName);
            queryWrapper.like(Objects.nonNull(_thisValue), columnName, _thisValue);
        } else if (condition.equalsIgnoreCase(ConditionEnum.RIGHT_LIKE.name())) {
            Object _thisValue = ExtMapperUtil.modelNullGetValue(model, modelName);
            queryWrapper.likeRight(Objects.nonNull(_thisValue), columnName, _thisValue);
        } else if (condition.equalsIgnoreCase(ConditionEnum.LEFT_LIKE.name())) {
            Object _thisValue = ExtMapperUtil.modelNullGetValue(model, modelName);
            queryWrapper.likeLeft(Objects.nonNull(_thisValue), columnName, _thisValue);
        } else if (condition.equalsIgnoreCase(ConditionEnum.IN.name())) {
            ExtMapperUtil.modelNullGetValue(model, modelName);
            String inValue = (String) model.getParamExtMap().get(modelName);
            queryWrapper.in(StringUtils.isNotEmpty(inValue), columnName, StringUtils.splitToList(inValue, String::valueOf));
        } else if (condition.equalsIgnoreCase(ConditionEnum.FIND_IN_SET.name())) {
            Object _thisValue = ExtMapperUtil.modelNullGetValue(model, modelName);
            queryWrapper.apply(
                    Objects.nonNull(_thisValue),
                    StringUtils.format("FIND_IN_SET({},{})", _thisValue, modelName)
            );
        } else if (
                condition.equalsIgnoreCase(ConditionEnum.GT.name()) ||
                        condition.equalsIgnoreCase(ConditionEnum.GE.name()) ||
                        condition.equalsIgnoreCase(ConditionEnum.LT.name()) ||
                        condition.equalsIgnoreCase(ConditionEnum.LE.name())
        ) {
            String columnNameDelSuffix = filterSuffix.apply(columnName);
            Object val = ReflectUtil.getFieldValue(model, modelName);
            OpenRunnable.run(val, Objects::nonNull, v -> {
                if (condition.equalsIgnoreCase(ConditionEnum.GT.name())) {
                    queryWrapper.gt(columnNameDelSuffix, v);
                } else if (condition.equalsIgnoreCase(ConditionEnum.GE.name())) {
                    queryWrapper.ge(columnNameDelSuffix, v);
                } else if (condition.equalsIgnoreCase(ConditionEnum.LT.name())) {
                    queryWrapper.lt(columnNameDelSuffix, v);
                } else if (condition.equalsIgnoreCase(ConditionEnum.LE.name())) {
                    queryWrapper.le(columnNameDelSuffix, v);
                }
            });
        }
    }

}
