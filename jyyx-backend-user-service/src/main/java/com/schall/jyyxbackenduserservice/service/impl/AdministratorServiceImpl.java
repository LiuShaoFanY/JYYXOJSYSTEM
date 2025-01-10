package com.schall.jyyxbackenduserservice.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.schall.jyyx.model.entity.Administrator;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyxbackenduserservice.mapper.AdministratorMapper;
import com.schall.jyyxbackenduserservice.service.AdministratorService;
import com.schall.jyyxbackenduserservice.service.UserService;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@Slf4j
public class AdministratorServiceImpl extends ServiceImpl<AdministratorMapper, Administrator> implements AdministratorService {
    @Resource
    private AdministratorMapper administratorMapper;
    @Resource
    @Lazy // 使用 @Lazy 注解打破循环依赖
    private UserService userService;

    @Override
    public Administrator createAdministrator(Long user_id, String adminId, String department) {
        log.info("创建管理员信息: user_id={}, adminId={}, department={}", user_id, adminId, department);

        // 校验
        if (user_id == null || adminId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 检查 adminId 是否已存在
        QueryWrapper<Administrator> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("adminId", adminId);
        if (this.getOne(queryWrapper) != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "管理员编号已存在");
        }

        // 获取用户信息
        User user = userService.getById(user_id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 创建管理员信息
        Administrator administrator = new Administrator();
        administrator.setUser_id(user_id);
        administrator.setAdminId(adminId);
        administrator.setDepartment(department);
        administrator.setUserAccount(user.getUserAccount()); // 设置 userAccount
        administrator.setUserPassword(user.getUserPassword()); // 设置 userPassword
        administrator.setUserName(user.getUserName()); // 设置昵称

        boolean save = this.save(administrator);
        if (!save) {
            log.error("保存管理员信息失败: user_id={}, adminId={}", user_id, adminId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建管理员信息失败");
        }

        log.info("管理员信息创建成功: user_id={}, adminId={}", user_id, adminId);
        return administrator;
    }

    @Override
    public Administrator getAdministratorByUserId(Long user_id) {
        log.info("Fetching administrator with user_id: {}", user_id);
        if (user_id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Administrator> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user_id);
        Administrator administrator = this.getOne(queryWrapper);
        if (administrator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到对应的管理员信息");
        }

        // 获取用户的昵称
        User user = userService.getById(user_id);
        if (user != null) {
            administrator.setUserName(user.getUserName()); // 设置昵称
        }

        return administrator;
    }


    @Override
    public Administrator getByUserId(Long userId) {
        return administratorMapper.selectByUserId(userId);
    }
}