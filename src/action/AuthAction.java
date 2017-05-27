package action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import chok.devwork.BaseController;
import chok.util.EncryptionUtil;
import chok.util.http.MyCookie;
import entity.User;
import listener.SessionListener;
import service.TicketService;
import service.UserService;

@Scope("prototype")
@Controller
@RequestMapping("/auth")
public class AuthAction extends BaseController<User> 
{
	@Autowired
	private UserService service;
	
	public static final String LOGINER = "sso.loginer";

	/**
	 * 用户登录系统的入口
	 */
	@RequestMapping("/login")
	public String login() throws IOException
	{
		response.setHeader("P3P", "CP=CAO PSA OUR");
		response.setHeader("Access-Control-Allow-Origin", "*");
		//response.setHeader("P3P", "CP='CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR'");
		//response.setHeader("P3P", "CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");
		String serviceURL = req.getString("service");
		if(log.isDebugEnabled())
		{
			log.debug(serviceURL);
		}
		MyCookie cookie = new MyCookie(request, response);
		String cookieTicket = cookie.getValue(SessionListener.SSO_TICKET);
		if(cookieTicket != null)// 有cookie存在
		{
			String account;
			account = TicketService.getAccountByTicket(cookieTicket);
			if(account != null)
			{
				// 无需登录，生成ticket给应用去登录
//				if(serviceURL.startsWith(request.getContextPath() + "/password"))
//				{
//					response.sendRedirect(serviceURL);
//				}
//				else
//				{
					serviceURL += ((serviceURL.indexOf("?") != -1) ? "&ticket=" : "?ticket=") + TicketService.getOnceTicket(cookieTicket);
					if(log.isDebugEnabled())
					{
						log.debug("sso-server's new serviceURL -> "+serviceURL);
					}
					response.sendRedirect(serviceURL);
//				}
				return null;
			}
			removeLoginInfo(request, response);// 把相关信息删除
		}
		put("service", serviceURL);
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
				User u = (User)service.query(m).get(0);
				if(!EncryptionUtil.getMD5(req.getString("password")).equals(u.getString("tc_password")))
				{// 验证密码
					result.setSuccess(false);
					result.setMsg("密码不正确");
				}
				else
				{// 验证通过
					// 生成ticket
					String cookieTicket = putLoginInfo(request, response, u.getString("tc_code"), u.getString("tc_name"));
					// 携带ticket返回子系统
//					response.sendRedirect(serviceURL += ((serviceURL.indexOf("?") != -1) ? "&ticket=" : "?ticket=" + ticket));
					serviceURL += ((serviceURL.indexOf("?") != -1) ? "&ticket=" : "?ticket=" + TicketService.getOnceTicket(cookieTicket));
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

	/**
	 * 用户登出系统
	 */
	@RequestMapping("/logout")
	public void logout() throws IOException
	{
		try
		{
			MyCookie cookie = new MyCookie(request, response);
			String cookieTicket = String.valueOf(cookie.getValue(SessionListener.SSO_TICKET));
			if(!cookieTicket.equals("null") && cookieTicket.length() > 0)
			{
				removeLoginInfo(request, response);// 试着删除
			}
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
		}
		String serviceURL = java.net.URLEncoder.encode(req.getString("service"), "UTF-8");
		response.sendRedirect(request.getContextPath() + "/auth/login.action?service=" + String.valueOf(serviceURL));
		return;
	}
	
	/**
	 * 已登录用户修改密码的入口
	 */
	@RequestMapping("/password")
	public String password() throws IOException
	{
		response.setHeader("P3P", "CP=CAO PSA OUR");
		response.setHeader("Access-Control-Allow-Origin", "*");
		//response.setHeader("P3P", "CP='CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR'");
		//response.setHeader("P3P", "CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");
		String serviceURL = req.getString("service");
		if(log.isDebugEnabled())
		{
			log.debug(serviceURL);
		}
		MyCookie cookie = new MyCookie(request, response);
		String cookieTicket = cookie.getValue(SessionListener.SSO_TICKET);
		if(cookieTicket != null)// 有cookie存在
		{
			String _account = TicketService.getAccountByTicket(cookieTicket);
			if(_account != null)
			{
				request.setAttribute("account", _account);
				request.setAttribute("service", serviceURL);
				request.setAttribute("errorMsg", "");
				return "/password.jsp";
			}
		}
		removeLoginInfo(request, response);// 把相关信息删除
		serviceURL = request.getContextPath() + "/password?service=" + java.net.URLEncoder.encode(serviceURL, "UTF-8");// 登录后回来修改页并可以再重定向回原页
		response.sendRedirect(request.getContextPath() + "/login?service=" + String.valueOf(java.net.URLEncoder.encode(serviceURL, "UTF-8")));
		return null;
	}
	@RequestMapping("/password2")
	public void password2() 
	{
		try 
		{
			User po = service.getByTcCode(req.getString("account"));
			if (null==po)
			{
				result.setSuccess(false);
				result.setMsg(req.getString("account")+" 账号不存在");
			}
			else
			{
				if(!EncryptionUtil.getMD5(req.getString("old_password")).equals(po.getString("tc_password")))
				{
					result.setSuccess(false);
					result.setMsg("原密码不正确");
				}
				else
				{
					po.set("tc_password", EncryptionUtil.getMD5(req.getString("new_password")));
					service.updPwd(po);
					Map<Object, Object> data = new HashMap<Object, Object>();
					data.put("loginUrl", "auth/login.action?service="+req.getString("service"));
					result.setData(data);
					result.setSuccess(true);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			result.setSuccess(false);
			result.setMsg(e.getMessage());
		}
		printJson(result);
	}
	

	private String putLoginInfo(HttpServletRequest request, HttpServletResponse response, String account, String name)
	{
		String ticket = String.valueOf(request.getSession().getAttribute(SessionListener.SSO_TICKET));
		TicketService.removeSession(ticket);// 如果有，删除原session带的信息
		MyCookie cookie = new MyCookie(request, response);
		ticket = TicketService.saveSession(account);
		request.getSession().setAttribute(SessionListener.SSO_TICKET, ticket);// 这里主要用在session侦听中，超时退出时用来获取ticket
		cookie.addCookie(SessionListener.SSO_TICKET, ticket, -1, "/", null, false, true);// 更新
		// 使用ticket作为密码进行des加密(账号#姓名)
		cookie.addCookie(SessionListener.SSO_CODE, EncryptionUtil.encodeDes((account+"#"+name), ticket), -1, "/", null, false, true);// 更新
		return ticket;
	}

	private void removeLoginInfo(HttpServletRequest request, HttpServletResponse response)
	{
		MyCookie cookie = new MyCookie(request, response);
		String cookieTicket = String.valueOf(cookie.getValue(SessionListener.SSO_TICKET));
		TicketService.removeSession(cookieTicket);//  如果有，删除原cookie带的信息，此处为cookie存在信息时才调用
		cookie.delCookie(SessionListener.SSO_TICKET);
		cookie.delCookie(SessionListener. SSO_CODE);// 删除账号#姓名
	}

}