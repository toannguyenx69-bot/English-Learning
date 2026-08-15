#!/usr/bin/env python3
import argparse
import json
import os
import sys
import mysql.connector

VALID_DIFFICULTIES = {"EASY", "NORMAL", "HARD"}

def load_json(path):
    with open(path, "r", encoding="utf-8") as f:
        data = json.load(f)
    if not isinstance(data, list):
        raise ValueError("JSON root must be an array.")
    return data

def validate_questions(questions):
    errors = []
    seen_questions = set()

    for i, item in enumerate(questions, 1):
        prefix = f"Question #{i}"

        if not isinstance(item, dict):
            errors.append(f"{prefix}: item must be an object.")
            continue

        question = item.get("question")
        explanation = item.get("explanation")
        difficulty = item.get("difficulty")
        topic = item.get("topic")
        answers = item.get("answers")

        if not isinstance(question, str) or not question.strip():
            errors.append(f"{prefix}: question is required.")
        if not isinstance(explanation, str) or not explanation.strip():
            errors.append(f"{prefix}: explanation is required.")
        if difficulty not in VALID_DIFFICULTIES:
            errors.append(f"{prefix}: difficulty must be EASY, NORMAL, or HARD.")
        if not isinstance(topic, str) or not topic.strip():
            errors.append(f"{prefix}: topic is required.")

        if isinstance(question, str):
            key = question.strip().lower()
            if key in seen_questions:
                errors.append(f"{prefix}: duplicate question.")
            seen_questions.add(key)

        if not isinstance(answers, list):
            errors.append(f"{prefix}: answers must be an array.")
            continue

        if len(answers) != 4:
            errors.append(f"{prefix}: expected exactly 4 answers, found {len(answers)}.")

        correct_count = 0
        seen_answers = set()

        for j, answer in enumerate(answers, 1):
            ap = f"{prefix}, answer #{j}"
            if not isinstance(answer, dict):
                errors.append(f"{ap}: must be an object.")
                continue

            text = answer.get("answer")
            correct = answer.get("correct")

            if not isinstance(text, str) or not text.strip():
                errors.append(f"{ap}: answer is required.")
            if not isinstance(correct, bool):
                errors.append(f"{ap}: correct must be true or false.")
            elif correct:
                correct_count += 1

            if isinstance(text, str):
                key = text.strip().lower()
                if key in seen_answers:
                    errors.append(f"{ap}: duplicate answer text.")
                seen_answers.add(key)

        if correct_count != 1:
            errors.append(
                f"{prefix}: expected exactly 1 correct answer, found {correct_count}."
            )

    if errors:
        raise ValueError(
            "Validation failed:\n" + "\n".join(f"  - {e}" for e in errors)
        )

def get_connection():
    return mysql.connector.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "3306")),
        database=os.getenv("DB_NAME", "english_learning"),
        user=os.getenv("DB_USER", "admin"),
        password=os.getenv("DB_PASSWORD", ""),
    )

def import_data(connection, questions):
    question_sql = """
        INSERT INTO grammar_questions
        (question, explanation, difficulty, topic, created_at, updated_at)
        VALUES (%s, %s, %s, %s, NOW(), NOW())
    """
    answer_sql = """
        INSERT INTO grammar_answers
        (question_id, answer, is_correct)
        VALUES (%s, %s, %s)
    """

    q_cursor = connection.cursor()
    a_cursor = connection.cursor()

    try:
        q_count = 0
        a_count = 0

        for item in questions:
            q_cursor.execute(
                question_sql,
                (
                    item["question"].strip(),
                    item["explanation"].strip(),
                    item["difficulty"],
                    item["topic"].strip(),
                ),
            )

            question_id = q_cursor.lastrowid

            rows = [
                (question_id, a["answer"].strip(), a["correct"])
                for a in item["answers"]
            ]
            a_cursor.executemany(answer_sql, rows)

            q_count += 1
            a_count += len(rows)

        return q_count, a_count
    finally:
        q_cursor.close()
        a_cursor.close()

def main():
    parser = argparse.ArgumentParser(
        description="Import grammar questions and answers into MySQL."
    )
    parser.add_argument("json_file", help="Path to grammar_questions.json")
    args = parser.parse_args()

    try:
        questions = load_json(args.json_file)
        print(f"Loaded questions: {len(questions)}")

        validate_questions(questions)
        print("Validation PASSED.")

        connection = get_connection()
        try:
            q_count, a_count = import_data(connection, questions)
            connection.commit()
            print("Import completed successfully.")
            print(f"Questions inserted: {q_count}")
            print(f"Answers inserted:   {a_count}")
        except Exception:
            connection.rollback()
            print("Import failed. Transaction rolled back.", file=sys.stderr)
            raise
        finally:
            connection.close()

    except Exception as e:
        print(f"ERROR: {e}", file=sys.stderr)
        sys.exit(1)

if __name__ == "__main__":
    main()
