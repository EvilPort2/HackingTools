<?php

class CampaignRunFactory
{
    public static function getList($db, $userId, $attackTypes, $AttackTypesOperator = 'IN')
    {
        $query = mysqli_query($db, '
        SELECT 
            cr.ID as id, cr.campaignID, cr.RunDirectory as runDirectory, 
            cr.PhoneGroup as phoneGroup, RunTime as runTime, cr.UserID as userId,
            c.Name as campaignName, lca.AttackID as attackID, aa.Value as attackType
        FROM campaign_runs cr
            LEFT JOIN campaigns c ON cr.CampaignID = c.ID 
            LEFT JOIN linkcampaignsattacks lca ON lca.CampaignID = cr.CampaignID
            LEFT JOIN attack_attributes aa 
                ON aa.AttackID = lca.AttackID
        WHERE 
            cr.UserID = ' . $userId . ' AND
            aa.Attribute = "Attack_Type" AND aa.Value ' . $AttackTypesOperator . '("' . implode('", "', $attackTypes) . '") 
        ORDER BY cr.Date DESC;'
        );

        $campaignRuns = array();
        while ($row = mysqli_fetch_object($query)) {
            $campaignRuns[] = $row;
        }

        if (!count($campaignRuns)) {
            $_SESSION['page_errors'] =
                (isset($_SESSION['page_errors']) ? $_SESSION['page_errors'] : '')
                . 'No Campaign Runs found<br>';
        }

        return $campaignRuns;
    }

    public static function getById($db, $userId, $id)
    {
        $query = mysqli_query($db, '
            SELECT 
                cr.ID as id, cr.campaignID as campaignId, cr.RunDirectory as runDirectory, cr.PhoneGroup as phoneGroup, 
                cr.RunTime as runTime, cr.Date as createdTime, cr.RunOutput as runOutput, cr.UserID as userId,
                c.Name as campaignName, lca.AttackID as attackId
            FROM campaign_runs cr
                LEFT JOIN campaigns c ON cr.CampaignID = c.ID 
                LEFT JOIN linkcampaignsattacks lca ON lca.CampaignID = cr.CampaignID
            WHERE 
                cr.UserID = ' . $userId . ' AND
                cr.ID = ' . $id . '
            LIMIT 1;'
        );

        $campaignRun = mysqli_fetch_object($query);

        return $campaignRun;
    }
}