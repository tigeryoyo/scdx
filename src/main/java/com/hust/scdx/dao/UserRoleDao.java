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

	public UserRole selectUserRoleByUserId(int userId) {
		UserRoleExample example = new UserRoleExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<UserRole> userRoleS = userRoleMapper.selectByExample(example);
		if(userRoleS == null || userRoleS.isEmpty()){
			return null;
		}
		return userRoleS.get(0);
	}
}
