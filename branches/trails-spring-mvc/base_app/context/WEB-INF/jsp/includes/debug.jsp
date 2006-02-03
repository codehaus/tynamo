<!-- 
     Debugging, every request, session, context parameter and attribute 
     is logged to the standard output.
-->

<%
   out.println("[Request attributes: ]");
   out.println("<br>");
   java.util.Enumeration attributeNames = request.getAttributeNames();
   
   String name = null;
   
   while(attributeNames.hasMoreElements()) {
     name = (String) attributeNames.nextElement();  
     out.newLine();
     out.println(name + " - " + request.getAttribute(name));
     out.println("<br>");
   }
   out.println("<br>");
   out.println("===========================================================");
   out.println("<br>");
   out.println("[Request parameters: ]");
   
   java.util.Enumeration paramNames = request.getParameterNames();
   
   while(paramNames.hasMoreElements()) {
     name = (String) paramNames.nextElement();  
     out.newLine();
     out.println(name + " - " + request.getParameter(name));
     out.println("<br>");
   }
   
   out.println("===========================================================");
   out.println("<br>");
   out.flush();
%>