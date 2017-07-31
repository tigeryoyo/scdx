package com.hust.scdx.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.RolePowerDao;
import com.hust.scdx.model.RolePower;
import com.hust.scdx.service.RolePowerService;

@Service
public class RolePowerServiceImpl implements RolePowerService {

	@Autowired
	private RolePowerDao rolePowerDao;

	@Override
	public List<RolePower> selectRolePowerByRoleId(int roleId) {
		return rolePowerDao.selectRolePowerByRoleId(roleId);
	}
}
