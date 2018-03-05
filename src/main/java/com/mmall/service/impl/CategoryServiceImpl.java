package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 *商品类别的业务
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 增加一个商品类别
     * @param categoryName
     * @param parenId
     * @return
     */
    public ServerResponse addCategory(String categoryName , Integer parenId){

        if(parenId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName((categoryName));
        category.setParentId(parenId);
        category.setStatus(true);  //这个分类是可用的

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * 更新商品品类名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }

        return ServerResponse.createByErrorMessage("更新品类名称失败");
    }

    /**
     * 查询商品类别的所有平行子节点
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Category>> getChildrenParalleCategory(Integer categoryId){
            List<Category> categoryLIst = categoryMapper.selectCategoryChildrenByParentId(categoryId);
            if(CollectionUtils.isEmpty(categoryLIst)){
                logger.info("未找到当前分类的子分类");
            }
            return ServerResponse.createBySuccess(categoryLIst);
    }


    /**
     * 递归查询本节点的id以及所有子节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet(); //效果等同与new HashSet<Category>();
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


    //递归算法,算出子节点    先找父节点》找子节点集合》找子节点的子节点........每次递归都add新节点
    //假如 1是根节点  1下面有10、20、30 ， 20下面有200 300 这样的结构  add顺序是1、10、20、200、300、30;
    private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category !=null){
            categorySet.add(category);
        }
        //查找子节点,递归算法一定要有一个退出的条件
        //mybati 默认对List的查询策略是 list不会为空  所以不用做空判断
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category category1 : categoryList) {
            findChildCategory(categorySet,category1.getId());
        }
        return categorySet;
    }


}
