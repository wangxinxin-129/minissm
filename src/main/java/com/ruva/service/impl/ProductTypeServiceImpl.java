package com.ruva.service.impl;

import com.ruva.mapper.ProductTypeMapper;
import com.ruva.pojo.ProductType;
import com.ruva.pojo.ProductTypeExample;
import com.ruva.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ProductTypeServiceImpl")
public class ProductTypeServiceImpl implements ProductTypeService {

    //在业务逻辑层，一定会有数据访问层的对象
    @Autowired
    ProductTypeMapper productTypeMapper;

    @Override
    public List<ProductType> getAll() {
        return productTypeMapper.selectByExample(new ProductTypeExample());
    }
}
