package com.hust.scdx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.dao.mapper.ResultMapper;
import com.hust.scdx.model.Result;

@Repository
public class ResultDao {

	@Autowired
	ResultMapper resultMapper;

	public int insert(Result result) {
		return resultMapper.insert(result);
	}

}
