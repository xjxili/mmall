package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户的业务
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登陆的业务
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Pwd = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,md5Pwd);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //将密码设置成空的
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);

        }

    /**
     * 注册业务
     * @param user
     * @return
     */
    public ServerResponse<String> regist(User user){
        /*
        对校验的代码进行复用封装
         */
            ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
            if(!validResponse.isSuccess()){
                return validResponse;
            }

            validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
            if(!validResponse.isSuccess()){
                return validResponse;
            }

            user.setRole(Const.Role.ROLE_CUSTOMER);
            //MD5加密
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

            int resultCount = userMapper.insert(user);
            if(resultCount == 0){
                return ServerResponse.createByErrorMessage("注册失败");
            }

            return ServerResponse.createBySuccessMessage("注册成功");
        }

    /**
     * 校验用户名或邮箱
     * @param str
     * @param type
     * @return
     */
        public ServerResponse<String> checkValid(String str,String type){
            //isNotBlank 判断如果有值结果为true
            if(StringUtils.isNotBlank(type)){
                //校验
                if(Const.USERNAME.equals(type)){
                    int resultCount = userMapper.checkUsername(str);
                    if(resultCount > 0){
                        return ServerResponse.createByErrorMessage("用户名已存在");
                    }
                }
                if(Const.EMAIL.equals(type)){
                   int resultCount = userMapper.checkEmail(str);
                    if(resultCount > 0){
                        return ServerResponse.createByErrorMessage("邮箱已存在");
                    }
                }
            }else{
                return ServerResponse.createByErrorMessage("参数错误");
            }

            return ServerResponse.createBySuccessMessage("校验成功");
        }

    /**
     * 查找密码提示问题
     * @param username
     * @return
     */
    public ServerResponse selectQuestion(String username){
            ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
            if(validResponse.isSuccess()){
                //用户不存在
                return ServerResponse.createByErrorMessage("用户不存在");
            }
            String question = userMapper.selectQuestionByUsename(username);
            if(StringUtils.isNotBlank(question)){
                //问题存在
                return ServerResponse.createBySuccess(question);
            }
            return ServerResponse.createByErrorMessage("找回密码的问题是空的");
        }

    /**
     * 验证问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
            int resultCount = userMapper.checkAnswer(username,question,answer);
            if(resultCount > 0){
                //说明问题和答案是这个用户的,并且正确 ,生成token 并存到guava缓存当中
                String forgetToken = UUID.randomUUID().toString();
                TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
                return ServerResponse.createBySuccess(forgetToken);
            }
            return ServerResponse.createByErrorMessage("问题的答案错误");
    }

//todo url拼接username 然后再调用此接口 待会验证是否会出现漏洞

    /**
     * 忘记密码》密码重置
     * @param username
     * @param passwordNew
     * @param forgetToken  需要token验证以防被任意调用
     * 流程:验证问题答案后分发一个Token 》 调用重置密码时从缓存中获取Token,
     *                     1.验证传了forgetToken,2.验证用户名,3.根据用户名获取缓存中的token,两个token比对
     * @return
     */
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        //如果为空
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,Token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //必须判断username不为空,如果为空  token+"" 还是可以获取到token
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }

        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);

            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登陆状态》修改密码
     * @param passwordOld
     * @param passwordNew
     * @param user
     * @return
     */
    public ServerResponse<String> resetPassword (String passwordOld,String passwordNew,User user){
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是该用户,因为我们会查询一个Count(1),如果不指定id,那么结果就是true
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword((MD5Util.MD5EncodeUtf8(passwordNew)));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * 更新用户信息
     * @param user  前端传过来被修改了的user对象
     * @return
     */
    public ServerResponse<User> updateInformation(User user){
        //username不能被更新
        //email要进行校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }


    public ServerResponse<User> getInfomation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }



    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }



}
