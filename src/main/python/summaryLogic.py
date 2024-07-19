import re

from csvIO import write_file



def summarize_conversation(summarizer, conversation):
    print(conversation)
    output_file = "summary.csv"
    # Generate the summary
    text = conversation.split()
    summary = summarizer(conversation, max_length = int(len(text)), min_length = int(len(text)/3))
    summary_text = summary[0]['summary_text']

    # Clean up the summary text
    summary_text = re.sub(r'\s+', ' ', summary_text).strip()
    summary_text = re.sub(r'\.\s*\.$', '.', summary_text)

    # Write the summary to the output file
    write_file(summary_text, output_file, 'summary')
    print(summary_text)

    return summary_text

