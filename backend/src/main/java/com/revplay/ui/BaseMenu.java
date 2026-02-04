package com.revplay.ui;

import com.revplay.app.SessionManager;

public abstract class BaseMenu {
  protected final SessionManager session;

  protected BaseMenu() {
    this.session = SessionManager.getInstance();
  }

  public abstract void display();
}
