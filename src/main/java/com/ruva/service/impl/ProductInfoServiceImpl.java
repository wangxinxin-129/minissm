package com.ruva.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruva.mapper.ProductInfoMapper;
import com.ruva.pojo.ProductInfo;
import com.ruva.pojo.ProductInfoExample;
import com.ruva.pojo.vo.ProductInfoVo;
import com.ruva.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    //切记：业务逻辑层中一定有数据访问层的对象
    @Autowired
    ProductInfoMapper productInfoMapper;

    @Override
    public List<ProductInfo> getAll() {
        return productInfoMapper.selectByExample(new ProductInfoExample());
    }

    @Override
    public PageInfo splitPage(Integer pageNum, Integer pageSize) {
        //分页插件使用PageHelper工具类完成分页设置
        PageHelper.startPage(pageNum, pageSize);

        //进行PageInfo的数据封装
        //进行有条件的查询操作，必须要创建ProductInfoExample对象
        ProductInfoExample example = new ProductInfoExample();

        //设置排序，按主键降序排列
        example.setOrderByClause("p_id desc");

        //设置完排序后，取集合，切记：一定要在取集合之前，设置PageHelper.startPage(pageNum, pageSize);
        List<ProductInfo> list = productInfoMapper.selectByExample(example);

        //将查到的集合封装进PageInfo 对象中
        PageInfo<ProductInfo> pageInfo = new PageInfo<>(list);

        return pageInfo;
    }

    @Override
    public Integer save(ProductInfo info) {
        return productInfoMapper.insert(info);
    }

    @Override
    public ProductInfo getById(Integer pid) {
        return productInfoMapper.selectByPrimaryKey(pid);
    }

    @Override
    public Integer update(ProductInfo info) {
        return productInfoMapper.updateByPrimaryKey(info);
    }

    @Override
    public Integer delete(Integer pid) {
        return productInfoMapper.deleteByPrimaryKey(pid);
    }

    @Override
    public Integer deleteBatch(String[] ids) {
        return productInfoMapper.deleteBatch(ids);
    }

    @Override
    public List<ProductInfo> selectCondition(ProductInfoVo vo) {
        return productInfoMapper.selectCondition(vo);
    }

    @Override
    public PageInfo<ProductInfo> splitPageVo(ProductInfoVo vo, Integer pageSize) {
        //取出集合之前，先要设置PageHelper.startPage()属性
        PageHelper.startPage(vo.getPage(), pageSize);
        List<ProductInfo> list = productInfoMapper.selectCondition(vo);
        return new PageInfo<>(list);
    }
}
