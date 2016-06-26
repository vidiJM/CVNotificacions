<?php
 
class funciones_BD {
 
    private $db;
 
    /*
    * Constructor
    */
    function __construct() {
        require_once 'connectbd.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    /*
    * Destructor
    */
    function __destruct() { }

    /*
    * Comprovem que existeix l'usuari
    */
    public function login($user){

        $query="SELECT COUNT(*) FROM mdl_user WHERE username='$user'";
        $result=mysql_query($query) or die(mysql_error());
        $count = mysql_fetch_row($result);

        if ($count[0]==0){
            return true;
        } else {
            return false;
        }
    }

    public function saveidGCM($user,$idGCM){
        
        $id = mysql_query("SELECT id FROM mdl_user WHERE username='$user'");
        $result = mysql_query("INSERT INTO app_tfg_user (usuari,userid,device) VALUES ('$user','$id','$idGCM')");
        $num_rows = mysql_num_rows($result); //numero de filas retornadas

        if ($num_rows > 0) {

            // el usuario existe 
            return true;
        } else {
            // no existe
            return false;
        }

    }
}?>

