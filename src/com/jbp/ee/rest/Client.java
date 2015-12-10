package com.jbp.ee.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jbp.beans.Coupon;
import com.jbp.beans.CouponType;
import com.jbp.ee.utils.ClientDTO;
import com.jbp.facade.AdminFacade;
import com.jbp.facade.CompanyFacade;
import com.jbp.facade.CouponClientFacade;
import com.jbp.facade.CustomerFacade;
import com.jbp.main.ClientType;
import com.jbp.main.CouponSystem;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

// this class is a superclass for all clients (admin/copmany/customer)
// it provides a method for all subclasses that can validate user input
// for common method : getByType / getByPrice
// both can throw an exceptions.

public class Client{

	// ClientDTO is @XmlRootElement. needed for JAX-B to produce json response
	// login logic is here, so all JAX-RS resources can call by "super"
	//
	public ClientDTO whoami(HttpServletRequest request){
		CouponClientFacade ccf = (CouponClientFacade) request.getSession(false).getAttribute("FACADE");
		if (ccf instanceof CustomerFacade){
			CustomerFacade cf = (CustomerFacade) ccf;
			return new ClientDTO(ClientType.CUSTOMER,
									cf.getCustomer().getCustName(),
									cf.getCustomer().getPassword());
			}
		if (ccf instanceof CompanyFacade){
			CompanyFacade cf = (CompanyFacade) ccf;
			return new ClientDTO(ClientType.COMPANY,
									cf.getCompany().getCompName(),
									cf.getCompany().getPassword());
			}
		if (ccf instanceof AdminFacade){
			AdminFacade af = (AdminFacade) ccf;
			return new ClientDTO(ClientType.ADMIN,
					"admin", "***pass***");
		}
		Utils.logMessage(this, Severity.ERROR, "error - facade unknown !");
		return null;
	}


	// after successful login, whoami() invoked to build and return a formatted ClientDTO
	public ClientDTO login(String username, String password, ClientType clientType, HttpServletRequest request){
		// login
		Utils.logMessage(this, Severity.INFO, "trying login : "+username+"/"+password+" from Client Class");
		CouponClientFacade ccf;
		CouponSystem cs = CouponSystem.getInstance();

		ccf = cs.login(username, password, clientType);
		if (ccf==null) {
			Utils.logMessage(this, Severity.INFO, "login failed.");
			throw new RuntimeException("login failed.");
		}
		HttpSession session=request.getSession(true);
		Utils.logMessage(this, Severity.INFO, "session:" + session.getId() + (session.isNew() ? " - new.":" - existsing."));
		session.setAttribute("AUTH", true);
		session.setAttribute("FACADE", ccf);
		return whoami(request);
	}

	// generic logout for all client types
	public ClientDTO logout(HttpServletRequest request){
		HttpSession session=request.getSession(true);
		// get whoami before invalidating the session
		ClientDTO client = whoami(request);
		session.invalidate();
		return client;
	}

	// UTILITY METHODS
	//
	//
	//
	// this will return a double if conversion succeeded
	// if not - it will throw an exception
	protected double validatedPrice(String requestedPrice){
		double price;
		try {
			price = Double.parseDouble(requestedPrice);
		} catch (NumberFormatException e) {
			// create a new exception with the desired message
			throw new NumberFormatException("Invalid input : "+requestedPrice+". The price must be a number");
		}
		return price;
	}

	// this will return an enum if conversion succeeded
	// if not - it will throw an exception
	protected CouponType validateCouponType(String requestedType){
		for(CouponType type : CouponType.values()){
			if (type.name().equalsIgnoreCase(requestedType)){
				return type;
			}
		}
		throw new NumberFormatException("Type : " + requestedType + " is undefined !");
	}
}
