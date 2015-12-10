package com.jbp.ee.main;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

public class RestFilter extends HttpServlet implements Filter {
	private static final long serialVersionUID = 1L;

	// the SPA_URL is http://[ip]:port/ctx/
	private static final String SPA_URL = "/";
	// not needed to refer "/index.html", "/" is better
	//private static final String SPA_URL = "/index.html";
	private static final String LOGIN_KEYWORD = "/login";
	private static final String ALLOWED_URL = "/manage/resetPool";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request =(HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse) res;

		Utils.logMessage(this, Severity.DEBUG, "getRequestURI()="+request.getRequestURI());
		if(request.getRequestURI().contains(LOGIN_KEYWORD)){
		Utils.logMessage(this, Severity.DEBUG, "request contains " + LOGIN_KEYWORD +". bypassing filter...");
			// bypass the filter to process the login
			chain.doFilter(req, res);
		} else if (request.getRequestURI().contains(ALLOWED_URL)){
			// bypass the filter to reset the connection pool
			chain.doFilter(req, res);
		} else {
			Utils.logMessage(this, Severity.DEBUG, "no \"" + LOGIN_KEYWORD + "\" in client request. going to filter");
			// need to check if facade exists and user
			// authenticated before continue
			HttpSession session = request.getSession(false);

			if (session !=null){
				if (session.getAttribute("AUTH") != null && session.getAttribute("FACADE") != null){
					Utils.logMessage(this, Severity.DEBUG, "session is authenticated " + session.getId());
					// continue to JAX-RS servlet for REST services
					Utils.logMessage(this, Severity.DEBUG, "filter done. continue to JAX-RS");
					chain.doFilter(request, response);
					// necessary ?
					//return;
				}else{
					Utils.logMessage(this, Severity.INFO, "session NOT authenticated " + session.getId());
					Utils.logMessage(this, Severity.INFO, "client tried : "+ request.getRequestURI() + ", redirecting to " + request.getContextPath() + SPA_URL);
					//response.sendRedirect(request.getContextPath() + "login.html");
					response.sendRedirect(request.getContextPath() + SPA_URL);
				}
			}
			else{
				Utils.logMessage(this, Severity.INFO, "session is null - redirecting to : " + request.getContextPath() + SPA_URL);
				//System.out.println("tried : "+ request.getRequestURI() + ", redirecting to " + request.getContextPath() + SPA_URL);
				//response.sendRedirect(request.getContextPath() + SPA_URL);
				response.sendRedirect(request.getContextPath() + SPA_URL);
			}

		}
		// check session BUT dont create one
		Utils.logMessage(this, Severity.DEBUG, "filter done.");
		// send response from filter itself

	}


	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("RestFilter.init()");

	}



}
