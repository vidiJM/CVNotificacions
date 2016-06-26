<?php
error_reporting(E_ERROR); 

class DB_Connect {
 
    // Constructor
    function __construct() { }
 
    // Destructor
    function __destruct() { }
 
    // Connexió a la BBD
    public function connect() {
        require_once 'config.php';
        
        $con = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
        mysql_select_db(DB_DATABASE);
 
        return $con;
    }
 
    public function close() {
        mysql_close();
    } 
}?>