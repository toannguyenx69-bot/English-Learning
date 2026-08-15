#!/usr/bin/env python3
"""
Import Longman 3000 vocabulary JSON into MySQL.

Input JSON structure:
{
  "word":   {"0": "CD", "1": "DVD", ...},
  "wtypes": {"0": "n",  "1": "n",  ...},
  "freqs":  {"0": ["S3", "W3"], ...},
  "defs":   {"0": [...], "1": [...], ...},
  "example":{"0": [...], "1": [...], ...}
}

Target table:
vocabularies(
    id, word, meaning, pronunciation, part_of_speech,
    example, difficulty, created_at, updated_at
)

Install:
    pip install mysql-connector-python

PowerShell example:
    $env:DB_HOST="localhost"
    $env:DB_PORT="3306"
    $env:DB_NAME="english_learning"
    $env:DB_USER="admin"
    $env:DB_PASSWORD="YOUR_PASSWORD"

Run:
    python import_longman.py longman3000_vocabulary.json

Notes:
- Longman file does NOT contain pronunciation or CEFR difficulty.
- pronunciation is therefore imported as NULL.
- difficulty is a frequency-rank heuristic, NOT an official CEFR level:
      rank 1-1000   -> EASY
      rank 1001-2500 -> MEDIUM
      rank 2501+     -> HARD
- The file contains 3653 indexed entries but duplicate words across parts
  of speech. Because vocabularies.word is UNIQUE, duplicate word entries are
  merged into one row.
- Entries without definitions are skipped because meaning is NOT NULL.
"""

import argparse
import json
import os
from collections import OrderedDict
from datetime import datetime

import mysql.connector


POS_MAP = {
    "n": "noun",
    "v": "verb",
    "adj": "adjective",
    "adv": "adverb",
    "pron": "pronoun",
    "prep": "preposition",
    "conj": "conjunction",
    "interjection": "interjection",
    "modal": "modal",
    "determiner": "determiner",
    "number": "number",
    "indefinite article": "article",
    "definite article": "article",
    "auxiliary": "auxiliary",
}


def difficulty_from_rank(rank: int) -> str:
    """Heuristic only; this is not an official Longman/CEFR classification."""
    if rank <= 1000:
        return "EASY"
    if rank <= 2500:
        return "MEDIUM"
    return "HARD"


def clean_text(value):
    if value is None:
        return None
    return " ".join(str(value).replace("\u2003", " ").split()).strip()


def build_records(data):
    words = data["word"]
    wtypes = data.get("wtypes", {})
    freqs = data.get("freqs", {})
    definitions = data.get("defs", {})
    examples = data.get("example", {})

    # Ordered by the source index.
    grouped = OrderedDict()

    for key in sorted(words.keys(), key=lambda x: int(x)):
        word = clean_text(words[key])
        if not word:
            continue

        defs = definitions.get(key)
        exs = examples.get(key)

        if not defs:
            # meaning is NOT NULL in the DB.
            continue

        if not isinstance(defs, list):
            defs = [defs]

        if not isinstance(exs, list):
            exs = [] if exs is None else [exs]

        item = grouped.setdefault(
            word.lower(),
            {
                "word": word,
                "parts_of_speech": [],
                "definitions": [],
                "examples": [],
                "rank": int(key) + 1,
                "frequency": [],
            },
        )

        pos = POS_MAP.get(wtypes.get(key), wtypes.get(key))
        if pos and pos not in item["parts_of_speech"]:
            item["parts_of_speech"].append(pos)

        for d in defs:
            if isinstance(d, dict):
                definition = clean_text(d.get("definition"))
                example = clean_text(d.get("example"))
            else:
                definition = clean_text(d)
                example = None

            if definition and definition not in item["definitions"]:
                item["definitions"].append(definition)

            if example and example not in item["examples"]:
                item["examples"].append(example)

        for f in freqs.get(key, []) or []:
            if f not in item["frequency"]:
                item["frequency"].append(f)

    records = []

    for item in grouped.values():
        if not item["definitions"]:
            continue

        # DB meaning is VARCHAR(500). Keep the most useful definitions
        # while staying within the schema limit.
        meaning_parts = []
        current_len = 0
        for definition in item["definitions"]:
            candidate_len = len(definition) + (2 if meaning_parts else 0)
            if current_len + candidate_len > 500:
                break
            meaning_parts.append(definition)
            current_len += candidate_len

        if not meaning_parts:
            # A single very long definition: truncate safely.
            meaning_parts = [item["definitions"][0][:500]]

        meaning = "; ".join(meaning_parts)

        # Use the first available example. TEXT can hold much more, but
        # keeping one example keeps the initial dataset compact.
        example = item["examples"][0] if item["examples"] else None

        # part_of_speech is VARCHAR(30).
        pos = ", ".join(item["parts_of_speech"])[:30] or "unknown"

        records.append(
            (
                item["word"][:100],
                meaning,
                None,  # pronunciation: not present in the downloaded file
                pos,
                example,
                difficulty_from_rank(item["rank"]),
            )
        )

    return records


def get_connection():
    return mysql.connector.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "3306")),
        database=os.getenv("DB_NAME", "english_learning"),
        user=os.getenv("DB_USER", "admin"),
        password=os.getenv("DB_PASSWORD", ""),
    )


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("json_file", help="Path to longman3000_vocabulary.json")
    args = parser.parse_args()

    with open(args.json_file, "r", encoding="utf-8") as f:
        data = json.load(f)

    required = {"word", "wtypes", "freqs", "defs", "example"}
    missing = required - set(data.keys())
    if missing:
        raise ValueError(f"Missing JSON sections: {sorted(missing)}")

    records = build_records(data)

    print(f"Source entries: {len(data['word'])}")
    print(f"Rows prepared for MySQL: {len(records)}")

    conn = get_connection()
    cursor = conn.cursor()

    sql = """
        INSERT INTO vocabularies
            (word, meaning, pronunciation, part_of_speech,
             example, difficulty, created_at, updated_at)
        VALUES
            (%s, %s, %s, %s, %s, %s, NOW(), NOW())
        ON DUPLICATE KEY UPDATE
            meaning = VALUES(meaning),
            pronunciation = COALESCE(VALUES(pronunciation), pronunciation),
            part_of_speech = VALUES(part_of_speech),
            example = COALESCE(VALUES(example), example),
            difficulty = VALUES(difficulty),
            updated_at = NOW()
    """

    try:
        cursor.executemany(sql, records)
        conn.commit()
        print(f"Imported/updated rows: {cursor.rowcount}")
    except Exception:
        conn.rollback()
        raise
    finally:
        cursor.close()
        conn.close()

    print("Import completed successfully.")


if __name__ == "__main__":
    main()
