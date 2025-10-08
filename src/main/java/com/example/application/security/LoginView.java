package com.example.application.security;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "login")
@AnonymousAllowed
public class LoginView extends FlexLayout {

  public LoginView(){
      LoginOverlay login = new LoginOverlay();
      login.setOpened(true);
      login.setForgotPasswordButtonVisible(false);
      login.setAction("login");

      add(login);
  }
}