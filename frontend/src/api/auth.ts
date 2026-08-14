import api from './client';

export type LoginRequest = {
  email: string;
  password: string;
};

export type RegisterRequest = {
  username: string;
  email: string;
  password: string;
};

export type AuthResponse = {
  accessToken: string;
  tokenType: string;
};

export async function login(payload: LoginRequest): Promise<AuthResponse> {
  const response = await api.post<AuthResponse>('/api/v1/auth/login', payload);
  return response.data;
}

export async function register(payload: RegisterRequest): Promise<void> {
  await api.post('/api/v1/auth/register', payload);
}
