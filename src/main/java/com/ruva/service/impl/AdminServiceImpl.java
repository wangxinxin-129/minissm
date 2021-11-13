package com.ruva.service.impl;

import com.ruva.mapper.AdminMapper;
import com.ruva.pojo.Admin;
import com.ruva.pojo.AdminExample;
import com.ruva.service.AdminService;
import com.ruva.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    //在业务逻辑层中，一定会有数据访问层的对象
    @Autowired
    AdminMapper adminMapper;

    @Override
    public Admin login(String name, String pwd) {
        Admin admin = new Admin();
        //根据传入的用户名到数据库中查询相应的用户对象

        //如果有条件，则一定要创建AdminExample的对象，用来封装条件
        AdminExample example = new AdminExample();

        /**
         * 如何添加条件
         * select * from admin where a_name = "admin"
         */
        //添加用户名a_name条件
        example.createCriteria().andANameEqualTo(name);
        List<Admin> list = adminMapper.selectByExample(example);
        if(list.size() > 0){
            admin = list.get(0);

            //如果查询到用户对象，再进行密码的对比，注意密码是密文
            String miPwd = MD5Util.getMD5(pwd);
            if(miPwd.equals(admin.getaPass())) {
                return admin;
            }
        }
        return null;
    }
}
