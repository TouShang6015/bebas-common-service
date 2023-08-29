package com.org.bebas.mapper.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.org.bebas.core.model.BaseModel;
import com.org.bebas.core.security.SecurityBaseUtil;
import com.org.bebas.mapper.core.ModelManagerAbstract;
import com.org.bebas.mapper.exception.BebasMapperException;
import com.org.bebas.mapper.service.IService;
import com.org.bebas.mapper.utils.ExtMapperUtil;
import com.org.bebas.mapper.utils.ModelUtil;
import com.org.bebas.mapper.utils.ReflectUtil;
import com.org.bebas.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * service服务层增强类 缓存层
 *
 * @author WuHao
 * @date 2022/5/13 15:52
 */
public abstract class ServiceImpl<Mapper extends BaseMapper<Model>, Model extends BaseModel> extends ModelManagerAbstract<Mapper, Model> implements IService<Model> {

    protected final Logger log = LoggerFactory.getLogger(ServiceImpl.class);

    @Override
    public long countByParam(Model param) {
        return super.count(ExtMapperUtil.modelToWrapper(param));
    }

    @Override
    public List<Model> listByParam(Model param) {
        return super.list(ExtMapperUtil.modelToWrapper(param));
    }

    /**
     * 查询列表并且分页通过model
     *
     * @param page
     * @param param
     * @return
     */
    @Override
    public IPage<Model> listPageByParam(IPage<Model> page, Model param) {
        return super.page(page, ExtMapperUtil.modelToWrapper(param));
    }

    @Override
    public int insertBatch(List<Model> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        list.parallelStream().forEach(ModelUtil::initModel);
        super.saveBatch(list, 100);
        return list.size();
    }

    @Override
    public boolean save(Model entity) {
        ModelUtil.initModel(entity);
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<Model> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        try {
            String time = DateUtils.nowDateFormat();
            String userId = null;
            try {
                userId = String.valueOf(SecurityBaseUtil.getUserId());
            } catch (Exception ignored) {
            }
            final String finalUserId = userId;
            for (Model item : entityList) {
                ModelUtil.initModel(item, true, time, finalUserId);
            }
            CollUtil.split(entityList, 100).forEach(super::saveBatch);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BebasMapperException("批量新增失败");
        }
    }

    @Override
    public boolean removeById(Model entity) {
        Long id = entity.getId();
        return this.removeById(id);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        return this.removeByIds(list);
    }

    @Override
    public boolean updateById(Model entity) {
        Long id = entity.getId();
        if (Objects.isNull(id)) {
            return false;
        }
        ModelUtil.initModel(entity, Boolean.FALSE);
        return super.updateById(entity);
    }

    /**
     * sum聚合函数统计
     *
     * @param columnName 列名
     * @param wrapper    条件构造
     * @return
     */
    @Override
    public Number sumByColumn(String columnName, QueryWrapper<Model> wrapper) {
        Assert.notEmpty(columnName, () -> new BebasMapperException("sum方法参数[columnName]不能为空"));
        String columnAttrName = ModelUtil.lineToHump(columnName.toLowerCase(Locale.ROOT));
        wrapper.select(String.format("sum(%s) as %s", columnName, columnAttrName));
        Model one = super.getOne(wrapper);
        if (Objects.isNull(one)) {
            return 0;
        }
        return (Number) ReflectUtil.getFieldValue(one, columnAttrName);
    }

}
