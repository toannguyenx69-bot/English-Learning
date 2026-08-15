import React, { useEffect, useState } from 'react';
import {
  getGrammarQuestion,
  getGrammarQuestionIds,
  type GrammarQuestion,
  submitGrammarAnswer,
  type GrammarAnswerFeedback,
} from '../api/grammar';

export default function GrammarPage() {
  const [questionId, setQuestionId] = useState<number | null>(null);
  const [questionIds, setQuestionIds] = useState<number[]>([]);
  const [questionIndex, setQuestionIndex] = useState(0);
  const [question, setQuestion] = useState<GrammarQuestion | null>(null);
  const [selectedAnswerId, setSelectedAnswerId] = useState<number | null>(null);
  const [feedback, setFeedback] = useState<GrammarAnswerFeedback | null>(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [score, setScore] = useState(0);
  const [attempts, setAttempts] = useState(0);

  useEffect(() => {
    const fetchFirstValidQuestion = async () => {
      try {
        const ids = await getGrammarQuestionIds();
        const validIds = ids.filter((id) => Number.isFinite(id) && id > 0).sort((a, b) => a - b);

        if (validIds.length === 0) {
          setQuestionIds([]);
          setQuestionIndex(0);
          setQuestionId(null);
          setQuestion(null);
          setError('No grammar questions available.');
          return;
        }

        setQuestionIds(validIds);
        setQuestionIndex(0);
        setQuestionId(validIds[0]);
      } catch (err) {
        setQuestionIds([]);
        setQuestionIndex(0);
        setQuestionId(null);
        setQuestion(null);
        setError(err instanceof Error ? err.message : 'Unable to load grammar questions.');
      }
    };

    fetchFirstValidQuestion();
  }, []);

  useEffect(() => {
    if (questionId === null) {
      return;
    }

    const fetchQuestion = async () => {
      setLoading(true);
      setError(null);
      setSelectedAnswerId(null);
      setFeedback(null);

      try {
        const data = await getGrammarQuestion(questionId);
        setQuestion(data);
      } catch (err) {
        setQuestion(null);
        setError(err instanceof Error ? err.message : 'Unable to load grammar question.');
      } finally {
        setLoading(false);
      }
    };

    fetchQuestion();
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
      setAttempts((prev) => prev + 1);

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
    if (questionIds.length === 0) {
      return;
    }

    setQuestionIndex((currentIndex) => {
      const nextIndex = currentIndex + 1 >= questionIds.length ? 0 : currentIndex + 1;
      setQuestionId(questionIds[nextIndex]);
      return nextIndex;
    });
  };

  if (loading) {
    return <p>Loading grammar question...</p>;
  }

  if (error) {
    return <p style={{ color: 'crimson' }}>Error: {error}</p>;
  }

  if (!question) {
    return <p>No grammar question available.</p>;
  }

  return (
    <div style={{ maxWidth: 800, margin: '40px auto', padding: 24, fontFamily: 'Arial, sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 style={{ margin: 0 }}>Grammar Practice</h1>
        <div style={{ fontWeight: 700 }}>
          Score: {score} / {attempts}
        </div>
      </div>

      <div style={{ border: '1px solid #dfe3ec', borderRadius: 12, background: '#fff', padding: 24 }}>
        <p style={{ color: '#666', marginTop: 0, marginBottom: 8 }}>
          {question.topic} • {question.difficulty}
        </p>

        <h2 style={{ marginTop: 0 }}>{question.question}</h2>

        <div style={{ display: 'grid', gap: 12, marginTop: 20 }}>
          {question.answers.map((answer) => {
            const isSelected = selectedAnswerId === answer.id;
            const isCorrectAnswer = feedback && answer.answer === feedback.correctAnswer;
            const isWrongSelection = feedback && isSelected && !feedback.correct;

            let buttonStyle: React.CSSProperties = {
              border: '1px solid #cfd8e3',
              background: '#fff',
              color: '#111827',
              borderRadius: 8,
              padding: '12px 14px',
              textAlign: 'left',
              cursor: submitting || Boolean(feedback) ? 'not-allowed' : 'pointer',
            };

            if (feedback && isCorrectAnswer) {
              buttonStyle = {
                ...buttonStyle,
                background: '#e8f5e9',
                border: '1px solid #2e7d32',
                color: '#1b5e20',
              };
            }

            if (feedback && isWrongSelection) {
              buttonStyle = {
                ...buttonStyle,
                background: '#ffebee',
                border: '1px solid #c62828',
                color: '#b71c1c',
              };
            }

            return (
              <button
                key={answer.id}
                type="button"
                onClick={() => handleAnswerSelect(answer.id)}
                disabled={submitting || Boolean(feedback)}
                style={buttonStyle}
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
              {feedback.correct ? 'Nice work.' : `Correct answer: ${feedback.correctAnswer}`}
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
              background: '#2563eb',
              color: '#fff',
              fontWeight: 700,
              cursor: 'pointer',
            }}
          >
            Next question
          </button>
        )}
      </div>
    </div>
  );
}
