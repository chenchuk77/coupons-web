angular.module('myApp', [])
//================================ ROOT SCOPE =================================================
.run(function($rootScope) {

	// globel method for object -> string
	$rootScope.toString = function(obj){
		var s = "{"
		for(key in obj){
		    s += key + ":" + obj[key] + ", ";
		}
		s += "}";
		return s;
	}
})

//================================ SERVICE ====================================================
// service is used to share properties between controllers
.service('clientService', function() {

	// params will be initialized by loginCtrl
	var restURL = "";
	var isloggedIn = false;
	var client = null;
	var loginFailed = false;

})

//================================ LOGIN CTRL ====================================================
.controller('loginCtrl', function($http, $scope, $rootScope, clientService) {

	// HTML can decide before showing login-div
	$scope.isLoggedIn = function (){
		return clientService.isloggedIn;
	}

	$scope.loginFailed = function (){
		return clientService.loginFailed;
	}

	// called from HTML ng-init directive
	$scope.init = function(){
		// init shared vars via service
		clientService.restURL = "rest/";
		clientService.isloggedIn = false;
		clientService.loginFailed = false;
		clientService.client = {name:"", password:"", clientType:""};
		console.log("loginCtrl.init() called, isloggedIn = " + clientService.isloggedIn );
	}

	// reset the connection pool
	$scope.resetConnectionPool = function(){
		console.log("requesting to reset connection pool");
		var url = clientService.restURL+"manage/resetPool";
		console.log("sending HTTP/GET : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("HTTP/200 received");
				$scope.poolMessage = response.data;
			}
	)};



	$scope.login = function(name, password, clientType){
		//var loginURL = login/
		console.log("loginCTRL.login() called. with : " +name+","+password+","+clientType);
		switch (clientType){
			case "CUSTOMER": loginURL = "customer/"; break;
			case "COMPANY": loginURL = "company/"; break;
			case "ADMIN": loginURL = "admin/"; break;}
			console.log("clientService.restURL : " + clientService.restURL);
			console.log("loginURL : " + loginURL);
		var url = clientService.restURL+loginURL+"login/"+name+"/"+password;
		console.log("sending HTTP/GET : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("HTTP/200 received, successfull login for : " +name+","+password+","+clientType);
				clientService.isloggedIn = true;
				if (clientService.loginFailed == true){
					clientService.loginFailed = false;
				}
				// get the JSON into a customer object
				clientService.client=response.data;
				console.log("clientService.isloggedIn = " + clientService.isloggedIn);
				console.log("clientService.client = name-"+clientService.client.name+" password-"+clientService.client.password+" clientType-"+clientService.client.clientType);
		}, function(error) {
			clientService.loginFailed = true;
			console.log("login failed  = " + clientService.loginFailed);
			alert("failed");
		});
	}
})

//================================ CUSTOMER CTRL =================================================
.controller('customerCtrl', function($http, $scope, $rootScope, clientService) {

	// HTML can decide before showing Customer div
	$scope.isCustomer = function(){
		return clientService.isloggedIn == true && clientService.client.clientType == "CUSTOMER"
	}

	// used to get the current customer to display its field in the HTML
	$scope.getCustomer = function (){
		return clientService.client;
	}

	// called from HTML ng-init directive
	$scope.init = function(){
		$scope.couponType = "ALL";
		$scope.coupon = null;
		$scope.maxPrice = 0;
		// data display mode (Purchased/Available)
		$scope.displayMode = null;
		// for highlight selected row
		$scope.selectedRow = null;
	}

	// HTML can decide if to show the 'purchase' button
	$scope.isPurchaseButtonEnabled = function(){
		// if the display-mode is the 'available coupons' this returns true
		return $scope.displayMode == "Available";
	}

	// set the $scope.coupon after selection, index is for highlighting
	$scope.setClickedRow = function(index, coupon){
	     $scope.coupon = coupon;
	     $scope.selectedRow = index;
	  }

	// show purchased coupons ( all / by type / by max price )
	$scope.getAllPurchasedCoupons = function(couponType) {
		var url = null;
		if (couponType==null || couponType=="" || couponType=="ALL"){
			url = clientService.restURL + "customer/getAllPurchasedCoupons";
		} else if (couponType=="UPTO_PRICE"){
			url = clientService.restURL + "customer/getAllPurchasedCoupons/price/" + $scope.maxPrice;
		} else {
			url = clientService.restURL + "customer/getAllPurchasedCoupons/type/" + couponType;
		}
		console.log("getting purchased coupons from : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("Ok. HTTP/200 - response received");
				$scope.selectedRow = null;
				$scope.coupon = null;
				$scope.myCoupons=response.data;
				$scope.displayMode = "Purchased";
		});
	}

	// show available coupons ( all / by type / by max price )
	$scope.getAllAvailableCoupons = function(couponType) {
		var url = null;
		if (couponType==null || couponType=="" || couponType=="ALL"){
			url = clientService.restURL + "customer/getAllAvailableCoupons";
		} else if (couponType=="UPTO_PRICE"){
			url = clientService.restURL + "customer/getAllAvailableCoupons/price/" + $scope.maxPrice;
		} else {
			url = clientService.restURL + "customer/getAllAvailableCoupons/type/" + couponType;
		}
		console.log("getting available coupons from : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("Ok. HTTP/200 - response received");
				$scope.selectedRow = null;
				$scope.coupon = null;
				$scope.myCoupons=response.data;
				$scope.displayMode = "Available";
		});
	}

	// purchase a selected coupon from table.
	$scope.purchaseCoupon = function(coupon){
		var url = clientService.restURL + "customer/purchaseCoupon";
		$http.post(url, $scope.coupon)
			.then(function (response){
				alert($scope.coupon.title + " : purchased successfuly.");
				console.log("HTTP/200 ? response received");
				$scope.selectedRow = null;
				$scope.coupon = null;
				$scope.getAllPurchasedCoupons("ALL");
				$scope.displayMode = "Purchased";
		});
	}
})

