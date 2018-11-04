from API import *

#from test_completion import get_test_completion as test_completion
#from test_completion import get_test_completion_string as test_completion_string

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

}

def combine_statistics(dates, stats, tests):
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

    data = {}
    for user in dates.keys():
        user_dates = dates[user]
        additions = 0
        deletions = 0
        count = 0
        time = 0
        if user in stats:
            info = stats[user]
            additions = info["additions"]
            deletions = info["deletions"]
            count = info["commit_count"]
            time = info["time_spent"]
        test_score = 0
        # This if else is dumb, fix it. strings and files should both be handled gracefully
        if user in tests:
            info = tests[user]
            test_score = info["total"]
        elif "total" in tests:
            test_score = tests["total"]
        user_data = {}
        if len(user_dates) == 2:
            user_data["Start Date"] = format_date(user_dates[0])
            user_data["End Date"] = format_date(user_dates[1])
        user_data["Additions"] = "{} lines".format(additions)
        user_data["Deletions"] = "{} lines".format(deletions)
        user_data["Commit Count"] = "{} commits".format(count)
        user_data["Estimated Time Spent"] = helper.time_string(time)
        user_data["Current Test Score"] = "{}%".format(int(test_score))

        array_data = []
        for stat_name in user_data:
            stat_value = user_data[stat_name]
            array_data.append({"stat_name": stat_name, "stat_value": stat_value, "index":priorities[stat_name]})
        data[user] = array_data

    return data


def format_date(date):
    """Converts a git date string to the iso format date string"""

    date_data = datetime.strptime(date, "%Y-%m-%d")
    return date_data.date().isoformat()


def sum_statistics(commit_data):
    """Converts data per student per commit into cumulative statistics per student

    **Args**:
        **commit_data** (dict): A dictionary of students mapped to lists of commit data
        The dict has the following form: ::
            
            {
                "name1": [
                    {
                        "additions": int,
                        "deletions": int,
                        "commit_count": int,
                        "time_spent": int (seconds)
                    }
                    ...
                ]
                ...
            }

    **Returns**:
        dict: A dictionary mapping students to cumulative statistics,
        The dictionary has the following form: ::

            {
                "name1": {
                    "additions": int,
                    "deletions": int,
                    "commit_count": int,
                    "time_spent": int (seconds)
                }
                ...
            }
        
    """
    new_data = {}
    for student in commit_data:
        commits = commit_data[student]
        total_add = 0
        total_del = 0
        total_count = 0
        total_time = 0
        for commit in commits:
            total_add += commit["additions"]
            total_del += commit["deletions"]
            total_time += commit["time_spent"]
            total_count += commit["commit_count"]
        student_data = {}
        student_data["additions"] = total_add
        student_data["deletions"] = total_del
        student_data["commit_count"] = total_count
        student_data["time_spent"] = total_time
        new_data[student] = student_data
    return new_data


# Runs on file call
def jsonprint(args):
    student_id = args.name
    commit_date_file = args.timefile
    commit_data_file = args.logfile
    test_case_string = args.tests

    dates_dict = GitLog.startend.startend(commit_date_file)
    # for user in dates_dict.keys():
    #    start_end = dates_dict[user]
    #    print("{} -> {}".format(user, start_end))

    # print(counts_dict)

    student_data = GitLog.daily.daily(
        commit_data_file, max_change=args.limit, timeout=args.timeout
    )
    formatted_student_data = sum_statistics(student_data)
    # TODO: check for valid dicts

    test_data = Progress.currentprogress.progress_from_string(test_case_string)

    data = combine_statistics(dates_dict, formatted_student_data, test_data)
    # print(data)
    api_json = json.dumps(data[student_id])
    # Outputs json to stdout
    print(api_json)
