<!DOCTYPE html PUBLIC>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<title>Check sign in</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=9">
</head>

<body>

  <ul>
    <li>authorization: ${it.authorization}
    <li>token: ${it.token}
    <li>ident: ${it.ident}
    <li>password: ${it.password}
  </ul>

</body>

</html>