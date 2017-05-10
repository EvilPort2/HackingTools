<?php

require_once './php/db_creds.php';

class DBFactory {
	
	protected static $connection = null;
	
	public static function getConnection() {
		
		if (!self::$connection) {
			
			global $db_hostname, $db_username, $db_password, $db_database;
			self::$connection = mysqli_connect($db_hostname, $db_username, $db_password, $db_database);
		}
		
		return self::$connection;
	}
	
	public static function getAll($query, $paramTypes = '', $paramValues = []) {
		
		is_array($paramValues) || $paramValues = [$paramValues];
		$connection = self::getConnection();
		$stmt = mysqli_stmt_init($connection);
		mysqli_stmt_prepare($stmt, $query);
		if ($paramValues && $paramTypes) {
			
			$bindFunctionParams = [& $stmt, & $paramTypes];
			
			foreach ($paramValues as $paramKey => $param) {
				
				$bindFunctionParams[] = & $paramValues[$paramKey];
			}
			call_user_func_array('mysqli_stmt_bind_param', $bindFunctionParams);
		}
		mysqli_stmt_execute($stmt);
		
		$result = [];
		$executeResult = mysqli_stmt_get_result($stmt);
        while ($row = mysqli_fetch_object($executeResult)) {
        
			$result[] = $row;
        }
		
		return $result;
	}
	
	public static function getOne($query, $paramTypes = '', $paramValues = []) {
		
		return self::getAll($query, $paramTypes, $paramValues)[0];
	}
}