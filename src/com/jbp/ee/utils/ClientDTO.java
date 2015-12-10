package com.jbp.ee.utils;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.jbp.main.ClientType;

@XmlRootElement
// this class needed for JAX-B
// to marshal an object to JSON/XML ( + unmarshal )
// probably used by whoami()
//
public class ClientDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private ClientType clientType;
	private String name;
	private String password;

	public ClientDTO(){
		// for jaxb
	}
	public ClientDTO(ClientType clientType, String name, String password){
		this.clientType=clientType;
		this.name=name;
		this.password=password;
	}


	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
