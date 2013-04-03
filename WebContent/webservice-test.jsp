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
<?xml version="1.0" encoding="UTF-8"?>
<pdo:patient_data xmlns:pdo="http://www.i2b2.org/xsd/hive/pdo/1.1/pdo">
 <pdo:patient_set>
  <patient download_date="2012-10-02T12:30:04 00:00" import_date="2012-10-02T12:30:04 00:00" sourcesystem_cd="BRICCS" update_date="2012-10-02T12:30:04 00:00" upload_id="1">
   <patient_id source="BRICCS">demo-835619175</patient_id>
   <param column="vital_status_cd" name="date interpretation code">N</param>
   <param column="birth_date" name="birthdate">1912-10-15T00:00:00.000 01:00</param>
   <param column="age_in_years_num" name="age">99</param>
   <param column="race_cd" name="ethnicity">Unknown</param>
   <param column="sex_cd" name="sex">UNSPECIFIED</param>
   <study_name source="BRICCS">cp1356016280136</study_name>
  </patient>
 </pdo:patient_set>
</pdo:patient_data>
</textarea><br>
</td>
</tr>
<tr>
<td>
<input type="text" id="civiId" value = "700"> 
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
var civiId = $('#civiId').val();

$.ajax({  contentType: "application/xml; charset=utf-8", type: "POST", url: "http://" + window.location.hostname  + "/catissueplusWS/rest/service/pdo", data: {incomingXML: xmlContent, activity_id: civiId}, success: function(msg) {  alert(msg); }, });
}); 
</script>                                        
</body>                                                                 
</html>
