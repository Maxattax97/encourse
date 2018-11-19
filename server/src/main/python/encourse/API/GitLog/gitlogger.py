from . import *
import operator


class GitLog:
    def __init__(self, gitlog):
        if all(isinstance(element, GitCommit) for element in gitlog):
            self.commits = gitlog
        else:
            self.commits = parselog(gitlog)
        self.time_estimate = self._estimate_time()

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
            hours = (commit.timedelta.seconds / 3600) 
            if hours < timeout:
                total_time += hours


class GitCommit:
    def __init__(self, files, timestamp, timedelta):
        self.files = files
        self.timestamp = timestamp
        self.timedelta = timedelta

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
        if timestamp is date:
            self._timestamp = timestamp

    timedelta = property(operator.attrgetter("_timedelta"))

    @timedelta.setter
    def timedelta(self, timedelta):
        print("setter called: timedelta")
        if timedelta is timedelta:
            self._timedelta = timedelta

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
