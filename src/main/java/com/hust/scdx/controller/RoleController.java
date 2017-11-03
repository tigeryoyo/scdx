package com.hust.scdx.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Role;
import com.hust.scdx.service.RoleService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/role")
public class RoleController {
	@Autowired
	private RoleService roleService;

	/**
	 * 查找所有用户
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectAllRole")
	public Object selectAllRole(HttpServletRequest request) {
		List<Role> roles = roleService.selectAllRole(request);
		if (null == roles || roles.size() == 0) {
			return ResultUtil.errorWithMsg("查找用户角色失败。");
		}
		return ResultUtil.success(roles);
	}

}