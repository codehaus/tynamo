<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="trails" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
  
  <head>
    <link rel="stylesheet" media="all" title="trails" href="style/style.css" type="text/css">
    <script type="text/javascript" src="js/datePicker.js"></script>
  </head>
  
  <body>
  
   <div id="header">
   </div>
    
   <div id="page">
      
    <div id="menu">
    	<a href="<c:url value="/listAllEntities.htm"></c:url>">Home</a>	 
      <c:forEach var="item" items="${trailsEntities}">
       <br>
      	<c:choose>
         <c:when test="${!item.child}">
     		  	<a href="<c:url value="/listAllInstances.htm"><c:param name="type" value="${item.type.name}"/></c:url>">List <c:out value="${item.pluralDisplayName}" /></a>	 
  	     </c:when>
	      </c:choose>
        </c:forEach>        
    </div>  
      
    <div id="content">
