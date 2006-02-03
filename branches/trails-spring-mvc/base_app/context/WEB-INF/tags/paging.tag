<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>

<%@ attribute name="selectedPageNumber" required="true" type="java.lang.Integer" %>
<%@ attribute name="totalNumberOfPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="classDescriptor" required="true" type="java.lang.Object" %>

<%
  int selected = selectedPageNumber.intValue();
  int total = totalNumberOfPages.intValue();
  int pageNumber = 1;
  
  if (selected == 0) {
    selected++;
  }

  while (pageNumber < selected) {
%>     
  
  <a href="<c:url value="/listAllInstances.htm"/>?type=<c:out value="${classDescriptor.type.name}"/>&pageNumber=<%=pageNumber%>">  
    <%=pageNumber%>
  </a>
    
<%   
     pageNumber++;
  }
  
  out.print("["+selected+"]");

  pageNumber = selected + 1;

  while (pageNumber <= total) {
%>
  <a href="<c:url value="/listAllInstances.htm"/>?type=<c:out value="${classDescriptor.type.name}"/>&pageNumber=<%=pageNumber%>">  
    <%=pageNumber%>
  </a>

<%     
    pageNumber++;
  }
%>  
