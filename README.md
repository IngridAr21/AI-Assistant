# Conversational AI Task Extraction Assistant

Turn your audio conversations into clear summaries and actionable to-do lists — an easy way to save time and stay organized.


## Features

- **Speech Recognition:** Converts audio into text.  
- **Task Extraction:** Finds and organizes tasks from conversations.  
- **Summarization:** Creates quick summaries of dialogues.  
- **To-do Lists:** Generates task lists automatically based on what’s said.

## Overview

This tool listens to your conversations and pulls out important tasks and summaries. It works well even if the audio isn’t perfect, like with background noise or multiple speakers.


## How It Works

We use the Whisper speech recognition model from Hugging Face to transcribe audio into text. After transcription, the system identifies and categorizes tasks in the dialogue using natural language processing and rule-based methods.


## Tech & Tools

- Python  
- Hugging Face Transformers (for speech-to-text)  
- Librosa (audio processing)  
- Scikit-learn (K-means clustering for speaker separation, optional)  

The pipeline flows from audio input → transcription → task extraction → output.


## Setup & Usage

1. Install required libraries and models (details to be added).  
2. Open a terminal in the project folder and run:

   ```bash
   python main.py
   ````

3. Wait until you see:

   ```
   * Debugger is active! Debugger PIN: ###-###-###
   ```

4. Run `MainPage.java` to open the interface.

5. Click the audio input button to upload an audio file and wait for it to process (terminal shows "Speech to Text Done!").

6. Use the Text File button to review or edit the transcript and add speaker names if needed.

7. Select a summary type and click the summary button. *(Extractive summaries require speaker segmentation.)*

8. Pick a to-do list type and generate your task list.


## Example Input Dialogue

```
Jack: I want to go to the mall.  
Phil: I will pick you up.
```


## Screenshots & Demo

*(Coming soon!)*

