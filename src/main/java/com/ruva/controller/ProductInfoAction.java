package com.ruva.controller;

import com.github.pagehelper.PageInfo;
import com.ruva.pojo.ProductInfo;
import com.ruva.pojo.vo.ProductInfoVo;
import com.ruva.service.ProductInfoService;
import com.ruva.utils.FileNameUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/prod")
public class ProductInfoAction {

    //每页显示的记录数
    public static final Integer PAGE_SIZE = 5;

    //异步上传的图片名称
    String saveFileName = "";

    //切记：在界面层中，一定会有业务逻辑层对象
    @Autowired
    ProductInfoService productInfoService;

    //显示全部商品不分页
    @RequestMapping("/getAll")
    public String getAll(HttpServletRequest request) {
        List<ProductInfo> list = productInfoService.getAll();
        request.setAttribute("list", list);
        return "product";
    }

    //显示第一页的五条记录
    @RequestMapping("/split")
    public String split(HttpServletRequest request) {
        PageInfo info = null;
        Object vo = request.getSession().getAttribute("prodVo");
        if(vo != null){
            info = productInfoService.splitPageVo((ProductInfoVo) vo, PAGE_SIZE);
            request.getSession().removeAttribute("prodVo");
        } else {
            //得到第一页的数据
            info = productInfoService.splitPage(1, PAGE_SIZE);
        }
        request.setAttribute("info", info);
        return "product";
    }

    //ajax分页翻页处理
    @RequestMapping("/ajaxSplit")
    @ResponseBody
    public void ajaxSplit(ProductInfoVo vo, HttpSession session) {
        //取得当前page参数的页面的数据
        PageInfo info = productInfoService.splitPageVo(vo, PAGE_SIZE);
        session.setAttribute("info", info);
    }

    //异步ajax文件上传处理
    @RequestMapping("/ajaxImg")
    @ResponseBody
    public Object ajaxImg(MultipartFile pimage, HttpServletRequest request) {
        //提取生成文件名UUID + 上传图片的后缀：.jpg    .png
        saveFileName = FileNameUtil.getUUIDFileName() + FileNameUtil.getFileType(pimage.getOriginalFilename());

        //得到项目中图片存储的路径
        String path = request.getServletContext().getRealPath("/image_big");

        //转存
        try {
            pimage.transferTo(new File(path + File.separator + saveFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回客户端JSON对象，封装图片的路径，为了在页面实现立即回显
        JSONObject object = new JSONObject();
        object.put("imgurl", saveFileName);

        return object.toString();
    }

    @RequestMapping("/save")
    public String save(ProductInfo info, HttpServletRequest request) {

        Integer num = -1;

        info.setpImage(saveFileName);
        info.setpDate(new Date());

        //info中有表单提交的5个数据，有异步ajax上来的图片名称数据，有上架时间的数据
        try {
            num = productInfoService.save(info);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (num > 0) {
            request.setAttribute("msg", "增加成功！");
        } else {
            request.setAttribute("msg", "增加失败！");
        }

        //清空saveFileName变量中的内容，为了下次增加或修改的异步ajax的上传处理
        saveFileName = "";
        //增加成功后应该重新访问数据库，所以跳转到分页显示的action上
        return "forward:/prod/split.action";
    }

    @RequestMapping("/one")
    public String one(Integer pid, ProductInfoVo vo, Model model, HttpSession session) {
        ProductInfo info = productInfoService.getById(pid);
        model.addAttribute("prod", info);
        //将多条件及页码放入session中，更新处理结束后，分页时读取条件和页码进行处理
        session.setAttribute("prodVo", vo);
        return "update";
    }

    @RequestMapping("/update")
    public String update(ProductInfo info, HttpServletRequest request) {
        //因为ajax的异步图片上传，则saveFileName里有上传上来的图片的名称，
        // 如果没有使用异步ajax上传过图片，则saveFileName="";
        //实体类info使用隐藏表单域提供上来的pImage原始图片名称
        if(!saveFileName.equals("")) {
            info.setpImage(saveFileName);
        }
        //完成更新操作
        Integer num = -1;
        try {
            num = productInfoService.update(info);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(num > 0) {
            //此时说明更新成功！
            request.setAttribute("msg", "更新成功！");
        }else {
            //更新失败！
            request.setAttribute("msg", "更新失败！");
        }

        //处理完更新后，saveFileName里可能有数据，而下一次要使用这个变量作为判断的依据，
        // 就会出错，所以必须清空saveFileName
        saveFileName = "";
        return  "forward:/prod/split.action";
    }

    @RequestMapping("/cancel")
    public String cancel(HttpServletRequest request) {

        return "forward:/prod/split.action";
    }

    @RequestMapping("/delete")
    public String delete(Integer pid, ProductInfoVo vo, HttpServletRequest request) {
        Integer num = -1;
        try {
            num = productInfoService.delete(pid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(num > 0) {
            //删除成功！
            request.setAttribute("msg", "删除成功！");
        } else {
            //删除失败！
            request.setAttribute("msg", "删除失败！");
            request.getSession().setAttribute("deleteProdVo", vo);
        }

        //删除成功后，跳到分页显示
        return "forward:/prod/deleteAjaxSplit.action";
    }

    @RequestMapping(value = "/deleteAjaxSplit", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object deleteAjaxSplit(HttpServletRequest request) {
        PageInfo info = null;
        Object vo = request.getSession().getAttribute("deleteProdVo");
        if(vo != null){
            info = productInfoService.splitPageVo((ProductInfoVo) vo, PAGE_SIZE);
        } else {
            //取得第一页的数据，
            info = productInfoService.splitPage(1, PAGE_SIZE);
        }
        request.getSession().setAttribute("info", info);
        return request.getAttribute("msg");
    }

    @RequestMapping("/deleteBatch")
    public String deleteBatch(String pids, HttpServletRequest request) {
        //将上传上来的字符串截断，形成商品id的字符串数组
        String []ps = pids.split(",");
        try {
            Integer num = productInfoService.deleteBatch(ps);
            if (num > 0) {
                request.setAttribute("msg", "批量删除成功！");
            } else {
                request.setAttribute("msg", "批量删除失败！");
            }
        } catch (Exception e) {
            request.setAttribute("msg", "商品可不删除！");
        }
        return "forward:/prod/deleteAjaxSplit.action";
    }

}
