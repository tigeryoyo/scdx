package com.hust.scdx.service;

import java.util.List;

import com.hust.scdx.model.Power;

public interface PowerService {
	
	boolean insertPower(int roleId, String powerName, String powerUrl);

	boolean deletePower(int powerId);

	List<Power> selectAllPower();

	List<Power> selectPowerByRoleId(int roleId);
	
	boolean resetRolePower(int roleId,List<Integer> powerIds);

}
