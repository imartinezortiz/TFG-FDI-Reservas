$(document).ready(function(){
		var user = {};
		var token = $("meta[name='_csrf']").attr("content");
	 	var header = $("meta[name='_csrf_header']").attr("content");
	 	var reqHeaders = [];
	 	reqHeaders[header] = token;
		
//		for(var i in roles){
//			if(roles[i].role == "ROLE_USER"){
//				$("#chkUser").prop("checked","true");
//			}
//			else if(roles[i].role == "ROLE_ADMIN"){
//				$("#chkAdmin").prop("checked","true");
//			}
//			else{
//				$("#chkSecre").prop("checked","true");
//			}
//		}
		
		
});	

function editarUsuario(user, reqHeaders){

	//var inputElements = document.getElementsByClassName('checkbox');
//	for(var i=0; inputElements[i]; ++i){
//	      if(inputElements[i].checked){
//	    	  if ((i == 1) || (i == 3)){
//	           admin = inputElements[i].value;
////	           break;
//	    	  }
//	    	  else if ((i == 5) || (i == 7)){
//	           usuario = inputElements[i].value;
////	           break;
//	    	  }
//	    	  else if ((9 == 1) || (i == 11)){
//	           gestor = inputElements[i].value;
////	           break;
//	    	  }
//	      }
//	}
	
	var usuario = document.getElementById("chkUser").checked.toString();
	var admin = document.getElementById("chkAdmin").checked.toString();
	var gestor = document.getElementById("chkSecre").checked.toString();
	
	$.ajax({
			
			url: baseURL + '/administrar/usuarios/editar/' + idUsuario + '/' + usuario + '/' + admin + '/' + gestor,
			//url: baseURL + 'admin/administrar/usuarios/editar/' + idUsuario ,
			type: 'PUT',
			headers : reqHeaders,
			data: JSON.stringify(user),
			contentType: 'application/json',
			
			success : function(datos) {   
				 window.location = "/reservas/admin/administrar/usuarios/page/1";
			},    
			error : function(xhr, status) {
				alert(usuario + admin + gestor),
 			alert('Disculpe, existió un problema');
 			
			}
		});
	
}