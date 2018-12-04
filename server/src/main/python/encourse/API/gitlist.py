from API import *
import copy


def jsonify(gitlogs, student_id=None):
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
    git_dict = {}
    for student in gitlogs.student_log:
        log = gitlogs.student_log[student]
        daily_commits = log.commitsByDay()
        for day in daily_commits:
            day["date"] = helper.date_string(day["timestamp"])
            del day["timestamp"]
            files = day["files"]
            day["files"] = []
            for f in files:
                day["files"].append(f.name)
            day["time_spent"] = (
                helper.time_string(day["time_spent"]) if "time_spent" in day else "X"
            )
        git_dict[student] = daily_commits
    # TODO: Short circuit if student is defined and his or her data is parsed
    if student_id:
        return json.dumps(git_dict[student])
    return json.dumps(git_dict)


def jsonprint(args):
    student_id = args.name
    commit_data_file = args.logfile
    students = GitLog.GitParser(commit_data_file)

    api_json = jsonify(students, student_id)
    print(api_json)
