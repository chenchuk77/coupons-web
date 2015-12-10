package com.jbp.ee.rest;


import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

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

import com.jbp.beans.Company;
import com.jbp.beans.Coupon;
import com.jbp.beans.Customer;
import com.jbp.ee.utils.ClientDTO;
import com.jbp.exceptions.CompanyCreationException;
import com.jbp.exceptions.CompanyNotFoundException;
import com.jbp.exceptions.CompanyRemovalException;
import com.jbp.exceptions.CompanyUpdateException;
import com.jbp.exceptions.CouponCreationException;
import com.jbp.exceptions.CouponNotAvailableException;
import com.jbp.exceptions.CouponRemovalException;
import com.jbp.exceptions.CouponUpdateException;
import com.jbp.exceptions.CustomerCreationException;
import com.jbp.exceptions.CustomerNotFoundException;
import com.jbp.exceptions.CustomerRemovalException;
import com.jbp.exceptions.CustomerUpdateException;
import com.jbp.facade.AdminFacade;
import com.jbp.facade.CompanyFacade;
import com.jbp.facade.CustomerFacade;
import com.jbp.main.ClientType;
import com.jbp.main.CouponSystem;
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;


@Path("/admin")
public class AdminResource extends Client {
	// provides details about the client
	@GET
	@Path("/whoami")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientDTO whoami(@Context HttpServletRequest request){
		return super.whoami(request);
	}

	// login as a admin, ClientDTO returns
	@GET
	@Path("/login/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientDTO login(@Context HttpServletRequest request,
							@PathParam("username") String username,
							@PathParam("password") String password){

		Utils.logMessage(this, Severity.INFO, "trying admin login : "+username+"/"+password);
		return super.login(username, password,ClientType.ADMIN, request);
	}

	// logout and invalidate session, ClientDTO returns
	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public ClientDTO logout(@Context HttpServletRequest request){
		return super.logout(request);
	}

	@POST
	@Path("/createCompany")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Company createCompany(@Context HttpServletRequest request, Company company)
				throws CompanyCreationException {
		AdminFacade af=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		af.createCompany(company);
		return company;
	}

	@DELETE
	@Path("/removeCompany")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Company removeCompany(@Context HttpServletRequest request, Company company)
				throws CompanyRemovalException {
		AdminFacade af=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		af.removeCompany(company);
		return company;
	}

	@PUT
	@Path("/updateCompany")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Company updateCompany(@Context HttpServletRequest request, Company company)
				throws CompanyUpdateException {
		AdminFacade af=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		af.updateCompany(company);
		return company;
	}

	@Path("/getCompany/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Company getcCompany(@Context HttpServletRequest request, @PathParam("id") long id)
			throws CompanyNotFoundException{
		AdminFacade af = (AdminFacade) request.getSession(false).getAttribute("FACADE");
		return af.getCompanyByID(id);
	}

	@Path("/getAllCompanies")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Company[] getAllCompanies(@Context HttpServletRequest request){
		AdminFacade af = (AdminFacade) request.getSession(false).getAttribute("FACADE");
		Collection<Company> companyCollection = af.getAllCompanies();
		return companyCollection.toArray(new Company[0]);
	}

	@POST
	@Path("/createCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Customer createCustomer(@Context HttpServletRequest request, Customer customer)
				throws CustomerCreationException {
		AdminFacade af=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		af.createCustomer(customer);
		return customer;
	}

	@DELETE
	@Path("/removeCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Customer removeCustomer(@Context HttpServletRequest request, Customer customer)
				throws CustomerRemovalException {
		AdminFacade af=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		af.removeCustomer(customer);
		return customer;

	}

	@PUT
	@Path("/updateCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Customer updateCustomer(@Context HttpServletRequest request, Customer customer)
				throws CustomerUpdateException {
		AdminFacade af=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		af.updateCustomer(customer);
		return customer;
	}

	@Path("/getCustomer/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomer(@Context HttpServletRequest request, @PathParam("id") long id)
		//	throws CustomerNotFoundException
	{
		//System.out.println("creating admin facade");
		AdminFacade af=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		//System.out.println("initializing customer object");
		Customer customer=null;
		try {
			Utils.logMessage(this, Severity.INFO,"trying to bring customer from DB");
			customer = af.getCustomer(id);
			customer.getCoupons();
			System.out.println("the customer is : " + customer);
		} catch (CustomerNotFoundException | NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return customer;
		}
		return customer;

	}

	@Path("/getAllCustomers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Customer[] getAllCustomers(@Context HttpServletRequest request){
		AdminFacade af;

		HttpSession session =request.getSession(false);
		af = (AdminFacade) session.getAttribute("FACADE");
		//=(AdminFacade)request.getSession(false).getAttribute("FACADE");
		Collection<Customer> customerCollection = af.getAllCustomers();
		return customerCollection.toArray(new Customer[0]);
	}

	@Path("/getConfig")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> getConfig(@Context HttpServletRequest request){

		return Utils.getSystemParameters();
	}
}
