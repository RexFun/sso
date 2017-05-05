package action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import chok.devwork.BaseController;
import chok.sso.Api;
import entity.User;
import service.ApiService;
import service.TicketService;
import service.UserService;

@Scope("prototype")
@Controller
@RequestMapping("/api")
public class ApiAction extends BaseController<Api>
{
	@Autowired
	private ApiService service;
	@Autowired
	private UserService userService;
	
	@RequestMapping("/getLoginer")
	public void getLoginer() 
	{
		String onceTicket = req.getString("ticket");
		System.out.println("onceTicket=>"+onceTicket);
		String account = null;
		// 根据ticket获取用户account
		if(onceTicket.length()>0)
		{
			account = TicketService.getAccountByOnceTicket(onceTicket);
		}
		// 根据用户account获取用户对象
		if(account != null)
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("tc_code", account);
			User u = (User) userService.getInfo(m).get(0);
			printJson(u);
		}
		else
		{
			printJson("");
		}
	}
	
	@RequestMapping("/getAppByUserId")
	public void getAppByUserId() 
	{
		printJson(service.getAppByUserId(req.getParameterValueMap(false, true)));
	}
	@RequestMapping("/getMenuByUserId")
	public void getMenuByUserId() 
	{
		printJson(service.getMenuByUserId(req.getParameterValueMap(false, true)));
	}
	@RequestMapping("/getBtnByUserId")
	public void getBtnByUserId() 
	{
		printJson(service.getBtnByUserId(req.getParameterValueMap(false, true)));
	}
	@RequestMapping("/getActByUserId")
	public void getActByUserId() 
	{
		printJson(service.getActByUserId(req.getParameterValueMap(false, true)));
	}
}