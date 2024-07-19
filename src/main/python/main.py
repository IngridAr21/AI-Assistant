import nltk
import spacy as spacy
import torch
from transformers import AutoModelForSpeechSeq2Seq, AutoProcessor, pipeline
from flask import Flask, jsonify, request
from transformers import pipeline

from extractiveSummarizer import ExtractiveSummarizer
from summaryLogic import summarize_conversation
from todoLogic import process_todo
from csvIO import read_file
from csvIO import write_file
from NewSpeechRecognition import speech
from ruleBasedTodo import rule_based_summarize


app = Flask(__name__)
summarizer = pipeline("summarization", model="philschmid/bart-large-cnn-samsum")
nltk.download('punkt')
nltk.download('averaged_perceptron_tagger')
nlp = spacy.load("en_core_web_sm")

device = "cuda:0" if torch.cuda.is_available() else "cpu"
torch_dtype = torch.float16 if torch.cuda.is_available() else torch.float32
model_id = "openai/whisper-large-v3"
model = AutoModelForSpeechSeq2Seq.from_pretrained(
    model_id, torch_dtype=torch_dtype, low_cpu_mem_usage=True, use_safetensors=True
)
model.to(device)
processor = AutoProcessor.from_pretrained(model_id)
pipe = pipeline(
    "automatic-speech-recognition",
    model=model,
    tokenizer=processor.tokenizer,
    feature_extractor=processor.feature_extractor,
    max_new_tokens=128,
    chunk_length_s=30,
    batch_size=16,
    return_timestamps=True,
    torch_dtype=torch_dtype,
    device=device,
)

@app.route('/speech', methods=['POST'])
def handle_speech():
    try:
        data = request.get_json()
        file_path = data.get('file_path')
        mode = data.get('mode')

        processed_data = speech(file_path, mode, pipe)
        return jsonify({'result': processed_data}), 200
    except Exception as e:
        app.logger.error(f"Error occurred: {e}", exc_info=True)
        return jsonify({'error': str(e)}), 500

@app.route('/todo', methods=['POST'])
def handle_todo():
    try:
        file = "summary.csv"
        text = read_file(file)

        # Process text
        processed_data = process_todo(nlp,text)

        return jsonify({'result': processed_data}), 200
    except Exception as e:
        app.logger.error(f"Error occurred: {e}", exc_info=True)
        return jsonify({'error': str(e)}), 500


@app.route('/summary', methods=['POST'])
def handle_summary():
    try:
        file = "textfile.csv"
        text = read_file(file)

        # Process text
        processed_data = summarize_conversation(summarizer, text)

        return jsonify({'result': processed_data}), 200
    except Exception as e:
        app.logger.error(f"Error occurred: {e}", exc_info=True)
        return jsonify({'error': str(e)}), 500
    
    
@app.route('/extractive', methods=['POST'])
def handle_extractive_summary():
    try:
        file = "textfile.csv"
        text = read_file(file)
        
        ExctSummarizer = ExtractiveSummarizer()
        
        summary = ExctSummarizer.summarize(text)
        print(summary)
        write_file(summary, "summary.csv", "summary")
        return jsonify({'result': summary}),200 # DO i also have to return a # here? like 200 and 500 above
    except Exception as e:
        app.logger.error(f"Error occurred: {e}", exc_info=True)
        return jsonify({'error': str(e)}), 500

@app.route('/rulebased', methods=['POST'])
def handle_rule_based_todo():
    try:
        file = "summary.csv"
        text = read_file(file)

        summary = rule_based_summarize(text)
        print(summary)
        write_file(summary, "todo.csv", "list")
        return jsonify({'result': summary}),200
    except Exception as e:
        app.logger.error(f"Error occurred: {e}", exc_info=True)
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
