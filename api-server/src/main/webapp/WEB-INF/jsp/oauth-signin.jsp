<!DOCTYPE html PUBLIC>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Sign in</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=9">

<link rel="stylesheet" type="text/css" href="../../resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../../resources/ext-theme-classic/ext-theme-classic-all.css" />
<link rel="stylesheet" type="text/css" href="../../resources/css/api.css" />
<script type="text/javascript" src="../../resources/js/ext-all.js"></script>

<script type="text/javascript">
  Ext.onReady(function() {
    Ext.QuickTips.init();
    var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
    var loginForm = Ext.widget({
      xtype : 'form',
      layout : 'form',
      standardSubmit : true,
      collapsible : false,
      id : 'login_form',
      frame : true,
      title : 'Sign in',
      bodyStyle:'padding:5px 5px 0',
      iconCls : 'icon-lock',
      width : 350,
      fieldDefaults : {
        msgTarget : 'side',
        labelWidth : 75
      },
      defaultType : 'textfield',
      defaults: {
        anchor: '100%'
      },
      items : [{
        xtype:'fieldset',
        defaultType: 'textfield',
        layout: 'anchor',
        defaults: {
          anchor: '100%'
        },
        items :[{
            fieldLabel : 'Username',
            afterLabelTextTpl : required,
            name : 'username',
            allowBlank : false,
            listeners: {
              afterRender: function(field) {
                field.focus(false, 100);
              }
            }
          }, {
            inputType : 'password',
            fieldLabel : 'Password',
            afterLabelTextTpl : required,
            name : 'password',
            allowBlank : false,
            listeners: {
              specialKey: function(field, e) {
                if (field.getValue() != 'null') {
                  if (e.getKey() === e.ENTER) {
                    submitLogin();
                  }
                }
              }
            }
        }]
      }],
      dockedItems: [{
        xtype: 'toolbar',
        flex: 1,
        dock: 'bottom',
        ui: 'footer',
        layout: {
            pack: 'center',
            type: 'hbox'
        },
        items: [{
          xtype: 'button',
          text: 'Forgot password?',
          iconCls: 'icon-question',
          handler : function() {
              forgotPassword();
          }
        }]
      }
      ],
      buttonAlign: 'center',
      buttons : [{
          text : 'Go back',
          iconCls : 'icon-cancel',
          handler : function() {
            window.history.back();
          }
        }, {
          text : 'Sign in',
          iconCls : 'icon-sign-in',
          handler : function() {
            submitLogin();
          }
      }]
    });
    
    function submitLogin() {
      var form = loginForm.getForm();
      form.url = '${it.postSigninUrl}';
      form.method = 'POST';
      if (form.isValid()) {
        form.submit({
            clientValidation : true,
            params : {
              authorize : 'allow',
              oauth_token : '${it.oauthToken}'
            },
            success : function(form, action) {
              Ext.Msg.alert('Success', action.result.msg);
            },
            failure : function(form, action) {
              switch (action.failureType) {
                case Ext.form.Action.CLIENT_INVALID:
                  Ext.Msg.alert('Failure', 'Form fields may not be submitted with invalid values');
                  break;
                case Ext.form.Action.CONNECT_FAILURE:
                  Ext.Msg.alert('Failure', 'Server reported:' + action.response.status + ' ' + action.response.statusText);
                  break;
                case Ext.form.Action.SERVER_INVALID:
                  Ext.Msg.alert('Failure', action.result.msg);
              }
            }
          });
        }
    };
    
    function forgotPassword() {
      window.open('${it.forgotPasswordUrl}','_blank');
      window.history.back();
    };
    
    loginForm.render(Ext.Element.get('form'));
  });
</script>

</head>

<body>

  <div class="login-background">
    <div class="loginForm" id="form"></div>
  </div>

  <input id="oauth_token" name="oauth_token" type="hidden" value="${it.oauthToken}" />

</body>

</html>
