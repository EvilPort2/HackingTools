<?php

require_once 'DBFactory.php';

/**
 *
 */
class TargetFactory
{
    public static function getResponsedList($db, $userId, $campaignId = null)
    {
        $query = mysqli_query($db, $x='
        SELECT 
            DISTINCT (cr.Number) as number, cr.Name as name, cr.RunTime
        FROM campaign_results cr
        WHERE 
            cr.UserID = ' . (int) $userId . ' AND 1
            #cr.ResultsString <> "" AND
            #cr.Number <> ""
			' . ($campaignId ? ' AND CampaignRunID IN (SELECT ID FROM campaign_runs WHERE CampaignID = ' . (int) $campaignId . ')' : '') . '
        ORDER BY cr.RunTime DESC'
        );

        $targets = array();
        while ($row = mysqli_fetch_object($query)) {
            $targets[] = $row;
        }

        if (!count($targets)) {
            $_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No targets found1<br>';
        }

        return $targets;
    }
	
	public static function getTotalList($db, $userId)
	{
		$query = mysqli_query(
			$db, 
			'SELECT  ID, Name, Number, UserID, PhoneGroup FROM phonebook WHERE UserID = ' . (int) $userId
				. ' AND (ID IN (SELECT MIN(ID) FROM  phonebook GROUP BY Number))'
        );

        $targets = array();
        while ($row = mysqli_fetch_object($query)) {
            
			$targets[] = $row;
        }

        if (!count($targets)) {
            
			$_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No targets found<br>';
        }

        return $targets;
	}
	
	public static function getTotalAttackedList($db, $userId, $campaignId = null)
	{
		$query = mysqli_query(
			$db, $x='
			SELECT * FROM phonebook WHERE ID IN(
			SELECT MIN(p.ID)
			FROM phonebook p
			INNER JOIN campaign_runs c ON c.PhoneGroup=p.PhoneGroup
			WHERE p.UserID = ' . (int) $userId . '
			' . ($campaignId ? ' AND CampaignID = ' . (int) $campaignId : '') . '
			GROUP BY p.Number
		)'
        );

        $targets = array();
        while ($row = mysqli_fetch_object($query)) {
            
			$targets[] = $row;
        }

        if (!count($targets)) {
            
			$_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No targets found3<br>';
        }

        return $targets;
	}
	
	public static function getResponsedWithCampaignsList($userId, $campaignId = null)
	{
		$query = '
			SELECT
				p.Number,
				GROUP_CONCAT(p.Name) as Name,
				MIN(p.PhoneGroup) as PhoneGroup,
				GROUP_CONCAT(c.ID) AS CampaignId,
				GROUP_CONCAT(c.Name) AS CampaignName
			FROM phonebook p
			INNER JOIN campaign_runs cr ON cr.PhoneGroup = p.PhoneGroup
			INNER JOIN campaign_results cres ON cres.CampaignRunId = cr.ID
			INNER JOIN campaigns c ON c.ID = cr.CampaignID
			WHERE p.UserID = ? '
				. ($campaignId ? ' AND cres.CampaignID = ' . (int) $campaignId : '') . '
			GROUP BY p.Number';
		//echo $query.'----'.$userId;die;
		$targets = DBFactory::getAll($query, 'i', $userId);

        if (!count($targets)) {
            
			$_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No targets found3<br>';
        }

        return $targets;
	}
	
	public static function getTotalWithCampaignsList($userId, $campaignId = null)
	{
		$query = '
			SELECT
				p.Number,
				GROUP_CONCAT(p.Name) as Name,
				MIN(p.PhoneGroup) as PhoneGroup,
				GROUP_CONCAT(c.ID) AS CampaignId,
				GROUP_CONCAT(c.Name) AS CampaignName
			FROM phonebook p
			LEFT JOIN campaign_runs cr ON cr.PhoneGroup = p.PhoneGroup
			LEFT JOIN campaign_results cres ON cres.CampaignRunId = cr.ID
			LEFT JOIN campaigns c ON c.ID = cr.CampaignID
			WHERE p.UserID = ? '
				. ($campaignId ? ' AND cres.CampaignID = ' . (int) $campaignId : '') . '
			GROUP BY p.Number';
		//echo $query.'----'.$userId;die;
		$targets = DBFactory::getAll($query, 'i', $userId);

        if (!count($targets)) {
            
			$_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No targets found3<br>';
        }

        return $targets;
	}
	
