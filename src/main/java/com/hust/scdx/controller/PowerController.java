package com.hust.scdx.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Power;
import com.hust.scdx.service.PowerService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/power")
public class PowerController {
	@Autowired
	private PowerService powerService;

	/**
	 * 根据角色id查询角色所有权限 只有开发者（roleId=1）有权使用添加权限，故添加权限的时候自动将该权限添加至开发者权限表中
	 * 
	 * @param roleId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/insertPower")
	public Object insertPower(@RequestParam(value = "powerName", required = true) String powerName,
			@RequestParam(value = "powerUrl", required = true) String powerUrl, HttpServletRequest request) {
		if (!powerService.insertPower(1, powerName, powerUrl)) {
			return ResultUtil.errorWithMsg("添加权限出现错误。");
		}
		return ResultUtil.success("添加权限成功。");
	}

	/**
	 * 根据角色id查询角色所有权限
	 * 
	 * @param roleId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/deletePower")
	public Object deletePower(@RequestParam(value = "powerId", required = true) int powerId,
			HttpServletRequest request) {
		if (!powerService.deletePower(powerId)) {
			return ResultUtil.errorWithMsg("删除权限出现错误。");
		}
		return ResultUtil.successWithoutMsg();
	}

	/**
	 * 根据角色id查询角色所有权限
	 * 
	 * @param roleId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectAllPower")
	public Object selectAllPower(HttpServletRequest request) {
		List<Power> powers = powerService.selectAllPower();
		if (null == powers || powers.size() == 0) {
			return ResultUtil.errorWithMsg("查询所有权限出现错误。");
		}
		return ResultUtil.success(powers);
	}

	/**
	 * 根据角色id查询角色所有权限
	 * 
	 * @param roleId
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/selectPowerByRoleId")
	public Object selectPowerByRoleId(@RequestParam(value = "roleId", required = true) int roleId,
			HttpServletRequest request) {
		List<Power> powers = powerService.selectPowerByRoleId(roleId);
		if (null == powers || powers.size() == 0) {
			return ResultUtil.errorWithMsg("查询角色权限出现错误。");
		}
		return ResultUtil.success(powers);
	}

}