//================================ COMPANY CTRL =================================================
.controller('companyCtrl', function($http, $scope, $rootScope, clientService) {

	// HTML can decide before showing Customer div
	$scope.isCompany = function(){
		return clientService.isloggedIn == true && clientService.client.clientType == "COMPANY"
	}

	// used to get the current company to display its field in the HTML
	$scope.getCompany = function (){
		return clientService.client;
	}

	// called from HTML ng-init directive
	$scope.init = function(){
		$scope.couponType = "ALL";
		$scope.coupon = null;
		$scope.couponId = "";
		// data display mode (Purchased/Available)
		$scope.displayMode = null;
		// for highlight selected row
		$scope.selectedRow = null;
		$scope.operationFailed = false;
	}

	// set the $scope.coupon after selection, index is for highlighting
	$scope.setClickedRow = function(index, coupon){
	     $scope.coupon = coupon;
	     $scope.selectedRow = index;
	}

	// clear the coupon in context. used to clear fields
	$scope.clearCoupon = function(){
	     $scope.coupon = null;
	     $scope.selectedRow = null;
	     $scope.couponId = "";
  	}

	// use to show client a message if operation failed
	$scope.isOperationFailed = function(){
		return $scope.operationFailed;
	}

	// show coupons ( all / by type / by max price )
	$scope.getCoupons = function(couponType) {
		var url = null;
		if (couponType==null || couponType=="" || couponType=="ALL"){
			url = clientService.restURL + "company/getAllCoupons";
		} else if (couponType=="BY_ID"){
			url = clientService.restURL + "company/getCoupon/" + $scope.couponId;
		} else {
			url = clientService.restURL + "company/getAllCoupons/type/" + couponType;
		}
		console.log("getting coupons from : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("Ok. HTTP/200 - response received");
				$scope.operationFailed = false;
				$scope.selectedRow = null;
				$scope.coupon = null;

				// using angular.isArray() because myScope must be an array to fit into table
				// if getting single couponById - must be wrapped inside an array of 1 element
				if (angular.isArray(response.data)){
					console.log("got an array");
					$scope.myCoupons=response.data;
				}
				else {
					console.log("got single coupon, clearing the old myCoupons array, and get response to myCoupons[0]");
					$scope.myCoupons = [];
					$scope.myCoupons[0]=response.data;
				}
				console.log("response.data.title = "+response.data.title);
		});
	}

	// purchase a selected coupon from table.
	$scope.createCoupon = function(coupon){
		var url = clientService.restURL + "company/createCoupon";
		$http.post(url, $scope.coupon)
			.then(function (response){
				alert($scope.coupon.title + " : created successfuly.");
				console.log("HTTP/200 response received, coupon created, initializing");
				$scope.selectedRow = null;
				$scope.coupon = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCoupons("ALL");
			}, function(error) {
				$scope.operationFailed = true;
				console.log("creating coupon failed");
		});
	}

	// DELETE is different ( must use explicitly add content-type
	// not working without header !
	$scope.removeCoupon = function(coupon){
		var url = clientService.restURL + "company/removeCoupon";
		$http({
	        method: "DELETE",
	        url: url,
	        data: $scope.coupon,
	        headers: {'Content-Type': 'application/json'}
	    }).then(function (response){
	    	alert($scope.coupon.title + " : created successfuly.");
			console.log("HTTP/200 response received, coupon removed, initializing");
			$scope.operationFailed = false;
			$scope.selectedRow = null;
			$scope.coupon = null;
			// refresh display
			$scope.getCoupons("ALL");
		});
	}

	// purchase a selected coupon from table.
	$scope.updateCoupon = function(coupon){
		var url = clientService.restURL + "company/updateCoupon";
		$http.put(url, $scope.coupon)
			.then(function (response){
		    	alert($scope.coupon.title + " : updated successfuly.");
				console.log("HTTP/200 response received, coupon updated, initializing");
				$scope.selectedRow = null;
				$scope.coupon = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCoupons("ALL");
			}, function(error) {
				$scope.operationFailed = true;
				console.log("updating coupon failed");
		});
	}
})

