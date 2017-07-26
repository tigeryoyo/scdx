package com.hust.scdx.service;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.params.ExtfileCondition;

public interface ExtfileService {

	int insert(ExtfileCondition con, HttpServletRequest request);
}
