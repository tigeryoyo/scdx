package com.hust.scdx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import com.hust.scdx.redis.RedisFacade;

@Service
public class RedisService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

	private RedisFacade redis = RedisFacade.getInstance(true);

	public void setObject(String key, Object object, HttpServletRequest request) {
		String sessionId = getsessionId(request);
		if (sessionId == null) {
			return;
		}
		redis.setObject(sessionId + key, object);
		redis.expire(sessionId + key, 3600);
	}

	public Object getObject(String key, HttpServletRequest request) {
		String sessionId = getsessionId(request);
		if (sessionId == null) {
			return null;
		}
		redis.expire(sessionId + key, 3600);
		return redis.getObject(sessionId + key);
	}

	public void setString(String key, String value, HttpServletRequest request) {
		String sessionId = getsessionId(request);
		if (sessionId == null) {
			return;
		}
		redis.setString(sessionId + key, value);
		redis.expire(sessionId + key, 3600);
	}

	public String getString(String key, HttpServletRequest request) {
		String sessionId = getsessionId(request);
		if (sessionId == null) {
			return null;
		}
		redis.expire(sessionId + key, 3600);
		String value = redis.getString(sessionId + key);
		return value;
	}

	public void del(String key, HttpServletRequest request) {
		String sessionId = getsessionId(request);
		if (sessionId == null) {
			return;
		}
		redis.delete(sessionId + key);
	}

	private String getsessionId(HttpServletRequest request) {
		String sessionId = request.getSession().getId();
		if (sessionId == null || sessionId == "") {
			logger.error("获取sessionId错误。");
		}
		return sessionId;
	}

}
