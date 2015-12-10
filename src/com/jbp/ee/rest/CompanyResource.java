package com.jbp.ee.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.jbp.beans.Coupon;
import com.jbp.ee.utils.ClientDTO;
import com.jbp.exceptions.CouponCreationException;
import com.jbp.exceptions.CouponNotAvailableException;
import com.jbp.exceptions.CouponRemovalException;
import com.jbp.exceptions.CouponUpdateException;
import com.jbp.facade.CompanyFacade;
import com.jbp.facade.CustomerFacade;
import com.jbp.main.ClientType;
import com.jbp.main.CouponSystem;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;


@Path("/company")
public class CompanyResource extends Client {
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
		return super.login(username, password,ClientType.COMPANY, request);
	}

	// logout and invalidate session, ClientDTO returns
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientDTO logout(@Context HttpServletRequest request){
		return super.logout(request);
	}

	@POST
	@Path("/createCoupon")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Coupon createCoupon(@Context HttpServletRequest request, Coupon coupon)
				throws CouponCreationException {
		CompanyFacade cf=(CompanyFacade)request.getSession(false).getAttribute("FACADE");
		// getting the companyId from facade, to be added
		// to coupon upon creation.
		long id = cf.getCompany().getId();
		coupon.setCompanyId(id);
		cf.createCoupon(coupon);
		return coupon;
	}

	@DELETE
	@Path("/removeCoupon")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Coupon removeCoupon(@Context HttpServletRequest request, Coupon coupon)
				throws CouponRemovalException {
		CompanyFacade cf=(CompanyFacade)request.getSession(false).getAttribute("FACADE");
		cf.removeCoupon(coupon);
		return coupon;

	}

	@PUT
	@Path("/updateCoupon")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Coupon updateCoupon(@Context HttpServletRequest request, Coupon coupon)
				throws CouponUpdateException {
		CompanyFacade cf=(CompanyFacade)request.getSession(false).getAttribute("FACADE");
		cf.updateCoupon(coupon);
		return coupon;
	}

	@Path("/getCoupon/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon getCoupon(@Context HttpServletRequest request, @PathParam("id") long id){
		CompanyFacade cf = (CompanyFacade) request.getSession(false).getAttribute("FACADE");
		return cf.getCoupon(id);
	}

	@Path("/getAllCoupons")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllCoupons(@Context HttpServletRequest request){
		CompanyFacade cf = (CompanyFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllCoupons();
		return couponCollection.toArray(new Coupon[0]);
	}

	@Path("/getAllCoupons/type/{type}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Coupon[] getAllCouponsByType(@Context HttpServletRequest request, @PathParam("type") String type){
		CompanyFacade cf = (CompanyFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Coupon> couponCollection = cf.getAllCouponsByType(validateCouponType(type));
		return couponCollection.toArray(new Coupon[0]);
	}
}
