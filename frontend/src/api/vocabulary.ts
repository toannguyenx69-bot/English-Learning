import api from './client';

export type Vocabulary = {
  id: number;
  word: string;
  meaning: string;
  pronunciation?: string | null;
  partOfSpeech: string;
  example?: string | null;
  difficulty: string;
  imageUrl?: string | null;
  authorName?: string | null;
  authorUrl?: string | null;
  sourceUrl?: string | null;
  createdAt?: string;
  updatedAt?: string;
};

export type VocabularyPage = {
  content: Vocabulary[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export type LearnedVocabulary = {
  id: number;
  vocabularyId: number;
  word: string;
  meaning: string;
  pronunciation?: string | null;
  partOfSpeech: string;
  example?: string | null;
  difficulty: string;
  createdAt?: string;
};

export async function getVocabularies(params?: {
  page?: number;
  size?: number;
  q?: string;
  sortBy?: string;
  sortDir?: string;
}): Promise<VocabularyPage> {
  const response = await api.get<VocabularyPage>('/api/v1/vocabularies', { params });
  return response.data;
}

export async function getVocabularyById(id: number): Promise<Vocabulary> {
  const response = await api.get<Vocabulary>(`/api/v1/vocabularies/${id}`);
  return response.data;
}

export async function markVocabularyAsLearned(id: number): Promise<void> {
  await api.post(`/api/v1/vocabularies/${id}/learn`);
}

export async function unmarkVocabularyAsLearned(id: number): Promise<void> {
  await api.delete(`/api/v1/vocabularies/${id}/learn`);
}

export async function getLearnedVocabularies(): Promise<LearnedVocabulary[]> {
  const response = await api.get<LearnedVocabulary[]>('/api/v1/users/me/vocabularies');
  return response.data;
}

export async function getLearnedProgress(): Promise<{
  totalLearned: number;
  totalVocabularies: number;
  progressPercent: number;
}> {
  const response = await api.get('/api/v1/users/me/vocabularies/progress');
  return response.data;
}