//================================ ADMIN CTRL =================================================
.controller('adminCtrl', function($http, $scope, $rootScope, clientService) {

	// HTML can decide before showing Customer div
	$scope.isAdmin = function(){
		 return clientService.isloggedIn == true && clientService.client.clientType == "ADMIN"
	}

	// used to get the admin to display its field in the HTML
	$scope.getAdmin = function (){
		return clientService.client;
	}

	// called from HTML ng-init directive
	$scope.init = function(){
		console.log("admin init() called.");
		$scope.companiesMode = "ALL";
		$scope.company = null;
		$scope.companyId = "";
		$scope.customer = null;
		$scope.customerId = "";
		$scope.properties = null;
		// data display mode (Purchased/Available)
		$scope.displayMode = null;
		// for highlight selected row
		$scope.companiesSelectedRow = null;
		$scope.operationFailed = false;
	}

	// set the $scope.company after selection, index is for highlighting
	$scope.setCompanyClickedRow = function(index, company){
	     $scope.company = company;
	     $scope.companiesSelectedRow = index;
	  }

	// clear the company in context. used to clear fields
	$scope.clearCompany = function(){
	     $scope.company = null;
	     $scope.companiesSelectedRow = null;
	     $scope.couponId = "";
	  }

	// use to show client a message if operation failed
	$scope.isOperationFailed = function(){
		return $scope.operationFailed;
	}

	// set the $scope.customer after selection, index is for highlighting
	$scope.setCustomerClickedRow = function(index, customer){
	     $scope.customer = customer;
	     $scope.customersSelectedRow = index;
	  }

	// clear the company in context. used to clear fields
	$scope.clearCustomer = function(){
	     $scope.customer = null;
	     $scope.customersSelectedRow = null;
	     $scope.customerId = "";
	  }

	// show companies ( all / by id )
	$scope.getCompanies = function(companiesMode) {
		//$scope.myCoupons = "";
		var url = null;
		if (companiesMode==null || companiesMode=="" || companiesMode=="ALL"){
			url = clientService.restURL + "admin/getAllCompanies";
		} else if (companiesMode=="BY_ID"){
			url = clientService.restURL + "admin/getCompany/" + $scope.companyId;
		}
		console.log("getting companies from : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("Ok. HTTP/200 - response received");
				$scope.operationFailed = false;
				$scope.companiesSelectedRow = null;
				$scope.company = null;

				// using angular.isArray() because myScope must be an array to fit into table
				// if getting single couponById - must be wrapped inside an array of 1 element
				if (angular.isArray(response.data)){
					console.log("got an array");
					$scope.companies=response.data;
				}
				else {
					console.log("got single company, clearing the old companies array, and get response to companies[0]");
					$scope.companies = [];
					$scope.companies[0]=response.data;
				}
		});
	}

	// create a new company
	$scope.createCompany = function(company){
		var url = clientService.restURL + "admin/createCompany";
		$http.post(url, $scope.company)
			.then(function (response){
		    	alert($scope.company.compName + " : created successfuly.");
				console.log("HTTP/200 response received, company created, initializing");
				$scope.companiesSelectedRow = null;
				$scope.company = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCompanies("ALL");
			}, function(error) {
				$scope.operationFailed = true;
				console.log("create company failed");
		});
	}

	// DELETE is different ( must use explicitly add content-type )
	// remove a company
	$scope.removeCompany = function(company){
		var url = clientService.restURL + "admin/removeCompany";
		$http({
	        method: "DELETE",
	        url: url,
	        data: $scope.company,
	        headers: {'Content-Type': 'application/json'}
	    }).then(function (response){
		    	alert($scope.company.compName + " : removed successfuly.");
				console.log("HTTP/200 response received, company removed, initializing");
				$scope.companiesSelectedRow = null;
				$scope.company = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCompanies("ALL");
		});
	}

	// update company
	$scope.updateCompany = function(company){
		var url = clientService.restURL + "admin/updateCompany";
		$http.put(url, $scope.company)
			.then(function (response){
		    	alert($scope.company.compName + " : updated successfuly.");
		    	console.log("HTTP/200 response received, company updated, initializing");
				$scope.companiesSelectedRow = null;
				$scope.company = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCompanies("ALL");
			}, function(error) {
				$scope.operationFailed = true;
				console.log("updating company failed");
		});
	}

	// show customers ( all / by id )
	$scope.getCustomers = function(customersMode) {
		var url = null;
		if (customersMode==null || customersMode=="" || customersMode=="ALL"){
			url = clientService.restURL + "admin/getAllCustomers";
		} else if (customersMode=="BY_ID"){
			url = clientService.restURL + "admin/getCustomer/" + $scope.customerId;
		}
		console.log("getting customers from : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("Ok. HTTP/200 - response received");
				$scope.operationFailed = false;
				$scope.customersSelectedRow = null;
				$scope.customer = null;
				// using angular.isArray() because myScope must be an array to fit into table
				// if getting single couponById - must be wrapped inside an array of 1 element
				if (angular.isArray(response.data)){
					console.log("got an array");
					$scope.customers=response.data;
				}
				else {
					console.log("got single customer, clearing the old customers array, and get response to customers[0]");
					$scope.customers = [];
					$scope.customers[0]=response.data;
				}
		});
	}

	// create a new customer
	$scope.createCustomer = function(customer){
		var url = clientService.restURL + "admin/createCustomer";
		$http.post(url, $scope.customer)
			.then(function (response){
		    	alert($scope.customer.custName + " : updated successfuly.");
				console.log("HTTP/200. object received : " + $rootScope.toString(response.data));
				$scope.customersSelectedRow = null;
				$scope.customer = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCustomers("ALL");
			}, function(error) {
				$scope.operationFailed = true;
				console.log("create customer failed");
		});
	}

	// DELETE is different ( must use explicitly add content-type )
	// remove a customer
	$scope.removeCustomer = function(customer){
		var url = clientService.restURL + "admin/removeCustomer";
		$http({
	        method: "DELETE",
	        url: url,
	        data: $scope.customer,
	        headers: {'Content-Type': 'application/json'}
	    }).then(function (response){
	    		alert($scope.customer.custName + " : removed successfuly.");
				console.log("HTTP/200. object received : " + $rootScope.toString(response.data));
				$scope.customersSelectedRow = null;
				$scope.customer = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCustomers("ALL");
		});
	}

	// update customer
	$scope.updateCustomer = function(customer){
		var url = clientService.restURL + "admin/updateCustomer";
		$http.put(url, $scope.customer)
			.then(function (response){
	    		alert($scope.customer.custName + " : updated successfuly.");
				console.log("HTTP/200. object received : " + $rootScope.toString(response.data));
				$scope.customersSelectedRow = null;
				$scope.customer = null;
				$scope.operationFailed = false;
				// refresh display
				$scope.getCustomers("ALL");
			}, function(error) {
				$scope.operationFailed = true;
				console.log("updating customer failed");
		});
	}

	//getting system propertis from admin API
	$scope.getSystemProperties = function(){
		if($scope.properties == null){
			var url = clientService.restURL + "admin/getConfig";
			$http.get(url)
			.then(function(response) {
				console.log("HTTP/200. object received : " + $rootScope.toString(response.data));
				$scope.properties = response.data;
			});
		}
	}
})













