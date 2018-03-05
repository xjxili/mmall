package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId,Integer status);

    //后台获取商品详情
    ServerResponse manageProductDetail(Integer productId);

    //获取商品列表
    ServerResponse<PageInfo> getProductList(int pageNum,int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);

    /*
     portal业务接口
     */
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    //根据 关键字 和 类别 排序 获取商品列表
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);

}
