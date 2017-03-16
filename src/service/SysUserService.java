package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chok.devwork.BaseDao;
import chok.devwork.BaseService;
import dao.SysUserDao;
import entity.SysUser;

@Service("sysUserService")
public class SysUserService extends BaseService<SysUser,Long>
{
	@Autowired
	private SysUserDao sysUserDao;

	@Override
	public BaseDao<SysUser,Long> getEntityDao() {
		return sysUserDao;
	}
	
	@Override
	public SysUser getById(Long id) 
	{
		SysUser po = sysUserDao.getById(id);
		return po;
	}
}
