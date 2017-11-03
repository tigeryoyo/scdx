package com.hust.scdx.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.model.Power;
import com.hust.scdx.model.PowerExample;
import com.hust.scdx.model.PowerExample.Criteria;
import com.hust.scdx.dao.mapper.PowerMapper;

@Repository
public class PowerDao {
	@Autowired
	private PowerMapper powerMapper;

	/**
	 * 插入权限
	 * 
	 * @param powerName
	 * @param powerUrl
	 * @return
	 */
	public boolean insertPower(String powerName, String powerUrl) {
		Power power = new Power();
		power.setPowerName(powerName);
		power.setPowerUrl(powerUrl);
		if (0 == powerMapper.insert(power)) {
			return false;
		}
		return true;
	}

	/**
	 * 删除权限，由于外键原因，role_power表相对应的power会连带删除
	 * 
	 * @param powerId
	 * @return
	 */
	public boolean deletePower(int powerId) {
		if (0 == powerMapper.deleteByPrimaryKey(powerId)) {
			return false;
		}
		return true;
	}

	/**
	 * 查询所有权限
	 * 
	 * @return
	 */
	public List<Power> selectAllPower() {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andPowerIdIsNotNull();
		return powerMapper.selectByExample(example);
	}

	/**
	 * 根据powerId查询power
	 * 
	 * @param powerId
	 * @return
	 */
	public Power selectPowerById(int powerId) {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andPowerIdEqualTo(powerId);
		List<Power> powers = powerMapper.selectByExample(example);
		if (null == powers || powers.isEmpty()) {
			return null;
		}
		return powers.get(0);
	}

	public Power selectPowerByNameAndUrl(String powerName, String powerUrl) {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andPowerNameEqualTo(powerName);
		criteria.andPowerUrlEqualTo(powerUrl);
		List<Power> powers = powerMapper.selectByExample(example);
		if (null == powers || powers.isEmpty()) {
			return null;
		}
		return powers.get(0);
	}

}
