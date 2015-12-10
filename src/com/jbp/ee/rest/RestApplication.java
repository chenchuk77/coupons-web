package com.jbp.ee.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.jbp.ee.utils.CSExceptionHandler;

@ApplicationPath("/rest")
public class RestApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return new HashSet<>(
				Arrays.asList(
						new ManageResource(),
						new AdminResource(),
						new CompanyResource(),
						new CustomerResource(),
						new CSExceptionHandler())
						//new CouponSystemLoader())
						//new CouponSystemLoader())
				);
	}

}
