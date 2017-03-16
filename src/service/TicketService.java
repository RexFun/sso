package service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Service;

@Service("ticketService")
public class TicketService 
{
	private static ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();// 记录所有登录凭证
	
	/**
	 * 生成ticket
	 * @return ticket String
	 */
	public static String genTicket(String account)
	{
		String ticket = UUID.randomUUID().toString().toUpperCase();//46CAA926-307D-4D44-BEE7-B1438E2F840F
		map.put(ticket, account);
		return ticket;
	}
	
	/**
	 * 根据ticket返回account
	 * @param ticket String
	 * @return account String
	 */
	public static String getAccountByTicket(String ticket)
	{
		return map.get(ticket);
	}
}
