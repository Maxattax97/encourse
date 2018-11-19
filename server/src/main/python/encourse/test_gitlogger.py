#!/usr/bin/env python
from API.GitLog import *

files = [
    GitFile("main.cc", 20, 10),
    GitFile("main.cc", 20, 10),
    GitFile("main.cc", 20, 10),
]
commit = GitCommit(files, date.today(), timedelta(minutes=-32))

gitlog = GitLog([commit, commit])

print(gitlog.commits)
