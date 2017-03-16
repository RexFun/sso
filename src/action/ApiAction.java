package action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import chok.devwork.BaseController;
import entity.SysUser;
import service.SysUserService;
import service.TicketService;

@Scope("prototype")
@Controller
@RequestMapping("/api")
public class ApiAction extends BaseController<Object>
{
	@Autowired
	private SysUserService service;
	
	@RequestMapping("/getLoginer")
	public void getLoginer() 
	{
		String ticket = req.getString("ticket");
		System.out.println("ticket=>"+ticket);
		// 根据ticket获取用户account
		String account = TicketService.getAccountByTicket(ticket);
		// 根据用户account获取用户对象
		Map<String, String> m = new HashMap<String, String>();
		m.put("tc_code", account);
		SysUser u = (SysUser) service.get(m).get(0);
		printJson(u);
	}
}