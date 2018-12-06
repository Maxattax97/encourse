from . import *
import operator


class GitParser:
    def __init__(self, student_log):
        self.student_log = {}
        if student_log:
            self.parse_students(student_log)

    def __str__(self):
        output = ""
        for student in self.student_log:
            log = self.student_log[student]
            output += "=====================================\n"
            output += student + "\n"
            output += str(log) + "\n\n"
        return output

    student_log = property(operator.attrgetter("_student_log"))

    @student_log.setter
    def student_log(self, student_log):
        if isinstance(student_log, dict):
            self._student_log = student_log

    def parse_students(self, student_log):
        student_name = ""
        student_commits = []

        commit_files = []
        commit_timestamp = None  # datetime.datetime
        commit_timedelta = None  # datetime.timedelta
        commit_hash = ""
        for line in student_log:
            if line[:5] == "Start":
                student_name = line[6:].strip()
                commit_files = []
                commit_timestamp = None  # datetime.datetime
                commit_timedelta = None  # datetime.timedelta
                commit_hash = ""
            elif line[:3] == "End":
                student_commits.append(
                    GitCommit(
                        commit_files, commit_timestamp, commit_timedelta, commit_hash
                    )
                )
                self.student_log[student_name] = GitLog(student_commits, student_name)
                student_commits = []
            elif len(line.split(" ")) == 4:
                # This line is a commit time
                words = line.strip().split(" ")
                # Check type of line
                time_string = words[0].strip() + " " + words[1].strip()
                timestamp = datetime.strptime(time_string, "%Y-%m-%d %H:%M:%S")
                if timestamp:
                    if commit_files:
                        # If a time is found, add the previously stored data as a commit
                        student_commits.append(
                            GitCommit(
                                commit_files,
                                commit_timestamp,
                                commit_timedelta,
                                commit_hash,
                            )
                        )
                    # Since a commit was just added, start tracking data for a new commit
                    commit_files = []
                    commit_hash = ""
                    if commit_timestamp:
                        commit_timedelta = timestamp - commit_timestamp
                    else:
                        # No previous timestamp exists (eg. the first commit)
                        commit_timedelta = timedelta(minutes=0)
                    commit_timestamp = timestamp
                    commit_hash = words[3].strip()

                    timestamp = None
            elif len(line.split("\t")) == 3:
                # This line is a commit file
                words = line.strip().split("\t")
                file_additions = (
                    int(words[0].strip()) if helper.is_number(words[0]) else 0
                )
                file_deletions = (
                    int(words[1].strip()) if helper.is_number(words[1]) else 0
                )
                file_name = words[2].strip()
                if file_additions > 0 or file_deletions > 0:
                    commit_files.append(
                        GitFile(file_name, file_additions, file_deletions)
                    )


class GitLog:
    def __init__(self, gitlog, username):
        self.username = username
        if all(isinstance(element, GitCommit) for element in gitlog):
            self.commits = gitlog
        else:
            self.commits = parselog(gitlog)
        self.time_estimate = self.estimate_time(self.commits)

    def __repr__(self):
        commits_repr = []
        for commit in self.commits:
            commits_repr.append(repr(commit))
        return repr([self.username, commits_repr])

    def __str__(self):
        output = "Git Log - {}: \n".format(self.username)
        for commit in self.commits:
            output += "\t" + str(commit) + "\n"
        return output

    def short_str(self):
        output = "Git Log - {}: \n".format(self.username)
        for commit in self.commits:
            if commit.additions + commit.deletions > 100:
                output += "\t" + str(commit) + "\n"
        return output

    username = property(operator.attrgetter("_username"))

    @username.setter
    def username(self, username):
        self._username = username

    commits = property(operator.attrgetter("_commits"))

    @commits.setter
    def commits(self, commits):
        # Check if commits are the right format
        if all(isinstance(element, GitCommit) for element in commits):
            self._commits = commits
            self._commits.sort(key=lambda x: x.timestamp)

    time_estimate = property(operator.attrgetter("_time_estimate"))

    @time_estimate.setter
    def time_estimate(self, time_estimate):
        self._time_estimate = time_estimate

    def count_changes(self, max_size=None):
        if not max_size:
            max_size = sys.maxsize
        additions = 0
        deletions = 0
        for commit in self.commits:
            commit_additions, commit_deletions = commit.count_changes(max_size=max_size)
            additions += commit_additions
            deletions += commit_deletions
        return additions, deletions

    def commits_by_day(self):
        """Generates a list of commit objects with aggregated date from every commit from that day"""
        current_date = None
        daily_commits = {}
        commits = []
        for commit in self.commits:
            if current_date == None:
                # This is the first commit
                current_date = commit.timestamp.date()
                commits.append(commit)
            elif commit.timestamp.date() != current_date:
                # Add commits to the dict under the date which they occurred
                daily_commits[current_date.isoformat()] = current_commits
                commits = []

                # Update the date and add the current commit to the now empty list
                current_date = commit.timestamp.date()
                commits.append(commit)
            elif commit.timestamp.date() == current_date:
                commits.append(commit)

        return daily_commits

    def commitsByDay_OLD(self):
        days = []
        current_date = 0
        current_day = {}
        current_commits = []
        first_commit = None
        last_commit = None
        for commit in self.commits:
            if commit.timestamp.date() != current_date:
                if current_date != 0:
                    current_day["time_spent"] = self.estimate_time(
                        current_commits, timeout=1
                    )
                    current_day["commit_count"] = len(current_commits)
                    current_day["first_commit"] = first_commit.commit_hash
                    current_day["last_commit"] = last_commit.commit_hash
                    days.append(current_day)

                if not first_commit:
                    first_commit = commit
                    last_commit = commit

                current_date = commit.timestamp.date()
                current_day = {
                    "timestamp": current_date,
                    "additions": 0,
                    "deletions": 0,
                    "files": [],
                    "time_spent": 0.0,
                    "commit_count": 1,
                    "first_commit": "",
                    "last_commit": "",
                }
                current_commits = [commit]
                first_commit = commit
                last_commit = commit
            else:
                current_commits.append(commit)
                current_day["additions"] += commit.additions
                current_day["deletions"] += commit.deletions

                if commit.timestamp < first_commit.timestamp:
                    first_commit = commit
                if commit.timestamp > last_commit.timestamp:
                    last_commit = commit

                for f in commit.files:
                    if f.name not in [n.name for n in current_day["files"]]:
                        current_day["files"].append(f)
                    else:
                        for match in commit.files:
                            if f.name == match.name:
                                match.additions += f.additions
                                match.deletions += f.deletions
                                break
        current_day["time_spent"] = self.estimate_time(current_commits, timeout=1)
        current_day["commit_count"] = len(current_commits)
        days.append(current_day)
        return days

    # TODO: Move to GitLog, add time range input
    def estimate_time(self, commits, timeout=None):
        total_time = 0
        if not timeout:
            timeout = sys.maxsize

        for commit in commits:
            # TODO: Improve time estimate heuristic
            hours = commit.time_delta.seconds / 3600
            if hours < timeout:
                total_time += hours
        return total_time

    def date_range(self):
        """Returns a list of every date with a commit
            
        **Returns**
            list: A list of datetime date objects representing every date between start
            and end (inclusive)

        """
        first = min([c.timestamp for c in self.commits])
        last = max([c.timestamp for c in self.commits])
        dates = []
        for n in range(int((last.day - first.day) + 1)):
            dates.append(first + timedelta(n))
        return dates


