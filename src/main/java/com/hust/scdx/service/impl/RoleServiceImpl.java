package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.RoleDao;
import com.hust.scdx.dao.UserRoleDao;
import com.hust.scdx.model.Role;
import com.hust.scdx.model.User;
import com.hust.scdx.model.UserRole;
import com.hust.scdx.service.RoleService;
import com.hust.scdx.service.UserService;

@Service
public class RoleServiceImpl implements RoleService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

	@Autowired
	UserService userService;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private UserRoleDao userRoleDao;

	/**
	 * 根据当前用户角色查找用户能查看的角色
	 * 
	 * @param request
	 * @return
	 */
	@Override
	public List<Role> selectAllRole(HttpServletRequest request) {
		List<Role> res = new ArrayList<Role>();
		try {
			User user = userService.selectCurrentUser(request);
			int currentRoleId = userRoleDao.selectUserRoleByUserId(user.getUserId()).getRoleId();
			List<Role> roles = roleDao.selectAllRole();
			res.addAll(roles);
			res.add(0, roleDao.selectRoleByRoleId(currentRoleId));
		} catch (Exception e) {
			logger.error("查找用户角色失败。");
			return null;
		}
		return res;
	}

}