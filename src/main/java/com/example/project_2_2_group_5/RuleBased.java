package com.example.project_2_2_group_5;
import java.util.ArrayList;
import java.util.List;

public class RuleBased {

    public static String preprocessText(String text) {
        text = text.trim().replaceAll("\\s+", " ");
        text = text.replaceAll("\\.{2,}", ".");
        text = text.replaceAll("\\.{2,}", ".");
        text = text.replaceAll("\\s+", " ");
        return text;
    }

    public static List<SentenceScore> calculateSentenceScores(String text) {
        List<SentenceScore> scores = new ArrayList<>();
        String[] sentences = text.split("[.!?]");
        for (String sentence : sentences) {
            sentence = sentence.trim();
            int lengthScore = sentence.split("\\s+").length;
            int modalScore = containsModalVerbs(sentence) ? 2 : 0;
            int desireScore = containsDesireWords(sentence) ? 1 : 0;
            int totalScore = lengthScore + modalScore + desireScore;
            scores.add(new SentenceScore(sentence, totalScore));
        }
        return scores;
    }

    public static boolean containsModalVerbs(String sentence) {
        String[] modalVerbs = {"can", "could", "may", "might", "must", "shall", "should", "will", "would"};
        for (String modal : modalVerbs) {
            if (sentence.toLowerCase().contains(" " + modal + " ")) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsDesireWords(String sentence) {
        String[] desireWords = {"want", "need", "desire", "wish", "crave", "long for"};
        for (String word : desireWords) {
            if (sentence.toLowerCase().contains(" " + word + " ")) {
                return true;
            }
        }
        return false;
    }

    public static List<String> generateSummary(List<SentenceScore> scores, int summarySize) {
        scores.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        List<String> summary = new ArrayList<>();
        int remainder = summarySize;
        for (SentenceScore score : scores) {
            if (remainder == 0) {
                break;
            }
            summary.add(score.getSentence());
            remainder--;
        }
        return summary;
    }

    public static void summarize(String text) {

        text = preprocessText(text);

        List<SentenceScore> scores = calculateSentenceScores(text);

        int totalSentences = scores.size();
        int summarySize = Math.min(4, totalSentences);

        List<String> summarySentences = generateSummary(scores, summarySize);

        System.out.println("Summary:");
        StringBuilder summary = new StringBuilder();
        for (String sentence : summarySentences) {
            System.out.println(sentence);
            summary.append(sentence).append(" ");
        }
        CsvIO.writeToFile(String.valueOf(summary),"src/main/python/summary.csv");


    }
}

class SentenceScore {
    private String sentence;
    private int score;

    public SentenceScore(String sentence, int score) {
        this.sentence = sentence;
        this.score = score;
    }

    public String getSentence() {
        return sentence;
    }

    public int getScore() {
        return score;
    }
}
