package com.example.application.data.checkout;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//@Entity
public class PersonalDetails {
	
	@NotBlank
	@Size(max = 255, message = "{name.invalid}")
	private String name;
	
	@NotBlank
	@Email
	@Size(max = 255, message = "{email.invalid}")
	@Column(unique = true)
	private String email;
	@NotBlank
	@Size(max = 20, message = "{phone.invalid}")
	@Pattern(regexp = "^(\\+\\d+)?([ -]?\\d+){4,14}$", message = "{phone.invalid}")	
	private String phone;
	private boolean remember;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isRemember() {
		return remember;
	}
	public void setRemember(boolean remember) {
		this.remember = remember;
	}	
}
