Plugin for FengOffice.
It extends standard FengOffice API.

http://wiki.fengoffice.com/doku.php/api_documentation 

Some description:

---- Authentication 

Every request to api must include user token.  
You may get this token by calling login operation: 
[FO_URL]/index.php?m=login&username=[USER_NAME]&password=[USER_PASSWORD] 


---- API Methods 


-- list_members 
Returns a list of members 

Using:  
[FO_URL]/index.php?c=vatapi&m=list_members&srv=[MEMBER_TYPE]&auth=[USER_TOKEN] 
[MEMBER_TYPE] may be one of the: workspace, tag, customer_project. 
[USER_TOKEN] - see authentication 


Differences from standard API: 
    Can list Tags 
    Addition field "color" 


-- listing  

Returns a list of objects 

Using: 
[FO_URL]/index.php?c=vatapi&m=listing&srv=[OBJECT_TYPE]&args={[ARGS]}&auth=[USER_TOKEN] 
[OBJECT_TYPE] - name of object type (ProjectTasks, ProjectNotes, Contacts and so on) 
[ARGS] - array of addition parameters 
    order - column for sorting !!!You must use names of Database columns 
    order_dir - direction of sorting (ASC or DESC) 
    members - filter results by selected members. Value is defined as JSON array. Example: "members":[101,105] 
    start - define the number of row from which will be output result 
    limit - define the quantity of outputting rows  
    created_by_id - filter results by id of its creator 
    assigned_to (only for ProjectTasks) - filter results by id o assigned to 
    status  (only for ProjectTasks) - filter tasks by status: 
        case 0:  Incomplete tasks 
        case 1:  Complete tasks 
        case 10: Active tasks 
        case 11: Overdue tasks 
        case 12: Today tasks 
        case 13: Today + Overdue tasks 
        case 14: Today + Overdue tasks 
        case 20: Actives task by current user 
        case 21: Subscribed tasks by current user 

[USER_TOKEN] - see authentication 

Example of query with args:  
my.fengoffice.com/index.php?c=vatapi&m=listing&srv=ProjectTasks&args={"order":"due_date","order_dir":"DESC","members":["658,646"],"limit":5,"status":0}&auth=xxxx 

Differences from standard API: 

    arg "lupdate" - show results that have been updated after the date specified.  
Example: "lupdate":"1450957587 
!! Date & time format are defined by Database 

    Added 2 addition args: 
        columns - shows only selected columns. Value is defined as JSON array.  
    !! Use names of columns like they are displayed in output 
    Example: "columns":["id","duedate"] 

        filter - additional sql-conditions 
    !! Use sql expressions and column names 
    Example: "filter":"id in(select rel_object_id from fo_linked_objects where object_id=3052)" 

    Added to output field ptid (ProjectTask ID) for Timeslots 

    Remove case sensitive for object type name (value of 'srv') 

    Add field "all_members'. Standard 'members' and 'memPath' didn't return full list of members including workspace hierarchy. 

    "Lastupdated" is numerical value 

    If "status" is not entered, returns tasks with any status (in origin - returns only completed tasks) 

     

-- save_object  

Add or update object (in standard realization - only ProjectTask or ProjectNote) 

Using: 
[FO_URL]/index.php?c=vatapi&m=save_object&srv=[OBJECT_TYPE]&args={[ARGS]}&auth=[USER_TOKEN] 
[OBJECT_TYPE] - task or note  
[ARGS] - data for saving 
For ProjectTask: 
    id - nothing if it's adding new task or id task for update 
    name 
    description 
    dueDate 
    completed 
    assign_to 
     priority 
    members - json array of members ids 

For ProjectNote: 
    id - nothing if it's adding new task or id task for update 
    title 
    text 
    members - json array  of members ids 


Differences from standard API: 

    Add saving of TimeSlot (args: id, uname, date (start of timeslot), time (duration), ptid, members) 
    Return object's id 
    Renamed fields "title" and "due_date" for according listing 
    dueDate must be timestamp 

 
-- trash 

Move object to trash 

Using: 
[FO_URL]/index.php?c=vatapi&m=trash&oid=[OBJECT_ID]&auth=[USER_TOKEN] 

[OBJECT_ID] - id of object to trash  

Differences from standard API: 

    In standard API use parameter "id". I renamed it for unified. 


-- get_object 

Get object's information. Works for ProjectTasks only 

Using: 
[FO_URL]/index.php?c=vatapi&m=get_object&oid=[OBJECT_ID]&auth=[USER_TOKEN] 
[OBJECT_ID] - id of object 

