package com.revplay.model;

import java.sql.Timestamp;

public abstract class BaseModel {
  protected Timestamp createdAt;
  protected Timestamp updatedAt;

  protected BaseModel() {
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }
}
