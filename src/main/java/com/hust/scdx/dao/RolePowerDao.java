package com.hust.scdx.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.model.RolePower;
import com.hust.scdx.model.RolePowerExample;
import com.hust.scdx.model.RolePowerExample.Criteria;
import com.hust.scdx.dao.mapper.RolePowerMapper;

@Repository
public class RolePowerDao {
	@Autowired
	private RolePowerMapper rolePowerMapper;
	
	public List<RolePower> selectRolePowerByRoleId(int roleId) {
		RolePowerExample example = new RolePowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andRoleIdEqualTo(roleId);
		List<RolePower> rolePowers = rolePowerMapper.selectByExample(example);
		if(rolePowers == null || rolePowers.isEmpty()){
			return null;
		}
		return rolePowers;
	}
}