	public static function getTotalAttackedListByRunId($campaignRunId = null)
	{
		$query = '
			SELECT * FROM phonebook WHERE ID IN(
			SELECT MIN(p.ID)
			FROM phonebook p
			INNER JOIN campaign_runs c ON c.PhoneGroup=p.PhoneGroup
			WHERE c.ID = ?
			GROUP BY p.Number
		)';

        $targets = DBFactory::getAll($query, 'i', $campaignRunId);

        if (!count($targets)) {
            
			$_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No targets found3<br>';
        }

        return $targets;
	}

    public static function getById($db, $userId, $number)
    {
        $query = mysqli_query($db, '
        SELECT 
            cr.ID as id, cr.Name as name, cr.Number as number, cr.CampaignRunID as campaignRunId, 
            cr.UserID as userId, cr.ResultsString as resultsString, cr.RunTime as runTime,
            crr.RunDirectory as runDirectory
        FROM campaign_runs crr
            LEFT JOIN campaign_results cr ON cr.CampaignRunID = crr.ID
        WHERE
            cr.Number = "' . $number . '"
        ORDER BY cr.RunTime DESC'
        );

        $campaignRuns = array();
        while ($row = mysqli_fetch_object($query)) {
            self::populate($db, $row);
            $campaignRuns[] = $row;
        }
//_d([111,$campaignRuns]);
        if (!count($campaignRuns)) {
            $_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No Campaign Runs found<br>';
        }

        return $campaignRuns;
    }
	
	public static function getAimedForCompaign($db, $campaignRun)
	{
		_d([1111,DBFactory::getAll('SELECT * FROM ')]);
	}

    public static function getForCampaign($db, $campaignRun)
    {
        $query = mysqli_query($db, '
            SELECT 
                ID as id, CampaignRunID as campaignRunId, Name as name, Number as number, "' . $campaignRun->runDirectory . '" as runDirectory, 
                UserID as userId, ResultsString as resultsString, RunTime as runTime
            FROM campaign_results
            WHERE CampaignRunID = ' . (int) $campaignRun->id . '
            ORDER BY RunTime DESC;'
        );

        $targets = array();
        while ($row = mysqli_fetch_object($query)) {
            $targets[] = $row;
        }

        if (self::populateForCampaign($db, $campaignRun, $targets)) {
            $query = mysqli_query($db, '
                SELECT 
                    ID as id, CampaignRunID as campaignRunId, Name as name, Number as number, "' . $campaignRun->runDirectory . '" as runDirectory, 
                    UserID as userId, ResultsString as resultsString, RunTime as runTime
                FROM campaign_results
                WHERE CampaignRunID = ' . (int) $campaignRun->id . '
                ORDER BY RunTime DESC;'
            );

            $targets = array();
            while ($row = mysqli_fetch_object($query)) {
                $targets[] = $row;
            }
        }

        if (!count($targets)) {
            $_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No results found<br>';
        }

        return $targets;
    }

