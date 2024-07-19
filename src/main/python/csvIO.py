import os


# Method to read string from csv file
def read_file(input_file):
    with open(input_file, mode='r', encoding='utf-8') as file:
        text = file.read()
    return text


# Method to write string to csv file
def write_file(content, file_path, type):
    try:
        # Make sure directory exists
        directory = os.path.dirname(file_path)
        if directory:
            os.makedirs(directory, exist_ok=True)

        # Open the file in append mode
        with open(file_path, 'a', encoding='utf-8') as file:
            if type == "list":
                file.write(content + '\n')
            else:
                file.write(content)
    except IOError as e:
        print(f"An error occurred: {e}")

def isFileEmpty(file):
    # if file exists
    if not os.path.exists(file):
        return True
    # if file is empty
    return os.path.getsize(file) == 0

