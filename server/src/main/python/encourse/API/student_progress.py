from API import *


def extract_progress(progress_data):
    """Extracts additions and deletions from commit data

    **Args**:
        **commit_data** (dict): A list of dictionaries containing historical progress,
        each with the following form: ::

            {
                "date": datetime,
                "progress": int,
            }

    **Returns**:
        dict: A dictionary that maps dates to additions and deletions
        with the following form: ::

            {
                "date1": {
                    "progress": int,
                }
                ...
            }

    """
    new_data = {}
    for info in progress_data:
        date = helper.date_string(info["date"])
        new_data[date] = info["progress"]
    return new_data


def jsonify(visible_data, hidden_data, times):
    """Convert data to json for /progress endpoint

    Converts the data to a list of dictionaries. One dictionary is
    created per day. A dictionary is also created for each day in between
    the first and last date, even if there is no data for that date. In that case,
    progress for that day are set to the same value as the previous day.
    
    **Args**:
        **commit_data** (dict): A dictionary containing the git commit list: ::
            
            {
                "date1": {
                    "progress": int,
                },
                ...
            }

        **times** (str, str): A tuple of start and end dates of the form "mm-dd-yyyy"

    **Returns**:
        json: A json list of entries, one per day, of the following form: ::

            {
                "date": "mm-dd-yyyy",
                "progress": int,
            }

    """
    daily_data = []
    # Create date range using times file
    date1 = datetime.strptime(times[0], "%Y-%m-%d").date()
    date2 = datetime.strptime(times[1], "%Y-%m-%d").date()
    dates = helper.daterange(date1, date2)
    visible = 0
    hidden = 0
    total = 0

    # Fill each date with comulative progress
    for day in dates:
        day = helper.date_string(day)
        new_entry = {}
        new_entry["date"] = day
        if day in visible_data:
            visible = visible_data[day]
        if day in hidden_data:
            hidden = hidden_data[day]
        total = (hidden + visible) / 2.0
        if hidden == 0:
            total = visible
        new_entry["visible"] = visible
        new_entry["hidden"] = hidden
        new_entry["progress"] = round(total)
        daily_data.append(new_entry)

    # Convert progress to a percentage by divding by the final total
    # for item in daily_data:
    #    item["progress"] = round(item["progress"] / progress * 100)
    return daily_data


def jsonprint(args):
    visible_file = args.visiblefile
    hidden_file = args.hiddenfile
    commit_times_file = args.timefile
    student_id = args.name

    visible_data = Progress.pastprogress.pastprogress(visible_file)
    individual_visible_data = visible_data[student_id]
    reformatted_visible_data = extract_progress(individual_visible_data)

    hidden_data = Progress.pastprogress.pastprogress(hidden_file)
    individual_hidden_data = hidden_data[student_id]
    reformatted_hidden_data = extract_progress(individual_hidden_data)

    commit_times = GitLog.startend.startend(commit_times_file)
    individual_commit_times = commit_times[student_id]

    api_formatted_data = jsonify(
        reformatted_visible_data, reformatted_hidden_data, individual_commit_times
    )
    api_json = json.dumps(api_formatted_data)
    print(api_json)