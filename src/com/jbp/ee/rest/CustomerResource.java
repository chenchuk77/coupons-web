package com.jbp.ee.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.jbp.beans.Coupon;
import com.jbp.ee.utils.ClientDTO;
import com.jbp.exceptions.CouponNotAvailableException;
import com.jbp.exceptions.CouponUpdateException;
import com.jbp.facade.CustomerFacade;
import com.jbp.main.ClientType;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

// CustomerResource extends Client which encapsulates common
// operations needed by all JAX-RS resources.
// the method
// assuming the filter verified the existence of session + facade
// no need to check every method.

@Path("/customer")
public class CustomerResource extends Client {
	// provides details about the client
	@GET
	@Path("/whoami")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientDTO whoami(@Context HttpServletRequest request){
		return super.whoami(request);
	}

	// login as a customer, ClientDTO returns
	@GET
	@Path("/login/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientDTO login(@Context HttpServletRequest request,
							@PathParam("username") String username,
							@PathParam("password") String password){

		Utils.logMessage(this, Severity.INFO, "trying customer login : "+username+"/"+password);
		return super.login(username, password,ClientType.CUSTOMER, request);
	}

	// logout and invalidate session, ClientDTO returns
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientDTO logout(@Context HttpServletRequest request){
		return super.logout(request);
	}

	// purchase a new coupon, Coupon returns
	@Path("/purchaseCoupon")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon purchaseCoupon(@Context HttpServletRequest request, Coupon coupon)
			throws CouponNotAvailableException, CouponUpdateException{
		CustomerFacade cf=(CustomerFacade)request.getSession(false).getAttribute("FACADE");
		cf.purchaseCoupon(coupon);
		return coupon;
	}

	// list of all my coupons
	@Path("/getAllPurchasedCoupons")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllPurchasedCoupons(@Context HttpServletRequest request){
		CustomerFacade cf = (CustomerFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllPurchasedCoupons();
		return couponCollection.toArray(new Coupon[0]);
	}

	// list by type. using super.validateCouponType to handle invalid input
	@Path("/getAllPurchasedCoupons/type/{type}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllPurchasedCouponsByType(@Context HttpServletRequest request,
												 @PathParam("type") String type){
		CustomerFacade cf = (CustomerFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllPurchasedCouponsByType(validateCouponType(type));
				return couponCollection.toArray(new Coupon[0]);
	}

	// list by max price. using super.validatePrice to handle invalid input
	@Path("/getAllPurchasedCoupons/price/{price}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllPurchasedCouponsByPrice(@Context HttpServletRequest request,
												  @PathParam("price") String price){
		CustomerFacade cf = (CustomerFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllPurchasedCouponsByPrice(validatedPrice(price));
		return couponCollection.toArray(new Coupon[0]);
	}

	// list of all my coupons
	@Path("/getAllAvailableCoupons")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllAvailableCoupons(@Context HttpServletRequest request){
		CustomerFacade cf = (CustomerFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllAvailableCoupons();
		return couponCollection.toArray(new Coupon[0]);
	}

	// list by type. using super.validateCouponType to handle invalid input
	@Path("/getAllAvailableCoupons/type/{type}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllAvailableCouponsByType(@Context HttpServletRequest request,
												 @PathParam("type") String type){
		CustomerFacade cf = (CustomerFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllAvailableCouponsByType(validateCouponType(type));
				return couponCollection.toArray(new Coupon[0]);
	}

	// list by max price. using super.validatePrice to handle invalid input
	@Path("/getAllAvailableCoupons/price/{price}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllAvailableCouponsByPrice(@Context HttpServletRequest request,
										   		  @PathParam("price") String price){
		CustomerFacade cf = (CustomerFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllAvailableCouponsByPrice(validatedPrice(price));
		return couponCollection.toArray(new Coupon[0]);
	}
}
