package org.berlin.crawl.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.berlin.crawl.bean.BotSeed;
import org.berlin.crawl.dao.BotCrawlerDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.struts.ActionSupport;

public class ListHostsAction extends ActionSupport {

	private final static String SUCCESS = "success";

	private static final Logger logger = LoggerFactory.getLogger(ListHostsAction.class);

	public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		final HttpSession httpSession = request.getSession();
		final ApplicationContext ctx = getWebApplicationContext();
		final BotCrawlerDAO dao = new BotCrawlerDAO();
		final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
		Session session = sf.openSession();
		// Now print the number of links //
		final List<Long> ii = dao.countLinks(session);
		logger.warn("Count of Links : " + ii);

		final ListHostsForm f = (ListHostsForm) form;

		final List<BotSeed> oldseeds = dao.findSeedRequests(session);
		final Set<String> old = new HashSet<String>();
		for (final BotSeed o : oldseeds) {
			old.add(o.getHost().toLowerCase().trim());
		} // End of the for //
			// Also print top hosts //
		final List<String> mainList = new ArrayList<String>();
		final List<Object[]> hosts = dao.findTopHosts(session);
		Collections.reverse(hosts);
		for (final Object[] oo : hosts) {
			final String hst = String.valueOf(oo[0]).trim().toLowerCase();
			if (old.contains(hst)) {
				continue;
			}
			final Long l = (Long) oo[1];
			if (l > 2) {
				mainList.add(hst);
			}
		} // End of for //

		final String[] hostsArr = mainList.toArray(new String[]{});
		f.setHosts(hostsArr);
		final String[] selected = f.getSelectedHosts();

		if (selected != null) {
			if (selected.length != 0) {
				// Loop through and add the seeds
				for (final String hostForSeed : selected) {
					final BotSeed seed = new BotSeed();
					seed.setEnabled("Y");
					seed.setHost(hostForSeed);
					seed.setPath("/");
					seed.setScheme("http");
					try {
						dao.createSeed(session, seed);
					} catch (final Throwable tt) {
						tt.printStackTrace();
					}
					System.out.println("Creating : " + seed);
				}
			} else {
				f.setSelectedHosts(old.toArray(new String[]{}));
			} // End of the if else //
		} else {
			f.setSelectedHosts(old.toArray(new String[]{}));
		} // End of the if //

		if (session != null) {
			// May not need to close the session
			session.close();
		} // End of the if //
		return mapping.findForward(SUCCESS);
	} // End of the method //

} // End of the class //
