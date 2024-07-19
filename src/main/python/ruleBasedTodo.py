from typing import List
import re
from transformers import pipeline
from sklearn.cluster import DBSCAN
from transformers import BertTokenizer, BertModel
import torch
import numpy as np


# Rule-Based Summarizer class
class SentenceScore:
    def __init__(self, sentence: str, score: int, index: int):
        self.sentence = sentence
        self.score = score
        self.index = index

    def get_sentence(self) -> str:
        return self.sentence

    def get_score(self) -> int:
        return self.score

    def get_index(self) -> int:
        return self.index

class RuleBased:

    @staticmethod
    def preprocess(text: str) -> str:
        text = text.strip().replace(r'\s+', ' ')
        text = re.sub(r'\.{2,}', '.', text)
        text = re.sub(r'\s+', ' ', text)
        return text

    @staticmethod
    def calculate_sentence_scores(text: str) -> List[SentenceScore]:
        scores = []
        sentences = re.split(r'[.!?]', text)
        for index, sentence in enumerate(sentences):
            sentence = sentence.strip()
            if not sentence:
                continue

            length_score = len(sentence.split())
            modal_score = 2 if RuleBased.contains_modal(sentence) else 0
            desire_score = 1 if RuleBased.contains_desire(sentence) else 0
            total_score = length_score + modal_score + desire_score
            scores.append(SentenceScore(sentence, total_score, index))
        return scores

    @staticmethod
    def contains_modal(sentence: str) -> bool:
        modal_verbs = ["can", "could", "may", "might", "must", "shall", "should", "will", "would"]
        modal_contractions = ["can't", "couldn't", "mayn't", "mightn't", "mustn't", "shan't", "shouldn't", "won't", "wouldn't"]
        for modal in modal_verbs:
            if f" {modal} " in sentence.lower():
                return True
        for contraction in modal_contractions:
            if f" {contraction} " in sentence.lower():
                return True
        return False

    @staticmethod
    def contains_desire(sentence: str) -> bool:
        desire_words = ["want", "need", "desire", "wish", "crave", "long for"]
        for word in desire_words:
            if f" {word} " in sentence.lower():
                return True
        return False

    @staticmethod
    def generate_summary(scores: List[SentenceScore], summary_size: int) -> List[str]:
        scores.sort(key=lambda s: s.get_score(), reverse=True)
        top_scores = scores[:summary_size]
        top_scores.sort(key=lambda s: s.get_index())  # Maintain original order
        summary = []
        for score in top_scores:
            summary_sentence = score.get_sentence().replace(':', ' said')
            summary.append(summary_sentence)
        return summary

def rule_based_summarize(text: str) -> str:
    text = RuleBased.preprocess(text)
    scores = RuleBased.calculate_sentence_scores(text)
    total_sentences = len(scores)
    summary_size = max(3, total_sentences // 2)  # Minimum 3 sentences, or half the total sentences
    summary_sentences = RuleBased.generate_summary(scores, summary_size)
    summary = " ".join(summary_sentences)
    return summary