    private static function populate($db, $target)
    {
        if (!empty($target->resultString) && !empty($target->runTime)) {
            return $target;
        }

        $resultsDirectory = $_SESSION['dagah_dir'] . $_SESSION['savedruns_dir']
            . $target->runDirectory . $_SESSION['results_dir'];
        if (!is_dir($resultsDirectory)) {
            $_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                .'Can not open result\'s directory ' . $target->runDirectory . '<br>';
            return false;
        }

        $dir = opendir($resultsDirectory);

        $counter = 0;

        while ($filePath = readdir($dir)) {
            if (substr($filePath, -8) == '-results') {
                $file = fopen($resultsDirectory . $filePath, 'r');
                if ($file === false) {
                    $_SESSION['page_errors'] =
                        (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                        . 'Can not read result\'s file<br>';
                    continue;
                }

                while (($line = fgets($file)) !== false) {
                    $counter++;
                    $elements = explode(' ', $line, 3);
                    if (count($elements) != 3) {
                        continue;
                    }

                    $number = trim($elements[1], '\ \,');
                    if ($number != $target->number) {
                        continue;
                    }

                    $runTime = date_create_from_format('d/M/Y:H:i:s', trim($elements[0], '\ \[\]'));
                    !$runTime instanceof DateTime
                        ? $runTime = null
                        : $runTime = $runTime->format('Y-m-d H:i:s');

                    $resultsString = trim($elements[2]);

                    $isModified = mysqli_query($db, '
                        UPDATE campaign_results SET 
                            ResultsString = "'. mysqli_real_escape_string($db, $resultsString) .'",
                            RunTime = "' . mysqli_real_escape_string($db, $runTime) . '"
                        WHERE 
                            Number = "' . mysqli_escape_string($db, $number) . '" AND
                            CampaignRunId = ' . $target->campaignRunId . '
                        LIMIT 1;
                    ');

                    if ($isModified === true) {
                        $target->runTime = $runTime;
                        $target->resultsString = $resultsString;
                    }

                    break;
                }
                fclose($file);
            }
        }
        closedir($dir);

        return $target;
    }

    private static function populateForCampaign($db, $campaignRun, $targets)
    {
        $resultsDirectory = $_SESSION['dagah_dir'] . $_SESSION['savedruns_dir']
            . $campaignRun->runDirectory . $_SESSION['results_dir'];
        if (!is_dir($resultsDirectory)) {
            $_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'Can not open result\'s directory<br>';
            return false;
        }

        $dir = opendir($resultsDirectory);

        $counter = 0;

        while ($filePath = readdir($dir)) {
            if (substr($filePath, -8) == '-results') {
                $file = fopen($resultsDirectory . $filePath, 'r');
                if ($file === false) {
                    $_SESSION['page_errors'] =
                        (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                        . 'Can not read result\'s file<br>';
                    continue;
                }

                while (($line = fgets($file)) !== false) {
                    $counter++;
                    $elements = explode(' ', $line, 3);
                    if (count($elements) != 3) {
                        continue;
                    }

                    $isExists = false;

                    $runTime = date_create_from_format('d/M/Y:H:i:s', trim($elements[0], '\ \[\]'));

                    $runTime = $runTime instanceof DateTime
                        ? $runTime->format('Y-m-d H:i:s')
                        : null;

                    $number = trim($elements[1], '\ \,');
                    $result = trim($elements[2]);

                    foreach ($targets as $target) {
                        if ($target->number == $number) {
                            if (empty($target->resultsString) || empty($target->runTime)) {
                                mysqli_query($db, '
                                    UPDATE campaign_results SET 
                                        ResultsString = "'. mysqli_real_escape_string($db, $result) .'",
                                        RunTime = "' . mysqli_real_escape_string($db, $runTime) . '"
                                        WHERE Number = "' . mysqli_real_escape_string($db, $number) . '" LIMIT 1;
                                ');
                            }
                            $isExists = true;
                            break;
                        }
                    }
                    if ($isExists === false && !empty($number)) {
                        mysqli_query($db, '
                            INSERT INTO campaign_results SET
                                CampaignRunID = ' . $campaignRun->id . ',
                                Number = "' . mysqli_real_escape_string($db, $number) . '",
                                UserID = ' . $campaignRun->userId . ',
                                ResultsString = "'. mysqli_real_escape_string($db, $result) .'",
                                RunTime = "' . mysqli_real_escape_string($db, $runTime) . '";
                        ');
                    }
                }
                fclose($file);
            }
        }
        closedir($dir);

        return $counter > 0;
    }

}