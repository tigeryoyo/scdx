package com.hust.scdx.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hust.scdx.model.Power;
import com.hust.scdx.model.Power4Set;
import com.hust.scdx.service.PowerService;
import com.hust.scdx.util.ResultUtil;

@Controller
@RequestMapping("/power")
public class PowerController {
	@Autowired
	private PowerService powerService;
	
	private static final Logger logger = LoggerFactory.getLogger(PowerController.class);
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
		List<Power> allPowers = powerService.selectAllPower();
		if (null == allPowers || allPowers.size() == 0) {
			return ResultUtil.errorWithMsg("查询角色权限出现错误。");
		}

		List<Power> powers = powerService.selectPowerByRoleId(roleId);
		HashMap<Integer, Power4Set> p4s = new HashMap<>();
		for (Power power : allPowers) {
			p4s.put(power.getPowerId(), new Power4Set(power));
		}
		if(powers != null){
			for (Power power : powers) {
				try{
					p4s.get(power.getPowerId()).setOwned(true);
				}catch(Exception e){
					logger.info("存在拥有的权限id不在所有权限列表中");
					return ResultUtil.errorWithMsg("查询角色权限出现错误。");
				}
			}
		}
		List<Power4Set> list= new ArrayList<>(p4s.values());
		return ResultUtil.success(list);
	}
	
	@ResponseBody
	@RequestMapping("/changeRolePower")
	public Object changeRolePower(@RequestParam(value = "powerIds", required = false) List<Integer> powerIds,
			@RequestParam(value = "roleId", required = true) int roleId,
			HttpServletRequest request) {
		System.out.println(powerIds);
		if(powerService.resetRolePower(roleId, powerIds))
			return ResultUtil.errorWithMsg("修改角色权限成功！");
		return ResultUtil.errorWithMsg("修改角色权限出现错误。");
	}

}
