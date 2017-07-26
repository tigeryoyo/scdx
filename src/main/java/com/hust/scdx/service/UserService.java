package com.hust.scdx.service;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.model.User;

public interface UserService {

	boolean insertUserInfo(User user, String roleName);

	boolean deleteUserInfoById(int userId);

	boolean updateUserInfo(User user, String roleName);

	User selectCurrentUser(HttpServletRequest request);

	boolean login(String userName, String password, HttpServletRequest request);

	void logout(HttpServletRequest request);
}
