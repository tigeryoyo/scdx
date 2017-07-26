package com.hust.scdx.service;

import java.util.List;

import com.hust.scdx.model.RolePower;

public interface RolePowerService {
	List<RolePower> selectRolePowerByRoleId(int roleId);
}