class GitCommit:
    def __init__(self, files, timestamp, time_delta, commit_hash):
        self.files = files  # Recounts changes in setter
        self.timestamp = timestamp
        self.time_delta = time_delta
        self.commit_hash = commit_hash

    def __repr__(self):
        return "GitCommit({}, {}, {})".format(
            repr(self.files), repr(self.timestamp), repr(self.time_delta)
        )

    def __str__(self):
        file_list = "Commit: " + self.timestamp.isoformat() + "\n"
        if self.files:
            max_filename = max([len(f.name) for f in self.files])
            for f in self.files:
                file_list += (
                    "\t"
                    + f.name
                    + ":"
                    + (" " * (max_filename - len(f.name)))
                    + "\t"
                    + "+{}\t-{}\n".format(f.additions, f.deletions)
                )
        return file_list

    files = property(operator.attrgetter("_files"))

    @files.setter
    def files(self, files):
        self._files = files
        self.additions, self.deletions = self.count_changes()

    timestamp = property(operator.attrgetter("_timestamp"))

    @timestamp.setter
    def timestamp(self, timestamp):
        if isinstance(timestamp, date):
            self._timestamp = timestamp
        else:
            print("timestamp is not a timestamp")

    time_delta = property(operator.attrgetter("_time_delta"))

    @time_delta.setter
    def time_delta(self, time_delta):
        # print("timedelta" + str(time_delta))
        if isinstance(time_delta, timedelta):
            self._time_delta = time_delta
        else:
            print("time_delta is not a time delta")
            self._time_delta = timedelta(0)

    additions = property(operator.attrgetter("_additions"))

    @additions.setter
    def additions(self, additions):
        if additions >= 0:
            self._additions = additions

    deletions = property(operator.attrgetter("_deletions"))

    @deletions.setter
    def deletions(self, deletions):
        if deletions >= 0:
            self._deletions = deletions

    commit_hash = property(operator.attrgetter("_commit_hash"))

    @commit_hash.setter
    def commit_hash(self, commit_hash):
        self._commit_hash = commit_hash

    def count_changes(self, max_size=None):
        # sum up additions and deletionsfor each file
        if not max_size:
            max_size = sys.maxsize

        additions = 0
        deletions = 0
        for gitfile in self.files:
            if gitfile.additions < max_size:
                additions += gitfile.additions
            if gitfile.deletions < max_size:
                deletions += gitfile.deletions
        return additions, deletions


class GitFile:
    def __init__(self, name, additions, deletions):
        self.name = name
        self.additions = additions
        self.deletions = deletions

    def __repr__(self):
        return "GitFile({}, {}, {})".format(
            repr(self.name), repr(self.additions), repr(self.deletions)
        )

    def __str__(self):
        return self.name + "\t" + "+{}\t-{}\n".format(f.additions, f.deletions)
