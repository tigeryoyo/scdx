package com.hust.scdx.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.Role;

public interface RoleService {
	
	List<Role> selectAllRole(HttpServletRequest request);
	
}
