package com.jbp.ee.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.jbp.facade.CustomerFacade;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

//@WebListener
public class WebappListener implements HttpSessionListener,
										ServletContextListener {

	// list to hold session ids of active logged-in users
	private List<String> loggedInClients;

	// initialize
    public WebappListener() {
    	loggedInClients = new ArrayList<String>();
    }

    public List<String> showLoggedInClients() {
    	return this.loggedInClients;
    }

    // tracking session events creation/destroy
    // keep loggedIn clients in a list
    //
    public void sessionCreated(HttpSessionEvent se)  {
    	// get the session that create the event
    	HttpSession session = (HttpSession) se.getSource();
        Utils.logMessage(this, Severity.DEBUG, "session created =  "+ session.getId());
    	// add session_id to list
        this.loggedInClients.add(session.getId());
        Utils.logMessage(this, Severity.DEBUG, "total client sessions = " + this.loggedInClients.size());
    }
    public void sessionDestroyed(HttpSessionEvent se)  {
    	HttpSession session = (HttpSession) se.getSource();
    	Utils.logMessage(this, Severity.DEBUG, "session destroyed ="+ session.getId());
    	// remove session_id from list
        this.loggedInClients.remove(session.getId());
        Utils.logMessage(this, Severity.DEBUG, "total client sessions = " + this.loggedInClients.size());
    }

    // to check initialization of the application
	@Override
	public void contextInitialized(ServletContextEvent sce) {
        Utils.logMessage(this, Severity.DEBUG, "new context initialized. context-dir:"+ sce.getServletContext().getContextPath());
   }
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
        Utils.logMessage(this, Severity.INFO, "context destroyed. "+ sce.getServletContext().getServletContextName() + " / " + sce.getServletContext().getServerInfo());
   }

}
