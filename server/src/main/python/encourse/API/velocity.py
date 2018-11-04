from API import *


def jsonify(
    scores, daily_data, times, hidden_scores=None, max_velocity=None, max_rate=None
):
    """Convert data to json for /velocity endpoint

    Converts the data to a mapping of students to their respective daily velocities. 
    Each student is flagged as suspicious if their progress per hour exceeds 
    **max_velocity**, or if their progress per commit exceeds **max_rate**
    A dictionary is also created for each day in between the first and last date, 
    even if there is no data for that date. In that case, both velocity and rate 
    for that day are set zero.
    
    **Args**:
        **daily_data** (dict): A dictionary containing the git commit list:
        **times** (str, str): A tuple of start and end dates of the form "mm-dd-yyyy"

    **Returns**:
        json: A json dictionary

    """

    # Create date range using times file
    date1 = datetime.strptime(times[0], "%Y-%m-%d").date()
    date2 = datetime.strptime(times[1], "%Y-%m-%d").date()
    dates = helper.daterange(date1, date2)

    scores = {helper.date_string(x["date"]): x for x in scores}
    hidden_scores = {helper.date_string(x["date"]): x for x in hidden_scores}
    daily_data = {helper.date_string(x["date"]): x for x in daily_data}

    velocity_data = []
    cumulative_progress = 0
    min_progress = 0

    for day in dates:
        day = helper.date_string(day)
        new_entry = {}
        new_entry["date"] = day

        # get cumulative progress
        if day in scores:
            cumulative_progress = scores[day]["progress"]
        if day in hidden_scores:
            cumulative_progress = hidden_scores[day]["progress"]

        # calculate daily progress
        new_entry["progress"] = cumulative_progress - min_progress
        min_progress = max(min_progress, cumulative_progress)

        # copy daily data
        new_entry["time_spent"] = 0
        new_entry["commit_count"] = 0
        if day in daily_data:
            new_entry["time_spent"] = daily_data[day]["time_spent"]
            new_entry["commit_count"] = daily_data[day]["commit_count"]

        velocity_data.append(new_entry)

    return json.dumps(velocity_data)


def jsonprint(args):
    visible_file = args.visiblefile
    hidden_file = args.hiddenfile
    commit_log_file = args.logfile
    student_id = args.name

    visible_data = Progress.pastprogress.pastprogress(visible_file)
    individual_visible_data = visible_data[student_id]

    hidden_data = Progress.pastprogress.pastprogress(hidden_file)
    individual_hidden_data = hidden_data[student_id]

    daily_data = GitLog.daily.daily(
        commit_log_file, max_change=args.limit, timeout=args.timeout
    )
    individual_daily_data = daily_data[student_id]

    startend = helper.times_from_dailydata(individual_daily_data)

    api_json = jsonify(
        individual_visible_data,
        individual_daily_data,
        startend,
        hidden_scores=individual_hidden_data,
    )
    print(api_json)
