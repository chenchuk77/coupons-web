package com.jbp.ee.main;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.jbp.main.CouponSystem;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

// class to load main sys config and run the Coupon System
public class CouponSystemLoader extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String CONFIG_PATH = "/WEB-INF/cs.properties";


	@Override
	public void init() throws ServletException {
		//
		// invoked by the web container, load a local file from /WEB-INF
		// and set a parameter map of properties. the map is passed
		// to the Utils.sysParams to be parsed by CouponSystem singleton during init

		HashMap<String, String> sysParams = new HashMap<String, String>();
		Utils.logMessage(this, Severity.DEBUG, "init() invoked, file="+CONFIG_PATH);

		Properties properties = new Properties();
		try {

			// the servlet's way to get to /WEB-INF/
			properties.load(getServletContext().getResourceAsStream(CONFIG_PATH));


			//properties.load(new FileReader(CONFIG_PATH));
			for(String propName : properties.stringPropertyNames()){
				sysParams.put(propName, properties.getProperty(propName));
			}
			Utils.logMessage(this, Severity.DEBUG, "properties from file loaded to a hashmap");
		} catch (IOException e) {
			Utils.logMessage(this, Severity.ERROR, "cannot load properties file ! exiting.");
			e.printStackTrace();
			//System.exit(0);
		}
		// setting the Utils.sysParams
		Utils.setConfigMap(sysParams);
		Utils.logMessage(this, Severity.INFO, "trigerring CouponSystem singleton");
		CouponSystem cs = CouponSystem.getInstance();
		Utils.logMessage(this, Severity.INFO, "init() done.");

	}
}