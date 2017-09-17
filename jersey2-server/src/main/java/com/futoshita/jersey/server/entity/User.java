package com.futoshita.jersey.server.entity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

@XmlRootElement
public class User {
  
  @DecimalMin(value = "1")
  private Long id;
  @Email(message = "{user.wrong.email}", regexp = "[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
  private String email;
  @XmlAttribute(required = true)
  @NotNull(message = "{user.wrong.ident}")
  @Length(min = 2, max = 10)
  private String ident;
  @XmlAttribute(required = false)
  @Length(min = 8, max = 15)
  private String password;
  
  public User() {
    
  }
  
  public User(String ident, String password, String email) {
    this.ident = ident;
    this.password = password;
    this.email = email;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getIdent() {
    return ident;
  }
  
  public void setIdent(String ident) {
    this.ident = ident;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }
    
    final User that = (User) o;
    
    if (email != null ? !email.equals(that.email) : that.email != null) {
      return false;
    }
    if (ident != null ? !ident.equals(that.ident) : that.ident != null) {
      return false;
    }
    
    if (password != null ? !password.equals(that.password) : that.password != null) {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode() {
    int result = ident != null ? ident.hashCode() : 0;
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    return result;
  }
  
}