//================================ LOGOUT CTRL =================================================
.controller('logoutCtrl', function($http, $scope, clientService) {
	$scope.isLoggedIn = function (){
		return clientService.isloggedIn;
	}

	$scope.logout = function(){
		var logoutURL = null;
		console.log("logoutCTRL.logout() called. service clientType is = "+clientService.client.clientType);
		switch (clientService.client.clientType){
			case "CUSTOMER": logoutURL = "customer/"; break;
			case "COMPANY": logoutURL = "company/"; break;
			case "ADMIN": logoutURL = "admin/"; break;}
		console.log("clientService.restURL : " + clientService.restURL);
		console.log("logoutURL : " + logoutURL);
		var url = clientService.restURL+logoutURL + "logout";
		console.log("sending HTTP/GET : " + url);
		$http.get(url)
			.then(function(response) {
				console.log("HTTP/200 received, successfull login for : "+clientService.client.clientType);
				// logout and clear state.
				clientService.isloggedIn = false;
				//clientService.client=response.data;
				clientService.client = {name:"", password:"", clientType:""};
				console.log("clientService.isloggedIn = " + clientService.isloggedIn);
				console.log("logged out. client in context is now : " +clientService.client.name+","+clientService.client.password+","+clientService.client.clientType);
		});
	}
});

