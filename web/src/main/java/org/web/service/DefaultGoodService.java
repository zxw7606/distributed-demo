package org.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web.mapper.GoodMapper;
import org.web.vo.Good;

@Service
public class DefaultGoodService implements GoodService {

	@Autowired
	private GoodMapper goodMapper;

	@Override
	public int deleteByPrimaryKey(Integer id) {

		return goodMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Good record) {
		return insertSelective(record);
	}

	@Override
	public int insertSelective(Good record) {
		return goodMapper.insertSelective(record);
	}

	@Override
	public Good selectByPrimaryKey(Integer id) {
		return goodMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(Good record) {
		return goodMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Good record) {
		return updateByPrimaryKeySelective(record);
	}

}
