package com.hust.scdx.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.constant.Constant;
import com.hust.scdx.model.User;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/")
public class AuthController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	@Autowired
	private UserService userService;

	/**
	 * 登陆
	 */
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Object login(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password, HttpServletRequest request) {
		int roleId = userService.login(username, password, request);
		if (Constant.ERROR_CODE == roleId) {
			return ResultUtil.errorWithMsg("用户名或密码错误");
		}
		return ResultUtil.success(roleId);
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
		try {
			User user = userService.selectCurrentUser(request);
			if (user == null) {
				return ResultUtil.errorWithMsg("登陆失效,请重新登陆。");
			}
			return ResultUtil.success(user.getTrueName());
		} catch (Exception e) {
			logger.info("查找用户名失败,用户session失效。");
		}
		return ResultUtil.errorWithMsg("登陆失败,请重新登陆。");
	}

	/**
	 * 获取用户id
	 */
	@ResponseBody
	@RequestMapping(value = "/getCurrentUserId", method = RequestMethod.POST)
	public Object getCurrentUserId(HttpServletRequest request) {
		try {
			User user = userService.selectCurrentUser(request);
			if (user == null) {
				return ResultUtil.errorWithMsg("登陆失效,请重新登陆。");
			}
			return ResultUtil.success(user.getUserId());
		} catch (Exception e) {
			logger.info("查找用户名失败,用户session失效。");
		}
		return ResultUtil.errorWithMsg("登陆失败,请重新登陆。");
	}
}
