package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.PowerDao;
import com.hust.scdx.dao.RolePowerDao;
import com.hust.scdx.dao.UserDao;
import com.hust.scdx.dao.UserRoleDao;
import com.hust.scdx.model.RolePower;
import com.hust.scdx.model.User;
import com.hust.scdx.model.UserRole;
import com.hust.scdx.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private UserRoleDao userRoleDao;
	@Autowired
	private RolePowerDao rolePowerDao;
	@Autowired
	private PowerDao powerDao;
	
	@Override
	public boolean insertUserInfo(User user, String roleName) {
		return false;
	}

	@Override
	public boolean deleteUserInfoById(int userId) {
		int status = userDao.deleteByPrimaryKey(userId);
		if (status == 0) {
			logger.info("删除用户失败。");
			return false;
		}
		return true;
	}

	@Override
	public boolean updateUserInfo(User user, String roleName) {
		return false;
	}

	@Override
	public User selectCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(Constant.UESR);
	}
	
	/**
	 * 获取当前用户的所有权限
	 */
	List<String> selectUserPowerUrl(int userId) {
		UserRole userRole = userRoleDao.selectUserRoleByUserId(userId);
		if(userRole == null){
			logger.info("当前用户不存在角色。");
			return null;
		}
		List<RolePower> rolePowers = rolePowerDao.selectRolePowerByRoleId(userRole.getRoleId());
		if(rolePowers == null || rolePowers.isEmpty()){
			return null;
		}
		List<String> powers = new ArrayList<String>();
		for(RolePower rolePower : rolePowers){
			powers.add(powerDao.selectPowerById(rolePower.getPowerId()).getPowerUrl());
		}
		
		return powers;
	}

	@Override
	public boolean login(String username, String password, HttpServletRequest request) {
		User user = userDao.login(username, password);
		if (null == user) {
			logger.info("用户名或密码错误。");
			return false;
		}
		HttpSession session = request.getSession();
		// 将user存入session中,user为当前user对象。
		session.setAttribute(Constant.UESR, user);
		// 将用户权限存入session中,存的是权限路径list
		session.setAttribute(Constant.USERPOWER, selectUserPowerUrl(user.getUserId()));
		return true;
	}

	@Override
	public void logout(HttpServletRequest request) {
		// 销毁session
		request.getSession().invalidate();
	}
}
