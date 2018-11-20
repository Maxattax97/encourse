from . import *
import operator


class GitLog:
    def __init__(self, gitlog):
        if all(isinstance(element, GitCommit) for element in gitlog):
            self.commits = gitlog
        else:
            self.commits = parselog(gitlog)
        self.time_estimate = self._estimate_time()

    def __repr__(self):
        commits_repr  = []
        for commit in self.commits:
            commits_repr.append(repr(commit))
        return repr(commits_repr)

    def __str__(self):
        output = "Git Log: \n"
        for commit in self.commits:
            output += "\t" + str(commit) + "\n"
        return output

    commits = property(operator.attrgetter("_commits"))

    @commits.setter
    def commits(self, commits):
        print("setter called: commits")
        # Check if commits are the right format
        if all(isinstance(element, GitCommit) for element in commits):
            self._commits = commits

    time_estimate = property(operator.attrgetter("_time_estimate"))

    @time_estimate.setter
    def time_estimate(self, time_estimate):
        print("setter called: time_estimate")
        self._time_estimate = time_estimate

    def parselog(self, gitlog):
        pass

    # TODO: Move to GitLog, add time range input
    def _estimate_time(self, timeout=None):
        total_time = 0
        for commit in self.commits:
            if not timeout:
                timeout = sys.maxsize
            # TODO: Improve time estimate heuristic
            hours = (commit.time_delta.seconds / 3600) 
            if hours < timeout:
                total_time += hours


class GitCommit:
    def __init__(self, files, timestamp, time_delta):
        self.files = files
        self.timestamp = timestamp
        self.time_delta = time_delta

    def __repr__(self):
        return "GitCommit({}, {}, {})".format(repr(self.files), repr(self.timestamp), repr(self.time_delta)) 

    def __str__(self):
        file_list = "Commit: " + self.timestamp.isoformat() + "\n"
        max_filename = max([len(f.name) for f in self.files])
        for f in self.files:
            file_list += "\t" + f.name + ":" + (" " * (max_filename - len(f.name))) + "\t" + "+{}\t-{}\n".format(f.additions, f.deletions)
        return file_list

    files = property(operator.attrgetter("_files"))

    @files.setter
    def files(self, files):
        print("setter called: files")
        self._files = files
        self._count_changes()

    timestamp = property(operator.attrgetter("_timestamp"))

    @timestamp.setter
    def timestamp(self, timestamp):
        print("setter called: timestamp")
        print(type(timestamp))
        if isinstance(timestamp, date):
            print("hi")
            self._timestamp = timestamp

    time_delta = property(operator.attrgetter("_time_delta"))

    @time_delta.setter
    def time_delta(self, time_delta):
        print("setter called: time_delta")
        if isinstance(time_delta, timedelta):
            self._time_delta = time_delta

    additions = property(operator.attrgetter("_additions"))

    @additions.setter
    def additions(self, additions):
        print("setter called: additions")
        if additions >= 0:
            self._additions = additions

    deletions = property(operator.attrgetter("_deletions"))

    @deletions.setter
    def deletions(self, deletions):
        print("setter called: deletions")
        if deletions >= 0:
            self._deletions = deletions

    def _count_changes(self):
        # sum up additions and deletionsfor each file
        self.additions = 0
        self.deletions = 0
        for gitfile in self.files:
            self.additions += gitfile.additions
            self.deletions += gitfile.deletions


class GitFile:
    def __init__(self, name, additions, deletions):
        self.name = name
        self.additions = additions
        self.deletions = deletions

    def __repr__(self):
        return "GitFile({}, {}, {})".format(repr(self.name), repr(self.additions), repr(self.deletions))
