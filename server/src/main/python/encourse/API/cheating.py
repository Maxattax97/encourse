from API import *

import statistics

# TODO: Refactor so that this api is not dependent on velocity


def is_suspicious(student_stats, class_stats):
    """For each statistic in **class_stats** determine whether the student is within one
    standard deviation above the mean. If the student is outside of the standard 
    deviation for every statistic, flag them as suspicious.
    """

    if len(student_stats.keys()) < 1 or len(class_stats) < 1:
        # Return False if no statistics are available
        return False
    for stat in student_stats:
        if stat in class_stats:
            upper_threshold = class_stats[stat]["mean"] + class_stats[stat]["stdev"]
            value = student_stats[stat]
            if value < upper_threshold:
                # The student was within the safe threshold for at least one statistic
                return False
    return True


def jsonify(git_data, test_progress, hidden_progress=None):
    velocity_averages = []
    rate_averages = []
    student_stats = {}
    for student in git_data.keys():
        student_data = git_data[student]
        student_progress = test_progress[student]
        student_hidden = hidden_progress[student]
        startend = helper.times_from_dailydata(student_data)

        velocity_data = json.loads(
            velocity.jsonify(
                student_progress, student_data, startend, hidden_scores=student_hidden
            )
        )

        progress = 0.0
        time_spent = 0.0
        commit_count = 0.0
        for day in velocity_data:
            progress += day["progress"]
            time_spent += day["time_spent"]
            commit_count += day["commit_count"]

        # Convert time_spent from seconds to hours
        velocity_averages.append(progress / time_spent)
        rate_averages.append(progress / commit_count)

        student_stats[student] = {
            "rate": progress / commit_count,
            "velocity": progress / time_spent,
        }

    # Find the mean and population standard deviation of velocity measures
    velocity_mean = statistics.mean(velocity_averages)
    velocity_stdev = statistics.pstdev(velocity_averages)
    helper.eprint("Velocity Average: {}".format(velocity_mean))
    helper.eprint("Velocity Standard Deviation: {}".format(velocity_stdev))

    # Find the mean and population standard deviation of rate measures
    rate_mean = statistics.mean(rate_averages)
    rate_stdev = statistics.pstdev(rate_averages)
    helper.eprint("Rate Average: {}".format(rate_mean))
    helper.eprint("Rate Standard Deviation: {}".format(rate_stdev))

    class_stats = {
        "rate": {"mean": rate_mean, "stdev": rate_stdev},
        "velocity": {"mean": velocity_mean, "stdev": velocity_stdev},
    }

    suspicious_students = [
        s for s in student_stats if is_suspicious(student_stats[s], class_stats)
    ]
    suspicious_students = student_stats

    # Convert statistics into a single suspciousness score
    student_list = []
    for student in suspicious_students:
        student_stats = suspicious_students[student]
        student_rate = student_stats["rate"]
        student_velocity = student_stats["velocity"]

        # Convert rate to standard normal
        std_rate = (student_rate - rate_mean) / rate_stdev
        std_velocity = (student_velocity - velocity_mean) / velocity_stdev

        # Standardize combined metric (mean = 0, stdev = 2)
        score = (std_rate + std_velocity) / 2

        # Add student to the list
        student_list.append({"id": student, "score": score, "metrics": {"rate": student_rate, "veloctiy": student_velocity}})

    return json.dumps(student_list)


def jsonprint(args):

    visible_file = args.visiblefile
    hidden_file = args.hiddenfile
    commit_log_file = args.logfile

    visible_progress = Progress.pastprogress.pastprogress(visible_file)
    hidden_progress = Progress.pastprogress.pastprogress(hidden_file)

    git_data = GitLog.daily.daily(
        commit_log_file, max_change=args.limit, timeout=args.timeout
    )

    api_json = jsonify(git_data, visible_progress, hidden_progress=hidden_progress)

    print(api_json)
