package listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import service.TicketService;

public class SessionListener implements HttpSessionListener
{
	public final static String SSO_TICKET = "SSO_TICKET";
	public final static String SSO_CODE = "SSO_CODE";
	public void sessionCreated(HttpSessionEvent event)
	{
		System.out.println("sessionCreated:::" + event.getSession().getId());
	}

	public void sessionDestroyed(HttpSessionEvent event)
	{
		System.out.println("sessionDestroyed:::" + event.getSession().getId());
		try
		{
			HttpSession session = event.getSession();
			String ticket = String.valueOf(session.getAttribute(SessionListener.SSO_TICKET));
			if(!ticket.equals("null") && ticket.length() > 0)
			{
				TicketService.removeSession(ticket);// 应该是超时
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
