import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000,
});

export type HealthResponse = {
  status: string;
};

export async function getHealth(): Promise<HealthResponse> {
  const resp = await api.get<HealthResponse>('/api/v1/health');
  return resp.data;
}
