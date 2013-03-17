package org.berlin.crawl.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.berlin.crawl.bean.BotWebMonitorInfo;
import org.berlin.crawl.dao.BotCrawlerDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.struts.ActionSupport;

public class ListMessagesAction extends ActionSupport {
	
	private final static String SUCCESS = "success";

	private static final Logger logger = LoggerFactory.getLogger(ListMessagesAction.class);
	
	public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {		
		response.setContentType("application/json");		
		final HttpSession httpSession = request.getSession();
		final ApplicationContext ctx = getWebApplicationContext();
		final BotCrawlerDAO dao = new  BotCrawlerDAO();
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		Session session = sf.openSession();		
		// Now print the number of links //
		final List<Long> ii = dao.countLinks(session);
		logger.warn("Count of Links : " + ii);
		
		// Take the diff
		final BotWebMonitorInfo infLast = (BotWebMonitorInfo) httpSession.getAttribute("countInfo");				
		final BotWebMonitorInfo inf = new BotWebMonitorInfo();		
		inf.setCount(ii.get(0));
		if (infLast != null) {			
			inf.setDiff(inf.getCount() - infLast.getCount());
		} // End of the if //
		inf.setCountMessage(ii.get(0) + " : " + new Date() + " : " + inf.getDiff() + " (per minute)");
		httpSession.setAttribute("countInfo", inf);
		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //		
		return mapping.findForward(SUCCESS);
	} // End of the method //

} // End of the class //
