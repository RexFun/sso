package service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chok.devwork.BaseDao;
import chok.devwork.BaseService;
import dao.UserDao;
import entity.User;

@Service
public class UserService extends BaseService<User,Long>
{
	@Autowired
	private UserDao userDao;

	@Override
	public BaseDao<User,Long> getEntityDao() 
	{
		return userDao;
	}

	public User getByTcCode(String tcCode)
	{
		return userDao.getByTcCode(tcCode);
	}
	
	public List<User> queryInfo(Map<String, Object> param)
	{
		return userDao.queryInfo(param);
	}
	
	public void updPwd(User po)
	{
		userDao.updPwd(po);
	}
}
