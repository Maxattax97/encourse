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
        daily_commits = log.commits_by_day()
        student_days = []
        for day in daily_commits:
            commits = daily_commits[day]
            daily_dict = {}
            daily_dict["date"] = day
            daily_dict["files"] = []
            files = set()
            for commit in commits:
                files = files.union(set([f.name for f in commit.files]))
            daily_dict["files"] = list(files)
            daily_dict["time_spent"] = log.estimate_time(commits)
            student_days.append(daily_dict)
        git_dict[student] = student_days
    # TODO: Short circuit if student is defined and his or her data is parsed
    if student_id:
        return json.dumps(git_dict[student_id])
    return json.dumps(git_dict)


def hash_list(parser, student_id=None):
    if student_id:
        return json.dumps({student_id: create_entries(parser.student_log[student_id])})
    students = {}
    for student in parser.student_log:
        log = parser.student_log[student]
        students[student_id] = create_entries(log)
    return json.dumps(students)


def create_entries(log, max_changes=None):
    if max_changes == None:
        max_changes = sys.maxsize
    entries = []
    for commit in log.commits:
        entry = {"additions": 0, "deletions": 0, "hash": "", "date": ""}
        entry["additions"], entry["deletions"] = commit.count_changes(
            max_size=max_changes
        )
        entry["hash"] = commit.commit_hash
        entry["date"] = commit.timestamp.date().isoformat()
        entries.append(entry)
    return entries


def jsonprint(args):
    student_id = args.name
    commit_data_file = args.logfile
    parser = GitLog.GitParser(commit_data_file)

    api_json = hash_list(parser, student_id=student_id)
    print(api_json)
