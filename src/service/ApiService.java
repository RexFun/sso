package service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chok.devwork.BaseDao;
import chok.devwork.BaseService;
import chok.sso.Api;
import dao.ApiDao;

@Service
public class ApiService extends BaseService<Api,Long>
{
	@Autowired
	private ApiDao apiDao;

	@Override
	public BaseDao<Api,Long> getEntityDao() {
		return apiDao;
	}

	public List<Api> getAppByUserId(Map<String, Object> param) 
	{
		return apiDao.getAppByUserId(param);
	}
	public List<Api> getMenuByUserId(Map<String, Object> param) 
	{
		return apiDao.getMenuByUserId(param);
	}
	public List<Api> getBtnByUserId(Map<String, Object> param) 
	{
		return apiDao.getBtnByUserId(param);
	}
	public List<Api> getActByUserId(Map<String, Object> param) 
	{
		return apiDao.getActByUserId(param);
	}
}
