from daily_git_data import get_daily_commit_dataas daily_data
from past_progress import past_progress
from get_velocity import jsonify as velocity

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("logfile", help="path to commit log file")
    parser.add_argument("progressfile", help="path to test case progress file")
    parser.add_argument("-l", "--limit", help="ignore file changes above limit")

    args = parser.parse_args()

    commit_data_file = open(args.logfile, "r")
    progress_file = open(args.progressfile, "r")

    data = (
        daily_data(commit_data_file, max_change=int(args.limit))
        if args.limit
        else daily_data(commit_data_file)
    )
    
    historic_scores = past_progress(progress_file)


    for student in data.keys():
        student_data = data[student]

        velocity_data = velocity(student_data, progress_file) 

