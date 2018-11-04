from API import *
import copy


def jsonify(git_data, student=None):
    """ Converts git log data json formatted for the /commitList endpoint

    Description
    
    **Args**:
        **git_data** (dict): A dictionary containing the git commit list: ::
            
            {
                "name1": [
                    {
                        "date": datetime,
                        "time_spent": int (seconds),
                        "files": ["file1",...],
                    },
                    ...
                ],
                ...
            }

    **Returns**:
        dict: A dictionary identical to **git_data**, except all "date" and "time_spent"
        properties are converted to human readable strings

    """
    # Create complete copy. Copy module may be removed in the future
    data = copy.deepcopy(git_data)
    for student in data:
        student_data = data[student]
        for day in student_data:
            day["date"] = helper.date_string(day["date"])
            day["time_spent"] = helper.time_string(day["time_spent"])
    if student:
        return json.dumps(data[student])
    return json.dumps(data)


def jsonprint(args):
    student_id = args.name
    commit_data_file = args.logfile
    data = GitLog.daily.daily(commit_data_file)

    api_json = jsonify(data, student_id)
    print(api_json)
