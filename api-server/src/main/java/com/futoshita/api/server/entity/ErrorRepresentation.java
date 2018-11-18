package com.futoshita.api.server.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "error")
public class ErrorRepresentation implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute(required = true)
    private String message;
    @XmlAttribute(required = false)
    private String messageTemplate;

    public ErrorRepresentation() {

    }

    public ErrorRepresentation(String message) {
        this.message = message;
    }

    public ErrorRepresentation(String message, String messageTemplate) {
        this.message = message;
        this.messageTemplate = messageTemplate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }
}
