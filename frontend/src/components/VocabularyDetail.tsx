import React from 'react';
import { Vocabulary } from '../api/vocabulary';

type Props = {
  vocabulary: Vocabulary | null;
  loading: boolean;
  error: string | null;
  learned: boolean;
  onToggleLearned: (id: number) => void;
};

export default function VocabularyDetail({ vocabulary, loading, error, learned, onToggleLearned }: Props) {
  if (loading) {
    return <p>Loading vocabulary details...</p>;
  }

  if (error) {
    return <p style={{ color: 'crimson' }}>Error: {error}</p>;
  }

  if (!vocabulary) {
    return <p>Select a vocabulary item to view details.</p>;
  }

  return (
    <div style={{ border: '1px solid #d9d9d9', borderRadius: 8, padding: 20, background: '#fff' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 12 }}>
        <h2 style={{ margin: 0 }}>{vocabulary.word}</h2>
        <button
          type="button"
          onClick={() => onToggleLearned(vocabulary.id)}
          style={{
            border: learned ? '1px solid #2e7d32' : '1px solid #666',
            background: learned ? '#e8f5e9' : '#fff',
            color: learned ? '#1b5e20' : '#333',
            borderRadius: 6,
            padding: '8px 12px',
            cursor: 'pointer',
          }}
        >
          {learned ? 'Learned' : 'Mark learned'}
        </button>
      </div>

      <p><strong>Meaning:</strong> {vocabulary.meaning}</p>
      <p><strong>Part of speech:</strong> {vocabulary.partOfSpeech}</p>
      {vocabulary.pronunciation && <p><strong>Pronunciation:</strong> {vocabulary.pronunciation}</p>}
      {vocabulary.example && <p><strong>Example:</strong> {vocabulary.example}</p>}
      <p><strong>Difficulty:</strong> {vocabulary.difficulty}</p>
    </div>
  );
}
