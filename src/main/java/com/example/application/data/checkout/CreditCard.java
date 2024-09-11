package com.example.application.data.checkout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreditCard {
	@NotBlank
    String cardHolder;
	@NotBlank
    String cardNumber;
	@NotBlank
    String securityCode;
	@Size(max = 2, message = "{month.invalid}")
	@Pattern(regexp = "^\\d+", message = "{month.invalid}")	    
    String expirationMonth;
	@Size(max = 2, message = "{month.invalid}")
	@Pattern(regexp = "^\\d+", message = "{month.invalid}")	    
    String expirationYear;
	
	public String getCardHolder() {
		return cardHolder;
	}
	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
	public String getExpirationMonth() {
		return expirationMonth;
	}
	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}
	public String getExpirationYear() {
		return expirationYear;
	}
	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}
}
