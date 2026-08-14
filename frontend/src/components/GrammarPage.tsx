import React, { useEffect, useState } from 'react';
import { getGrammarQuestion, GrammarAnswerFeedback, GrammarQuestion, submitGrammarAnswer } from '../api/grammar';

const START_QUESTION_ID = 1;

export default function GrammarPage() {
  const [questionId, setQuestionId] = useState<number>(START_QUESTION_ID);
  const [question, setQuestion] = useState<GrammarQuestion | null>(null);
  const [selectedAnswerId, setSelectedAnswerId] = useState<number | null>(null);
  const [feedback, setFeedback] = useState<GrammarAnswerFeedback | null>(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [score, setScore] = useState(0);
  const [answeredCount, setAnsweredCount] = useState(0);

  const loadQuestion = async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getGrammarQuestion(id);
      setQuestion(data);
      setSelectedAnswerId(null);
      setFeedback(null);
    } catch (err) {
      setQuestion(null);
      setError(err instanceof Error ? err.message : 'Unable to load grammar question.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadQuestion(questionId);
  }, [questionId]);

  const handleAnswerSelect = async (answerId: number) => {
    if (!question || submitting || selectedAnswerId !== null) {
      return;
    }

    setSelectedAnswerId(answerId);
    setSubmitting(true);
    setError(null);

    try {
      const result = await submitGrammarAnswer(question.id, answerId);
      setFeedback(result);
      setAnsweredCount((prev) => prev + 1);

      if (result.correct) {
        setScore((prev) => prev + 1);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to submit answer.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleNextQuestion = () => {
    setQuestionId((prev) => prev + 1);
  };

  return (
    <div style={{ maxWidth: 760, margin: '40px auto', padding: 24, fontFamily: 'Arial, sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 style={{ margin: 0 }}>Grammar Practice</h1>
        <div style={{ fontWeight: 700 }}>
          Score: {score} / {answeredCount}
        </div>
      </div>

      {loading && <p>Loading question...</p>}
      {error && <p style={{ color: 'crimson' }}>Error: {error}</p>}

      {!loading && !error && question && (
        <div style={{ border: '1px solid #d9d9d9', borderRadius: 12, padding: 24, background: '#fff' }}>
          <p style={{ color: '#666', marginTop: 0 }}>
            {question.topic} • {question.difficulty}
          </p>
          <h2 style={{ marginTop: 8 }}>{question.question}</h2>

          <div style={{ display: 'grid', gap: 12, marginTop: 20 }}>
            {question.answers.map((answer) => {
              const isSelected = selectedAnswerId === answer.id;
              const isCorrect = feedback && answer.answer === feedback.correctAnswer;
              const isWrongSelection = feedback && isSelected && !feedback.correct;

              let background = '#fff';
              let border = '1px solid #bdbdbd';
              let color = '#222';

              if (feedback && isCorrect) {
                background = '#e8f5e9';
                border = '1px solid #2e7d32';
                color = '#1b5e20';
              } else if (feedback && isWrongSelection) {
                background = '#ffebee';
                border = '1px solid #c62828';
                color = '#b71c1c';
              }

              return (
                <button
                  key={answer.id}
                  type="button"
                  disabled={submitting || Boolean(feedback)}
                  onClick={() => handleAnswerSelect(answer.id)}
                  style={{
                    padding: '12px 14px',
                    textAlign: 'left',
                    borderRadius: 8,
                    cursor: submitting || feedback ? 'not-allowed' : 'pointer',
                    background,
                    border,
                    color,
                    fontSize: 16,
                  }}
                >
                  {answer.answer}
                </button>
              );
            })}
          </div>

          {feedback && (
            <div
              style={{
                marginTop: 24,
                padding: 16,
                borderRadius: 8,
                background: feedback.correct ? '#e8f5e9' : '#ffebee',
                border: `1px solid ${feedback.correct ? '#2e7d32' : '#c62828'}`,
                color: feedback.correct ? '#1b5e20' : '#b71c1c',
              }}
            >
              <strong>{feedback.correct ? 'Correct!' : 'Incorrect.'}</strong>
              <p style={{ margin: '8px 0 0' }}>
                {feedback.correct ? 'Great job.' : `Correct answer: ${feedback.correctAnswer}`}
              </p>
              <p style={{ margin: '8px 0 0' }}>{feedback.explanation}</p>
            </div>
          )}

          {feedback && (
            <button
              type="button"
              onClick={handleNextQuestion}
              style={{
                marginTop: 24,
                padding: '10px 16px',
                border: 'none',
                borderRadius: 8,
                background: '#1976d2',
                color: '#fff',
                cursor: 'pointer',
              }}
            >
              Next question
            </button>
          )}
        </div>
      )}
    </div>
  );
}
