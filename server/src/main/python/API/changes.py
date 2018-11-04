from API import *


def reformat(commit_list) -> dict:
    """Reformats a list of dates into a dictionary
    
    **Args**:
        **commit_list**: A list of dictionaries of the form::

            {
                'additions': int,
                'deletions': int,
                'date': datetime
            }

    **Returns**:
        dict: a single dictionary of the following form: ::

            "mm-dd-yyyy": {
                "additions": int,
                "deletions": int
            }

    """
    commit_dict = {}
    for info in commit_list:
        new_entry = {}
        new_entry["additions"] = info["additions"]
        new_entry["deletions"] = info["deletions"]
        date = helper.date_string(info["date"])
        commit_dict[date] = new_entry
    return commit_dict


def jsonify_data(commit_data, times) -> json:
    """Convert data to json for api

    Converts the data to a list of dictionaries. One dictionary is
    created per day. A dictionary is also created for each day in between
    the first and last date, even if there is no data for that date. In that case,
    additions and deletions for that day are set to 0.
    
    **Args**:
        **commit_data** (dict): A dictionary containing the git commit list: ::
            
            {
                "name1": [
                    {
                        "additions": datetime,
                        "deletions": int (seconds),
                    },
                    ...
                ],
                ...
            }

    **Returns**:
        json: A json list of entries, one per day, of the following form: ::

          {
                "date": "mm-dd-yyyy",
                "additions": int,
                "deletions": int
            }

    """
    daily_data = []
    date1 = datetime.strptime(times[0], "%Y-%m-%d").date()
    date2 = datetime.strptime(times[1], "%Y-%m-%d").date()
    dates = helper.daterange(date1, date2)
    foundCommit = False
    total_add = 0
    total_del = 0
    for day in dates:
        day = helper.date_string(day)
        new_entry = {}
        new_entry["date"] = day
        if day in commit_data:
            total_add += commit_data[day]["additions"]
            total_del += commit_data[day]["deletions"]
            foundCommit = True
        elif foundCommit == False:
            continue
        new_entry["additions"] = total_add
        new_entry["deletions"] = total_del

        daily_data.append(new_entry)
    return json.dumps(daily_data)


def jsonprint(args):
    commit_data_file = open(args.logfile, "r")
    commit_times_file = open(args.timefile, "r")
    student_id = args.name

    data = (
        GitLog.daily.daily(commit_data_file, max_change=int(args.limit))
        if args.limit
        else get_progress(commit_data_file)
    )
    individual_data = data[student_id]

    reformatted_data = reformat(individual_data)

    commit_times = GitLog.startend.startend(commit_times_file)
    # eprint(commit_times)
    individual_commit_times = commit_times[student_id]

    api_json = jsonify_data(reformatted_data, individual_commit_times)
    print(api_json)
