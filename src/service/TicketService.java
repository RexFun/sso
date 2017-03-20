package service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import chok.util.EncryptionUtil;

@Service
public class TicketService 
{
	private static ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();// 记录所有登录凭证
	// 记录免登凭证算法密钥，它同时也是密钥的过期时间，一个登录凭证每次只能生成一个免登凭证，新生成的将会踢掉旧的
	private static ConcurrentMap<String, Long> mapOnce = new ConcurrentHashMap<String, Long>();

	/**
	 * 保存用户登录状态，并返回ticket
	 * @param IUser
	 * @return
	 */
	public static String saveSession(String account)
	{
		String ticket = UUID.randomUUID().toString().toUpperCase();//46CAA926-307D-4D44-BEE7-B1438E2F840F
		map.put(ticket, account);// 这里可以考虑放到数据库或者memcache中
		return ticket;
	}
	public static void removeSession(String ticket)
	{
		map.remove(ticket);
		mapOnce.remove(ticket);
	}

	/**
	 * 根据ticket返回登录用户名，没有则返回null
	 * @param ticket
	 * @return
	 */
	public static String getAccountByTicket(String ticket)
	{
		try
		{
			return map.get(ticket);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private static long timeout = 600000;// 有效时间仅为10分钟1000*60*10
	public static String getOnceTicket(String ticket)
	{
		Long time = System.currentTimeMillis();
		Long outtime = time + timeout;
		mapOnce.put(ticket, outtime);
		String time_base64 = EncryptionUtil.encodeBase64(String.valueOf(outtime));
		String onceTicket = EncryptionUtil.encodeDes(ticket, String.valueOf(time)) + time_base64;// ticket是36位的uuid，加密后为144长的字母加数字字符串
		return onceTicket;
	}

	/**
	 * 根据onceTicket返回登录用户名，没有则返回null
	 * @param onceTicket
	 * @return String
	 */
	public static String getAccountByOnceTicket(String onceTicket)
	{
		String account = null;
		try
		{
			if(onceTicket != null && onceTicket.length() > 144)
			{
				long endtime = System.currentTimeMillis();
				int i = onceTicket.length();
				String _time_base64 = onceTicket.substring(144, i);
				String _time_str = EncryptionUtil.decodeBase64(_time_base64);
				long _outtime_key = Long.parseLong(_time_str);
				if(_outtime_key > endtime)//  过期直接忽略
				{
					String _onceTicket = onceTicket.substring(0, 144);
					String _ticket = EncryptionUtil.decodeDes(_onceTicket, String.valueOf((_outtime_key - timeout)));
					if(_ticket != null)
					{
						Long _key = mapOnce.get(_ticket);
						if(_key != null && _key == _outtime_key)// 密钥存在，且相同
						{
							mapOnce.remove(_ticket);// 使用后必须移除
							account = TicketService.getAccountByTicket(_ticket);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
		}
		return account;
	}
}
