<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
 <body>
 	<html:errors/>
 	
 	Select a host to add to the URL seed set (v1.3):
 	<br />  
 	
 	<html:form action="/ListHosts"> 	 		 	 	 		 
		<logic:iterate name="htmlCheckBoxForm" property="hosts" id="host">
		    <bean:write name="host"/>
		    <html:multibox property="selectedHosts">
				<bean:write name="host"/> 
			</html:multibox>
			<br />
		</logic:iterate>
 	
 	   <br />
	   <html:submit>Submit</html:submit> 				
 	</html:form>
 </body>
</html:html>

