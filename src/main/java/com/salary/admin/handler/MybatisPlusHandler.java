package com.salary.admin.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.salary.admin.utils.UserContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
/**
 * MyBatis-Plus 自动填充配置
 * 实现 "零代码" 维护审计字段
 */
@Component
public class MybatisPlusHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        String username = getCurrentUsername();
        // 自动填充创建和更新时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 自动填充创建和更新人
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
        // 自动设置逻辑删除标识
        this.strictInsertFill(metaObject, "deleteFlag", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新操作时，只填充更新时间和更新人
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUsername());
    }
    /**
     * 获取当前操作人
     */
    private String getCurrentUsername() {
        // 直接复用咱们写好的 ThreadLocal 基建！
        String username = UserContextUtil.getUsername();

        // 如果能拿到上下文里的用户名，直接返回
        if (StringUtils.isNotBlank(username)) {
            return username;
        }
        return "system"; // 如果是登录接口等未认证场景，默认 system
    }
}
