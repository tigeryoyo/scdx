package com.hust.scdx.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hust.scdx.model.UserExample;
import com.hust.scdx.model.UserExample.Criteria;
import com.hust.scdx.service.impl.UserServiceImpl;
import com.hust.scdx.dao.mapper.UserMapper;
import com.hust.scdx.model.User;

@Repository
public class UserDao {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserMapper userMapper;

	public int insert(User record) {
		return userMapper.insert(record);
	}

	public int deleteByPrimaryKey(Integer userId) {
		return userMapper.deleteByPrimaryKey(userId);
	}

	public int deleteByUserName(String userName) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameEqualTo(userName);
		return userMapper.deleteByExample(example);
	}

	public int updateByPrimaryKey(User record) {
		return userMapper.updateByPrimaryKey(record);
	}

	public User selectByPrimaryKey(Integer userId) {
		return userMapper.selectByPrimaryKey(userId);
	}

	/**
	 * 通过用户名查找用户
	 * 
	 * @param userName
	 * @return
	 */
	public User selectUserByUserName(String userName) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameEqualTo(userName);
		List<User> users = userMapper.selectByExample(example);
		if (null == users || users.size() == 0) {
			return null;
		}
		return users.get(0);
	}

	/**
	 * 查询所有用户
	 */
	public List<User> selectAllUser() {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdIsNotNull();
		return userMapper.selectByExample(example);
	}

	public User login(String username, String password) {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserNameEqualTo(username);
		criteria.andPasswordEqualTo(password);
		List<User> users = userMapper.selectByExample(example);
		if (null == users || users.size() == 0) {
			logger.info("用户名或密码错误。");
			return null;
		}
		return users.get(0);
	}

}
