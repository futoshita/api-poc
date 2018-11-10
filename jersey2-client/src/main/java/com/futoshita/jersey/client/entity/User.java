package com.futoshita.jersey.client.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "error")
public class User {

    @XmlAttribute(required = true)
    private Long id;
    @XmlAttribute(required = true)
    private String email;
    @XmlAttribute(required = true)
    private String ident;
    @XmlAttribute(required = false)
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

}
