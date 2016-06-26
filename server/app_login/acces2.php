
<?php

	require_once 'funciones_bd.php';
	
	$idGCM = $_POST['idGCM'];
	$user = $_POST['user'];
	
	$db = new funciones_BD();

	if($db->saveidGCM($user,$idGCM)){
		$resultado[]=array("logstatus"=>"0");
	} else {
		$resultado[]=array("logstatus"=>"1");
	}
	echo json_encode($resultado);
?>