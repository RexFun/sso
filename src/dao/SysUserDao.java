package dao;

import org.springframework.stereotype.Repository;

import entity.SysUser;
import chok.devwork.BaseDao;


@Repository("sysUserDao")
public class SysUserDao extends BaseDao<SysUser,Long>
{
	@Override
	public Class getEntityClass()
	{
		return SysUser.class;
	}

	public void updPwd(SysUser po)
	{
		this.getSqlSession().update(getStatementName("updPwd"), po);
	}
}
