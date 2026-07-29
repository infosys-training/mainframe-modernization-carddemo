import axios from "axios";

const API_BASE = import.meta.env.VITE_API_URL || "http://localhost:8000";

const api = axios.create({
  baseURL: API_BASE,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// Auth
export const login = (user_id: string, password: string) =>
  api.post("/api/auth/login", { user_id, password });

export const logout = () => api.post("/api/auth/logout");

// Accounts
export const getAccounts = (page = 1, pageSize = 20) =>
  api.get("/api/accounts", { params: { page, page_size: pageSize } });

export const getAccount = (id: number) => api.get(`/api/accounts/${id}`);

export const createAccount = (data: Record<string, unknown>) =>
  api.post("/api/accounts", data);

export const updateAccount = (id: number, data: Record<string, unknown>) =>
  api.put(`/api/accounts/${id}`, data);

// Cards
export const getCards = (page = 1, pageSize = 20, acctId?: number) =>
  api.get("/api/cards", {
    params: { page, page_size: pageSize, acct_id: acctId },
  });

export const getCard = (cardNum: string) => api.get(`/api/cards/${cardNum}`);

export const updateCard = (cardNum: string, data: Record<string, unknown>) =>
  api.put(`/api/cards/${cardNum}`, data);

// Transactions
export const getTransactions = (
  page = 1,
  pageSize = 20,
  cardNum?: string,
  acctId?: number
) =>
  api.get("/api/transactions", {
    params: { page, page_size: pageSize, card_num: cardNum, acct_id: acctId },
  });

export const getTransaction = (id: string) =>
  api.get(`/api/transactions/${id}`);

export const addTransaction = (data: Record<string, unknown>) =>
  api.post("/api/transactions", data);

// Users (admin only)
export const getUsers = (page = 1, pageSize = 20) =>
  api.get("/api/users", { params: { page, page_size: pageSize } });

export const getUser = (id: string) => api.get(`/api/users/${id}`);

export const createUser = (data: Record<string, unknown>) =>
  api.post("/api/users", data);

export const updateUser = (id: string, data: Record<string, unknown>) =>
  api.put(`/api/users/${id}`, data);

export const deleteUser = (id: string) => api.delete(`/api/users/${id}`);

// Reports
export const getTransactionReport = (params: Record<string, unknown>) =>
  api.get("/api/reports/transactions", { params });

// Billing
export const getBillingStatements = (acctId?: number) =>
  api.get("/api/billing/statements", { params: { acct_id: acctId } });

export default api;
