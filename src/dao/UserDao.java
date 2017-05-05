package dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import entity.User;
import chok.devwork.BaseDao;


@Repository
public class UserDao extends BaseDao<User,Long>
{
	@Override
	public Class<User> getEntityClass()
	{
		return User.class;
	}

	public User getByTcCode(String tcCode)
	{
		return this.getSqlSession().selectOne(getStatementName("getByTcCode"), tcCode);
	}
	
	public List<User> getInfo(Map<String, Object> param) 
	{
		return this.getSqlSession().selectList(getStatementName("getInfo"), param);
	}

	public void updPwd(User po)
	{
		this.getSqlSession().update(getStatementName("updPwd"), po);
	}
}
