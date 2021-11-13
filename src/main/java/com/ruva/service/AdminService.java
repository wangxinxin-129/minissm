package com.ruva.service;

import com.ruva.pojo.Admin;
import org.springframework.stereotype.Service;

public interface AdminService {



    //完成登录
    Admin login(String name, String pwd);

    /**
     *
     */

}
