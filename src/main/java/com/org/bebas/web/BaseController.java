package com.org.bebas.web;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.org.bebas.core.model.BaseModel;
import com.org.bebas.core.validator.group.GroupUpdate;
import com.org.bebas.enums.result.ResultEnum;
import com.org.bebas.mapper.service.IService;
import com.org.bebas.utils.StringUtils;
import com.org.bebas.utils.page.PageUtil;
import com.org.bebas.utils.result.Result;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公共控制器-基本增删改查
 *
 * @author WuHao
 * @since 2022/5/14 9:22
 */
public abstract class BaseController<S extends IService<M>, M extends BaseModel> {

    protected final Logger log = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected S service;

    @ResponseBody
    @GetMapping("/baseQueryById/{id}")
    @ApiOperation(value = "基础功能-通过ID查询单条记录", notes = "基础功能-通过ID查询单条记录", httpMethod = "GET", response = Result.class)
    @ApiOperationSupport(order = 1)
    protected Result baseQueryById(@PathVariable("id") Long id) {
        if (ObjectUtil.isNull(id) || id <= 0L) {
            return Result.fail("id不能为空！");
        }
        M m = service.getById(id);
        return Result.success(m);
    }

    @ResponseBody
    @PostMapping("/baseQueryByParam")
    @ApiOperation(value = "基础功能-条件查询", httpMethod = "POST", response = Result.class)
    @ApiOperationSupport(order = 2)
    protected Result baseQueryByParam(@RequestBody(required = false) M param) {
        if (ObjectUtil.isNull(param)) {
            param = service.modelInstance();
        }
        List<M> list = service.listByParam(param);
        return Result.success(list);
    }

    @ResponseBody
    @PostMapping("/baseQueryPageByParam")
    @ApiOperation(value = "基础功能-条件查询分页", httpMethod = "POST", response = Result.class)
    @ApiOperationSupport(order = 3)
    protected Result baseQueryPageByParam(@RequestBody(required = false) M param) {
        if (ObjectUtil.isNull(param)) {
            param = service.modelInstance();
        }
        IPage<M> page = PageUtil.pageBean(param);
        return Result.success(service.listPageByParam(page, param));
    }

    @ResponseBody
    @PostMapping("/baseAdd")
    @ApiOperation(value = "基础功能-新增", httpMethod = "POST", response = Result.class)
    @ApiOperationSupport(order = 4)
    protected <DTO> Result baseAdd(@RequestBody DTO m) {
        M param = JSON.parseObject(JSON.toJSONString(m), service.getModelClass());
        if (!service.save(param)) {
            return Result.fail(ResultEnum.FAIL_INSERT);
        }
        return Result.success(ResultEnum.SUCCESS_INSERT);
    }

    @ResponseBody
    @PutMapping("/baseEdit")
    @ApiOperation(value = "基础功能-修改", httpMethod = "PUT", response = Result.class)
    @ApiOperationSupport(order = 5)
    protected <DTO> Result baseEdit(@RequestBody @Validated(value = {GroupUpdate.class}) DTO m) {
        M param = JSON.parseObject(JSON.toJSONString(m), service.getModelClass());
        if (!service.updateById(param)) {
            return Result.fail(ResultEnum.FAIL_UPDATE);
        }
        return Result.success(ResultEnum.SUCCESS_UPDATE);
    }

    @ResponseBody
    @DeleteMapping("/baseDeleteByIds/{ids}")
    @ApiOperation(value = "基础功能-根据主键id删除(多个id根据,分隔)", httpMethod = "DELETE", response = Result.class)
    @ApiOperationSupport(order = 6)
    protected Result baseDeleteByIds(@PathVariable("ids") String ids) {
        if (StrUtil.isEmpty(ids)) {
            return Result.fail("删除条件id不能为空");
        }
        String[] idsArr = ids.split(StringPool.COMMA);
        if (idsArr.length > 1000) {
            return Result.fail("不能批量删除超过1000个数据");
        }
        List<Long> idList = StringUtils.splitToList(ids, Long::valueOf);
        if (service.removeByIds(idList)) {
            return Result.success(ResultEnum.SUCCESS_DELETE);
        }
        return Result.success(ResultEnum.FAIL_DELETE);
    }

}
