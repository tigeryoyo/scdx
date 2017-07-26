package com.hust.scdx.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.RolePowerDao;
import com.hust.scdx.model.RolePower;
import com.hust.scdx.service.RolePowerService;

@Service
public class RolePowerServiceImpl implements RolePowerService {
	private static final Logger logger = LoggerFactory.getLogger(RolePowerServiceImpl.class);

	@Autowired
	private RolePowerDao rolePowerDao;

	@Override
	public List<RolePower> selectRolePowerByRoleId(int roleId) {
		return rolePowerDao.selectRolePowerByRoleId(roleId);
	}
}
