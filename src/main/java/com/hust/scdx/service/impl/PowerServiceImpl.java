package com.hust.scdx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hust.scdx.model.Power;
import com.hust.scdx.dao.PowerDao;
import com.hust.scdx.service.PowerService;

@Service
public class PowerServiceImpl implements PowerService {

	@Autowired
	private PowerDao powerDao;

	@Override
	public Power selectPowerById(int powerId) {
		return powerDao.selectPowerById(powerId);
	}
}
