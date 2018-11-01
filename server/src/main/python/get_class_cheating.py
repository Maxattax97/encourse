from daily_git_data import get_daily_commit_data as daily_data
from past_progress import past_progress
from get_velocity import jsonify as get_velocity
from helper import eprint
from helper import times_from_dailydata as times

import argparse
import json
import statistics


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
    additions_averages = []
    deletions_averages = []
    student_stats = {}
    for student in git_data.keys():
        student_data = git_data[student]
        student_progress = test_progress[student]
        student_hidden = hidden_progress[student]
        startend = times(student_data)

        velocity_data = json.loads(get_velocity(student_progress, student_data, startend, hidden_scores=student_hidden))

        progress = 0.0
        time_spent = 0.0
        commit_count = 0.0
        for day in velocity_data:
            progress += day["progress"]
            time_spent += day["time_spent"]
            commit_count += day["commit_count"]

        additions = 0.0
        deletions = 0.0
        for day in student_data:
            additions += day['additions']
            deletions += day['deletions']

        # Convert time_spent from seconds to hours 
        time_spent /= 3600
        velocity_averages.append(progress/time_spent)
        rate_averages.append(progress/commit_count)
        additions_averages.append(additions)
        deletions_averages.append(deletions)

        student_stats[student] = {
            "rate": progress/commit_count,
            "velocity": progress/time_spent,
            "additions": additions,
            "deletions": deletions
        }

    # Find the mean and population standard deviation of velocity measures
    velocity_mean = statistics.mean(velocity_averages)
    velocity_stdev = statistics.pstdev(velocity_averages)
    eprint("Velocity Average: {}".format(velocity_mean))
    eprint("Velocity Standard Deviation: {}".format(velocity_stdev))

    # Find the mean and population standard deviation of rate measures
    rate_mean = statistics.mean(rate_averages)
    rate_stdev = statistics.pstdev(rate_averages)
    eprint("Rate Average: {}".format(rate_mean))
    eprint("Rate Standard Deviation: {}".format(rate_stdev))
    
    # Find the mean and population standard deviation of addition and deletion metrics
    additions_mean = statistics.mean(additions_averages)
    additions_stdev = statistics.pstdev(additions_averages)
    eprint("Additions Average: {}".format(additions_mean))
    eprint("Additions Standard Deviation: {}".format(additions_stdev))
    
    deletions_mean = statistics.mean(deletions_averages)
    deletions_stdev = statistics.pstdev(deletions_averages)
    eprint("Deletions Average: {}".format(deletions_mean))
    eprint("Deletions Standard Deviation: {}".format(deletions_stdev))

    class_stats = {
        "rate": {
            "mean": rate_mean,
            "stdev": rate_stdev
        },
        "velocity": {
            "mean": velocity_mean,
            "stdev": velocity_stdev
        },
        "additions": {
            "mean": additions_mean,
            "stdev": additions_stdev
        },
        "deletions": {
            "mean": deletions_mean,
            "stdev": deletions_stdev
        }
    }

    suspicious_students = [s for s in student_stats if is_suspicious(student_stats[s], class_stats) ]
    suspicious_students = student_stats

    # Convert statistics into a single suspciousness score
    student_list = []
    for student in suspicious_students:
        student_stats = suspicious_students[student]
        rate = student_stats["rate"]
        velocity = student_stats["velocity"]
        additions = student_stats["additions"]
        deletions = student_stats["deletions"]

        # Convert rate to standard normal
        std_rate = (rate - rate_mean) / rate_stdev
        std_velocity = (velocity - velocity_mean) / velocity_stdev
        std_additions = (additions - additions_mean) / additions_stdev
        std_deletions = (deletions - deletions_mean) / deletions_stdev
        
        #Standardize combined metric (mean = 0, stdev = 2)
        score = (std_rate + std_velocity + std_additions/2 + std_deletions/2) / 3

        # Add student to the list
        student_list.append({
            "id": student,
            "score": score
        })
        
    return json.dumps(student_list)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "visiblefile", help="path to historic progress file for visible test cases"
    )
    parser.add_argument(
        "hiddenfile", help="path to historic progress file for hidden test cases"
    )
    parser.add_argument("logfile", help="path to log file")
    parser.add_argument("-t", "--timeout", help="time spent timeout")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")
    parser.add_argument(
        "-v",
        "--velocity",
        help="the maximum daily progress per hour spent before a student is flagged as suspicious",
    )
    parser.add_argument(
        "-r",
        "--rate",
        help="the maximum daily progress per commit before a student is flagged as suspicious",
    )
    parser.add_argument("-O", "--obfuscate", action="store_true", help="obfuscate flag")

    args = parser.parse_args()

    visible_file = open(args.visiblefile, "r")
    hidden_file = open(args.hiddenfile, "r")
    commit_log_file = open(args.logfile, "r")

    visible_progress = past_progress(visible_file)
    hidden_progress = past_progress(hidden_file)

    git_data = daily_data(
        commit_log_file, max_change=args.limit, timeout=args.timeout
    )

    api_json = jsonify(git_data, visible_progress, hidden_progress=hidden_progress)

    print(api_json)
