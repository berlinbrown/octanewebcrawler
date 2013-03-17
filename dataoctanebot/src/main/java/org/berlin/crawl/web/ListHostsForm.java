package org.berlin.crawl.web;

import org.apache.struts.action.ActionForm;

public class ListHostsForm extends ActionForm {

	private String [] hosts = new String [] {};
	private String [] selectedHosts = null;
	
	/**
	 * @return the hosts
	 */
	public String[] getHosts() {
		return hosts;
	}

	/**
	 * @param hosts the hosts to set
	 */
	public void setHosts(final String[] hosts) {
		this.hosts = hosts;
	}

	/**
	 * @return the selectedHosts
	 */
	public String[] getSelectedHosts() {
		return selectedHosts;
	}

	/**
	 * @param selectedHosts the selectedHosts to set
	 */
	public void setSelectedHosts(final String[] selectedHosts) {
		this.selectedHosts = selectedHosts;
	}
	
}
