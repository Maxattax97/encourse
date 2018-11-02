from daily_git_data import get_daily_commit_data as daily_data
from past_progress import past_progress
from get_velocity import jsonify as get_velocity
from helper import eprint
from helper import times_from_dailydata as times

import argparse
import json
import statistics


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

    return json.dumps(class_stats, indent=2)

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
