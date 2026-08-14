import React from 'react';
import { Vocabulary } from '../api/vocabulary';

type Props = {
  vocabularies: Vocabulary[];
  loading: boolean;
  error: string | null;
  onSelect: (id: number) => void;
  onToggleLearned: (id: number) => void;
  learnedIds: number[];
};

export default function VocabularyList({
  vocabularies,
  loading,
  error,
  onSelect,
  onToggleLearned,
  learnedIds,
}: Props) {
  if (loading) {
    return <p>Loading vocabulary...</p>;
  }

  if (error) {
    return <p style={{ color: 'crimson' }}>Error: {error}</p>;
  }

  if (vocabularies.length === 0) {
    return <p>No vocabulary found.</p>;
  }

  return (
    <div style={{ display: 'grid', gap: 12 }}>
      {vocabularies.map((vocabulary) => {
        const learned = learnedIds.includes(vocabulary.id);

        return (
          <div
            key={vocabulary.id}
            style={{
              border: '1px solid #d9d9d9',
              borderRadius: 8,
              padding: 16,
              background: '#fff',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, alignItems: 'center' }}>
              <button
                type="button"
                onClick={() => onSelect(vocabulary.id)}
                style={{ background: 'transparent', border: 'none', padding: 0, fontSize: 20, cursor: 'pointer', textAlign: 'left' }}
              >
                <strong>{vocabulary.word}</strong>
              </button>

              <button
                type="button"
                onClick={() => onToggleLearned(vocabulary.id)}
                style={{
                  border: learned ? '1px solid #2e7d32' : '1px solid #666',
                  background: learned ? '#e8f5e9' : '#fff',
                  color: learned ? '#1b5e20' : '#333',
                  borderRadius: 6,
                  padding: '6px 10px',
                  cursor: 'pointer',
                }}
              >
                {learned ? 'Learned' : 'Mark learned'}
              </button>
            </div>

            <p style={{ margin: '8px 0' }}>{vocabulary.meaning}</p>
            <small>
              {vocabulary.partOfSpeech} • {vocabulary.difficulty}
              {vocabulary.pronunciation ? ` • ${vocabulary.pronunciation}` : ''}
            </small>
          </div>
        );
      })}
    </div>
  );
}
