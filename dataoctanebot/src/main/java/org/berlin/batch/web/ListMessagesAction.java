package org.berlin.batch.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ListMessagesAction extends Action {

	
	private final static String SUCCESS = "success";

	public ActionForward execute(final ActionMapping mapping, final ActionForm form
			, final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		return mapping.findForward(SUCCESS);
	}

} // End of the class //
