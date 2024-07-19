import soundfile as sf
import numpy as np
import pyaudio
import librosa
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from csvIO import write_file


def recognize_audio(filePath, pipe):
    # Read the audio file
    audio_input, sample_rate = sf.read(filePath)

    # Pipeline requires single channel audio
    if len(audio_input.shape) > 1:
        audio_input = np.mean(audio_input, axis=1)

    # Process the audio file 
    result = pipe({"array": audio_input, "sampling_rate": sample_rate})
    write_file(result["text"], "textfile.csv", "Audio")
    print("Speech to Text Done!")

def recognize_liveSpeech(pipe):

    CHUNK = 1024
    FORMAT = pyaudio.paInt16
    CHANNELS = 1
    RATE = 16000
    RECORD_SECONDS = 15  # Adjust duration of the recording
    p = pyaudio.PyAudio()

    stream = p.open(format=FORMAT,
                    channels=CHANNELS,
                    rate=RATE,
                    input=True,
                    frames_per_buffer=CHUNK)

    print("Recording...")

    frames = []

    for i in range(0, int(RATE / CHUNK * RECORD_SECONDS)):
        data = stream.read(CHUNK)
        frames.append(np.frombuffer(data, dtype=np.int16))

    print("Done recording!!!")

    stream.stop_stream()
    stream.close()
    p.terminate()

    audio_input = np.concatenate(frames)
    sample_rate = RATE

    
    result = pipe({"array": audio_input, "sampling_rate": sample_rate})
    print(result["text"])

# Process for speaker diarization (not connected)
def identifySpeakers(filePath, Speakers):
    audio, sr = sf.read(filePath)
    mfccs = librosa.feature.mfcc(y = audio, sr=sr)

    scaler = StandardScaler()
    mfccs_scaled = scaler.fit_transform(mfccs.T)
    kmeans = KMeans(Speakers)  
    speaker_labels = kmeans.fit_predict(mfccs_scaled)
    segmentTimes = librosa.frames_to_time(np.arange(len(audio)), sr=sr)

    # Essential for assigning speaker labels
    segments = []
    for i, label in enumerate(speaker_labels):
        segment_info = {
            "Time Segment": i,
            "Speaker": label,
            "timestamp": (segmentTimes[i], segmentTimes[i + 1] if i + 1 < len(segmentTimes) else segmentTimes[i])
        }
        segments.append(segment_info)

    return segments

def assignSpeakerLabels(segmentsDialogue, segmentsSpeakers):
    speakersAssigned = []

    # Identify by segments duration
    for segment1 in segmentsDialogue:
        start1 = segment1['timestamp'][0]
        end1 = segment1['timestamp'][1]

        speaker_count = {}
        for segment2 in segmentsSpeakers:
            start2 = segment2['timestamp'][0]
            end2 = segment2['timestamp'][1]

            #print(start1)
            #print(end1)
            #print(start2)
            #print(end2)
            if start1 >= start2 and end1 <= end2:
                speaker = segment2['Speaker']
                if speaker in speaker_count:
                    speaker_count[speaker] += 1
                else:
                    speaker_count[speaker] = 1
        
        # Most common speaker label
        if speaker_count:
            assignedLabel = max(speaker_count, key = speaker_count.get)
        else:
            assignedLabel = -1  # No speaker label found

        speakersAssigned.append(assignedLabel)

    return speakersAssigned

def speech(file, mode, pipe):
    if mode == "1":
        recognize_liveSpeech(pipe)
    elif mode == "2":
        recognize_audio(file, pipe)
        #speakerLabels = identifySpeakers(file, 2)
        #speakerLabelsAssigned = assignSpeakerLabels(segments, speakerLabels)
    else:
        print("Invalid mode selected")
