package com.hust.scdx.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.User;
import com.hust.scdx.service.UserService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;

	/**
	 * 添加新用户
	 * 
	 * @param userName
	 *            用户名
	 * @param trueName
	 *            姓名
	 * @param password
	 *            密码
	 * @param telphone
	 *            电话
	 * @param email
	 *            邮箱
	 * @param roleName
	 *            用户角色名
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insertUser")
	public Object insertUser(@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "trueName", required = true) String trueName,
			@RequestParam(value = "password", required = true) String password,
			@RequestParam(value = "telphone", required = true) String telphone,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "userRoleName", required = true) String userRoleName, HttpServletRequest request) {
		if (null != userService.selectUserByUserName(userName)) {
			return ResultUtil.errorWithMsg("该用户名已被占用。");
		}
		User user = new User();
		user.setUserName(userName);
		user.setTrueName(trueName);
		user.setPassword(password);
		user.setTelphone(telphone);
		user.setEmail(email);
		user.setCreateDate(new Date());
		user.setAlgorithm(1);
		user.setGranularity(1);
		if (!userService.insertUser(user, userRoleName)) {
			return ResultUtil.errorWithMsg("创建用户失败。");
		}
		return ResultUtil.success("删除用户成功。");
	}

	/**
	 * 根据用户id删除用户
	 * 
	 * @param userId
	 *            用户id
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deleteUserById")
	public Object deleteUserById(@RequestParam(value = "userId", required = true) int userId,
			HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		if (user.getUserId().equals(userId)) {
			return ResultUtil.successWithoutStatus("不能删除自己。");
		}
		if (!userService.deleteUserById(userId)) {
			logger.error("删除用户（用户id:" + userId + "）失败。");
			return ResultUtil.errorWithMsg("删除用户失败。");
		}
		return ResultUtil.success("删除用户成功。");
	}

	/**
	 * 更改用户基本信息
	 * 
	 * @param trueName
	 *            真实姓名
	 * @param telphone
	 *            电话
	 * @param email
	 *            邮件
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateUser")
	public Object updateUser(@RequestParam(value = "trueName", required = true) String trueName,
			@RequestParam(value = "telphone", required = true) String telphone,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "userRoleName", required = false) String userRoleName, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		user.setTrueName(trueName);
		user.setTelphone(telphone);
		user.setEmail(email);
		if (!userService.updateUser(user, userRoleName)) {
			return ResultUtil.errorWithMsg("修改个人信息失败。");
		}
		return ResultUtil.success("修改个人信息成功。");
	}

	/**
	 * 更改用户密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updatePassword")
	public Object updatePassword(@RequestParam(value = "oldPassword", required = true) String oldPassword,
			@RequestParam(value = "newPassword", required = true) String newPassword, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		if (!user.getPassword().equals(oldPassword)) {
			return ResultUtil.errorWithMsg("请输入正确的原始密码。");
		}
		user.setPassword(newPassword);
		if (!userService.updateUser(user, null)) {
			return ResultUtil.errorWithMsg("修改密码出现错误。");
		}
		return ResultUtil.success("更改密码成功。");
	}

	/**
	 * 查询用户信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectCurrentUser")
	public Object selectCurrentUser(HttpServletRequest request) {
		return ResultUtil.success(userService.selectCurrentUser(request));
	}

	/**
	 * 查询用户信息
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectAllUser")
	public Object selectAllUser(HttpServletRequest request) {
		// userId,userName,trueName,tel,email,userRoleName
		List<String[]> list = userService.selectAllUser();
		if (list == null || list.isEmpty()) {
			return ResultUtil.errorWithMsg("查询所有用户出现错误。");
		}
		return ResultUtil.success(list);
	}

	/**
	 * 设置算法和粒度选择
	 * 
	 * @param algorithm
	 * @param granularity
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/setAlgorithmAndGranularity")
	public Object setAlgorithmAndgranularity(@RequestParam(value = "algorithm", required = true) int algorithm,
			@RequestParam(value = "granularity", required = true) int granularity, HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		user.setAlgorithm(algorithm);
		user.setGranularity(granularity);
		boolean status = userService.updateUser(user, null);
		if (status == false) {
			return ResultUtil.errorWithMsg("update user error");
		}
		return ResultUtil.success("update user success");
	}

	/**
	 * 获取当前用户的算法选择和粒度选择，显示到页面
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getAlgorithmAndGranularity")
	public Object getAlgorithmAndgranularity(HttpServletRequest request) {
		User user = userService.selectCurrentUser(request);
		return ResultUtil.success(user);
	}
}
