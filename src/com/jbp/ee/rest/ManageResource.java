package com.jbp.ee.rest;


import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.jbp.main.CouponSystem;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;


@Path("/manage")
public class ManageResource {

	public static final String CONFIG_PATH = "/WEB-INF/cs.properties";

//	@PostConstruct
//	public void init(@Context ServletContext servletContext){
//
//		// invoked by the web container, load a local file from /WEB-INF
//		// and set a parameter map of properties. the map is passed
//		// to the Utils.sysParams to be parsed by CouponSystem singleton during init
//
//		HashMap<String, String> sysParams = new HashMap<String, String>();
//		Utils.logMessage(this, Severity.DEBUG, "@PostConstruct init() invoked, file="+CONFIG_PATH);
//
//		Properties properties = new Properties();
//		try {
//
//			// the servlet's way to get to /WEB-INF/
//			properties.load(servletContext.getResourceAsStream(CONFIG_PATH));
//
//
//			//properties.load(new FileReader(CONFIG_PATH));
//			for(String propName : properties.stringPropertyNames()){
//				sysParams.put(propName, properties.getProperty(propName));
//			}
//			Utils.logMessage(this, Severity.DEBUG, "properties from file loaded to a hashmap");
//		} catch (IOException e) {
//			Utils.logMessage(this, Severity.ERROR, "cannot load properties file ! exiting.");
//			e.printStackTrace();
//			//System.exit(0);
//		}
//		// setting the Utils.sysParams
//		Utils.setConfigMap(sysParams);
//		Utils.logMessage(this, Severity.INFO, "trigerring CouponSystem singleton");
//		CouponSystem cs = CouponSystem.getInstance();
//		Utils.logMessage(this, Severity.INFO, "init() done.");
//
//	}


	// this method requesting the conn pool to refresh the connections.
	// the url ../manage/resetPool can bypass the filter without login.
	// refresh needed because MySQL timeouts.
	//
	@Path("/resetPool")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String resetPool(@Context HttpServletRequest request){
		try {
			Utils.logMessage(this, Severity.DEBUG, "GET : /manage/resetPool called.");
			CouponSystem.getInstance().getConnPool().resetPool();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		HttpSession session=request.getSession(false);
		if(session != null){
			session.invalidate();
			Utils.logMessage(this, Severity.DEBUG, "session invalidated.");
		} else {
			Utils.logMessage(this, Severity.DEBUG, "no session exists.");
		}
		return "conn pool refreshed.";
	}
}
