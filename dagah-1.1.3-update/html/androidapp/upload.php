<?php
header('Content-Type: text; charset=utf-8');
$modemlocation = "/home/dagah/dagah/modem";
$savedlocation = "/home/dagah/dagah/savedruns";
$delete=$_REQUEST['deleteline'];
$command=$_REQUEST['connectline'];
$blu=$_REQUEST['bluetoothline'];
if (isset($delete))
{
	$commandfile = file_get_contents('command');
	if(strpos($commandfile, $delete) !== FALSE)
	{
		$commandfile = str_replace($delete . PHP_EOL, '', $commandfile);
		file_put_contents('command', $commandfile);
	} 
}
elseif (isset($command))
{
	 $myfile3 = fopen($modemlocation . "/config", "r");
	 $myline = fgets($myfile3);
	 $pieces2 = explode(" ", $myline);
	 $mykey = trim($pieces2[2]);
	 $pieces = explode(" ", $command);
	 if (strcmp(trim($pieces[1]),"CONNECT") == 0) {
	   	$myfile = fopen($modemlocation . "/connected", "w");
                fwrite($myfile,"1");
                fclose($myfile);
		$myfile2 = fopen("connect", "w");
		fwrite($myfile2,$mykey . " CONNECTED");
		fclose($myfile2);
	}
	else if  (strcmp(trim($pieces[1]),"DISCONNECT") == 0) {
                $myfile = fopen($modemlocation . "/connected", "w");
                fwrite($myfile,"0");
                fclose($myfile);
		$myfile2 = fopen("connect", "w");
                fwrite($myfile2,$mykey . "");
                fclose($myfile2);
        }


}
elseif (isset($blu))
{

	$pieces = explode(" ", $blu);
	file_put_contents("test.txt",$blu, FILE_APPEND | LOCK_EX);
	if (strcmp(trim($pieces[1]),"Nearby") == 0) {
		$mycampaign = trim($pieces[0]);
		$myfolder = $savedlocation . $mycampaign . "/results";
		$myfile = fopen($myfolder . "/bluetooth-scan.txt", "w");
		$sliced = array_slice($pieces, 1);
                $mywrite = implode(" ", $sliced);
		$mywrite2 = str_replace(",", PHP_EOL, $mywrite);
		fwrite($myfile,$mywrite2);
                fclose($myfile);
	}
	elseif (strcmp(trim($pieces[1]),"Paired") == 0) {
		$mycampaign = trim($pieces[0]);
                $myfolder = $savedlocation . $mycampaign . "/results";
                $myfile = $myfolder . "/bluetooth-paired.txt";
		$sliced = array_slice($pieces, 1);
                $mywrite = implode(" ", $sliced);
		$mywrite2 = str_replace(",", PHP_EOL, $mywrite);
		file_put_contents($myfile, $mywrite2, FILE_APPEND | LOCK_EX);
	}
}
?>
