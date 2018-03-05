package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    public ServerResponse addCategory(String categoryName , Integer parenId);

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    public ServerResponse<List<Category>> getChildrenParalleCategory(Integer categoryId);

    public ServerResponse selectCategoryAndChildrenById(Integer categoryId);

}
