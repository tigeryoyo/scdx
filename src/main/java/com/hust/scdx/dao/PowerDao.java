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
	
	public Power selectPowerById(int powerId) {
		PowerExample example = new PowerExample();
		Criteria criteria = example.createCriteria();
		criteria.andPowerIdEqualTo(powerId);
		List<Power> powers = powerMapper.selectByExample(example);
		if(null == powers || powers.isEmpty()){
			return null;
		}
		return powers.get(0);
	}
	
}
