package com.futoshita.jersey.server.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "error")
public class ErrorRepresentation implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  @XmlAttribute(required = true)
  private String message;
  
  public ErrorRepresentation() {
    
  }
  
  public String getMessage() {
    return message;
  }
  
  public void setMessage(String message) {
    this.message = message;
  }
  
}
