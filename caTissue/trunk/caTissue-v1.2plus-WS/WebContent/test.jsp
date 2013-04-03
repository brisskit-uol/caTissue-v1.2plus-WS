 <html>                                                                  
 <head>                                                                  
 <script type="text/javascript" src="jquery-1.7.1.min.js"></script>          
 <script type="text/javascript">                                         
   // we will add our javascript code here                                     
 </script>                                                               
 </head>                                                                 
 <body>  
 <table border="1">
<tr>
<td> 

<textarea id="xmlv" cols="100" rows="30">
</textarea><br>
</td>
</tr>
<tr>
<td>
<input type="button" id="myButton" value="Submit" /> 
</td>
</tr>
</table> 


<script>


//$.post("http://localhost:8080/Myi2b2WS2/rest/test/postjsonjson", {"incomingXML":incomingXML}, function (response){alert(response);});
//$.get("http://localhost:8080/Myi2b2WS2/rest/test/getjsonjson", {"incomingXML":incomingXML}, function (response){alert(response);});

//$.post("http://localhost:8080/Myi2b2WS2/rest/test/xmltest", {"incomingXML":incomingXML}, function (response){alert(response);});

//$.ajax({  contentType: "application/xml; charset=utf-8", type: "POST", url: "http://localhost:8080/Myi2b2WS2/rest/test/xmltest2", data: { xml : incomingXML}, success: function(msg) {  alert(msg); }, });
$('#myButton').click(function(){
var xmlContent = $('#xmlv').val();
$.ajax({  contentType: "application/xml; charset=utf-8", type: "POST", url: "http://localhost:8080/catissueplusWS/rest/service/pdo", data: {incomingXML: xmlContent, activity_id: "1004"}, success: function(msg) {  alert(msg); }, });
}); 
</script>                                        
</body>                                                                 
</html>
