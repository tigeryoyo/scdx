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

}
