package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.dao.PowerDao;
import com.hust.scdx.dao.RoleDao;
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
	private RoleDao roleDao;
	@Autowired
	private UserRoleDao userRoleDao;
	@Autowired
	private RolePowerDao rolePowerDao;
	@Autowired
	private PowerDao powerDao;

	/**
	 * 插入用户信息与角色。
	 */
	@Override
	public boolean insertUser(User user, int roleId) {
		try {
			if (0 == userDao.insert(user)) {
				logger.info("添加用户失败。");
				return false;
			}
			user = userDao.selectUserByUserName(user.getUserName());
			if (0 == userRoleDao.insertUserRole(user.getUserId(), roleId)) {
				logger.info("添加用户角色失败。");
				userDao.deleteByUserName(user.getUserName());
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 插入用户信息与角色。
	 */
	@Override
	public boolean insertUser(User user, String userRoleName) {
		try {
			return insertUser(user, roleDao.selectRoleIdByRoleName(userRoleName));
		} catch (Exception e) {
			logger.error("添加用户失败。");
			return false;
		}
	}

	/**
	 * 删除指定用户，由于数据库的外键关联相对应的user_role表中的数据也会删除。
	 */
	@Override
	public boolean deleteUserById(int userId) {
		if (0 == userDao.deleteByPrimaryKey(userId)) {
			logger.info("删除用户(userId:" + userId + ")失败。");
			return false;
		}
		return true;
	}

	/**
	 * 更新用户信息
	 */
	@Override
	public boolean updateUser(User user, String userRoleName) {
		if (0 == userDao.updateByPrimaryKey(user)) {
			logger.info("更改用户信息失败。");
			return false;
		}
		if (userRoleName != null) {
			if (0 == userRoleDao.updateUserRole(user.getUserId(), roleDao.selectRoleIdByRoleName(userRoleName))) {
				logger.info("更新用户角色失败。");
				return false;
			}
		}
		return true;
	}

	/**
	 * 查找当前用户
	 */
	@Override
	public User selectCurrentUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(Constant.UESR);
	}

	/**
	 * 根据用户id查找用户角色名称
	 */
	private String selectUserRoleNameByUserId(int userId) {
		return roleDao.selectRoleNameByRoleId(userRoleDao.selectUserRoleByUserId(userId).getRoleId());
	}

	/**
	 * 根据用户名查找用户
	 */
	@Override
	public User selectUserByUserName(String userName) {
		return userDao.selectUserByUserName(userName);
	}

	/**
	 * 查询所有用户,key=User,value=userRoleName
	 */
	@Override
	public List<String[]> selectAllUser() {
		List<User> users = userDao.selectAllUser();
		List<String[]> list = new ArrayList<String[]>();
		try {
			for (User user : users) {
				String[] strs = new String[6];
				strs[0] = String.valueOf(user.getUserId());
				strs[1] = user.getUserName();
				strs[2] = user.getTrueName();
				strs[3] = user.getTelphone();
				strs[4] = user.getEmail();
				strs[5] = selectUserRoleNameByUserId(user.getUserId());
				list.add(strs);
			}
		} catch (Exception e) {
			logger.error("查找所有用户失败。");
			return null;
		}
		return list;
	}

	/**
	 * 获取当前用户的所有权限
	 */
	HashSet<String> selectUserPowersByUserId(int userId) {
		UserRole userRole = userRoleDao.selectUserRoleByUserId(userId);
		if (userRole == null) {
			logger.info("当前用户不存在角色。");
			return null;
		}
		List<RolePower> rolePowers = rolePowerDao.selectRolePowerByRoleId(userRole.getRoleId());
		if (rolePowers == null || rolePowers.isEmpty()) {
			return null;
		}
		HashSet<String> powers = new HashSet<String>();
		for (RolePower rolePower : rolePowers) {
			powers.add(powerDao.selectPowerById(rolePower.getPowerId()).getPowerUrl());
		}

		return powers;
	}

	/**
	 * 用户登陆
	 */
	@Override
	public int login(String username, String password, HttpServletRequest request) {
		User user = userDao.login(username, password);
		if (null == user) {
			logger.info("用户名或密码错误。");
			return Constant.ERROR_CODE;
		}
		HttpSession session = request.getSession();
		// 将user存入session中,user为当前user对象。
		session.setAttribute(Constant.UESR, user);
		// 将用户权限存入session中,存的是权限路径list
		session.setAttribute(Constant.USERPOWER, selectUserPowersByUserId(user.getUserId()));
		return userRoleDao.selectUserRoleByUserId(user.getUserId()).getRoleId();
	}

	/**
	 * 用户退出
	 */
	@Override
	public void logout(HttpServletRequest request) {
		// 销毁session
		request.getSession().invalidate();
	}

}
