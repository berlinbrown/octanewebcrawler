package org.berlin.batch;

import java.util.List;

import org.berlin.batch.bean.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataBotJob {

	private static final Logger logger = LoggerFactory.getLogger(DataBotJob.class);

	public static void main(final String[] args) throws Exception {
		logger.info("Running");
		final ApplicationContext ctx = new ClassPathXmlApplicationContext("/org/berlin/batch/batch-databot-context.xml");
		add(ctx);
		list(ctx);		
		final JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
        final Job job = ctx.getBean(Job.class);               
        jobLauncher.run(job, new JobParametersBuilder()
                .addString("inputResource", "file:./data.zip")
                .addString("targetDirectory", "./import/")
                .addString("targetFile","data.txt")
                .addString("date", "2010-06-27")
                .toJobParameters()
        );		
		logger.info("Done");
	} // End of the method //

	public static void list(final ApplicationContext ctx) {
		// Use the hibernate template class
		Session session = null;		
		Transaction transaction = null;
		try {
			final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
			session = sf.openSession();
			transaction = session.beginTransaction();
			final List list = session.createQuery("from Event").list();
			for (final Object e : list) {
				logger.info(String.valueOf(e));
			}
			// End of work //
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at list", e);
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			if (session != null) {
				// May not need to close the session
				session.close();
			}
		}
	} // End of the method //

	public static void add(final ApplicationContext ctx) {
		// Use the hibernate template class
		Session session = null;
		Transaction transaction = null;
		try {
			final SessionFactory sf = (SessionFactory) ctx.getBean("sessionFactory");
			session = sf.openSession();
			transaction = session.beginTransaction();
			final Event event = new Event();
			event.setMessage("Running application ..");
			session.saveOrUpdate(event);

			// End of work //
			transaction.commit();
			session.flush();
		} catch (final Exception e) {
			logger.error("Error at add", e);
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			if (session != null) {
				// May not need to close the session
				session.close();
			}
		}
	}
} // End of the class //
