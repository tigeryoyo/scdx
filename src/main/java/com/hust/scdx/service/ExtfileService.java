package com.hust.scdx.service;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.params.ExtfileQueryCondition;

public interface ExtfileService {

	int insert(ExtfileQueryCondition con, HttpServletRequest request);
}
