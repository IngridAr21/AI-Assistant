import re
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize

from csvIO import write_file

# Function to check if a sentence contains verbs
def contains_verbs(sentence, nlp):
    #tokenize the sentence
    doc = nlp(sentence)
    #do a for loop to check if the token is a verb or not
    for token in doc:
        #if the token is a verb, then return true
        if token.pos_ == "VERB":
            return True
    #if it is not a verb, then return false
    return False


# Function to process to-do lists
def process_todo(nlp,text):
    # Get English stopwords
    stop_words = set(stopwords.words('english'))

    # Split text into sentences
    sentences = re.split(r'(?<!\w\.\w.)(?<![A-Z][a-z]\.)(?<=\.|\?)\s', text)

    #initalize the filtered sentences
    filtered_sentences = []

    #initalize sentence dictionary
    sentence_dictionary = {"work": [], "personal": []}

    #do a for loop to go throug each sentence
    for sentence in sentences:
        # Cleaning data
        sentence = sentence.lower()
        sentence = re.sub("\'s", " ", sentence)
        sentence = re.sub("\'ve", " have ", sentence)
        sentence = re.sub("\'t", " not", sentence)
        sentence = re.sub("\'re", " are", sentence)
        sentence = re.sub("\'d", " would", sentence)
        sentence = re.sub("\'ll", " will", sentence)
        sentence = sentence.strip()
        additional_words = set(['think', 'will', 'might', 'need'])


        # Tokenizing words
        words = word_tokenize(sentence)
        #check if the word is not in the stop words
        if any(word == 'not' for word in words):
            #then continue
            continue

        # Filtering out stopwords
        filtered_words = [word for word in words if word.lower() not in stop_words.union(additional_words)]
        #join the filtered words
        filtered_sentence = ' '.join(filtered_words)
        #split the filtered sentence
        text = filtered_sentence.split()
        #if the filtered sentence contains verbs and the length is greater than 2, 
        if contains_verbs(filtered_sentence, nlp) and len(text) > 2:
            #then append the filtered sentence to list of the filtered sentences
            filtered_sentences.append(filtered_sentence)
    #print the list of the filtered sentences
    print(filtered_sentences)
    output_file = "todo.csv"
    # Print filtered sentences
    for sentence in filtered_sentences:
        #classify and extract the sentence
        context = classify_and_extract(nlp,sentence)
        #append this to the sentence dictionary
        sentence_dictionary[context].append(sentence)

    # Print sentences by context
    for context, sentences in sentence_dictionary.items():
        print(sentence_dictionary)
        print(f"{context.capitalize()} context:")
        for sentence in sentences:
            print("Printing this sentence " + sentence)
            write_file(sentence,output_file,"list")

    return filtered_sentences


# Function to classify dialogues and extract to-do lists
def classify_and_extract(nlp,sentence):
    #tokenize the sentence
    doc = nlp(sentence)

    #extract entities
    entities = [ent.label_ for ent in doc.ents]
    print(f"Sentence: {sentence}")
    print(f"Entities: {entities}")

    #check if entities are in the sentence
    if 'ORG' in entities or 'WORK_OF_ART' in entities or 'MONEY' in entities or 'GPE' in entities or 'PRODUCT' in entities or 'EVENT' in entities:
        context = "work"
    else:
        context = "personal"

    return context