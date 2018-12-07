from API import *


def jsonify(data):
    out = []
    bins = {}
    for username1, user_data in data.items():
        for username2, identical_count in user_data.items():
            similarity_bin = identical_count // 5

            if similarity_bin not in bins:
                bins[similarity_bin] = 0
            bins[similarity_bin] += 1

            item = {
                "user1": username1,
                "user2": username2,
                "similarity": identical_count,
                "similarity_bin": similarity_bin,
                "height": bins[similarity_bin],
            }

            out.append(item)

    return json.dumps(out)


def get_identical_count(time_file):
    """ Converts diff git project folder diff data to python dictionary

    Uses git diff and diff to create a list comparing every user with every other user.

    **Args**:
        **file** (file): A specially formatted file generated from a bash script.
        A small example of the formatting follows: ::
            
            user1:user2;55_user3;245_user4;60
            user2:user1;55_user3;83_user4;67
            user3:user1;245_user2;83_user4;90
            user4:user1;60_user2;67_user3;90

    **Returns**:
        dict: A dictionary that shows the similarity between two users.
        The dictionary is of the following form: ::
         
            {
                "user1": {
                    "user2": int
                }
                ...
            }

    """

    data = {}

    for line in time_file:
        line = line.lstrip(" ").rstrip("\n")
        username, other_users = line.split(":")
        data[username] = {}

        other_users = other_users.split("_")

        for other_user in other_users:
            # Handle empty lines
            if ";" not in other_user:
                break
            other_username, identical_count = other_user.split(";")
            data[username][other_username] = int(identical_count)

    return data


def jsonprint(args):
    diff_file = args.diff_file

    data = get_identical_count(diff_file)
    formatted_data = jsonify(data)
    print(formatted_data)
