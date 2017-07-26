package com.hust.scdx.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.service.UserService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/")
public class AuthController {
	
	@Autowired
	private UserService userService;

	/**
	 * 登陆
	 */
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Object login(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password, HttpServletRequest request) {
		if (userService.login(username, password, request)) {
			return ResultUtil.successWithoutMsg();
		}
		return ResultUtil.errorWithMsg("用户名或密码错误");
	}

	/**
	 * 登出
	 */
	@ResponseBody
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public Object logout(HttpServletRequest request) {
		userService.logout(request);
		return ResultUtil.successWithoutMsg();
	}

	/**
	 * 获取用户真实姓名
	 */
	@ResponseBody
	@RequestMapping(value = "/getCurrentUserTrueName", method = RequestMethod.POST)
	public Object getCurrentUserTrueName(HttpServletRequest request) {
		return ResultUtil.success(userService.selectCurrentUser(request).getTrueName());
	}

	/**
	 * 获取用户id
	 */
	@ResponseBody
	@RequestMapping(value = "/getCurrentUserId", method = RequestMethod.POST)
	public Object getCurrentUserId(HttpServletRequest request) {
		return ResultUtil.success(userService.selectCurrentUser(request).getUserId());
	}
}
