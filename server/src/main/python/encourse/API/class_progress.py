from API import *


# TODO: Reduce code length by generating dict keys from
#       test totals (instead of using elif).
# TODO: Then merge jsonify functions
def jsonify(visible_data, hidden_data):
    """Formats data for /classProgress endpoint

    Converts information in **data** into an appropriately formatted json 
    for the /classProgress endpoint     

    **Args**:
        **data** (dict): A dictionary of the following format: ::

            {
               "name1": {
                    "Test1": (bool, float),
                    ...
               },
               ...
            }

    **Return**:
        dict: A dictionary of histogram data split into 10% bins: ::

            {
                "0-10%": int,
                "10-20%": int,
                "20-30%": int,
                "30-40%": int,
                "40-50%": int,
                "50-60%": int,
                "60-70%": int,
                "70-80%": int,
                "80-900%": int
                "90-100%": int,
            }

        where each percentage bin contains a value 0 <= int <= 100

    """
    test_data = merge_inputs(visible_data, hidden_data)
    histogram_data = {
        "0-10%": 0,
        "10-20%": 0,
        "20-30%": 0,
        "30-40%": 0,
        "40-50%": 0,
        "50-60%": 0,
        "60-70%": 0,
        "70-80%": 0,
        "80-90%": 0,
        "90-100%": 0,
    }
    for student in test_data:
        info = test_data[student]
        if info["total"] <= 10:
            histogram_data["0-10%"] += 1
        elif info["total"] <= 20:
            histogram_data["10-20%"] += 1
        elif info["total"] <= 30:
            histogram_data["20-30%"] += 1
        elif info["total"] <= 40:
            histogram_data["30-40%"] += 1
        elif info["total"] <= 50:
            histogram_data["40-50%"] += 1
        elif info["total"] <= 60:
            histogram_data["50-60%"] += 1
        elif info["total"] <= 70:
            histogram_data["60-70%"] += 1
        elif info["total"] <= 80:
            histogram_data["70-80%"] += 1
        elif info["total"] <= 90:
            histogram_data["80-90%"] += 1
        elif info["total"] <= 100:
            histogram_data["90-100%"] += 1
    return json.dumps(histogram_data)


def jsonify_with_names(visible_data, hidden_data):
    """Formats data for /classProgress endpoint

    Converts information in **data** into an appropriately formatted json 
    for the /classProgress endpoint     

    **Args**:
        **data** (dict): A dictionary of the following format: ::

            {
               "name1": {
                    "Test1": (bool, float),
                    ...
               },
               ...
            }

    **Return**:
        dict: A dictionary of histogram data split into 10% bins: ::

            {
                "0-10%": int,
                "10-20%": int,
                "20-30%": int,
                "30-40%": int,
                "40-50%": int,
                "50-60%": int,
                "60-70%": int,
                "70-80%": int,
                "80-900%": int
                "90-100%": int,
            }

        where each percentage bin contains a value 0 <= int <= 100

    """
    test_data = merge_inputs(visible_data, hidden_data)
    histogram_data = {
        "0-10%": [],
        "10-20%": [],
        "20-30%": [],
        "30-40%": [],
        "40-50%": [],
        "50-60%": [],
        "60-70%": [],
        "70-80%": [],
        "80-90%": [],
        "90-100%": [],
    }

    # Since we are iterating through keys of a dictionary,
    # we do not need to worry about appending duplicates to the bins
    for student in test_data:
        info = test_data[student]
        if info["total"] <= 10:
            histogram_data["0-10%"].append(student)
        elif info["total"] <= 20:
            histogram_data["10-20%"].append(student)
        elif info["total"] <= 30:
            histogram_data["20-30%"].append(student)
        elif info["total"] <= 40:
            histogram_data["30-40%"].append(student)
        elif info["total"] <= 50:
            histogram_data["40-50%"].append(student)
        elif info["total"] <= 60:
            histogram_data["50-60%"].append(student)
        elif info["total"] <= 70:
            histogram_data["60-70%"].append(student)
        elif info["total"] <= 80:
            histogram_data["70-80%"].append(student)
        elif info["total"] <= 90:
            histogram_data["80-90%"].append(student)
        elif info["total"] <= 100:
            histogram_data["90-100%"].append(student)
    return json.dumps(histogram_data)


def merge_inputs(visible, hidden):
    """Merge test results per student from two input sources"""
    merged = visible
    # Add hidden values to merged
    for key in hidden:
        if key in merged:
            merged[key]["tests"].update(hidden[key]["tests"])
        else:
            merged[key]["tests"] = hidden[key]["tests"]
    # TODO: Recalculate totals for each student
    return merged


def merge_data(visible, hidden):
    """Sums the values in **visible** and **hidden** for each bin"""
    visible = json.loads(visible)
    hidden = json.loads(hidden)
    for key in visible:
        visible[key] += hidden[key]
    return json.dumps(visible)


def jsonprint(args):
    visible_test_score_file = args.visible
    hidden_test_score_file = args.hidden

    visible_data = Progress.currentprogress.progress_from_file(visible_test_score_file)
    hidden_data = Progress.currentprogress.progress_from_file(hidden_test_score_file)

    formatted_data = jsonify_with_names(visible_data, hidden_data)
    api_json = formatted_data
    print(api_json)
