from . import *
import operator


class GitLogger:
    def __init__(self, gitlog):
        self.commits = parselog(gitlog)

    def parselog(self, gitlog):
        pass


class GitCommit:
    def __init__(self, files, timestamp, timedelta):
        self.files = files
        self.timestamp = timestamp
        self.timedelta = timedelta
        # self.time_estimate = self._estimate_time()

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

    # TODO: Move to GitLog, add time range input
    def _estimate_time(self, timeout=None):
        if not timeout:
            timeout = sys.maxsize
        # TODO: Imporve time estimate heuristic
        if (self.timedelta.seconds / 3600) < timeout:
            return self.timedelta


class GitFile:
    def __init__(self, name, additions, deletions):
        self.name = name
        self.additions = additions
        self.deletions = deletions
