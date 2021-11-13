package com.ruva.service;

import com.github.pagehelper.PageInfo;
import com.ruva.pojo.ProductInfo;
import com.ruva.pojo.vo.ProductInfoVo;

import java.util.List;

public interface ProductInfoService {

    //显示全部商品（不分页）
    List<ProductInfo> getAll();

    //分页功能实现
    PageInfo splitPage(Integer pageNum, Integer pageSize);

    //增加商品
    Integer save(ProductInfo info);

    //按住键id 查询商品
    ProductInfo getById(Integer pid);

    //更新商品
    Integer update(ProductInfo info);

    //单个商品的删除
    Integer delete(Integer pid);

    //批量删除商品
    Integer deleteBatch(String []ids);

    //多条件商品查询
    List<ProductInfo> selectCondition(ProductInfoVo vo);

    //多条件查询分页
    public PageInfo splitPageVo(ProductInfoVo vo, Integer pageSize);
}
