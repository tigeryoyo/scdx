package com.hust.scdx.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.User;
import com.hust.scdx.model.params.UserQueryCondition;

public interface UserService {

	int login(String userName, String password, HttpServletRequest request);

	void logout(HttpServletRequest request);

	boolean insertUser(User user, int roleId);

	boolean insertUser(User user, String userRoleName);
	
	boolean deleteUserById(int currentUserId, int userId);

	boolean updateUser(User user, String userRoleName);

	User selectUserByUserName(String userName);

	User selectCurrentUser(HttpServletRequest request);

	List<String[]> selectAllUser();
	
	long selectCountOfUser(UserQueryCondition uc);
	
	List<String[]> selectUserByCondition(UserQueryCondition uc);


}
