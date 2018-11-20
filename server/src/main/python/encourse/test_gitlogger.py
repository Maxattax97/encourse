#!/usr/bin/env python
from API.GitLog import *

files = [
    GitFile("main.cc", 27, 14),
    GitFile("shell.cc", 13, 1),
    GitFile("Makefile", 4, 0),
]
commit = GitCommit(files, date.today(), timedelta(minutes=-32))

files = [
    GitFile("main.cc", 24, 3),
    GitFile("shell.cc", 27, 2),
    GitFile("README", 1, 0),
]
commit2 = GitCommit(files, date.today() - timedelta(days=3), timedelta(minutes=-24))

gitlog = GitLog([commit, commit2])

print(repr(gitlog))
print(gitlog)
