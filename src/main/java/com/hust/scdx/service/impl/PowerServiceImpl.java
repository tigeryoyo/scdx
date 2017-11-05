package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.dao.PowerDao;
import com.hust.scdx.dao.RolePowerDao;
import com.hust.scdx.model.Power;
import com.hust.scdx.model.RolePower;
import com.hust.scdx.service.PowerService;

@Service
public class PowerServiceImpl implements PowerService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PowerServiceImpl.class);

	@Autowired
	private PowerDao powerDao;
	@Autowired
	private RolePowerDao rolePowerDao;

	/**
	 * 插入权限
	 */
	@Override
	public boolean insertPower(int roleId, String powerName, String powerUrl) {
		if (!powerDao.insertPower(powerName, powerUrl)) {
			logger.error("添加权限失败。");
			return false;
		}
		Power power = powerDao.selectPowerByNameAndUrl(powerName, powerUrl);
		return rolePowerDao.insertRolePower(roleId, power.getPowerId());
	}

	/**
	 * 删除权限
	 */
	@Override
	public boolean deletePower(int powerId) {
		return powerDao.deletePower(powerId);
	}

	/**
	 * 查询所有权限
	 */
	@Override
	public List<Power> selectAllPower() {
		return powerDao.selectAllPower();
	}

	/**
	 * 根据角色id查询角色所有权限
	 */
	@Override
	public List<Power> selectPowerByRoleId(int roleId) {
		List<Power> powers = new ArrayList<Power>();
		try {
			List<RolePower> rolePowers = rolePowerDao.selectRolePowerByRoleId(roleId);
			for (RolePower rolePower : rolePowers) {

				Power power = powerDao.selectPowerById(rolePower.getPowerId());
				if (power != null) {
					powers.add(power);
				}
			}
		} catch (Exception e) {
			logger.error("查询角色权限失败。");
			return null;
		}
		return powers;
	}

	/**
	 * 根据给定权限id集合，重新设置指定角色id的的权限
	 */
	@Override
	public boolean resetRolePower(int roleId, List<Integer> powerIds) {
		// TODO Auto-generated method stub
		//判断指定id是否拥有权限，有则全部删除
		if(null!=rolePowerDao.selectRolePowerByRoleId(roleId)){
			//删除指定角色id的权限
			if(!rolePowerDao.deleteRolePowerByRoleId(roleId)){
				return false;
			}
		}
		if(null == powerIds || powerIds.size() == 0){
			return true;
		}
		try{
			for (Integer powerId : powerIds) {
				rolePowerDao.insertRolePower(roleId, powerId);
			}
		}catch(Exception e){
			logger.info("重置权重过程中，添加权限失败！");
			return false;
		}
		return true;
	}
	
	

}
