package com.jbp.ee.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
import com.jbp.utils.Severity;
import com.jbp.utils.Utils;

@Provider
public class CSExceptionHandler implements ExceptionMapper<Exception>{
	//public class CSExceptionHandler implements ExceptionMapper<IllegalAccessException>{
	//implements ExceptionMapper<CouponNotAvailableException>{


//	@Override
//	public Response toResponse(CouponNotAvailableException exception) {
//		// JAX-RS used this to handle unique exceptions
//		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
//	}

	@Override
	public Response toResponse(Exception exception) {
		// JAX-RS used this to handle ALL exceptions

		String customMessage = null;

		if (exception instanceof CouponCreationException) {
			customMessage = "Cannot create coupon !";
		} else if (exception instanceof CouponRemovalException) {
			customMessage = "Cannot remove coupon !";
		} else if (exception instanceof CouponUpdateException) {
			customMessage = "Cannot update coupon !";
		} else if (exception instanceof CouponNotAvailableException){
			customMessage = "Coupon not available !";
		} else if (exception instanceof CompanyCreationException) {
			customMessage = "Cannot create company !";
		} else if (exception instanceof CompanyRemovalException) {
			customMessage = "Cannot remove company !";
		} else if (exception instanceof CompanyUpdateException) {
			customMessage = "Cannot update company !";
		} else if (exception instanceof CompanyNotFoundException) {
			customMessage = "Company not found !";
		} else if (exception instanceof CustomerCreationException) {
			customMessage = "Cannot create customer !";
		} else if (exception instanceof CustomerRemovalException) {
			customMessage = "Cannot remove customer !";
		} else if (exception instanceof CustomerUpdateException) {
			customMessage = "Cannot update customer !";
		} else if (exception instanceof CustomerNotFoundException) {
			customMessage = "Customer not found !";
		} else {
			customMessage = "Unknown error !";
		}

		Utils.logMessage(this, Severity.INFO, "exception occured : " + customMessage + " : " + exception.getMessage());
		//exception.printStackTrace();

		return Response.status(Status.BAD_REQUEST).entity(customMessage).build();
	}

}
