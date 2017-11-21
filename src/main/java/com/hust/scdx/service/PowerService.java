package com.hust.scdx.service;

import java.util.List;

import com.hust.scdx.model.Power;
import com.hust.scdx.model.params.PowerQueryCondition;

public interface PowerService {
	
	boolean insertPower(int roleId, String powerName, String powerUrl);

	boolean deletePower(int powerId);

	List<Power> selectAllPower();
	
	List<Power> selectPowerByCondition(PowerQueryCondition qc);

	List<Power> selectPowerByRoleId(int roleId);
	
	boolean resetRolePower(int roleId,List<Integer> powerIds);
	
	long selectCountOfPower(PowerQueryCondition qc);
	
	boolean updatePower(Power power);

}
