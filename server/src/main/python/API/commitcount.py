from API import *


def jsonify(commit_data):
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
    date1 = commit_data[0]["date"]
    date2 = commit_data[len(commit_data) - 1]["date"]
    dates = helper.daterange(date1, date2)

    # Create a list of dictionaries for each date between the first and last
    for date in dates:
        new_bar = {"date": helper.date_string(date), "count": 0}
        new_data.append(new_bar)

    # Replace the counts for each date with actual data
    for entry in commit_data:
        date = helper.date_string(entry["date"])
        count = entry["commit_count"]
        for e in new_data:
            if e["date"] == date:
                e["count"] = count
                break
    return json.dumps(new_data)


def jsonprint(args):

    commit_data_file = args.logfile
    student_id = args.name

    commit_data = GitLog.daily.daily(commit_data_file)
    data = commit_data[student_id]

    formatted_data = jsonify(data)
    print(formatted_data)
