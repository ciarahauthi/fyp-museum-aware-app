import { apiFetch } from "./api";

export const exhibitsService = {
  getAll: () => apiFetch("/api/exhibits/admin"),

  getById: (id) => apiFetch(`/api/exhibits/${id}`),
};