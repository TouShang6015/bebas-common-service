package com.org.bebas.mapper.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.TableName;
import com.org.bebas.constants.RedisConstant;
import com.org.bebas.core.model.BaseModel;
import com.org.bebas.core.security.SecurityBaseUtil;
import com.org.bebas.mapper.exception.BebasMapperException;
import com.org.bebas.utils.DateUtils;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model工具类
 *
 * @author WuHao
 * @since 2022/5/13 16:06
 */
public class ModelUtil {

    private static final Pattern linePattern = Pattern.compile("_(\\w)");
    private static final Pattern humpPattern = Pattern.compile("[A-Z]");

    public static String modelMainKey(Class<? extends BaseModel> c) {
        return RedisConstant.NameSpace.MODULE_DATA + RedisConstant.NameSpace.TABLE + ModelUtil.getTableName(c) + ":";
    }

    /**
     * dto转换为 Model类型
     *
     * @param dto
     * @param modelClass
     * @param <DTO>
     * @param <Model>
     * @return
     */
    public static <DTO, Model extends BaseModel> Model dtoConvertModel(DTO dto, Class<Model> modelClass) {
        return JSON.parseObject(JSON.toJSONString(dto), modelClass);
    }

    /**
     * 获取类中的表名字
     *
     * @param c Model class
     * @return tableName 表名
     */
    public static String getTableName(Class<? extends BaseModel> c) {
        Annotation[] annotations = c.getDeclaredAnnotations();
        if (annotations.length <= 0) {
            throw new BebasMapperException("ModelUtil error ：无法获取表名称!");
        }
        for (Annotation annotation : annotations) {
            if (annotation instanceof TableName) {
                String tableName = ((TableName) annotation).value();
                if (StrUtil.isEmpty(tableName))
                    throw new BebasMapperException("ModelUtil error ：tableName为空!");
                return tableName;
            }
        }
        throw new BebasMapperException("ModelUtil error ：tableName为空!");
    }

    /**
     * 初始化Model值，赋予默认基本参数
     * 默认创建为model赋予创建时间、创建者
     *
     * @param model
     * @param <Model>
     */
    public static <Model extends BaseModel> void initModel(Model model) {
        initModel(model, true);
    }

    /**
     * 初始化Model值，赋予默认基本参数
     *
     * @param model    模型
     * @param isCreate 是否创建创建时间
     * @param <Model>
     */
    public static <Model extends BaseModel> void initModel(Model model, boolean isCreate) {
        Optional.ofNullable(model).ifPresent(m -> {
            if (StrUtil.isEmpty(model.getCreateTime()))
                model.setCreateTime(null);
            if (StrUtil.isEmpty(model.getUpdateTime()))
                model.setUpdateTime(null);
            /*  设置主键id */
//            if (Objects.isNull(m.getId()) || m.getId() <= 0L) {
//                m.setId(BaseModel.uniqueId());
//            }
            if (isCreate) {
                /* 设置创建时间 */
                if (StrUtil.isEmpty(m.getCreateTime())) {
                    m.setCreateTime(DateUtils.nowDateFormat());
                    /* 设置创建者 */
                    if (StrUtil.isEmpty(m.getCreateOper())) {
                        try {
                            m.setCreateOper(String.valueOf(SecurityBaseUtil.getUserId()));
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            /* 设置修改时间 */
            m.setUpdateTime(DateUtils.nowDateFormat());
            m.afterInitModel();
            try {
                m.setUpdateOper(String.valueOf(SecurityBaseUtil.getUserId()));
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * 初始化Model值，赋予默认基本参数
     *
     * @param model    模型
     * @param isCreate 是否创建创建时间
     * @param <Model>
     */
    public static <Model extends BaseModel> void initModel(Model model, boolean isCreate, String time, String oper) {
        Optional.ofNullable(model).ifPresent(m -> {
            if (StrUtil.isEmpty(model.getCreateTime()))
                model.setCreateTime(time);
            if (StrUtil.isEmpty(model.getUpdateTime()))
                model.setUpdateTime(time);
            /*  设置主键id */
//            if (Objects.isNull(m.getId()) || m.getId() <= 0L) {
//                m.setId(BaseModel.uniqueId());
//            }
            if (isCreate) {
                /* 设置创建时间 */
                if (StrUtil.isEmpty(m.getCreateTime())) {
                    m.setCreateTime(time);
                    /* 设置创建者 */
                    if (StrUtil.isEmpty(m.getCreateOper())) {
                        try {
                            m.setCreateOper(oper);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            /* 设置修改时间 */
            m.setUpdateTime(time);
            m.afterInitModel();
            try {
                m.setUpdateOper(oper);
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * 驼峰转下划线,最后转为大写
     *
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString().toUpperCase();
    }

    /**
     * 下划线转驼峰,正常输出
     *
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
