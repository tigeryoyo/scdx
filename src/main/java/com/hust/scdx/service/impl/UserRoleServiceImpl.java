package com.hust.scdx.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.UserRoleDao;
import com.hust.scdx.model.UserRole;
import com.hust.scdx.service.UserRoleService;

@Service
public class UserRoleServiceImpl implements UserRoleService {
	private static final Logger logger = LoggerFactory.getLogger(UserRoleServiceImpl.class);

	@Autowired
	private UserRoleDao userRoleDao;

	@Override
	public UserRole selectUserRole(int userId) {
		UserRole userRole = userRoleDao.selectUserRoleByUserId(userId);
		if (null == userRole) {
			logger.info("用户权限不存在。");
			return userRole;
		}
		return userRole;
	}
}
