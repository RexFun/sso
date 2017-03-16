package action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import chok.devwork.BaseController;
import chok.util.EncryptionUtil;
import entity.SysUser;
import service.SysUserService;
import service.TicketService;

@Scope("prototype")
@Controller
@RequestMapping("/auth")
public class AuthAction extends BaseController<SysUser> 
{
	@Autowired
	private SysUserService service;
	
	public static final String LOGINER = "sso.loginer";
	
	@RequestMapping("/login")
	public String login()
	{
		put("service", req.getString("service"));
		return "/login.jsp";
	}
	
	@RequestMapping("/login2")
	public void login2()
	{
		response.setHeader("P3P", "CP=CAO PSA OUR");
		response.setHeader("Access-Control-Allow-Origin", "*");
		String serviceURL = req.getString("service");
		String account = req.getString("account");
		try
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("login_account", account);
			if(service.getCount(m)<1)
			{// 验证账号
				result.setSuccess(false);
				result.setMsg("账号不存在");
			}
			else
			{
				SysUser u = (SysUser)service.get(m).get(0);
				if(!EncryptionUtil.getMD5(req.getString("password")).equals(u.getString("tc_password")))
				{// 验证密码
					result.setSuccess(false);
					result.setMsg("密码不正确");
				}
				else
				{// 验证通过
					// 生成ticket
					String ticket = TicketService.genTicket(u.getString("tc_code"));
					// 携带ticket返回子系统
					
//					response.sendRedirect(serviceURL += ((serviceURL.indexOf("?") != -1) ? "&ticket=" : "?ticket=" + ticket));
					
					serviceURL += ((serviceURL.indexOf("?") != -1) ? "&ticket=" : "?ticket=" + ticket);
					Map<Object, Object> data = new HashMap<Object, Object>();
					data.put("serviceURL", serviceURL);
					result.setData(data);
					result.setSuccess(true);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			result.setSuccess(false);
			result.setMsg(e.getLocalizedMessage());
		}
		printJson(result);
	}
	
	@RequestMapping("/logout")
	public String logout()
	{
		session.removeAttribute(LOGINER);
		return "/login.jsp";
	}
}