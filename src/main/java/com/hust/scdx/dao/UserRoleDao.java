package com.hust.scdx.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.model.UserRole;
import com.hust.scdx.model.UserRoleExample;
import com.hust.scdx.model.UserRoleExample.Criteria;
import com.hust.scdx.dao.mapper.UserRoleMapper;

@Repository
public class UserRoleDao {
	@Autowired
	private UserRoleMapper userRoleMapper;

	/**
	 * 添加用户角色
	 * 
	 * @param userId
	 *            用户id
	 * @param roleId
	 *            用户角色id
	 * @return
	 */
	public int insertUserRole(Integer userId, int roleId) {
		UserRole userRole = new UserRole();
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		return userRoleMapper.insert(userRole);
	}

	/**
	 * 添加用户角色
	 * 
	 * @param userId
	 *            用户id
	 * @param userRoleName
	 *            用户角色名
	 * @return
	 */
	public int insertUserRole(Integer userId, String userRoleName) {
		return 0;
	}
	
	/**
	 * 根据用户id查找用户角色
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 */
	public UserRole selectUserRoleByUserId(int userId) {
		UserRoleExample example = new UserRoleExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<UserRole> userRoleS = userRoleMapper.selectByExample(example);
		if (userRoleS == null || userRoleS.isEmpty()) {
			return null;
		}
		return userRoleS.get(0);
	}

	/**
	 * 更新用户角色
	 * 
	 * @param userId
	 *            用户id
	 * @param roleId
	 *            修改完后的用户角色id
	 * @return
	 */
	public int updateUserRole(int userId, int roleId) {
		UserRole userRole = selectUserRoleByUserId(userId);
		userRole.setRoleId(roleId);
		return userRoleMapper.updateByPrimaryKey(userRole);
	}

}
