<?php  
   
/* 
 * Following code will get single product details 
 * A product is identified by product id (pid) 
 */  
   
// array for JSON response  
$response = array();  
   
// include db connect class  
require_once __DIR__ . '/db_key_connect.php';  
   
// connecting to db  
$db = new DB_CONNECT();  
   
// check for post data  
if (isset($_GET["kid"]) && isset($_GET["IMEI"]) && isset($_GET["RTIME"])) {  
    $kid = $_GET['kid'];
	$imei = $_GET['IMEI'];
	$rtime = $_GET['RTIME'];
	$kidir = $kid . $imei . $rtime;
	echo "</br>kidir:";
	echo $kidir;
	$keymd5 = md5($kidir);
	echo "</br>keymd5:";
	echo $keymd5;
   
    // get the key from keydb table  
    $result = mysql_query("SELECT * FROM keydb WHERE id = $kid"); 
	   
    if (!empty($result)) {  
        // check for empty result
		if (mysql_num_rows($result) > 0) {  
			$result = mysql_fetch_array($result);  
   
            $key = array();  
            $key["id"] = $result["id"];  
            $key["usable"] = $result["usable"]; 
			$keyget = $result["keyvalue"];
			echo "</br>keyget:";
			echo $keyget;
			$rc4value = rc4($keymd5, $keyget);
			echo "</br>rc4value:";
			echo $rc4value;
			//$rc4encode = array();
			//$rc4encode = getBytes($rc4value);
			//$rc4decode = rc4($keymd5, $rc4value);
			//echo "</br>rc4decode:";
			//echo $rc4decode;
			//$key["keyvalue"] = $rc4encode;
            $key["keyvalue"] = base64_encode($rc4value); 
			$keyvalue64 = $key["keyvalue"];
			echo "</br>keyvalue64:";
			echo $keyvalue64;
            // success  
            $response["success"] = 1;  
			//$response["keyvalue"] = $keyv;
            // user node  
            $response["key"] = array();
			//echo "</br>keyjson:";
			//echo json_encode($key);
            array_push($response["key"], $key);  
			//echo "</br>response:";
            // echoing JSON response
			echo "</br>responseJson:</br>";
            echo json_encode($response);  
        } else {  
            // no product found  
            $response["success"] = 0;  
            $response["message"] = "No key found";  
   
            // echo no users JSON  
            echo json_encode($response);  
        }  
    } else {  
        // no product found  
        $response["success"] = 0;  
        $response["message"] = "No key found";  
   
        // echo no users JSON  
        echo json_encode($response);  
    }  
} else {  
    // required field is missing  
    $response["success"] = 0;  
    $response["message"] = "Required field(s) is missing";  
   
    // echoing JSON response  
    echo json_encode($response);  
} 
//rc4
function rc4 ($pwd, $data)//$pwd密钥 $data需加密字符串
    {
        $key[] ="";
        $box[] ="";
     
        $pwd_length = strlen($pwd);
        $data_length = strlen($data);
		$cipher = ""; //null
     
        for ($i = 0; $i < 256; $i++)
        {
            $key[$i] = ord($pwd[$i % $pwd_length]);//ord返回对象的 ASCII值
            $box[$i] = $i;
        }
     
        for ($j = $i = 0; $i < 256; $i++)
        {
            $j = ($j + $box[$i] + $key[$i]) % 256;
            $tmp = $box[$i];
            $box[$i] = $box[$j];
            $box[$j] = $tmp;
        }
     
        for ($a = $j = $i = 0; $i < $data_length; $i++)
        {
            $a = ($a + 1) % 256;
            $j = ($j + $box[$a]) % 256;
     
            $tmp = $box[$a];
            $box[$a] = $box[$j];
            $box[$j] = $tmp;
     
            $k = $box[(($box[$a] + $box[$j]) % 256)];
            $cipher .= chr(ord($data[$i]) ^ $k);//chr从不同的 ASCII 值返回字符
        }
         
        return $cipher;
    }
//String2Bytes
function getBytes($string) { 
        $bytes = array(); 
        for($i = 0; $i < strlen($string); $i++){ 
             $bytes[] = ord($string[$i]); 
        } 
        return $bytes; 
    } 	
?>  