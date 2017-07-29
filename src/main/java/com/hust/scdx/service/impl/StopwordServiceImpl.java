package com.hust.scdx.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hust.scdx.dao.StopwordDao;
import com.hust.scdx.model.Stopword;
import com.hust.scdx.model.params.StopwordQueryCondition;
import com.hust.scdx.service.StopwordService;
import com.hust.scdx.service.UserService;

@Service
@Transactional
public class StopwordServiceImpl implements StopwordService {

	private static final Logger logger = LoggerFactory.getLogger(StopwordService.class);

	@Autowired
	private StopwordDao stopwordDao;
	@Autowired
	private UserService userService;

	@Override
	public List<Stopword> selectStopwordInforByWord(String word, int start, int limit) {
		// TODO Auto-generated method stub
		StopwordQueryCondition condition = new StopwordQueryCondition();
		condition.setWord(word);
		condition.setStart(start);
		condition.setLimit(limit);
		List<Stopword> list = stopwordDao.selectByExample(condition);
		if (list.isEmpty()) {
			logger.info("word is not exist");
			return list;
		}
		return list;
	}

	@Override
	public List<Stopword> selectAllStopwordInfor(int start, int limit) {
		// TODO Auto-generated method stub
		List<Stopword> list = stopwordDao.selectAllStopword(start, limit);

		return list;
	}

	@Override
	public long selectCount() {
		// TODO Auto-generated method stub
		return stopwordDao.selectCountOfStopword();
	}

	@Override
	public boolean insertStopword(Stopword stopword) {
		int num = stopwordDao.insert(stopword);
		if (0 == num) {
			logger.info("this word is existed");
			return false;
		}
		return true;
	}

	@Override
	public boolean insertStopwords(List<Stopword> list) {
		if (null == list || 0 == list.size()) {
			return false;
		}
		int num = stopwordDao.insertBatch(list);
		if (0 == num) {
			logger.info("insertBatch error!");
			return false;
		}
		List<String> stopwords = new ArrayList<String>();
		for (Stopword stopword : list) {
			stopwords.add(stopword.getWord());
		}
		SegmentServiceImpl.addStopwords(stopwords);
		return true;
	}

	@Override
	public boolean delStopwordById(Integer id) {
		int num = stopwordDao.deleteById(id);
		if (0 == num) {
			logger.info("delete stopword by id error");
			return false;
		}
		return true;
	}

	@Override
	public String getCurrentUser(HttpServletRequest request) {
		return userService.selectCurrentUser(request).getUserName();
	}

	@Override
	public long selectCountWord(String word) {
		StopwordQueryCondition condition = new StopwordQueryCondition();
		condition.setWord(word);
		condition.setLimit(0);
		condition.setStart(0);
		return stopwordDao.selectCountOfStopword(condition);
	}

}
