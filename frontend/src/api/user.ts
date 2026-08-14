import api from './client';

export type UserProfile = {
  id: number;
  username: string;
  email: string;
  createdAt?: string;
  updatedAt?: string;
};

export async function getCurrentUser(): Promise<UserProfile> {
  const response = await api.get<UserProfile>('/api/v1/users/me');
  return response.data;
}
