import React from 'react';
import { Vocabulary } from '../api/vocabulary';
import VocabularyPronunciation from './VocabularyPronunciation';

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

  const unsplashUrl = vocabulary.sourceUrl
    ? `${vocabulary.sourceUrl}?utm_source=english-learning&utm_medium=referral&utm_campaign=api-credit`
    : 'https://unsplash.com/?utm_source=english-learning&utm_medium=referral&utm_campaign=api-credit';

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

      {vocabulary.imageUrl ? (
        <div style={{ margin: '16px 0' }}>
          <img
            src={vocabulary.imageUrl}
            alt={vocabulary.word}
            style={{
              width: '100%',
              maxHeight: 320,
              objectFit: 'cover',
              borderRadius: 8,
              display: 'block',
            }}
          />
          {(vocabulary.authorName || vocabulary.authorUrl) && (
            <div style={{ marginTop: 8, fontSize: 13, color: '#555' }}>
              Photo by{' '}
              {vocabulary.authorUrl ? (
                <a
                  href={vocabulary.authorUrl}
                  target="_blank"
                  rel="noreferrer"
                  style={{ color: '#2563eb' }}
                >
                  {vocabulary.authorName || 'Photographer'}
                </a>
              ) : (
                <span>{vocabulary.authorName || 'Photographer'}</span>
              )}{' '}
              on{' '}
              <a
                href={unsplashUrl}
                target="_blank"
                rel="noreferrer"
                style={{ color: '#2563eb' }}
              >
                Unsplash
              </a>
            </div>
          )}
        </div>
      ) : (
        <div
          style={{
            margin: '16px 0',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            minHeight: 140,
            border: '1px dashed #d0d7de',
            borderRadius: 8,
            background: '#f6f8fa',
            color: '#666',
            fontSize: 14,
          }}
        >
          No image available
        </div>
      )}

      <VocabularyPronunciation vocabularyId={vocabulary.id} word={vocabulary.word} />

      <p><strong>Meaning:</strong> {vocabulary.meaning}</p>
      <p><strong>Part of speech:</strong> {vocabulary.partOfSpeech}</p>
      {vocabulary.example && <p><strong>Example:</strong> {vocabulary.example}</p>}
      <p><strong>Difficulty:</strong> {vocabulary.difficulty}</p>
    </div>
  );
}
