from API import *


def jsonify(log):
    """ Converts git log data json formatted for the /commitList endpoint

    Uses git log information to create a list of entries containing the date
    and commit count. Note that this function uses the data of a single student.
    
    **Args**:
        **git_data** (dict): A dictionary containing the git commit list: ::
            
            {
                "name1": [
                    {
                        "date": datetime,
                        "commit_count": int,
                    },
                    ...
                ],
                ...
            }

    **Returns**:
        json: A json list with entries of the following format: ::
            
            {
                "date": "mm-dd-yyyy",
                "count": int
            }

    """
    new_data = []
    date1 = log.commits[0].timestamp.date()
    date2 = log.commits[len(log.commits) - 1].timestamp.date()
    dates = helper.daterange(date1, date2)

    # Create a list of dictionaries for each date between the first and last
    for date in dates:
        new_bar = {"date": helper.date_string(date), "count": 0}
        new_data.append(new_bar)

    # Replace the counts for each date with actual data
    commits = log.commits_by_day()
    for day in commits:
        count = len(commits[day])
        for e in new_data:
            if e["date"] == day:
                e["count"] = count
                break

    return json.dumps(new_data)


def jsonprint(args):

    commit_data_file = args.logfile
    student_id = args.name

    commit_data = GitLog.GitParser(commit_data_file)
    data = commit_data.student_log[student_id]

    formatted_data = jsonify(data)
    print(formatted_data)
