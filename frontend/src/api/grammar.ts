import api from './client';

export type GrammarAnswerOption = {
  id: number;
  answer: string;
};

export type GrammarQuestion = {
  id: number;
  question: string;
  explanation: string;
  difficulty: string;
  topic: string;
  answers: GrammarAnswerOption[];
};

export type GrammarAnswerFeedback = {
  correct: boolean;
  correctAnswer: string;
  explanation: string;
};

export type GrammarStatistics = {
  totalAttempts: number;
  totalCorrect: number;
  accuracyRate: number;
};

export async function getGrammarQuestionIds(): Promise<number[]> {
  const response = await api.get<number[]>('/api/v1/grammar/questions');
  return response.data;
}

export async function getGrammarQuestion(questionId: number): Promise<GrammarQuestion> {
  const response = await api.get<GrammarQuestion>(`/api/v1/grammar/questions/${questionId}`);
  return response.data;
}

export async function submitGrammarAnswer(questionId: number, answerId: number): Promise<GrammarAnswerFeedback> {
  const response = await api.post<GrammarAnswerFeedback>(`/api/v1/grammar/questions/${questionId}/answer`, { answerId });
  return response.data;
}

export async function getGrammarStatistics(): Promise<GrammarStatistics> {
  const response = await api.get<GrammarStatistics>('/api/v1/users/me/grammar/statistics');
  return response.data;
}
