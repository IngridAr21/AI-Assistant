from transformers import BertTokenizer, BertModel
import torch
from sklearn.cluster import DBSCAN
import numpy as np
import re




class ExtractiveSummarizer:
    def __init__(self, model_name='bert-base-uncased', eps=0.3, min_samples=1):
        self.tokenizer = BertTokenizer.from_pretrained(model_name)
        self.model = BertModel.from_pretrained(model_name)
        self.eps = eps
        self.min_samples = min_samples

    def preprocess_text(self, text):
        text = re.sub(r'\W+', ' ', text)  # Remove non-alphanumeric characters
        text = text.lower()  # Convert to lowercase
        return text

    def get_sentence_embeddings(self, sentences):
        inputs = self.tokenizer(sentences, return_tensors='pt', padding=True, truncation=True)
        with torch.no_grad():
            outputs = self.model(**inputs)
        return outputs.last_hidden_state.mean(dim=1)

    def cluster_sentences_with_speakers(self, dialogue):
        # Separate dialogue into sentences and speakers
        sentences = []
        speakers = []
        for utterance in dialogue:
            split_utterance = utterance.split(": ", 1)
            if len(split_utterance) == 2:
                speaker, sentence = split_utterance
                sentences.append(sentence.strip())
                speakers.append(speaker.strip())
            else:
                print(f"Warning: Skipping utterance '{utterance}' due to missing delimiter ': '")

        num_sentences = len(sentences)
        if num_sentences == 0:
            print("Warning: No valid sentences found in the dialogue.")
            return dialogue  # Return the original dialogue without clustering

        # Get embeddings for each sentence in the dialogue
        embeddings = self.get_sentence_embeddings(sentences)
        embeddings_np = embeddings.numpy()

        # Perform DBSCAN clustering
        dbscan = DBSCAN(eps=self.eps, min_samples=self.min_samples, metric='cosine')
        labels = dbscan.fit_predict(embeddings_np)

        # Create a dictionary to store sentences for each cluster
        clustered_sentences = {}
        for idx, label in enumerate(labels):
            if label not in clustered_sentences:
                clustered_sentences[label] = []
            clustered_sentences[label].append((speakers[idx], sentences[idx], embeddings_np[idx]))

        # Generate summary sentences from each cluster
        summary_sentences = []
        temp=0
        for cluster, sentences_info in clustered_sentences.items():
            print(temp)
            temp = temp+1
            if cluster == -1:
                continue  # Skip noise points
            if sentences_info:  # Check if the cluster is non-empty
                # Calculate the centroid of the cluster
                cluster_embeddings = np.array([info[2] for info in sentences_info])
                centroid = cluster_embeddings.mean(axis=0)

                # Find the sentence closest to the centroid
                distances = np.linalg.norm(cluster_embeddings - centroid, axis=1)
                closest_idx = distances.argmin()
                representative_sentence = sentences_info[closest_idx][:2]
                print("Here...",representative_sentence)
                summary_sentences.append(representative_sentence)

        sum_sentences = []
        idx = 0

        for idx, utterance in enumerate(sentences):
            if len(sum_sentences) == len(summary_sentences):
                continue
            for text in summary_sentences:
                if utterance in text:
                    sum_sentences.append((speakers[idx], utterance))

        return sum_sentences

    def summarize(self, dialogues):
        print(dialogues)
        summaries = []
        templist = []
        templist.append(dialogues)
        for idx, dialogue in enumerate(templist):
            print(f"Dialogue {idx + 1}:")
            print("Dialogue: " + dialogue)
            dialogue = dialogue.splitlines()
            print(dialogue)
            summary = self.cluster_sentences_with_speakers(dialogue)

            print("Summary:")
            text = ""
            for speaker, sentence in summary:
                print(f"{speaker}: {sentence}")
                text = text + f"{speaker}: {sentence}\n"
            print()

            summaries.append(summary)
        return text

