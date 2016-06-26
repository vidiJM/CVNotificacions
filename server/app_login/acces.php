
<?php

	require_once 'funciones_bd.php';
	
	$usuario = $_POST['usuario'];
	//$password = $_POST['password'];
	
	$db = new funciones_BD();

	if($db->login($usuario)){
		$resultado[]=array("logstatus"=>"0");
	} else {
		$resultado[]=array("logstatus"=>"1");
	}
	echo json_encode($resultado);
?>