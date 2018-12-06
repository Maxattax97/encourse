from API import *

# from test_completion import get_test_completion as test_completion
# from test_completion import get_test_completion_string as test_completion_string


def testcli(args):
    print("hi")
    print("Successfully called get statistics function with args: {}".format(args))


priorities = {
    "Start Date": 0,
    "End Date": 1,
    "Additions": 2,
    "Deletions": 3,
    "Commit Count": 4,
    "Estimated Time Spent": 5,
    "Current Test Score": 6,
    "Current Hidden Score": 7,
    "Current Total Score": 8,
}

descriptions = {
    "Start Date": "The date of the student's first commit",
    "End Date": "The date of the student's last commit",
    "Additions": "Number of added lines of code made the by student so far",
    "Deletions": "Number of deleted lines of code made the by student so far",
    "Commit Count": "Number of commits made the by student so far",
    "Estimated Time Spent": "Estimated by recording the time between commits that appear less than an hour appart",
    "Current Test Score": "Score calculated by summing the points given for each passing test case visible to the student",
    "Current Hidden Score": "Score calculated by summing the points given for each passing test case hidden from the student",
    "Current Total Score": "Score calculated by summing the points given for each passing test case visible and hidden from the student",
    "Average Additions": "Average number of line of code added over the entire project",
    "Average Deletions": "Average number of line of code deleted over the entire project",
    "Average Commit Count": "Average number of commits",
    "Average Estimated Time Spent": "Average estimated time spent. Estimated by recording the time between commits that appear less than an hour appart",
}


def average_statistics(parser, visible, hidden=None, max_changes=None, timeout=None):
    """Creates a list of statistics for each user

    Combines the data from multiple sources into a set of statistics per student

    **Args**:
        **dates** (dict): A dict mapping students to a tuple of start and end dates: ::

            {
                "name1": ("mm-dd-yyyy",  "mm-dd-yyyy"),
                ...
            }

        **stats** (dict): A dictionary of git log data per user. 
        The dictionary is of the following form: ::

            {
                "name1": {
                    "additions": int,
                    "deletions": int,
                    "commit_counts": int,
                    "time_spent": int (seconds),
                }
                ...
            }

        **tests** (dict): A dictionary of each user's score for each test case.
        The dictionary is of the following form: ::

            {
                "tests": {
                    "Test1": ("P" or "F"),
                    ...
                },
                "total": int (percentage)
            }
    
    **Returns**
        json: A json dictionary mapping users to a list of statistics. 
        Each statistic is of the form: ::

            {
                "stat_name": int,
                "stat_value": int,
            }
        
    """
    if not max_changes:
        max_changes = sys.maxsize
    if not timeout:
        timeout = sys.maxsize

    users = len(parser.student_log.keys())
    total_additions = 0
    total_deletions = 0
    start_date = date.today()
    end_date = date.fromtimestamp(0)
    total_commits = 0
    total_time = 0
    total_vscore = 0
    total_hscore = 0
    vscored_users = 0
    hscored_users = 0
    for user in parser.student_log:
        log = parser.student_log[user]

        additions, deletions = log.count_changes(max_size=max_changes)
        total_additions += additions
        total_deletions += deletions

        total_commits += len(log.commits)
        total_time += log.estimate_time(log.commits, timeout=timeout)

        if user in visible:
            total_vscore += visible[user]["total"]
            vscored_users += 1
        if hidden and user in hidden:
            total_hscore += hidden[user]["total"]
            hscored_users += 1

    statistics = {
        "Start Date": log.commits[0].timestamp.date().isoformat(),
        "End Date": log.commits[-1].timestamp.date().isoformat(),
        "Additions": "{} lines".format(round(total_additions / float(users))),
        "Deletions": "{} lines".format(round(total_deletions / float(users))),
        "Commit Count": "{} commits".format(round(total_commits / float(users))),
        "Estimated Time Spent": "{} hours".format(round(total_time / float(users))),
        "Current Test Score": round(total_vscore / float(vscored_users))
        if vscored_users > 0
        else 0,
        "Current Hidden Score": round(total_hscore / float(hscored_users))
        if hscored_users > 0
        else 0,
        "Current Total Score": round(
            (total_vscore + total_hscore) / float(vscored_users + hscored_users)
        )
        if vscored_users + hscored_users > 0
        else 0,
    }

    stat_array = []
    for stat_name in statistics:
        stat_value = statistics[stat_name]
        description = descriptions[stat_name]

        stat_item = {
            "stat_value": stat_value,
            "index": priorities[stat_name],
            "stat_desc": description,
        }

        if users > 1 and stat_name != "Start Date" and stat_name != "End Date":
            stat_item["stat_name"] = "Average " + stat_name
        else:
            stat_item["stat_name"] = stat_name

        if stat_item["stat_name"] in descriptions and descriptions[stat_name] != None:
            stat_item["stat_desc"] = descriptions[stat_name]

        stat_array.append(stat_item)
    return stat_array


def format_date(date):
    """Converts a git date string to the iso format date string"""

    return date.isoformat()


# Runs on file call
def jsonprint(args):
    commit_data_file = args.logfile
    visible_file = args.visiblefile
    hidden_file = args.hiddenfile
    limit = args.limit
    timeout = args.timeout

    parser = GitLog.GitParser(commit_data_file)
    # TODO: check for valid dicts

    visible_scores = Progress.currentprogress.progress_from_file(visible_file)
    hidden_scores = Progress.currentprogress.progress_from_file(hidden_file)

    stats = average_statistics(
        parser, visible_scores, hidden=hidden_scores, max_changes=limit, timeout=timeout
    )
    # print(data)
    api_json = json.dumps(stats)
    # Outputs json to stdout
    print(api_json)
