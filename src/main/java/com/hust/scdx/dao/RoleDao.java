package com.hust.scdx.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.RoleMapper;
import com.hust.scdx.model.Role;
import com.hust.scdx.model.RoleExample;
import com.hust.scdx.model.RoleExample.Criteria;

@Repository
public class RoleDao {

	@Autowired
	private RoleMapper roleMapper;

	/**
	 * 根据角色id查找角色
	 * 
	 * @param roleId
	 */
	public Role selectRoleByRoleId(int roleId) {
		return roleMapper.selectByPrimaryKey(roleId);
	}

	/**
	 * 根据角色id查找角色名
	 * 
	 * @param roleId
	 */
	public String selectRoleNameByRoleId(int roleId) {
		return roleMapper.selectByPrimaryKey(roleId).getRoleName();
	}

	/**
	 * 查找所有用户角色
	 * 
	 * @return
	 */
	public List<Role> selectAllRole() {
		RoleExample example = new RoleExample();
		Criteria criteria = example.createCriteria();
		criteria.andRoleIdIsNotNull();
		example.setOrderByClause("role_id asc");
		return roleMapper.selectByExample(example);
	}

	/**
	 * 通过用户角色名查找用户角色id
	 * 
	 * @return
	 */
	public int selectRoleIdByRoleName(String userRoleName) {
		RoleExample example = new RoleExample();
		Criteria criteria = example.createCriteria();
		criteria.andRoleNameEqualTo(userRoleName);
		List<Role> roles = roleMapper.selectByExample(example);
		if (roles == null || roles.isEmpty()) {
			return -1;
		}
		return roles.get(0).getRoleId();
	}

}
